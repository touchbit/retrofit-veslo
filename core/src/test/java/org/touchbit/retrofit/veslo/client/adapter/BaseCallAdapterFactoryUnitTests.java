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

import internal.test.utils.CorruptedTestException;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import org.touchbit.retrofit.veslo.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.veslo.client.converter.defaults.JavaReferenceTypeConverter;
import org.touchbit.retrofit.veslo.exception.ConvertCallException;
import org.touchbit.retrofit.veslo.exception.PrimitiveConvertCallException;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"NullableProblems", "ConstantConditions", "unchecked", "rawtypes"})
@DisplayName("BaseCallAdapterFactory.class unit tests")
public class BaseCallAdapterFactoryUnitTests extends BaseCoreUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCallAdapterFactoryUnitTests.class);
    private static final BaseCallAdapterFactory FACTORY = new TestBaseCallAdapterFactory(LOGGER);

    @Test
    @DisplayName("BaseCallAdapterFactory constructor")
    public void test1639172842464() {
        assertNPE(() -> new TestBaseCallAdapterFactory(null), "logger");
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
            final ResponseBodyConverter converter = JavaReferenceTypeConverter.INSTANCE.responseBodyConverter(STRING_T, AA, retrofit);
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
            final ResponseBodyConverter converter = JavaReferenceTypeConverter.INSTANCE.responseBodyConverter(STRING_C, AA, retrofit);
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
            final ResponseBodyConverter converter = JavaReferenceTypeConverter.INSTANCE.responseBodyConverter(STRING_C, AA, retrofit);
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
            final ResponseBodyConverter converter = JavaReferenceTypeConverter.INSTANCE.responseBodyConverter(STRING_C, AA, retrofit);
            when(retrofit.responseBodyConverter(STRING_C, AA)).thenReturn(converter);
            final Object result = FACTORY.getSuccessfulResponseBody(response, STRING_C, AA, retrofit);
            assertThat(result, is("test1639173916792"));
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
            final ResponseBodyConverter converter = JavaReferenceTypeConverter.INSTANCE.responseBodyConverter(STRING_C, AA, retrofit);
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

    public static class TestBaseCallAdapterFactory extends BaseCallAdapterFactory {

        protected TestBaseCallAdapterFactory(Logger logger) {
            super(logger);
        }

        @Nullable
        @Override
        public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            throw new CorruptedTestException();
        }

    }

}
