/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo.util;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

@DisplayName("ExceptionBuilder.class unit tests")
public class ExceptionBuilderUnitTests extends BaseUnitTest {

    @Nested
    @DisplayName("#errorMessage(String) method tests")
    public class ErrorMessageMethodTests {

        @Test
        @DisplayName("errorMessage = foo")
        public void test1647358514918() {
            final String msg = builder().errorMessage("foo").build().getMessage();
            assertIs(msg, "\n  foo\n");
        }

        @Test
        @DisplayName("errorMessage = null")
        public void test1647358517164() {
            final String msg = builder().errorMessage(null).build().getMessage();
            assertIs(msg, "\n  null\n");
        }

    }

    @Nested
    @DisplayName("#errorCause() method tests")
    public class ErrorCauseMethodTests {

        @Test
        @DisplayName("errorCause is null")
        public void test1647358520180() {
            String err = builder().errorCause(null).getAdditionalInfo().toString();
            assertIs(err, "    Error cause: <absent>");
        }

        @Test
        @DisplayName("errorCause without internal Exceptions")
        public void test1647358522851() {
            String err = builder().errorCause(new RuntimeException("foo")).getAdditionalInfo().toString();
            assertIs(err, "    Error cause:\n" +
                          "     - RuntimeException: foo");
        }

        @Test
        @DisplayName("errorCause without internal Exceptions")
        public void test1647358525654() {
            String err = builder().errorCause(new RuntimeException("foo", new RuntimeException("bar")))
                    .getAdditionalInfo().toString();
            assertIs(err, "    Error cause:\n" +
                          "     - RuntimeException: foo\n" +
                          "     - RuntimeException: bar");
        }

    }

    @Nested
    @DisplayName("#getNestedCauses() method tests")
    public class GetNestedCausesMethodTests {

        @Test
        @DisplayName("throwable is null")
        public void test1647358529770() {
            List<Throwable> err = builder().getNestedCauses(null);
            assertThat(err, empty());
        }

        @Test
        @DisplayName("throwable without internal Exceptions")
        public void test1647358532535() {
            final RuntimeException bar = new RuntimeException("bar");
            List<Throwable> err = builder().getNestedCauses(bar);
            assertThat(err, containsInAnyOrder(bar));
        }

        @Test
        @DisplayName("throwable without internal Exceptions")
        public void test1647358534625() {
            final RuntimeException bar = new RuntimeException("bar");
            final RuntimeException foo = new RuntimeException("foo", bar);
            List<Throwable> err = builder().getNestedCauses(foo);
            assertThat(err, containsInAnyOrder(foo, bar));
        }

    }

    @Nested
    @DisplayName("#value() method tests")
    public class ValueMethodTests {

        @Test
        @DisplayName("return function call result if object != null")
        public void test1647373624441() {
            Object o = "foo";
            final String value = builder().value(Object::toString, o);
            assertIs(value, "foo");
        }

        @Test
        @DisplayName("return null if object == null")
        public void test1647373715131() {
            final String value = builder().value(Object::toString, null);
            assertIsNull(value);
        }

    }

    @Nested
    @DisplayName("#constructorArguments() method tests")
    public class ConstructorArgumentsMethodTests {

        @Test
        @DisplayName("Fill additionalInfo if args = [foo, bar]")
        public void test1647373766170() {
            final String result = builder().constructorArguments((Object[]) arrayOf("foo", "bar"))
                    .getAdditionalInfo().toString();
            assertIs(result, "    Constructor arguments:\n     - foo\n     - bar");
        }

        @Test
        @DisplayName("Fill additionalInfo if args = [null, null]")
        public void test1647373934210() {
            final String result = builder().constructorArguments((Object[]) arrayOf(null, (String) null))
                    .getAdditionalInfo().toString();
            assertIs(result, "    Constructor arguments:\n     - null\n     - null");
        }

        @Test
        @DisplayName("Fill additionalInfo if args = []")
        public void test1647374051464() {
            final String result = builder().constructorArguments(arrayOf())
                    .getAdditionalInfo().toString();
            assertIs(result, "    Constructor arguments: <absent>");
        }


        @Test
        @DisplayName("Fill additionalInfo if args = null")
        public void test1647373982715() {
            final String result = builder().constructorArguments((Object[]) null)
                    .getAdditionalInfo().toString();
            assertIs(result, "    Constructor arguments: <absent>");
        }

    }

    private static ExceptionBuilder<RuntimeException> builder() {
        return new ExceptionBuilder<RuntimeException>() {
            @Override
            public RuntimeException build() {
                return new RuntimeException(getMessage(), getCause());
            }
        };
    }

}
