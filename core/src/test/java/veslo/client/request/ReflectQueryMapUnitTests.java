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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static veslo.client.request.QueryParameterCaseRule.CAMEL_CASE;
import static veslo.client.request.QueryParameterCaseRule.SNAKE_CASE;
import static veslo.client.request.QueryParameterNullValueRule.*;

@SuppressWarnings({"SameParameterValue", "MismatchedQueryAndUpdateOfCollection", "unused"})
@DisplayName("ReflectQueryMap.class unit tests")
public class ReflectQueryMapUnitTests extends BaseCoreUnitTest {

    public static final String NULL_MARKER = new String(new byte[]{0});

    @Nested
    @DisplayName("#entrySet() method tests")
    public class EntrySetMethodTests {

        @Test
        @DisplayName("Get values from QueryMap without annotations (filled)")
        public void test1640008269346() {
            DefaultQueryMap queryMap = new DefaultQueryMap();
            queryMap.firstName = "firstNameValue";
            queryMap.lastName = "lastNameValue";
            final Map<String, Object> result = queryMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            assertThat(result, aMapWithSize(2));
            assertThat(result.get("firstName"), is("firstNameValue"));
            assertThat(result.get("lastName"), is("lastNameValue"));
        }

        @Test
        @DisplayName("Get values from QueryMap without annotations (null values)")
        public void test1640008763618() {
            final Map<String, Object> result = new DefaultQueryMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            assertThat(result, aMapWithSize(0));
        }

        @Test
        @DisplayName("Get values from QueryMap with annotations (filled)")
        public void test1640009129151() {
            NullMarkerSnakeCaseQueryMap queryMap = new NullMarkerSnakeCaseQueryMap();
            queryMap.ruleEmptyStringField = "ruleEmptyStringFieldValue";
            queryMap.camelCaseParameterName = "camelCaseParameterNameValue";
            final Map<String, Object> result = queryMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            assertThat(result, aMapWithSize(2));
            assertThat(result.get("rule_empty_string_field"), is("ruleEmptyStringFieldValue"));
            assertThat(result.get("lastName"), is("camelCaseParameterNameValue"));
        }

        @Test
        @DisplayName("Get values from QueryMap with annotations (null values)")
        public void test1640009137860() {
            final Map<String, Object> result = new NullMarkerSnakeCaseQueryMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            assertThat(result, aMapWithSize(2));
            assertThat(result.get("rule_empty_string_field"), is(""));
            assertThat(result.get("lastName"), is(NULL_MARKER));
        }

    }

    @Nested
    @DisplayName("#getParameterName() method tests")
    public class GetParameterNameMethodTests {

        @Test
        @DisplayName("return name without formatting")
        public void test1640007651161() {
            final String result = new DefaultQueryMap().getParameterName(null, null, "fooBar");
            assertThat(result, is("fooBar"));
        }

        @Test
        @DisplayName("return snake_case parameter name by QueryMapParameterRules annotation rule")
        public void test1640007925739() {
            final QueryMapParameterRules classRule = getQueryMapParameterRules(SNAKE_CASE);
            final String result = new DefaultQueryMap().getParameterName(null, classRule, "fooBar");
            assertThat(result, is("foo_bar"));
        }

        @Test
        @DisplayName("return name without formatting if QueryMapParameter.name is blank")
        public void test1640008193177() {
            final QueryMapParameter fieldRule = getQueryMapParameter("        ");
            final String result = new DefaultQueryMap().getParameterName(fieldRule, null, "test");
            assertThat(result, is("test"));
        }

        @Test
        @DisplayName("return snake_case parameter name by QueryMapParameter annotation name")
        public void test1640008023609() {
            final QueryMapParameter fieldRule = getQueryMapParameter("foo_bar");
            final String result = new DefaultQueryMap().getParameterName(fieldRule, null, "test");
            assertThat(result, is("foo_bar"));
        }

    }

    @Nested
    @DisplayName("#getParameterValue() method tests")
    public class GetParameterValueMethodTests {

        @Test
        @DisplayName("return incoming object if value = random sting")
        public void test1640003255155() {
            final String uuid = UUID.randomUUID().toString();
            final Object result = new DefaultQueryMap().getParameterValue(null, null, uuid);
            assertThat(result, is(uuid));
        }

        @Test
        @DisplayName("return null if queryMapParameter.rule = NONE")
        public void test1640003367514() {
            final QueryMapParameter queryMapParameter = getQueryMapParameter(NONE);
            final Object result = new DefaultQueryMap().getParameterValue(queryMapParameter, null, null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if queryMapParameter.rule = RULE_IGNORE")
        public void test1640003424275() {
            final QueryMapParameter queryMapParameter = getQueryMapParameter(RULE_IGNORE);
            final Object result = new DefaultQueryMap().getParameterValue(queryMapParameter, null, null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return NULL_MARKER if queryMapParameter.rule = RULE_NULL_MARKER")
        public void test1640003713235() {
            final QueryMapParameter queryMapParameter = getQueryMapParameter(RULE_NULL_MARKER);
            final Object result = new DefaultQueryMap().getParameterValue(queryMapParameter, null, null);
            assertThat(result, is(NULL_MARKER));
        }

        @Test
        @DisplayName("return '' if queryMapParameter.rule = RULE_EMPTY_STRING")
        public void test1640003893102() {
            final QueryMapParameter queryMapParameter = getQueryMapParameter(RULE_EMPTY_STRING);
            final Object result = new DefaultQueryMap().getParameterValue(queryMapParameter, null, null);
            assertThat(result, is(""));
        }

        @Test
        @DisplayName("return 'null' if queryMapParameter.rule = RULE_NULL_STRING")
        public void test1640003923322() {
            final QueryMapParameter queryMapParameter = getQueryMapParameter(RULE_NULL_STRING);
            final Object result = new DefaultQueryMap().getParameterValue(queryMapParameter, null, null);
            assertThat(result, is("null"));
        }

        @Test
        @DisplayName("return null if value = NONE")
        public void test1640004028266() {
            final Object result = new DefaultQueryMap().getParameterValue(null, null, "NONE");
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if value = RULE_IGNORE")
        public void test1640004042993() {
            final Object result = new DefaultQueryMap().getParameterValue(null, null, "RULE_IGNORE");
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return NULL_MARKER if value = RULE_NULL_MARKER")
        public void test1640004045407() {
            final Object result = new DefaultQueryMap().getParameterValue(null, null, "RULE_NULL_MARKER");
            assertThat(result, is(NULL_MARKER));
        }

        @Test
        @DisplayName("return '' if value = RULE_EMPTY_STRING")
        public void test1640004047792() {
            final Object result = new DefaultQueryMap().getParameterValue(null, null, "RULE_EMPTY_STRING");
            assertThat(result, is(""));
        }

        @Test
        @DisplayName("return 'null' if value = RULE_NULL_STRING")
        public void test1640004050715() {
            final Object result = new DefaultQueryMap().getParameterValue(null, null, "RULE_NULL_STRING");
            assertThat(result, is("null"));
        }

        @Test
        @DisplayName("return NULL_MARKER if QueryMapParameterRules.nullRule = RULE_NULL_MARKER")
        public void test1640004242635() {
            final QueryMapParameterRules rules = getQueryMapParameterRules(RULE_NULL_MARKER);
            final Object result = new DefaultQueryMap().getParameterValue(null, rules, null);
            assertThat(result, is(NULL_MARKER));
        }

    }

    private static final class DefaultQueryMap extends ReflectQueryMap {

        private Object firstName;
        private Object lastName;

    }

    @QueryMapParameterRules(nullRule = RULE_NULL_MARKER, caseRule = SNAKE_CASE)
    private static final class NullMarkerSnakeCaseQueryMap extends ReflectQueryMap {

        @QueryMapParameter(nullRule = RULE_EMPTY_STRING)
        private Object ruleEmptyStringField;
        @QueryMapParameter(name = "lastName")
        private Object camelCaseParameterName;

    }

    private static QueryMapParameterRules getQueryMapParameterRules(QueryParameterCaseRule caseRule) {
        return getQueryMapParameterRules(null, caseRule);
    }

    private static QueryMapParameterRules getQueryMapParameterRules(QueryParameterNullValueRule nullRule) {
        return getQueryMapParameterRules(nullRule, null);
    }

    private static QueryMapParameterRules getQueryMapParameterRules(QueryParameterNullValueRule nullRule,
                                                                    QueryParameterCaseRule caseRule) {
        return new QueryMapParameterRules() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return QueryMapParameterRules.class;
            }

            @Override
            public QueryParameterNullValueRule nullRule() {
                return nullRule != null ? nullRule : RULE_IGNORE;
            }

            @Override
            public QueryParameterCaseRule caseRule() {
                return caseRule != null ? caseRule : CAMEL_CASE;
            }

        };
    }

    private static QueryMapParameter getQueryMapParameter(String value) {
        return getQueryMapParameter(value, null);
    }

    private static QueryMapParameter getQueryMapParameter(QueryParameterNullValueRule rule) {
        return getQueryMapParameter(null, rule);
    }

    private static QueryMapParameter getQueryMapParameter(String value, QueryParameterNullValueRule rule) {
        return new QueryMapParameter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return QueryMapParameter.class;
            }

            @Override
            public String name() {
                return value != null ? value : "";
            }

            @Override
            public QueryParameterNullValueRule nullRule() {
                return rule != null ? rule : NONE;
            }
        };
    }

}
