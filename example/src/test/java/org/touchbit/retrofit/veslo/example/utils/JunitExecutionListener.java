/*
 * Copyright 2021 Shaburov Oleg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.touchbit.retrofit.veslo.example.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.junitplatform.AllureJunitPlatform;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.platform.engine.TestExecutionResult.Status.*;

public final class JunitExecutionListener
        extends AllureJunitPlatform
        implements TestWatcher {

    public static final Logger CONSOLE_LOG = LoggerFactory.getLogger("Console");
    public static final Logger ROUTING_LOG = LoggerFactory.getLogger("Routing");
    public static final Logger FRAMEWORK_LOG = LoggerFactory.getLogger("Framework");

    static {
        final String userDir = System.getProperty("user.dir");
        final Path reportPath;
        if (userDir.endsWith("example")) {
            reportPath = new File(userDir, "target/allure-results").toPath();
        } else {
            reportPath = new File(userDir, "example/target/allure-results").toPath();
        }
        System.setProperty("allure.results.directory", reportPath.toString());
    }

    @Override
    public void executionStarted(final TestIdentifier testIdentifier) {
        UniqueId parse = UniqueId.parse(testIdentifier.getUniqueId());
        final UniqueId.Segment lastSegment = parse.getLastSegment();
        final String fileName = new File("test", lastSegment.getValue().replace("()", "") + ".log").toPath().toString();
        setTestLogFileName(fileName);
        super.executionStarted(testIdentifier);
        if (testIdentifier.isTest()) {
            final String displayName = testIdentifier.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                ROUTING_LOG.info("Test started: {}", displayName);
            } else {
                ROUTING_LOG.info("Test started: {}", testIdentifier);
            }
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        super.executionFinished(testIdentifier, testExecutionResult);
        if (testIdentifier.isTest()) {
            final String displayName = testIdentifier.getDisplayName() == null ? "" : testIdentifier.getDisplayName();
            final TestExecutionResult.Status status = testExecutionResult.getStatus();
            if (testIdentifier.isTest()) {
                if (status == FAILED) {
                    final Throwable throwable = testExecutionResult.getThrowable().orElse(null);
                    if (throwable != null) {
                        testExecutionResult.getThrowable().ifPresent(t -> ROUTING_LOG.error("Test execution failed with:", t));
                        String err = throwable.toString();
                        String at = "";
                        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                            final String element = stackTraceElement.toString();
                            if (element.contains(".test") && element.contains("Tests.")) {
                                at = "\n  at " + element;
                            }
                        }
                        if (isIntellijIdeaConsoleJunitRun()) {
                            CONSOLE_LOG.error("FAILED: {}\n  {}\n  {}{}\n", displayName, getTestLogFilePath(), err, at);
                        }
                    } else {
                        if (isIntellijIdeaConsoleJunitRun()) {
                            CONSOLE_LOG.error("FAILED: {}\n  {}\n", displayName, getTestLogFilePath());
                        }
                    }
                }
                if (status == ABORTED) {
                    if (isIntellijIdeaConsoleJunitRun()) {
                        CONSOLE_LOG.warn("ABORTED: {}\n  {}\n", displayName, getTestLogFilePath());
                    }
                    ROUTING_LOG.warn("ABORTED: {}\n  {}\n", displayName, getTestLogFilePath());
                }
                if (status == SUCCESSFUL) {
                    if (isIntellijIdeaConsoleJunitRun()) {
                        CONSOLE_LOG.info("SUCCESSFUL: {}\n  {}\n", displayName, getTestLogFilePath());
                    }
                    ROUTING_LOG.info("SUCCESSFUL: {}\n  {}\n", displayName, getTestLogFilePath());
                }
            }
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        addTestLogAttachment();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        addTestLogAttachment();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        addTestLogAttachment();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        addTestLogAttachment();
    }

    private void addTestLogAttachment() {
        File testLogAbsoluteFilePath = getTestLogFilePath().toFile();
        if (testLogAbsoluteFilePath.exists()) {
            try {
                InputStream targetStream = new FileInputStream(testLogAbsoluteFilePath);
                Allure.addAttachment(testLogAbsoluteFilePath.getName(), targetStream);
            } catch (Exception e) {
                FRAMEWORK_LOG.error("Failed to add test log file to allure report", e);
            }
        }
    }

    public static Path getTestLogFilePath() {
        String s = MDC.get("log.file.name");
        if (s == null) {
            s = "default-routing.log";
        }
        return new File("target/logs/", s).toPath().toAbsolutePath();
    }

    public static void setTestLogFileName(String testLogFileName) {
        MDC.put("log.file.name", testLogFileName);
    }

    public static boolean isIntellijIdeaConsoleJunitRun() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.toString().contains("com.intellij.rt.junit")) {
                return true;
            }
        }
        return false;
    }

}
