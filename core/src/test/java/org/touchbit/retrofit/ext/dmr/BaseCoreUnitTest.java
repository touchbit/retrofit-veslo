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

package org.touchbit.retrofit.ext.dmr;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static internal.test.utils.TestUtils.array;
import static internal.test.utils.TestUtils.getGenericReturnTypeForMethod;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.TEXT_PLAIN;

@EverythingIsNonNull // for suppress interface inspections
@SuppressWarnings({"ConstantConditions", "SameParameterValue", "unused", "rawtypes"})
public class BaseCoreUnitTest extends BaseUnitTest {

    public static final TestsExtensionConverterFactory TEST_FACTORY = new TestsExtensionConverterFactory();
    public static final Type DUAL_RESPONSE_GENERIC_STRING_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "stringDualResponse");
    public static final Type DUAL_RESPONSE_RAW_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "rawDualResponse");

    protected static RequestBodyConverter getRequestBodyConverter(Type type, Annotation... methodAnnotations) {
        return TEST_FACTORY.requestBodyConverter(type, AA, methodAnnotations, RTF);
    }

    private interface BaseGenericTypes {
        DualResponse<String, String> stringDualResponse();

        DualResponse rawDualResponse();
    }

    public static final class TestsExtensionConverterFactory extends ExtensionConverterFactory {

        public TestsExtensionConverterFactory() {
            registerMimeRequestConverter(new TestConverter(), TEXT_PLAIN);
            registerMimeResponseConverter(new TestConverter(), TEXT_PLAIN);
            registerPackageRequestConverter(new TestPackageConverter(), PackageDTO.class.getPackage().getName());
            registerPackageResponseConverter(new TestPackageConverter(), PackageDTO.class.getPackage().getName());
        }

    }

    public static final class PrivateConstructorConverter implements ExtensionConverter<TestDTO> {

        private PrivateConstructorConverter() {
        }

        private static TestDTO convert(ResponseBody body) throws IOException {
            return new TestDTO(body.bytes());
        }

        public RequestBodyConverter requestBodyConverter(Type a1, Annotation[] a2, Annotation[] a3, Retrofit a4) {
            return body -> RequestBody.create(null, String.valueOf(body));
        }

        public ResponseBodyConverter<TestDTO> responseBodyConverter(Type a1, Annotation[] a2, Retrofit a3) {
            return PrivateConstructorConverter::convert;
        }

    }

    public static final class TestConverter implements ExtensionConverter<TestDTO> {

        public RequestBodyConverter requestBodyConverter(Type a1, Annotation[] a2, Annotation[] a3, Retrofit a4) {
            return body -> RequestBody.create(null, String.valueOf(body));
        }

        public ResponseBodyConverter<TestDTO> responseBodyConverter(Type a1, Annotation[] a2, Retrofit a3) {
            return body -> new TestDTO(body.bytes());
        }

    }

    public static final class TestPackageConverter implements ExtensionConverter<PackageDTO> {

        public RequestBodyConverter requestBodyConverter(Type a1, Annotation[] a2, Annotation[] a3, Retrofit a4) {
            return body -> RequestBody.create(null, String.valueOf(body));
        }

        public ResponseBodyConverter<PackageDTO> responseBodyConverter(Type a1, Annotation[] a2, Retrofit a3) {
            return body -> new PackageDTO(body.bytes());
        }
    }

    protected static Converters getConverters(RequestConverter... requestConverters) {
        return getConverters(array(), requestConverters);
    }

    protected static Converters getConverters(ResponseConverter... responseConverters) {
        return getConverters(responseConverters, array());
    }

    protected static Converters getConverters(ResponseConverter[] responseConverters,
                                              RequestConverter[] requestConverters) {
        return new Converters() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Converters.class;
            }

            @Override
            public ResponseConverter[] response() {
                return responseConverters;
            }

            @Override
            public RequestConverter[] request() {
                return requestConverters;
            }
        };
    }

    protected static ResponseConverter getResponseConverter(Class<? extends ExtensionConverter<?>> converter,
                                                            Class<?>... classes) {
        return new ResponseConverter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ResponseConverter.class;
            }

            @Override
            public Class<?>[] bodyClasses() {
                return classes;
            }

            @Override
            public Class<? extends ExtensionConverter<?>> converter() {
                return converter;
            }
        };
    }

    protected static RequestConverter getRequestConverter(Class<? extends ExtensionConverter<?>> converter,
                                                          Class<?>... classes) {
        return new RequestConverter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestConverter.class;
            }

            @Override
            public Class<?>[] bodyClasses() {
                return classes;
            }

            @Override
            public Class<? extends ExtensionConverter<?>> converter() {
                return converter;
            }
        };
    }

    protected static EndpointInfo getEndpointInfo(final String message) {
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

    protected static final class UnitDualResponseTest<SUC_DTO, ERR_DTO> extends DualResponse<SUC_DTO, ERR_DTO> {

        public UnitDualResponseTest(@Nullable SUC_DTO sucDTO,
                                    @Nullable ERR_DTO errDTO,
                                    @Nonnull okhttp3.Response response,
                                    @Nonnull String endpointInfo,
                                    @Nonnull Annotation[] callAnnotations) {
            super(sucDTO, errDTO, response, endpointInfo, callAnnotations);
        }

    }

}
