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

package org.touchbit.retrofit.veslo.client.adapter;

import internal.test.utils.RetrofitTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import org.touchbit.retrofit.veslo.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.veslo.exception.HttpCallException;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@DisplayName("JavaTypeCallAdapterFactory.class unit tests")
public class JavaTypeCallAdapterFactoryUnitTests extends BaseCoreUnitTest {

    private static final JavaTypeCallAdapterFactory FACTORY = new JavaTypeCallAdapterFactory();
    private static final Retrofit RETROFIT = RetrofitTestUtils.retrofit(FACTORY, new ExtensionConverterFactory());


    @Nested
    @DisplayName("Constructor Tests")
    public class JavaTypeCallAdapterFactoryConstructorTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639239683506() {
            assertNPE(() -> new JavaTypeCallAdapterFactory(null), "logger");
        }

        @Test
        @DisplayName("Logger parameter constructor")
        public void test1639240929790() {
            final JavaTypeCallAdapterFactory factory = new JavaTypeCallAdapterFactory(UNIT_TEST_LOGGER);
            assertThat(factory.logger, is(UNIT_TEST_LOGGER));
        }

    }

    @Nested
    @DisplayName("get() method tests")
    public class GetMethodTests {

        @Test
        @DisplayName("Get CallAdapter")
        public void test1639240905064() {
            assertThat(FACTORY.get(STRING_C, AA, RETROFIT), notNullValue());
        }

        @Nested
        @DisplayName("adapt() method tests")
        public class AdaptMethodTests {

            @Test
            @DisplayName("return successful DTO")
            public void test1639240907548() {
                final Call<Object> call = RetrofitTestUtils.getCall(200, "test1639240907548");
                final Object result = FACTORY.get(STRING_C, AA, RETROFIT).adapt(call);
                assertThat(result, is("test1639240907548"));
            }

            @Test
            @DisplayName("return null if successful body is empty")
            public void test1639241352095() {
                final Call<Object> call = RetrofitTestUtils.getCall(200, null);
                final Object result = FACTORY.get(STRING_C, AA, RETROFIT).adapt(call);
                assertThat(result, nullValue());
            }

            @Test
            @DisplayName("return error DTO")
            public void test1639241063504() {
                final Call<Object> call = RetrofitTestUtils.getCall(500, "test1639241063504");
                final Object result = FACTORY.get(STRING_C, AA, RETROFIT).adapt(call);
                assertThat(result, is("test1639241063504"));
            }

            @Test
            @DisplayName("return null if error body is empty")
            public void test1639241555028() {
                final Call<Object> call = RetrofitTestUtils.getCall(500, null);
                final Object result = FACTORY.get(STRING_C, AA, RETROFIT).adapt(call);
                assertThat(result, nullValue());
            }

            @Test
            @DisplayName("Wrap IOException to RuntimeException")
            public void test1639241727303() throws IOException {
                final Call<Object> call = RetrofitTestUtils.getCall(500, null);
                when(call.execute()).thenThrow(new IOException("test1639241727303"));
                assertThrow(() -> FACTORY.get(STRING_C, AA, RETROFIT).adapt(call))
                        .assertClass(HttpCallException.class)
                        .assertMessageIs("Failed to make API call.\ntest1639241727303\n")
                        .assertCause(cause -> cause
                                .assertClass(IOException.class)
                                .assertMessageIs("test1639241727303"));
            }

        }

        @Nested
        @DisplayName("responseType() method test")
        public class ResponseTypeMethodTest {

            @Test
            @DisplayName("Get Return type without modification")
            public void test1639241912634() {
                assertThat(FACTORY.get(STRING_C, AA, RETROFIT).responseType(), is(STRING_C));
                assertThat(FACTORY.get(DUAL_RESPONSE_GENERIC_STRING_TYPE, AA, RETROFIT).responseType(), is(DUAL_RESPONSE_GENERIC_STRING_TYPE));
                assertThat(FACTORY.get(DUAL_RESPONSE_RAW_TYPE, AA, RETROFIT).responseType(), is(DUAL_RESPONSE_RAW_TYPE));
                assertThat(FACTORY.get(PRIMITIVE_BOOLEAN_T, AA, RETROFIT).responseType(), is(PRIMITIVE_BOOLEAN_T));
            }

        }

    }

}
