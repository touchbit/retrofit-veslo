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

package org.touchbit.retrofit.veslo.client.inteceptor;

import internal.test.utils.OkHttpTestUtils;
import internal.test.utils.log.UnitTestLogger;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.slf4j.event.SubstituteLoggingEvent;

import java.io.IOException;
import java.net.SocketException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("LoggingAction class tests")
public class LoggingInterceptActionUnitTests {

    @Test
    @DisplayName("LoggingAction constructor without arguments")
    public void test1639065951188() {
        new LoggingInterceptAction();
    }

    @Test
    @DisplayName("#requestAction() logging an HTTP request")
    public void test1639065951194() throws IOException {
        UnitTestLogger logger = new UnitTestLogger();
        final Request request = OkHttpTestUtils.getRequest();
        final Request result = new LoggingInterceptAction(logger).requestAction(request);
        assertThat("", result, is(request));
        assertThat("", logger.getLogEventCount(), is(1));
        final SubstituteLoggingEvent logEvent = logger.getNextLogEvent();
        assertThat("logEvent.getLevel()", logEvent.getLevel(), is(Level.INFO));
        assertThat("logEvent.getThrowable()", logEvent.getThrowable(), nullValue());
        assertThat("logEvent.getMessage()", logEvent.getMessage(), is("REQUEST:\n" +
                "POST http://localhost/\n" +
                "Headers:\n" +
                "  Content-Type: text/plain\n" +
                "  X-Request-ID: generated\n" +
                "  Content-Length: 9\n" +
                "Body: (9-byte body)\n" +
                "  generated\n"));
    }

    @Test
    @DisplayName("#responseAction() logging an HTTP response")
    public void test1639065951214() throws IOException {
        UnitTestLogger logger = new UnitTestLogger();
        final Response response = OkHttpTestUtils.getResponse();
        final Response result = new LoggingInterceptAction(logger).responseAction(response);
        assertThat("", result, is(response));
        assertThat("", logger.getLogEventCount(), is(1));
        final SubstituteLoggingEvent logEvent = logger.getNextLogEvent();
        assertThat("logEvent.getLevel()", logEvent.getLevel(), is(Level.INFO));
        assertThat("logEvent.getThrowable()", logEvent.getThrowable(), nullValue());
        assertThat("logEvent.getMessage()", logEvent.getMessage(), is("RESPONSE:\n" +
                "200 TEST http://localhost/\n" +
                "Headers:\n" +
                "  Content-Type: text/plain\n" +
                "  Content-Length: 9\n" +
                "  X-Request-ID: generated\n" +
                "Body: (9-byte body)\n" +
                "  generated\n"));
    }

    @Test
    @DisplayName("#errorAction() logging runtime exception (with stacktrace)")
    public void test1639065951234() {
        UnitTestLogger logger = new UnitTestLogger();
        final RuntimeException exception = new RuntimeException("test1638279095907");
        new LoggingInterceptAction(logger).errorAction(exception);
        assertThat("", logger.getLogEventCount(), is(1));
        final SubstituteLoggingEvent logEvent = logger.getNextLogEvent();
        assertThat("logEvent.getMessage()", logEvent.getMessage(), is("Transport error"));
        assertThat("logEvent.getThrowable()", logEvent.getThrowable(), is(exception));
        assertThat("logEvent.getLevel()", logEvent.getLevel(), is(Level.ERROR));
    }

    @Test
    @DisplayName("#errorAction() logging java.net.* exception (without stacktrace)")
    public void test1639065951247() {
        UnitTestLogger logger = new UnitTestLogger();
        final SocketException exception = new SocketException("test1638279449397");
        new LoggingInterceptAction(logger).errorAction(exception);
        assertThat("", logger.getLogEventCount(), is(1));
        final SubstituteLoggingEvent logEvent = logger.getNextLogEvent();
        assertThat("logEvent.getMessage()", logEvent.getMessage(), is("java.net.SocketException: test1638279449397"));
        assertThat("logEvent.getThrowable()", logEvent.getThrowable(), nullValue());
        assertThat("logEvent.getLevel()", logEvent.getLevel(), is(Level.ERROR));
    }

}