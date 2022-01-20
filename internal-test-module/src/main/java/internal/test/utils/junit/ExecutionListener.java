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

package internal.test.utils.junit;

import internal.test.utils.log.LoggerProvider;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.net.URI;

import static internal.test.utils.log.LoggerProvider.CONSOLE_LOG;
import static internal.test.utils.log.LoggerProvider.ROUTING_LOG;
import static org.junit.platform.engine.TestExecutionResult.Status.FAILED;

public final class ExecutionListener extends SummaryGeneratingListener {

    @Override
    public void executionStarted(final TestIdentifier testIdentifier) {
        UniqueId parse = UniqueId.parse(testIdentifier.getUniqueId());
        final UniqueId.Segment lastSegment = parse.getLastSegment();
        final String fileName = new File("test", lastSegment.getValue().replace("()", "") + ".log").toPath().toString();
        LoggerProvider.setTestLogFileName(fileName);
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
        if (testIdentifier.getSource().isPresent() &&
                testIdentifier.getSource().get().toString().contains("veslo.example.tests.api")) {
            // log deduplication in example project when running tests from Intellij Idea for whole project
            return;
        }
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
                        final URI testLogFileUri = LoggerProvider.getTestLogFileUri();
                        CONSOLE_LOG.error("FAILED: {}\n  {}\n  {}{}\n", displayName, testLogFileUri, err, at);
                    } else {
                        CONSOLE_LOG.error("FAILED: {}\n  {}\n", displayName, LoggerProvider.getTestLogFileUri());
                    }
                }
            }
        }
    }

}
