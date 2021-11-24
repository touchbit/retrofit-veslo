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

import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;

import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class UnitTestLogger extends SubstituteLogger {

    private boolean isErrorEnabled = true;
    private boolean isWarnEnabled = true;
    private boolean isInfoEnabled = true;
    private boolean isDebugEnabled = true;
    private boolean isTraceEnabled = true;

    private static final ThreadLocal<Queue<SubstituteLoggingEvent>> QUEUE_THREAD_LOCAL = new ThreadLocal<>();

    public UnitTestLogger() {
        super("UnitTestLogger", getQueue(), false);
        QUEUE_THREAD_LOCAL.get().clear();
    }

    private static Queue<SubstituteLoggingEvent> getQueue() {
        if (QUEUE_THREAD_LOCAL.get() == null) {
            QUEUE_THREAD_LOCAL.set(new LinkedList<>());
        }
        return QUEUE_THREAD_LOCAL.get();
    }

    public SubstituteLoggingEvent getNextLogEvent() {
        return QUEUE_THREAD_LOCAL.get().poll();
    }

    public int getLogEventCount() {
        return QUEUE_THREAD_LOCAL.get().size();
    }

    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled;
    }

    public UnitTestLogger setErrorEnabled(boolean errorEnabled) {
        isErrorEnabled = errorEnabled;
        return this;
    }

    public UnitTestLogger setWarnEnabled(boolean warnEnabled) {
        isWarnEnabled = warnEnabled;
        return this;
    }

    public UnitTestLogger setInfoEnabled(boolean infoEnabled) {
        isInfoEnabled = infoEnabled;
        return this;
    }

    public UnitTestLogger setDebugEnabled(boolean debugEnabled) {
        isDebugEnabled = debugEnabled;
        return this;
    }

    public UnitTestLogger setTraceEnabled(boolean traceEnabled) {
        isTraceEnabled = traceEnabled;
        return this;
    }

}
