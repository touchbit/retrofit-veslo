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

package veslo.client.adapter;

import internal.test.utils.RetrofitTestUtils;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import veslo.BaseCoreUnitTest;
import veslo.ConvertCallException;
import veslo.HttpCallException;
import veslo.PrimitiveConvertCallException;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import veslo.client.converter.defaults.JavaReferenceTypeConverter;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "ConstantConditions", "rawtypes"})
@DisplayName("JavaTypeCallAdapterFactory.class unit tests")
public class JavaTypeCallAdapterFactoryUnitTests extends BaseCoreUnitTest {

    private static final JavaReferenceTypeConverter CONVERTER = JavaReferenceTypeConverter.INSTANCE;
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

    @Nested
    @DisplayName("getSuccessfulResponseBody() method")
    public class GetSuccessfulResponseBodyMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639173061390() {
            final Response<?> response = mock(Response.class);
            assertNPE(() -> FACTORY.getSuccessfulResponseBody(null, OBJ_T, AA, RTF), "response");
            assertNPE(() -> FACTORY.getSuccessfulResponseBody(response, null, AA, RTF), "returnType");
            assertNPE(() -> FACTORY.getSuccessfulResponseBody(response, OBJ_T, null, RTF), "annotations");
            assertNPE(() -> FACTORY.getSuccessfulResponseBody(response, OBJ_T, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return null if http status code = 204")
        public void test1639173308153() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(204);
            when(response.body()).thenReturn("");
            final Retrofit retrofit = mock(Retrofit.class);
            final ResponseBodyConverter converter = CONVERTER.responseBodyConverter(STRING_T, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_T, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, STRING_T, AA, retrofit);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return null if http status code = 205")
        public void test1639173770792() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(205);
            when(response.body()).thenReturn("");
            final Retrofit retrofit = mock(Retrofit.class);
            final ResponseBodyConverter converter = CONVERTER.responseBodyConverter(STRING_C, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_C, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, STRING_C, AA, retrofit);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return null if Response body = null")
        public void test1639173853475() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(200);
            when(response.body()).thenReturn(null);
            final Retrofit retrofit = mock(Retrofit.class);
            final ResponseBodyConverter converter = CONVERTER.responseBodyConverter(STRING_C, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_C, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, STRING_C, AA, retrofit);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return body if Response body present and status != 204 or 205")
        public void test1639173916792() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(200);
            when(response.body()).thenReturn("test1639173916792");
            final Retrofit retrofit = mock(Retrofit.class);
            final ResponseBodyConverter converter = CONVERTER.responseBodyConverter(STRING_C, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_C, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, STRING_C, AA, retrofit);
            assertThat(result, is("test1639173916792"));
        }

        @Test
        @DisplayName("Return null if Response returnType = Void")
        public void test1647374543997() {
            final Object result = FACTORY.getSuccessfulResponseBody(mock(Response.class), Void.class, AA, RTF);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return null if Response returnType = void")
        public void test1647374557732() {
            final Object result = FACTORY.getSuccessfulResponseBody(mock(Response.class), Void.TYPE, AA, RTF);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException if Response body not convertable and response.isSuccessful() = true")
        public void test1639174338437() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(204);
            when(response.body()).thenReturn("test1639173916792");
            when(response.isSuccessful()).thenReturn(true);
            final Retrofit retrofit = mock(Retrofit.class);
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenThrow(new ConvertCallException("test1639174338437"));
            assertThrow(() -> FACTORY.getSuccessfulResponseBody(response, OBJ_T, AA, retrofit))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("test1639174338437");
        }

        @Test
        @DisplayName("Return null if ConvertCallException throws and response.isSuccessful() = false")
        public void test1639174708852() {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(204);
            when(response.body()).thenReturn("test1639174708852");
            when(response.isSuccessful()).thenReturn(false);
            final Retrofit retrofit = mock(Retrofit.class);
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenThrow(new ConvertCallException("test1639174708852"));
            final Object result = FACTORY.getSuccessfulResponseBody(response, OBJ_T, AA, retrofit);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Wrapped IOException if Response body not convertable and response.isSuccessful() = true")
        public void test1639174770524() throws IOException {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(204);
            when(response.body()).thenReturn("test1639174770524");
            when(response.isSuccessful()).thenReturn(true);
            final Retrofit retrofit = mock(Retrofit.class);
            final Converter converter = mock(Converter.class);
            when(converter.convert(null)).thenThrow(new IOException("test1639174770524"));
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenReturn(converter);
            assertThrow(() -> FACTORY.getSuccessfulResponseBody(response, OBJ_T, AA, retrofit))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Error converting response body to type java.lang.Object")
                    .assertCause(cause -> cause
                            .assertClass(IOException.class)
                            .assertMessageIs("test1639174770524"));
        }

        @Test
        @DisplayName("Return null if IOException throws and response.isSuccessful() = false")
        public void test1639174998385() throws IOException {
            final Response<Object> response = mock(Response.class);
            when(response.code()).thenReturn(204);
            when(response.body()).thenReturn("test1639174708852");
            when(response.isSuccessful()).thenReturn(false);
            final Retrofit retrofit = mock(Retrofit.class);
            final Converter converter = mock(Converter.class);
            when(converter.convert(null)).thenThrow(new IOException("test1639174770524"));
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, OBJ_T, AA, retrofit);
            assertThat(result, nullValue());
        }

    }

    @Nested
    @DisplayName("getErrorResponseBody() method")
    public class GetErrorResponseBodyMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1640099550413() {
            final Response<?> response = mock(Response.class);
            assertNPE(() -> FACTORY.getErrorResponseBody(null, OBJ_T, AA, RTF), "response");
            assertNPE(() -> FACTORY.getErrorResponseBody(response, null, AA, RTF), "returnType");
            assertNPE(() -> FACTORY.getErrorResponseBody(response, OBJ_T, null, RTF), "annotations");
            assertNPE(() -> FACTORY.getErrorResponseBody(response, OBJ_T, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return body if Response.errorBody() present")
        public void test1639176585577() {
            final Response<Object> response = mock(Response.class);
            final ResponseBody responseBody = ResponseBody.create(null, "test1639176585577");
            when(response.errorBody()).thenReturn(responseBody);
            when(response.isSuccessful()).thenReturn(false);
            final Retrofit retrofit = mock(Retrofit.class);
            final ResponseBodyConverter converter = CONVERTER.responseBodyConverter(STRING_C, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_C, AA)).thenReturn(converter);
            final Object result = FACTORY.getErrorResponseBody(response, STRING_C, AA, retrofit);
            assertThat(result, is("test1639176585577"));
        }

        @Test
        @DisplayName("ConvertCallException if Response body not convertable and response.isSuccessful() = false")
        public void test1639176727307() {
            final ResponseBody responseBody = ResponseBody.create(null, "test1639176727307");
            final Response<Object> response = mock(Response.class);
            when(response.errorBody()).thenReturn(responseBody);
            when(response.isSuccessful()).thenReturn(false);
            final Retrofit retrofit = mock(Retrofit.class);
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenThrow(new ConvertCallException("test1639176727307"));
            assertThrow(() -> FACTORY.getErrorResponseBody(response, OBJ_T, AA, retrofit))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("test1639176727307");
        }

        @Test
        @DisplayName("Return null if ConvertCallException throws and response.isSuccessful() = true")
        public void test1639176754352() {
            final ResponseBody responseBody = ResponseBody.create(null, "test1639176754352");
            final Response<Object> response = mock(Response.class);
            when(response.errorBody()).thenReturn(responseBody);
            when(response.isSuccessful()).thenReturn(true);
            final Retrofit retrofit = mock(Retrofit.class);
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenThrow(new ConvertCallException("test1639176754352"));
            final Object result = FACTORY.getErrorResponseBody(response, OBJ_T, AA, retrofit);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return null if returnType = Void")
        public void test1647374677950() {
            final Object result = FACTORY.getErrorResponseBody(mock(Response.class), Void.class, AA, RTF);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Return null if returnType = void")
        public void test1647374681450() {
            final Object result = FACTORY.getErrorResponseBody(mock(Response.class), Void.TYPE, AA, RTF);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("Wrapped IOException if Response body not convertable and response.isSuccessful() = false")
        public void test1639176773772() throws IOException {
            final Response<Object> response = mock(Response.class);
            when(response.isSuccessful()).thenReturn(false);
            final Retrofit retrofit = mock(Retrofit.class);
            final Converter converter = mock(Converter.class);
            when(converter.convert(null)).thenThrow(new IOException("test1639176773772"));
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenReturn(converter);
            assertThrow(() -> FACTORY.getErrorResponseBody(response, OBJ_T, AA, retrofit))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Error converting response body to type java.lang.Object")
                    .assertCause(cause -> cause
                            .assertClass(IOException.class)
                            .assertMessageIs("test1639176773772"));
        }

        @Test
        @DisplayName("Return null if IOException throws and response.isSuccessful() = false")
        public void test1639176800976() throws IOException {
            final Response<Object> response = mock(Response.class);
            when(response.isSuccessful()).thenReturn(true);
            final Retrofit retrofit = mock(Retrofit.class);
            final Converter converter = mock(Converter.class);
            when(converter.convert(null)).thenThrow(new IOException("test1639176773772"));
            when(retrofit.responseBodyConverter(OBJ_T, AA)).thenReturn(converter);
            final Object result = FACTORY.getErrorResponseBody(response, OBJ_T, AA, retrofit);
            assertThat(result, nullValue());
        }

    }

    @Nested
    @DisplayName("checkPrimitiveConvertCall() method")
    public class CheckPrimitiveConvertCallMethodTests {

        @Test
        @DisplayName("There is no exception if returnType = null")
        public void test1639177071217() {
            FACTORY.checkPrimitiveConvertCall(null, "test1639177071217");
        }

        @Test
        @DisplayName("There is no exception if returnType is ParameterizedType")
        public void test1639177232170() {
            FACTORY.checkPrimitiveConvertCall(STRING_LIST_T, "test1639177071217");
        }

        @Test
        @DisplayName("There is no exception if body = null")
        public void test1639177148130() {
            FACTORY.checkPrimitiveConvertCall(Object.class, null);
        }

        @Test
        @DisplayName("There is no exception if type is primitive and body != null")
        public void test1639177296341() {
            FACTORY.checkPrimitiveConvertCall(Boolean.TYPE, "test1639177296341");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException occurred if type is primitive and body == null")
        public void test1639177352974() {
            assertThrow(() -> FACTORY.checkPrimitiveConvertCall(Boolean.TYPE, null))
                    .assertClass(PrimitiveConvertCallException.class);
        }

    }

}
