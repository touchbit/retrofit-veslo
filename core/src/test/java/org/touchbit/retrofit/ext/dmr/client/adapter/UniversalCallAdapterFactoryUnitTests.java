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

package org.touchbit.retrofit.ext.dmr.client.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import retrofit2.CallAdapter;

import static org.hamcrest.Matchers.*;

@SuppressWarnings("ConstantConditions")
@DisplayName("UniversalCallAdapterFactory.class unit tests")
public class UniversalCallAdapterFactoryUnitTests extends BaseCoreUnitTest {

    @Nested
    @DisplayName("Constructor tests")
    public class UniversalCallAdapterFactoryConstructorTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639245462409() {
            assertNPE(() -> new UniversalCallAdapterFactory(null), "logger");
        }

        @Test
        @DisplayName("Constructor with logger")
        public void test1639245513195() {
            final UniversalCallAdapterFactory factory = new UniversalCallAdapterFactory(UNIT_TEST_LOGGER);
            assertThat(factory.logger, is(UNIT_TEST_LOGGER));
        }

        @Test
        @DisplayName("Default constructor")
        public void test1639245571781() {
            final UniversalCallAdapterFactory factory = new UniversalCallAdapterFactory();
            assertThat(factory.logger, notNullValue());
        }

    }

    @Nested
    @DisplayName("get() method tests")
    public class GetMethodTests {

        @Test
        @DisplayName("Get DualResponseCallAdapterFactory")
        public void test1639245718535() {
            final UniversalCallAdapterFactory factory = new UniversalCallAdapterFactory();
            final CallAdapter<?, ?> callAdapter = factory.get(DUAL_RESPONSE_GENERIC_STRING_TYPE, AA, RTF);
            assertThat(callAdapter.getClass().getTypeName(), containsString(DualResponseCallAdapterFactory.class.getTypeName()));
        }

        @Test
        @DisplayName("Get JavaTypeCallAdapterFactory")
        public void test1639245721197() {
            final UniversalCallAdapterFactory factory = new UniversalCallAdapterFactory();
            final CallAdapter<?, ?> callAdapter = factory.get(STRING_C, AA, RTF);
            assertThat(callAdapter.getClass().getTypeName(), containsString(JavaTypeCallAdapterFactory.class.getTypeName()));
        }

    }

}
