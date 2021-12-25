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

package org.touchbit.retrofit.veslo.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BriefAssertionError extends AssertionError {

    private static Function<String, Boolean> breakFunction = (element) -> !element.contains("Base") &&
            (element.contains(".Test") || element.contains("Test.") ||
                    element.contains(".Tests") || element.contains("Tests."));

    public BriefAssertionError() {
        this(null, null);
    }

    public BriefAssertionError(String message) {
        this(message, null);
    }

    public BriefAssertionError(Throwable throwable) {
        this(null, throwable);
    }

    public BriefAssertionError(String message, Throwable throwable) {
        super(message, throwable);
        List<StackTraceElement> result = new ArrayList<>();
        for (StackTraceElement stackTraceElement : getStackTrace()) {
            result.add(stackTraceElement);
            if (breakFunction.apply(stackTraceElement.toString())) {
                break;
            }
        }
        setStackTrace(result.toArray(new StackTraceElement[]{}));
    }

    public static void setStackTraceBreakFunction(Function<String, Boolean> breakFunction) {
        BriefAssertionError.breakFunction = breakFunction;
    }

}
