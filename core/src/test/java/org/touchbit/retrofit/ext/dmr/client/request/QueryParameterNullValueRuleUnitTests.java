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

package org.touchbit.retrofit.ext.dmr.client.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.touchbit.retrofit.ext.dmr.client.request.QueryParameterNullValueRule.RULE_EMPTY_STRING;

@DisplayName("QueryParameterNullValueRule.class tests")
public class QueryParameterNullValueRuleUnitTests extends BaseCoreUnitTest {

    @Nested
    @DisplayName("#valueOf() method tests")
    public class ValueOfMethodTests {

        @Test
        @DisplayName("return rule by name")
        public void test1639994598780() {
            for (QueryParameterNullValueRule rule : QueryParameterNullValueRule.values()) {
                Object value = rule.name();
                assertThat(QueryParameterNullValueRule.valueOf(value), is(rule));
            }
        }

        @Test
        @DisplayName("return null if value == null")
        public void test1639995783850() {
            Object value = null;
            assertThat(QueryParameterNullValueRule.valueOf(value), nullValue());
        }

        @Test
        @DisplayName("return null if value == rule_empty_string (lower case)")
        public void test1639995865850() {
            Object value = RULE_EMPTY_STRING.name().toLowerCase();
            assertThat(QueryParameterNullValueRule.valueOf(value), nullValue());
        }

    }

}
