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

package internal.test.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.net.URI;

public class LoggerProvider {

    public static final Logger CONSOLE_LOG = LoggerFactory.getLogger("Console");
    public static final Logger ROUTING_LOG = LoggerFactory.getLogger("Routing");

    public static URI getTestLogFileUri() {
        String s = MDC.get("log.file.name");
        if (s == null) {
            s = "default-routing.log";
        }
        return new File("target/logs/", s).toPath().toAbsolutePath().toUri();
    }

    public static void setTestLogFileName(String testLogFileName) {
        MDC.put("log.file.name", testLogFileName);
    }

    private LoggerProvider() {
    }

}
