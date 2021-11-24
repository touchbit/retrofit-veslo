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

package internal.test.utils.asserter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class ThrowableAsserter {

    private static final String INDENT = "    ";
    private final Throwable throwable;
    private final String throwableInfo;

    public ThrowableAsserter(ThrowableRunnable runnable) {
        try {
            runnable.execute();
            throw new AssertionError("The function call completed without any exceptions being thrown.");
        } catch (Throwable t) {
            throwable = t;
            throwableInfo = getThrowableInfo(throwable);
        }
    }

    public ThrowableAsserter(Throwable throwable, String throwableInfo) {
        this.throwable = throwable;
        this.throwableInfo = throwableInfo;
    }

    public static void assertUtilityClassException(Class<?> aClass) {
        for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
            final int modifiers = constructor.getModifiers();
            if (!Modifier.isPrivate(modifiers)) {
                throw new AssertionError("Constructor access modifiers for " + aClass.getTypeName() + "\n" +
                        "Expected: is private\n" +
                        "  Actual: " + Modifier.toString(modifiers));
            }
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
                throw new AssertionError("The function call completed without UtilityClassException being thrown.");
            } catch (Exception e) {
                if (e.getCause() == null) {
                    throw new AssertionError("Unexpected exception", e);
                }
                if (!e.getCause().getClass().getName().contains("UtilityClassException")) {
                    throw new AssertionError("Exception class\n" +
                            "Expected: UtilityClassException.class \n" +
                            "  Actual: " + e.getCause().getClass());
                }
            }
        }
    }

    public static ThrowableAsserter assertThrow(ThrowableRunnable runnable, Class<?> throwableClass, String message) {
        return assertThrow(runnable).assertClass(throwableClass).assertMessageIs(message);
    }

    public static ThrowableAsserter assertThrow(ThrowableRunnable runnable) {
        return new ThrowableAsserter(runnable);
    }

    private String getThrowableInfo(Throwable throwable) {
        StringJoiner result = new StringJoiner("\n");
        List<String> messages = getThrowableMessages(throwable);
        for (int i = 0; i < messages.size(); i++) {
            final String msg;
            if (i == 0) {
                msg = INDENT + messages.get(i).replace("\n", "\\n");
            } else {
                msg = INDENT + "Caused by: " + messages.get(i).replace("\n", "\\n");
            }
            result.add(msg);
        }
        return result.toString();
    }

    private List<String> getThrowableMessages(Throwable throwable) {
        List<String> result = new ArrayList<>();
        if (throwable != null) {
            result.add(throwable.toString());
            if (throwable.getCause() != null) {
                result.addAll(getThrowableMessages(throwable.getCause()));
            }
        }
        return result;
    }

    public ThrowableAsserter assertMessageIs(String expected) {
        return assertIs("Throwable message by:", Throwable::getMessage, expected);
    }

    public ThrowableAsserter assertMessageContains(String... expectedStrings) {
        for (String expected : expectedStrings) {
            if (throwable.getMessage() == null || !throwable.getMessage().contains(expected)) {
                String expStr = (expected + "").replace("\n", "\\n");
                String actStr = (throwable.getMessage() + "").replace("\n", "\\n");
                throw new AssertionError("Throwable message by:\n" + throwableInfo + "\n\n" +
                        "Expected: message contains '" + expStr + "'\n" +
                        "  Actual: " + actStr);
            }
        }
        return this;
    }

    public ThrowableAsserter assertClass(Class<?> expected) {
        return assertIs("Throwable class by:", Throwable::getClass, expected);
    }

    public ThrowableAsserter assertNPE(String parameter) {
        return assertClass(NullPointerException.class)
                .assertMessageIs("Parameter '" + parameter + "' is required and cannot be null.");
    }

    public ThrowableAsserter assertCause(Function<ThrowableAsserter, ThrowableAsserter> function) {
        return function.apply(new ThrowableAsserter(throwable.getCause(), throwableInfo));
    }

    protected <R> ThrowableAsserter assertIs(String reason, Function<Throwable, R> actualFunction, R expected) {
        R actual = null;
        try {
            actual = actualFunction.apply(throwable);
        } catch (NullPointerException ignore) {
        }
        if (actual == null || !actual.equals(expected)) {
            String expStr = (expected + "").replace("\n", "\\n");
            String actStr = (actual + "").replace("\n", "\\n");
            throw new AssertionError(reason + "\n" + throwableInfo + "\n\n" +
                    "Expected: " + expStr + "\n" +
                    "  Actual: " + actStr);
        }
        return this;
    }

}
