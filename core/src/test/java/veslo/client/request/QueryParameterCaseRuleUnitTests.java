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

package veslo.client.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;

import static org.hamcrest.Matchers.is;
import static veslo.client.request.QueryParameterCaseRule.*;

@DisplayName("QueryParameterCaseRule.class unit tests")
public class QueryParameterCaseRuleUnitTests extends BaseCoreUnitTest {

    private static final String CAMEL_CASE_STR = "queryParameterCaseRuleUnitTests";
    private static final String KEBAB_CASE_STR = "query-parameter-case-rule-unit-tests";
    private static final String SNAKE_CASE_STR = "query_parameter_case_rule_unit_tests";
    private static final String DOT_CASE_STR = "query.parameter.case.rule.unit.tests";
    private static final String PASCAL_CASE_STR = "QueryParameterCaseRuleUnitTests";

    @Nested
    @DisplayName("#toDotCase() method tests")
    public class ToDotCaseMethodTests {

        @Test
        @DisplayName("CAMEL_CASE -> DOT_CASE")
        public void test1640000490797() {
            assertThat(DOT_CASE.format(CAMEL_CASE_STR), is(DOT_CASE_STR));
        }

        @Test
        @DisplayName("KEBAB_CASE -> DOT_CASE")
        public void test1640000646352() {
            assertThat(DOT_CASE.format(KEBAB_CASE_STR), is(DOT_CASE_STR));
        }

        @Test
        @DisplayName("SNAKE_CASE -> DOT_CASE")
        public void test1640000672679() {
            assertThat(DOT_CASE.format(SNAKE_CASE_STR), is(DOT_CASE_STR));
        }

        @Test
        @DisplayName("DOT_CASE -> DOT_CASE")
        public void test1640000674895() {
            assertThat(DOT_CASE.format(DOT_CASE_STR), is(DOT_CASE_STR));
        }

        @Test
        @DisplayName("PASCAL_CASE -> DOT_CASE")
        public void test1640000692553() {
            assertThat(DOT_CASE.format(PASCAL_CASE_STR), is(DOT_CASE_STR));
        }

    }

    @Nested
    @DisplayName("#toSnakeCase() method tests")
    public class ToSnakeCaseMethodTests {

        @Test
        @DisplayName("CAMEL_CASE -> SNAKE_CASE_STR")
        public void test1640001155733() {
            assertThat(SNAKE_CASE.format(CAMEL_CASE_STR), is(SNAKE_CASE_STR));
        }

        @Test
        @DisplayName("KEBAB_CASE -> SNAKE_CASE_STR")
        public void test1640001158117() {
            assertThat(SNAKE_CASE.format(KEBAB_CASE_STR), is(SNAKE_CASE_STR));
        }

        @Test
        @DisplayName("SNAKE_CASE -> SNAKE_CASE_STR")
        public void test1640001161215() {
            assertThat(SNAKE_CASE.format(SNAKE_CASE_STR), is(SNAKE_CASE_STR));
        }

        @Test
        @DisplayName("DOT_CASE -> SNAKE_CASE_STR")
        public void test1640001163947() {
            assertThat(SNAKE_CASE.format(DOT_CASE_STR), is(SNAKE_CASE_STR));
        }

        @Test
        @DisplayName("PASCAL_CASE -> SNAKE_CASE_STR")
        public void test1640001167539() {
            assertThat(SNAKE_CASE.format(PASCAL_CASE_STR), is(SNAKE_CASE_STR));
        }

    }

    @Nested
    @DisplayName("#toKebabCase() method tests")
    public class ToKebabCaseMethodTests {

        @Test
        @DisplayName("CAMEL_CASE -> KEBAB_CASE")
        public void test1640001301764() {
            assertThat(KEBAB_CASE.format(CAMEL_CASE_STR), is(KEBAB_CASE_STR));
        }

        @Test
        @DisplayName("KEBAB_CASE -> KEBAB_CASE")
        public void test1640001303921() {
            assertThat(KEBAB_CASE.format(KEBAB_CASE_STR), is(KEBAB_CASE_STR));
        }

        @Test
        @DisplayName("SNAKE_CASE -> KEBAB_CASE")
        public void test1640001306650() {
            assertThat(KEBAB_CASE.format(SNAKE_CASE_STR), is(KEBAB_CASE_STR));
        }

        @Test
        @DisplayName("DOT_CASE -> KEBAB_CASE")
        public void test1640001309316() {
            assertThat(KEBAB_CASE.format(DOT_CASE_STR), is(KEBAB_CASE_STR));
        }

        @Test
        @DisplayName("PASCAL_CASE -> KEBAB_CASE")
        public void test1640001311459() {
            assertThat(KEBAB_CASE.format(PASCAL_CASE_STR), is(KEBAB_CASE_STR));
        }

    }

    @Nested
    @DisplayName("#toCamelCase() method tests")
    public class ToCamelCaseMethodTests {

        @Test
        @DisplayName("CAMEL_CASE -> CAMEL_CASE")
        public void test1640001394216() {
            assertThat(CAMEL_CASE.format(CAMEL_CASE_STR), is(CAMEL_CASE_STR));
        }

        @Test
        @DisplayName("KEBAB_CASE -> CAMEL_CASE")
        public void test1640001396721() {
            assertThat(CAMEL_CASE.format(KEBAB_CASE_STR), is(CAMEL_CASE_STR));
        }

        @Test
        @DisplayName("SNAKE_CASE -> CAMEL_CASE")
        public void test1640001399133() {
            assertThat(CAMEL_CASE.format(SNAKE_CASE_STR), is(CAMEL_CASE_STR));
        }

        @Test
        @DisplayName("DOT_CASE -> CAMEL_CASE")
        public void test1640001401745() {
            assertThat(CAMEL_CASE.format(DOT_CASE_STR), is(CAMEL_CASE_STR));
        }

        @Test
        @DisplayName("PASCAL_CASE -> CAMEL_CASE")
        public void test1640001404498() {
            assertThat(CAMEL_CASE.format(PASCAL_CASE_STR), is(CAMEL_CASE_STR));
        }

    }

    @Nested
    @DisplayName("#toPascalCase() method tests")
    public class ToPascalCaseMethodTests {

        @Test
        @DisplayName("CAMEL_CASE -> PASCAL_CASE")
        public void test1640001604276() {
            assertThat(PASCAL_CASE.format(CAMEL_CASE_STR), is(PASCAL_CASE_STR));
        }

        @Test
        @DisplayName("KEBAB_CASE -> PASCAL_CASE")
        public void test1640001606989() {
            assertThat(PASCAL_CASE.format(KEBAB_CASE_STR), is(PASCAL_CASE_STR));
        }

        @Test
        @DisplayName("SNAKE_CASE -> PASCAL_CASE")
        public void test1640001609502() {
            assertThat(PASCAL_CASE.format(SNAKE_CASE_STR), is(PASCAL_CASE_STR));
        }

        @Test
        @DisplayName("DOT_CASE -> PASCAL_CASE")
        public void test1640001611768() {
            assertThat(PASCAL_CASE.format(DOT_CASE_STR), is(PASCAL_CASE_STR));
        }

        @Test
        @DisplayName("PASCAL_CASE -> PASCAL_CASE")
        public void test1640001614239() {
            assertThat(PASCAL_CASE.format(PASCAL_CASE_STR), is(PASCAL_CASE_STR));
        }

    }

}
