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

import internal.test.utils.RetrofitTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "InstantiatingObjectToGetClassObject", "unchecked", "ConstantConditions", "SameParameterValue"})
@DisplayName("DualCallAdapterFactory tests")
public class DualResponseCallAdapterFactoryUnitTests extends BaseCoreUnitTest {

    private static final DualResponseCallAdapterFactory FACTORY = new DualResponseCallAdapterFactory();
    private static final Retrofit RETROFIT = RetrofitTestUtils.retrofit(FACTORY, new ExtensionConverterFactory());

    @Nested
    @DisplayName("Constructor tests")
    public class ConstructorTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639236659356() {
            assertNPE(() -> new DualResponseCallAdapterFactory(null, DualResponse::new), "logger");
            assertNPE(() -> new DualResponseCallAdapterFactory(UNIT_TEST_LOGGER, null), "dualResponseConsumer");
        }

        @Test
        @DisplayName("No parameters constructor")
        public void test1639236855659() {
            final DualResponseCallAdapterFactory factory = new DualResponseCallAdapterFactory();
            assertThat(factory.getDualResponseConsumer(), notNullValue());
            assertThat(factory.logger, notNullValue());
        }

        @Test
        @DisplayName("IDualResponseConsumer parameter constructor")
        public void test1639237203780() {
            final IDualResponseConsumer<IDualResponse<?, ?>> consumer = UnitTestDualResponse::new;
            final DualResponseCallAdapterFactory factory = new DualResponseCallAdapterFactory(consumer);
            assertThat(factory.getDualResponseConsumer(), is(consumer));
            assertThat(factory.logger, notNullValue());
        }

        @Test
        @DisplayName("Logger parameter constructor")
        public void test1639237254772() {
            final DualResponseCallAdapterFactory factory = new DualResponseCallAdapterFactory(UNIT_TEST_LOGGER);
            assertThat(factory.getDualResponseConsumer(), notNullValue());
            assertThat(factory.logger, is(UNIT_TEST_LOGGER));
        }

        @Test
        @DisplayName("IDualResponseConsumer, Logger parameters constructor")
        public void test1639237301104() {
            final IDualResponseConsumer<IDualResponse<?, ?>> consumer = UnitTestDualResponse::new;
            final DualResponseCallAdapterFactory factory = new DualResponseCallAdapterFactory(UNIT_TEST_LOGGER, consumer);
            assertThat(factory.getDualResponseConsumer(), is(consumer));
            assertThat(factory.logger, is(UNIT_TEST_LOGGER));
        }

    }

    @Nested
    @DisplayName("get() method")
    public class GetMethodTests {

        @Test
        @DisplayName("#get() successful receipt of IDualResponse CallAdapter")
        public void test1639065951487() {
            final CallAdapter<?, IDualResponse<?, ?>> adapter = FACTORY.get(DUAL_RESPONSE_GENERIC_STRING_TYPE, AA, RETROFIT);
            assertThat("", adapter, notNullValue());
            assertThat("", adapter.responseType(), instanceOf(ParameterizedType.class));
            final Call call = RetrofitTestUtils.getCall(200, "");
            final IDualResponse response = adapter.adapt(call);
            assertThat("", response, instanceOf(DualResponse.class));
        }

    }

    @Nested
    @DisplayName("getCallAdapter() method")
    public class GetCallAdapterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639235535510() {
            final ParameterizedType type = FACTORY.getParameterizedType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
            assertNPE(() -> FACTORY.getCallAdapter(null, STRING_C, STRING_C, INFO, AA, RETROFIT), "type");
            assertNPE(() -> FACTORY.getCallAdapter(type, null, STRING_C, INFO, AA, RETROFIT), "successType");
            assertNPE(() -> FACTORY.getCallAdapter(type, STRING_C, null, INFO, AA, RETROFIT), "errorType");
            assertNPE(() -> FACTORY.getCallAdapter(type, STRING_C, STRING_C, null, AA, RETROFIT), "endpointInfo");
            assertNPE(() -> FACTORY.getCallAdapter(type, STRING_C, STRING_C, INFO, null, RETROFIT), "methodAnnotations");
            assertNPE(() -> FACTORY.getCallAdapter(type, STRING_C, STRING_C, INFO, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Get response type")
        public void test1639235392942() {
            final ParameterizedType expected = FACTORY.getParameterizedType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
            final Type actType = FACTORY.getCallAdapter(expected, STRING_C, STRING_C, INFO, AA, RETROFIT).responseType();
            assertThat(actType, is(expected));
        }

        @Test
        @DisplayName("Get default endpoint info")
        public void test1639236352471() {
            final ParameterizedType type = FACTORY.getParameterizedType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
            final Call call = RetrofitTestUtils.getCall(200, "");
            final String info = FACTORY.getCallAdapter(type, STRING_C, STRING_C, "", AA, RETROFIT)
                    .adapt(call)
                    .getEndpointInfo();
            assertThat(info, is("POST http://localhost/"));
        }

        @Test
        @DisplayName("Get specified endpoint info")
        public void test1639236506503() {
            final ParameterizedType type = FACTORY.getParameterizedType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
            final Call call = RetrofitTestUtils.getCall(200, "");
            final String info = FACTORY.getCallAdapter(type, STRING_C, STRING_C, " test1639236506503 ", AA, RETROFIT)
                    .adapt(call)
                    .getEndpointInfo();
            assertThat(info, is("test1639236506503"));
        }


    }

    @Nested
    @DisplayName("getIDualResponse() method")
    public class GetIDualResponseMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639065951517() {
            final Call call = RetrofitTestUtils.getCall(200, "");
            assertNPE(() -> FACTORY.getIDualResponse(null, STRING_C, STRING_C, INFO, AA, RETROFIT), "call");
            assertNPE(() -> FACTORY.getIDualResponse(call, null, STRING_C, INFO, AA, RETROFIT), "successType");
            assertNPE(() -> FACTORY.getIDualResponse(call, STRING_C, null, INFO, AA, RETROFIT), "errorType");
            assertNPE(() -> FACTORY.getIDualResponse(call, STRING_C, STRING_C, null, AA, RETROFIT), "endpointInfo");
            assertNPE(() -> FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, null, RETROFIT), "methodAnnotations");
            assertNPE(() -> FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Get default IDualResponse implementation")
        public void test1639065951499() {
            final Call call = RetrofitTestUtils.getCall(200, "");
            final IDualResponse iDualResponse = FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, RETROFIT);
            assertThat("", iDualResponse, instanceOf(DualResponse.class));
        }

        @Test
        @DisplayName("Get custom IDualResponse implementation")
        public void test1639065951507() {
            final Call call = RetrofitTestUtils.getCall(200, "");
            final IDualResponse iDualResponse = new DualResponseCallAdapterFactory(UnitTestDualResponse::new)
                    .getIDualResponse(call, STRING_C, STRING_C, INFO, AA, RETROFIT);
            assertThat("", iDualResponse, instanceOf(UnitTestDualResponse.class));
        }

        @Test
        @DisplayName("RuntimeException catching")
        public void test1639233145559() throws IOException {
            final Call call = mock(Call.class);
            when(call.execute()).thenThrow(new ConvertCallException("test1639233145559"));
            assertThrow(() -> FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, RETROFIT))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("test1639233145559");
        }

        @Test
        @DisplayName("Exception catching and wrap to RuntimeException")
        public void test1639233270487() throws IOException {
            final Call call = mock(Call.class);
            when(call.execute()).thenThrow(new IOException("test1639233270487"));
            assertThrow(() -> FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, RETROFIT))
                    .assertClass(HttpCallException.class)
                    .assertMessageIs("Failed to make API call. See the reason below.")
                    .assertCause(cause -> cause
                            .assertClass(IOException.class)
                            .assertMessageIs("test1639233270487"));
        }

    }

    @Nested
    @DisplayName("getParameterizedType() method")
    public class GetParameterizedTypeMethodTests {

        @Test
        @DisplayName("Get ParameterizedType from DualResponse class")
        public void test1639065951559() {
            final ParameterizedType expectedType = mock(ParameterizedType.class);
            when(expectedType.getRawType()).thenReturn(DualResponse.class);
            ParameterizedType parameterizedType = FACTORY.getParameterizedType(expectedType);
            assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(DualResponse.class));
            assertThat("ParameterizedType", parameterizedType, is(expectedType));
        }

        @Test
        @DisplayName("Return ParameterizedType from IDualResponse generic class")
        public void test1639065951569() {
            ParameterizedType parameterizedType = FACTORY.getParameterizedType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
            assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(DualResponse.class));
            assertThat("ParameterizedType", parameterizedType, is(DUAL_RESPONSE_GENERIC_STRING_TYPE));
        }

        @Test
        @DisplayName("IllegalArgumentException if type = IDualResponse raw class")
        public void test1639065951578() {
            assertThrow(() -> FACTORY.getParameterizedType(DUAL_RESPONSE_RAW_TYPE))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("API method must return generic type of IDualResponse<SUC_DTO, ERR_DTO>\n" +
                            "Actual: class org.touchbit.retrofit.ext.dmr.client.response.DualResponse");
        }

        @Test
        @DisplayName("IllegalArgumentException if type = unsupported generic class")
        public void test1639065951589() {
            Type unsupported = new HashMap<String, String>().getClass().getGenericInterfaces()[0];
            assertThrow(() -> FACTORY.getParameterizedType(unsupported))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("API method must return generic type of IDualResponse<SUC_DTO, ERR_DTO>\n" +
                            "Actual: java.util.Map<K, V>");
        }

        @Test
        @DisplayName("NPE if type = null")
        public void test1639065951600() {
            assertNPE(() -> FACTORY.getParameterizedType(null), "type");
        }

    }

    @Nested
    @DisplayName("getEndpointInfo() method")
    public class GetEndpointInfoMethodTests {

        @Test
        @DisplayName("Successfully getting a EndpointInfo message from a filled EndpointInfo annotation")
        public void test1639065951664() {
            Annotation[] annotations = new Annotation[]{getEndpointInfo("test1634561408525")};
            String endpointInfo = FACTORY.getEndpointInfo(annotations);
            assertThat("EndpointInfo", endpointInfo, is("test1634561408525"));
        }

        @Test
        @DisplayName("Successfully getting an empty EndpointInfo message from the blank EndpointInfo annotation")
        public void test1639065951672() {
            Annotation[] annotations = new Annotation[]{getEndpointInfo("   ")};
            String endpointInfo = FACTORY.getEndpointInfo(annotations);
            assertThat("EndpointInfo", endpointInfo, emptyString());
        }

        @Test
        @DisplayName("Successfully getting an empty EndpointInfo message from empty EndpointInfo annotation")
        public void test1639065951680() {
            Annotation[] annotations = new Annotation[]{getEndpointInfo(null)};
            String endpointInfo = FACTORY.getEndpointInfo(annotations);
            assertThat("EndpointInfo", endpointInfo, emptyString());
        }

        @Test
        @DisplayName("Successfully getting an empty EndpointInfo message if the EndpointInfo annotation not present")
        public void test1639065951688() {
            Annotation[] annotations = getAnyAnnotations();
            String endpointInfo = FACTORY.getEndpointInfo(annotations);
            assertThat("EndpointInfo", endpointInfo, emptyString());
        }

        @Test
        @DisplayName("Successfully getting an empty EndpointInfo message if the Annotation array is null")
        public void test1639065951700() {
            Annotation[] annotations = new Annotation[]{};
            String endpointInfo = FACTORY.getEndpointInfo(annotations);
            assertThat("EndpointInfo", endpointInfo, emptyString());
        }

    }

}
