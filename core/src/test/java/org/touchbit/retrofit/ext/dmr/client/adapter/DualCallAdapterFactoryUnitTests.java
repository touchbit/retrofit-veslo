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

import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.touchbit.retrofit.ext.dmr.asserter.HeadersAsserter.H_CONTENT_TYPE;
import static org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter.softlyAsserter;

@SuppressWarnings({"rawtypes", "InstantiatingObjectToGetClassObject", "unchecked", "ConstantConditions"})
@DisplayName("DualCallAdapterFactory tests")
public class DualCallAdapterFactoryUnitTests {

    private static final DualCallAdapterFactory DEFAULT_FACTORY = new DualCallAdapterFactory();
    private static final Class<?> OBJ_C = Object.class;
    private static final Class<?> STR_C = String.class;
    private static final Class<?> AB_C = AnyBody.class;
    private static final String INFO = "endpointInfo";
    private static final Retrofit R = new Retrofit.Builder()
            .addCallAdapterFactory(DEFAULT_FACTORY)
            .addConverterFactory(new ExtensionConverterFactory())
            .baseUrl("http://localhost")
            .build();
    private static final Annotation[] AA = new Annotation[]{};
    private static final String TYPE_ERR_MSG = "" +
            "API methods must return an implementation class " +
            "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse";

    @Test
    @DisplayName("#get() successful receipt of IDualResponse CallAdapter")
    public void test1637409355402() {
        final Type type = new DualResponse<>(null, null, null, null, null).getClass().getGenericSuperclass();
        final CallAdapter<Object, IDualResponse<?, ?>> adapter = DEFAULT_FACTORY.get(type, AA, R);
        assertThat("", adapter, notNullValue());
        assertThat("", adapter.responseType(), instanceOf(ParameterizedType.class));
        final Call call = getCall(200, "");
        final IDualResponse response = adapter.adapt(call);
        assertThat("", response, instanceOf(DualResponse.class));
    }

    @Test
    @DisplayName("#getIDualResponse() get default DualResponse")
    public void test1637409034428() {
        final Call call = getCall(200, "");
        final IDualResponse iDualResponse = DEFAULT_FACTORY.getIDualResponse(call, STR_C, STR_C, INFO, AA, R);
        assertThat("", iDualResponse, instanceOf(DualResponse.class));
    }

    @Test
    @DisplayName("#getIDualResponse() get default UnitTestDualResponse")
    public void test1637409128981() {
        final Call call = getCall(200, "");
        final IDualResponse iDualResponse = new DualCallAdapterFactory(UnitTestDualResponse::new)
                .getIDualResponse(call, STR_C, STR_C, INFO, AA, R);
        assertThat("", iDualResponse, instanceOf(UnitTestDualResponse.class));
    }


    @Test
    @DisplayName("#getIDualResponse() Parameter 'call' cannot be null.")
    public void test1637408763989() {
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(null, STR_C, STR_C, INFO,  AA, R))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'call' cannot be null.");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'successType' cannot be null.")
    public void test1637408774271() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, null, STR_C, INFO,  AA, R))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'successType' cannot be null.");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'errorType' cannot be null.")
    public void test1637408780676() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STR_C, null, INFO,  AA, R))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'errorType' cannot be null.");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'endpointInfo' cannot be null.")
    public void test1637408788466() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STR_C, STR_C, null,  AA, R))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'endpointInfo' cannot be null.");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'methodAnnotations' cannot be null.")
    public void test1637408795159() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STR_C, STR_C, INFO,  null, R))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'methodAnnotations' cannot be null.");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'retrofit' cannot be null.")
    public void test1637408802911() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STR_C, STR_C, INFO,  AA, null))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'retrofit' cannot be null.");
    }

    @Test
    @DisplayName("Successfully getting ParameterizedType from DualResponse class")
    public void test1634556848627() {
        final ParameterizedType mock = mock(ParameterizedType.class);
        when(mock.getRawType()).thenReturn(DualResponse.class);
        ParameterizedType parameterizedType = DEFAULT_FACTORY.getParameterizedType(mock);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(DualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(mock));
    }

    @Test
    @DisplayName("Successfully getting ParameterizedType from IDualResponse generic class")
    public void test1634556848628() {
        Type drType = getGenericIDualResponse().getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType = DEFAULT_FACTORY.getParameterizedType(drType);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(IDualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(drType));
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from IDualResponse raw class")
    public void test1634558941130() {
        Type drType = getRawIDualResponse().getClass().getGenericInterfaces()[0];
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(drType))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse");
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from unsupported generic class")
    public void test1634560579101() {
        Type unsupported = new HashMap<String, String>().getClass().getGenericInterfaces()[0];
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(unsupported))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: java.util.Map<K, V>");
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from 'null' class")
    public void test1634560867667() {
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(null))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: null");
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response, for String.class, if successful status code")
    public void test1637406202148() {
        final Call call = getCall(200, "test1637406202148");
        final Response<Object> retrofitResponse = DEFAULT_FACTORY.getRetrofitResponse(call, STR_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("", retrofitResponse.isSuccessful(), is(true)))
                .softly(() -> assertThat("", retrofitResponse.body(), notNullValue()))
                .softly(() -> assertThat("", retrofitResponse.body(), instanceOf(STR_C)))
                .softly(() -> assertThat("", retrofitResponse.body(), is("test1637406202148")))
                .softly(() -> assertThat("", retrofitResponse.errorBody(), nullValue()))
                .softly(() -> assertThat("", retrofitResponse.code(), is(200)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response, for String.class, if error status code")
    public void test1637406205130() {
        final Call call = getCall(400, "test1637406205130");
        final Response<Object> retrofitResponse = DEFAULT_FACTORY.getRetrofitResponse(call, STR_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("", retrofitResponse.isSuccessful(), is(false)))
                .softly(() -> assertThat("", retrofitResponse.body(), nullValue()))
                .softly(() -> assertThat("", retrofitResponse.errorBody(), notNullValue()))
                .softly(() -> assertThat("", retrofitResponse.errorBody().string(), is("test1637406205130")))
                .softly(() -> assertThat("", retrofitResponse.code(), is(400)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response for AnyBody.class if successful status code")
    public void test1637406208214() {
        final Call call = getCall(200, new AnyBody("test1637406208214"));
        final Response<AnyBody> response = DEFAULT_FACTORY.getRetrofitResponse(call, AB_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("isSuccessful()", response.isSuccessful(), is(true)))
                .softly(() -> assertThat("body()", response.body(), notNullValue()))
                .softly(() -> assertThat("body()", response.body(), instanceOf(AB_C)))
                .softly(() -> assertThat("body()", response.body().string(), is("test1637406208214")))
                .softly(() -> assertThat("errorBody()", response.errorBody(), nullValue()))
                .softly(() -> assertThat("code()", response.code(), is(200)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response for AnyBody.class if error status code")
    public void test1637406211648() {
        final Call call = getCall(400, "test1637406211648");
        final Response<AnyBody> response = DEFAULT_FACTORY.getRetrofitResponse(call, AB_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("isSuccessful()", response.isSuccessful(), is(false)))
                .softly(() -> assertThat("body()", response.body(), nullValue()))
                .softly(() -> assertThat("errorBody()", response.errorBody(), notNullValue()))
                .softly(() -> assertThat("errorBody()", response.errorBody().string(), is("test1637406211648")))
                .softly(() -> assertThat("code()", response.code(), is(400)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response for AnyBody.class if response body is null")
    public void test1637406461653() {
        final Request request = getRequest();
        final okhttp3.Response rawResponseBody = mock(okhttp3.Response.class);
        when(rawResponseBody.isSuccessful()).thenReturn(true);
        when(rawResponseBody.code()).thenReturn(200);
        final Response mResponse = mock(Response.class);
        when(mResponse.raw()).thenReturn(rawResponseBody);
        when(mResponse.isSuccessful()).thenReturn(true);
        when(mResponse.body()).thenReturn(null);
        final Call call = getCall(request, mResponse);
        final Response<AnyBody> response = DEFAULT_FACTORY.getRetrofitResponse(call, AB_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("isSuccessful()", response.isSuccessful(), is(true)))
                .softly(() -> assertThat("body()", response.body(), notNullValue()))
                .softly(() -> assertThat("body()", response.body(), instanceOf(AB_C)))
                .softly(() -> assertThat("body()", response.body().bytes(), nullValue()))
                .softly(() -> assertThat("errorBody()", response.errorBody(), nullValue()))
                .softly(() -> assertThat("code()", response.code(), is(200)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() get response for String.class if response body is null")
    public void test1637407126370() {
        final Request request = getRequest();
        final Response mResponse = mock(Response.class);
        when(mResponse.isSuccessful()).thenReturn(true);
        when(mResponse.body()).thenReturn(null);
        when(mResponse.code()).thenReturn(200);
        final Call call = getCall(request, mResponse);
        final Response<String> response = DEFAULT_FACTORY.getRetrofitResponse(call, STR_C, AA);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("isSuccessful()", response.isSuccessful(), is(true)))
                .softly(() -> assertThat("body()", response.body(), nullValue()))
                .softly(() -> assertThat("errorBody()", response.errorBody(), nullValue()))
                .softly(() -> assertThat("code()", response.code(), is(200)))
        );
    }

    @Test
    @DisplayName("#getRetrofitResponse() add annotation info to execution exception")
    public void test1637407697386() throws Exception {
        final Call call = getCall(200, "");
        when(call.execute()).thenThrow(new RuntimeException("test1637407697386"));
        Annotation[] annotations = new Annotation[]{new POST() {
            public Class<? extends Annotation> annotationType() {
                return POST.class;
            }

            public String value() {
                return "/api/test1637407697386/";
            }
        }};
        assertThrow(() -> DEFAULT_FACTORY.getRetrofitResponse(call, STR_C, annotations))
                .assertClass(HttpCallException.class)
                .assertMessageContains("Failed to make API call", "/api/test1637407697386/")
                .assertCause(cause -> cause
                        .assertClass(RuntimeException.class)
                        .assertMessageIs("test1637407697386"));
    }

    @Test
    @DisplayName("#getRetrofitResponse() Parameter 'call' cannot be null.")
    public void test1637408261853() {
        assertThrow(() -> DEFAULT_FACTORY.getRetrofitResponse(null, STR_C, AA))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'call' cannot be null.");
    }

    @Test
    @DisplayName("#getRetrofitResponse() Parameter 'successType' cannot be null.")
    public void test1637408308179() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getRetrofitResponse(call, null, AA))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'successType' cannot be null.");
    }

    @Test
    @DisplayName("#getRetrofitResponse() Parameter 'methodAnnotations' cannot be null.")
    public void test1637408311262() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getRetrofitResponse(call, STR_C, null))
                .assertClass(NullPointerException.class)
                .assertMessageContains("Parameter 'methodAnnotations' cannot be null.");
    }

    @Test
    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body != empty string")
    public void test1637391749128() {
        final Response response = getResponse(500, "test1637391749128");
        final AnyBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, AnyBody.class, AA, R);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
                .softly(() -> assertThat("AnyBody", errorDTO.string(), is("test1637391749128")))
        );
    }

    @Test
    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body = empty string")
    public void test1637402270673() {
        final Response response = getResponse(500, "");
        final AnyBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, AnyBody.class, AA, R);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
                .softly(() -> assertThat("AnyBody", errorDTO.string(), is("")))
        );
    }

    @Test
    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body = null")
    public void test1637393560766() {
        final Response response = getResponse(200, null);
        final AnyBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, AnyBody.class, AA, R);
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
                .softly(() -> assertThat("AnyBody", errorDTO.bytes(), nullValue()))
        );
    }

    @Test
    @DisplayName("#getErrorDTO() - return null if body=null")
    public void test1637394296875() {
        final Response response = getResponse(200, null);
        final Object errorDTO = DEFAULT_FACTORY.getErrorDTO(response, OBJ_C, AA, R);
        softlyAsserter(asserter -> asserter.softly(() -> assertThat("AnyBody", errorDTO, nullValue())));
    }

    @Test
    @DisplayName("#getErrorDTO() Failed to convert error body if DTO = unsupported class")
    public void test1635885324572() {
        final Response response = getResponse(500, "");
        assertThrow(() -> DEFAULT_FACTORY.getErrorDTO(response, OBJ_C, AA, R))
                .assertClass(HttpCallException.class)
                .assertMessageIs("Failed to convert error body.")
                .assertCause(cause1 -> cause1
                        .assertClass(ConvertCallException.class)
                        .assertMessageContains("Converter not found", "DTO type: class java.lang.Object"));
    }


    @Test
    @DisplayName("Successfully getting a EndpointInfo message from a filled EndpointInfo annotation")
    public void test1634561408525() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("test1634561408525")};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, is("test1634561408525"));
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from the blank EndpointInfo annotation")
    public void test1634561408526() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("   ")};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from empty EndpointInfo annotation")
    public void test1634561408527() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo(null)};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the EndpointInfo annotation not present")
    public void test1634561408528() {
        Annotation[] annotations = new Annotation[]{};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the Annotation array is null")
    public void test1634561408529() {
        Annotation[] annotations = new Annotation[]{};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    private Call getCall(int code, Object body) {
        final Request request = getRequest();
        final Response response = getResponse(code, body);
        return getCall(request, response);
    }

    private Call getCall(Request request, Response<?> response) {
        Call call = mock(Call.class);
        try {
            when(call.request()).thenReturn(request);
            when(call.execute()).thenReturn(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return call;
    }

    private Response getResponse(int code, Object body) {
        if (code >= 200 && code <= 299) {
            return Response.success(code, body);
        }
        final ResponseBody rawResponseBody;
        if (body == null) {
            rawResponseBody = mock(ResponseBody.class);
        } else {
            rawResponseBody = ResponseBody.create(MediaType.get("application/json"), String.valueOf(body));
        }
        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
                .body(rawResponseBody)
                .request(getRequest())
                .code(code)
                .protocol(Protocol.HTTP_1_1)
                .message("TEST")
                .build();
        return Response.error(rawResponseBody, rawResponse);
    }

    private Request getRequest() {
        final RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), "{}".getBytes());
        return new Request.Builder()
                .addHeader(H_CONTENT_TYPE, "application/json")
                .post(requestBody)
                .url("http://localhost")
                .build();
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

    private IDualResponse<String, String> getGenericIDualResponse() {
        return new IDualResponse<String, String>() {

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
            public String getErrorDTO() {
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

    private static final class UnitTestDualResponse<SUC_DTO, ERR_DTO> extends DualResponse<SUC_DTO, ERR_DTO> {
        public UnitTestDualResponse(Request rawRequest,
                                    Response<SUC_DTO> response,
                                    ERR_DTO errorDto,
                                    String endpointInfo,
                                    Annotation[] callAnnotations) {
            super(rawRequest, response, errorDto, endpointInfo, callAnnotations);
        }
    }

}
