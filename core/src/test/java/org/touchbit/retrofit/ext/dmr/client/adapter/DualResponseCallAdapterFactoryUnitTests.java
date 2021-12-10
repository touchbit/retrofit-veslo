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

import internal.test.utils.BaseUnitTest;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import static internal.test.utils.OkHttpUtils.getRequest;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "InstantiatingObjectToGetClassObject", "unchecked", "ConstantConditions", "SameParameterValue"})
@DisplayName("DualCallAdapterFactory tests")
public class DualResponseCallAdapterFactoryUnitTests extends BaseUnitTest {

    private static final DualResponseCallAdapterFactory DEFAULT_FACTORY = new DualResponseCallAdapterFactory();
    private static final String INFO = "endpointInfo";
    private static final Retrofit R = new Retrofit.Builder()
            .addCallAdapterFactory(DEFAULT_FACTORY)
            .addConverterFactory(new ExtensionConverterFactory())
            .baseUrl("http://localhost")
            .build();
    private static final Annotation[] AA = new Annotation[]{};

    @Test
    @DisplayName("#get() successful receipt of IDualResponse CallAdapter")
    public void test1639065951487() {
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
    public void test1639065951499() {
        final Call call = getCall(200, "");
        final IDualResponse iDualResponse = DEFAULT_FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, R);
        assertThat("", iDualResponse, instanceOf(DualResponse.class));
    }

    @Test
    @DisplayName("#getIDualResponse() get default UnitTestDualResponse")
    public void test1639065951507() {
        final Call call = getCall(200, "");
        final IDualResponse iDualResponse = new DualResponseCallAdapterFactory(UnitTestDualResponse::new)
                .getIDualResponse(call, STRING_C, STRING_C, INFO, AA, R);
        assertThat("", iDualResponse, instanceOf(UnitTestDualResponse.class));
    }


    @Test
    @DisplayName("#getIDualResponse() Parameter 'call' cannot be null.")
    public void test1639065951517() {
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(null, STRING_C, STRING_C, INFO, AA, R)).assertNPE("call");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'successType' cannot be null.")
    public void test1639065951523() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, null, STRING_C, INFO, AA, R)).assertNPE("successType");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'errorType' cannot be null.")
    public void test1639065951530() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STRING_C, null, INFO, AA, R)).assertNPE("errorType");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'endpointInfo' cannot be null.")
    public void test1639065951537() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STRING_C, STRING_C, null, AA, R)).assertNPE("endpointInfo");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'methodAnnotations' cannot be null.")
    public void test1639065951544() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, null, R))
                .assertNPE("methodAnnotations");
    }

    @Test
    @DisplayName("#getIDualResponse() Parameter 'retrofit' cannot be null.")
    public void test1639065951552() {
        final Call call = getCall(200, "");
        assertThrow(() -> DEFAULT_FACTORY.getIDualResponse(call, STRING_C, STRING_C, INFO, AA, null)).assertNPE("retrofit");
    }

    @Test
    @DisplayName("Successfully getting ParameterizedType from DualResponse class")
    public void test1639065951559() {
        final ParameterizedType mock = mock(ParameterizedType.class);
        when(mock.getRawType()).thenReturn(DualResponse.class);
        ParameterizedType parameterizedType = DEFAULT_FACTORY.getParameterizedType(mock);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(DualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(mock));
    }

    @Test
    @DisplayName("Successfully getting ParameterizedType from IDualResponse generic class")
    public void test1639065951569() {
        Type drType = getGenericIDualResponse().getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType = DEFAULT_FACTORY.getParameterizedType(drType);
        assertThat("ParameterizedType raw type", parameterizedType.getRawType(), is(IDualResponse.class));
        assertThat("ParameterizedType", parameterizedType, is(drType));
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from IDualResponse raw class")
    public void test1639065951578() {
        Type drType = getRawIDualResponse().getClass().getGenericInterfaces()[0];
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(drType))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse");
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from unsupported generic class")
    public void test1639065951589() {
        Type unsupported = new HashMap<String, String>().getClass().getGenericInterfaces()[0];
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(unsupported))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: java.util.Map<K, V>");
    }

    @Test
    @DisplayName("Exception when getting ParameterizedType from 'null' class")
    public void test1639065951600() {
        assertThrow(() -> DEFAULT_FACTORY.getParameterizedType(null))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("API methods must return an implementation class " +
                        "of interface org.touchbit.retrofit.ext.dmr.client.response.IDualResponse\n" +
                        "Actual: null");
    }

//    @Test
//    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body != empty string")
//    public void test1639065951610() {
//        final Response response = getResponse(500, "test1637391749128");
//        final RawBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, RawBody.class, AA, R);
//        softlyAsserter(asserter -> asserter
//                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
//                .softly(() -> assertThat("AnyBody", errorDTO.string(), is("test1637391749128")))
//        );
//    }
//
//    @Test
//    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body = empty string")
//    public void test1639065951621() {
//        final Response response = getResponse(500, "");
//        final RawBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, RawBody.class, AA, R);
//        softlyAsserter(asserter -> asserter
//                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
//                .softly(() -> assertThat("AnyBody", errorDTO.string(), is("")))
//        );
//    }
//
//    @Test
//    @DisplayName("#getErrorDTO() - return AnyBody if DTO = AnyBody and body = null")
//    public void test1639065951632() {
//        final Response response = getResponse(200, null);
//        final RawBody errorDTO = DEFAULT_FACTORY.getErrorDTO(response, RawBody.class, AA, R);
//        softlyAsserter(asserter -> asserter
//                .softly(() -> assertThat("AnyBody", errorDTO, notNullValue()))
//                .softly(() -> assertThat("AnyBody", errorDTO.bytes(), nullValue()))
//        );
//    }
//
//    @Test
//    @DisplayName("#getErrorDTO() - return null if body=null")
//    public void test1639065951643() {
//        final Response response = getResponse(200, null);
//        final Object errorDTO = DEFAULT_FACTORY.getErrorDTO(response, OBJ_C, AA, R);
//        softlyAsserter(asserter -> asserter.softly(() -> assertThat("AnyBody", errorDTO, nullValue())));
//    }
//
//    @Test
//    @DisplayName("#getErrorDTO() Failed to convert error body if DTO = unsupported class")
//    public void test1639065951651() {
//        final Response response = getResponse(500, "");
//        assertThrow(() -> DEFAULT_FACTORY.getErrorDTO(response, OBJ_C, AA, R))
//                .assertClass(HttpCallException.class)
//                .assertMessageIs("Failed to convert error body.")
//                .assertCause(cause1 -> cause1
//                        .assertClass(ConverterNotFoundException.class)
//                        .assertMessageContains("Converter not found", "DTO type: class java.lang.Object"));
//    }


    @Test
    @DisplayName("Successfully getting a EndpointInfo message from a filled EndpointInfo annotation")
    public void test1639065951664() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("test1634561408525")};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, is("test1634561408525"));
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from the blank EndpointInfo annotation")
    public void test1639065951672() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo("   ")};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message from empty EndpointInfo annotation")
    public void test1639065951680() {
        Annotation[] annotations = new Annotation[]{getEndpointInfo(null)};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the EndpointInfo annotation not present")
    public void test1639065951688() {
        Annotation[] annotations = new Annotation[]{new Test() {
            public Class<? extends Annotation> annotationType() {
                return Test.class;
            }
        }};
        String endpointInfo = DEFAULT_FACTORY.getEndpointInfo(annotations);
        assertThat("EndpointInfo", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("Successfully getting an empty EndpointInfo message if the Annotation array is null")
    public void test1639065951700() {
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

            @Nonnull
            @Override
            public String getEndpointInfo() {
                return null;
            }

            @Nonnull
            @Override
            public Annotation[] getCallAnnotations() {
                return new Annotation[0];
            }

            @Nonnull
            @Override
            public okhttp3.Response getResponse() {
                return null;
            }

            @Override
            public String getErrDTO() {
                return null;
            }

            @Nullable
            @Override
            public String getSucDTO() {
                return null;
            }

        };
    }

    private IDualResponse getRawIDualResponse() {
        return new IDualResponse() {

            @Nonnull
            @Override
            public String getEndpointInfo() {
                return null;
            }

            @Nonnull
            @Override
            public Annotation[] getCallAnnotations() {
                return new Annotation[0];
            }

            @Nonnull
            @Override
            public okhttp3.Response getResponse() {
                return null;
            }

            @Override
            public Object getErrDTO() {
                return null;
            }

            @Nullable
            @Override
            public Object getSucDTO() {
                return null;
            }
        };
    }

    private static final class UnitTestDualResponse<SUC_DTO, ERR_DTO> extends DualResponse<SUC_DTO, ERR_DTO> {

        public UnitTestDualResponse(@Nullable SUC_DTO sucDTO,
                                    @Nullable ERR_DTO errDTO,
                                    @Nonnull okhttp3.Response response,
                                    @Nonnull String endpointInfo,
                                    @Nonnull Annotation[] callAnnotations) {
            super(sucDTO, errDTO, response, endpointInfo, callAnnotations);
        }
    }

}