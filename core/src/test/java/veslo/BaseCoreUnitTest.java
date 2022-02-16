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

package veslo;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.RetrofitTestUtils;
import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.EndpointInfo;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.converter.api.Converters;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.converter.api.RequestConverter;
import veslo.client.converter.api.ResponseConverter;
import veslo.client.header.ContentType;
import veslo.client.response.DualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static internal.test.utils.TestUtils.array;
import static internal.test.utils.TestUtils.getGenericReturnTypeForMethod;
import static veslo.client.header.ContentTypeConstants.TEXT_PLAIN;

@EverythingIsNonNull // for suppress interface inspections
@SuppressWarnings({"ConstantConditions", "SameParameterValue", "unused", "rawtypes"})
public class BaseCoreUnitTest extends BaseUnitTest {

    public static final Type DUAL_RESPONSE_GENERIC_STRING_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "stringDualResponse");
    public static final Type DUAL_RESPONSE_PACKAGE_DTO_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "packageDTODualResponse");
    public static final Type DUAL_RESPONSE_RAW_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "rawDualResponse");
    public static final Type LIST_PACKAGE_DTO_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "packageDTOList");
    public static final Type MAP_PACKAGE_DTO_TYPE = getGenericReturnTypeForMethod(BaseGenericTypes.class, "packageDTOMap");
    public static final TestConverter TEST_CONVERTER = new TestConverter();

    protected static TestsExtensionConverterFactory getTestFactory() {
        return new TestsExtensionConverterFactory();
    }

    private interface BaseGenericTypes {
        DualResponse<String, String> stringDualResponse();

        DualResponse<PackageDTO, PackageDTO> packageDTODualResponse();

        DualResponse rawDualResponse();

        List<PackageDTO> packageDTOList();

        Map<String, PackageDTO> packageDTOMap();
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

        public static final TestConverter INSTANCE = new TestConverter();

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

    protected static Annotation[] getContentTypeHeaderAnnotations(ContentType value) {
        return getContentTypeHeaderAnnotations(value.toString());
    }

    protected static Annotation[] getContentTypeHeaderAnnotations(String value) {
        return RetrofitTestUtils.getCallMethodAnnotations("Content-Type: " + value);
    }

    protected static final class TestToStringConverter implements ExtensionConverter<String> {

        public static final TestToStringConverter INSTANCE = new TestToStringConverter();

        @Override
        @EverythingIsNonNull
        public RequestBodyConverter requestBodyConverter(final Type type,
                                                         final Annotation[] parameterAnnotations,
                                                         final Annotation[] methodAnnotations,
                                                         final Retrofit retrofit) {
            return body -> createRequestBody(methodAnnotations, body.toString());
        }

        public ResponseBodyConverter<String> responseBodyConverter(final Type type,
                                                                   final Annotation[] methodAnnotations,
                                                                   final Retrofit retrofit) {
            return responseBody -> type.toString();
        }

    }

}
