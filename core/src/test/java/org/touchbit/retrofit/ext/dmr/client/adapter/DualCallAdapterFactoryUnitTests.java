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

import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import retrofit2.Call;
import retrofit2.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "InstantiatingObjectToGetClassObject", "unchecked"})
@DisplayName("DualCallAdapterFactory tests")
public class DualCallAdapterFactoryUnitTests {

    private static final DualCallAdapterFactory ADAPTER_FACTORY = new DualCallAdapterFactory();
    private static final String TYPE_ERR_MSG = "" +
            "API methods must return an implementation class " +
            "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse";

    @Test
    @DisplayName("Successfully getting ParameterizedType from DualResponse class")
    public void test1634556848627() {
        final ParameterizedType mock = mock(ParameterizedType.class);
        when(mock.getRawType()).thenReturn(DualResponse.class);
        ParameterizedType parameterizedType = ADAPTER_FACTORY.getParameterizedType(mock);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(DualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(mock));
    }

    @Test
    @DisplayName("Successfully getting ParameterizedType from IDualResponse generic class")
    public void test1634556848628() {
        Type drType = getGenericIDualResponse().getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType = ADAPTER_FACTORY.getParameterizedType(drType);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(IDualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(drType));
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from IDualResponse raw class")
    public void test1634558941130() {
        Type drType = getRawIDualResponse().getClass().getGenericInterfaces()[0];
        assertThrow(() -> ADAPTER_FACTORY.getParameterizedType(drType), IllegalArgumentException.class, TYPE_ERR_MSG);
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from unsupported generic class")
    public void test1634560579101() {
        Type unsupported = new HashMap<String, String>().getClass().getGenericInterfaces()[0];
        assertThrow(() -> ADAPTER_FACTORY.getParameterizedType(unsupported), IllegalArgumentException.class, TYPE_ERR_MSG);
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from 'null' class")
    public void test1634560867667() {
        assertThrow(() -> ADAPTER_FACTORY.getParameterizedType(null), IllegalArgumentException.class, TYPE_ERR_MSG);
    }

    @Test
    @DisplayName("Successfully getting a EndpointInfo message from a filled EndpointInfo annotation")
    public void test1634561408525() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("test1634561408525")};
        String endpointInfo = ADAPTER_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, is("test1634561408525"));
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from the blank EndpointInfo annotation")
    public void test1634561408526() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("   ")};
        String endpointInfo = ADAPTER_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from empty EndpointInfo annotation")
    public void test1634561408527() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo(null)};
        String endpointInfo = ADAPTER_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the EndpointInfo annotation not present")
    public void test1634561408528() {
        Annotation[] annotations = new Annotation[]{};
        String endpointInfo = ADAPTER_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the Annotation array is null")
    public void test1634561408529() {
        Annotation[] annotations = new Annotation[]{};
        String endpointInfo = ADAPTER_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting DualResponse")
    public void test1634562687273() throws Exception {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        okhttp3.Response rawResponse = mock(okhttp3.Response.class);
        Call call = mock(Call.class);
        when(call.request()).thenReturn(request);
        when(call.execute()).thenReturn(response);
        when(call.execute().raw()).thenReturn(rawResponse);
        IDualResponse actDR = ADAPTER_FACTORY.getIDualResponse(call, Object.class, Object.class, "test1634562687273", null, null);
        assertThat("IDualResponse", actDR, notNullValue());
    }

    @Test
    @DisplayName("Exception: Failed to make API call.")
    public void test1635885162042() throws Exception {
        Request request = mock(Request.class);
        Call call = mock(Call.class);
        when(call.request()).thenReturn(request);
        when(call.execute()).thenThrow(new RuntimeException("test1635885162042"));
        assertThrow(() -> ADAPTER_FACTORY.getIDualResponse(call, Object.class, Object.class, "test1634562687273", null, null))
                .assertThrowMessageIs("Failed to make API call.")
                .assertThrowClassIs(HttpCallException.class)
                .assertCause(cause1 -> cause1
                        .assertThrowMessageIs("test1635885162042")
                        .assertThrowClassIs(RuntimeException.class));
    }

    @Test
    @DisplayName("Exception: Failed to convert error body.")
    public void test1635885324572() throws Exception {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        when(response.code()).thenReturn(500);
        when(response.isSuccessful()).thenReturn(false);
        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.errorBody()).thenReturn(responseBody);
        okhttp3.Response rawResponse = mock(okhttp3.Response.class);
        Call call = mock(Call.class);
        when(call.request()).thenReturn(request);
        when(call.execute()).thenReturn(response);
        when(call.execute().raw()).thenReturn(rawResponse);

        Runnable runnable = () -> ADAPTER_FACTORY.getIDualResponse(call, Object.class, Object.class, "test1634562687273", null, null);
        assertThrow(runnable, HttpCallException.class, "Failed to convert error body.");
    }


    private EndpointInfo getEndpointInfo(final String message) {
        return new EndpointInfo() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return EndpointInfo.class;
            }

            @Override
            public String value() {
                return message;
            }
        };
    }

    private IDualResponse getGenericIDualResponse() {
        return new IDualResponse<Object, Object>() {

            @Override
            public Request getRawRequest() {
                return null;
            }

            @Override
            public String getEndpointInfo() {
                return null;
            }

            @Override
            public Annotation[] getCallAnnotations() {
                return new Annotation[0];
            }

            @Override
            public Response getResponse() {
                return null;
            }

            @Override
            public Object getErrorDTO() {
                return null;
            }

        };
    }

    private IDualResponse getRawIDualResponse() {
        return new IDualResponse() {

            @Override
            public Request getRawRequest() {
                return null;
            }

            @Override
            public String getEndpointInfo() {
                return null;
            }

            @Override
            public Annotation[] getCallAnnotations() {
                return new Annotation[0];
            }

            @Override
            public Response getResponse() {
                return null;
            }

            @Override
            public Object getErrorDTO() {
                return null;
            }
        };
    }

}
