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

package veslo.client.converter;

import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import internal.test.utils.client.model.pack.nested.NestedPackageDTO;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import veslo.BaseCoreUnitTest;
import veslo.ConvertCallException;
import veslo.asserter.SoftlyAsserter;
import veslo.client.TransportEvent;
import veslo.client.converter.api.Converters;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.converter.api.ExtensionConverter.RequestBodyConverter;
import veslo.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import veslo.client.converter.api.RequestConverter;
import veslo.client.converter.api.ResponseConverter;
import veslo.client.converter.defaults.JavaPrimitiveTypeConverter;
import veslo.client.converter.defaults.JavaReferenceTypeConverter;
import veslo.client.converter.defaults.RawBodyTypeConverter;
import veslo.client.header.ContentType;
import veslo.client.header.ContentTypeConstants;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;
import veslo.client.response.DualResponse;
import veslo.util.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ConstantConditions", "SameParameterValue", "ConfusingArgumentToVarargsMethod"})
@DisplayName("ExtensionConverterFactory tests")
public class ExtensionConverterFactoryUnitTests extends BaseCoreUnitTest {

    private static final String REQUEST_CONVERTERS_INFO = Utils.readResourceFile("RequestConvertersInfo.txt");
    private static final String REQUEST_CONVERTERS_INFO_WITH_ANNOTATION = Utils.readResourceFile("RequestConvertersInfoWithAnnotated.txt");
    private static final String RESPONSE_CONVERTERS_INFO = Utils.readResourceFile("ResponseConvertersInfo.txt");
    private static final String RESPONSE_CONVERTERS_INFO_WITH_ANNOTATION = Utils.readResourceFile("ResponseConvertersInfoWithAnnotated.txt");

    @Test
    @DisplayName("Default converters initialization")
    public void test1639065948940() {
        ExtensionConverterFactory factory = new ExtensionConverterFactory();
        final Map<Type, ExtensionConverter<?>> rawRqt = factory.getRawRequestConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRqt = factory.getMimeRequestConverters();
        final Map<Type, ExtensionConverter<?>> javaTypeRqt = factory.getJavaTypeRequestConverters();
        final Map<Type, ExtensionConverter<?>> rawRsp = factory.getRawResponseConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRsp = factory.getMimeResponseConverters();
        final Map<Type, ExtensionConverter<?>> javaTypeRsp = factory.getJavaTypeResponseConverters();
        SoftlyAsserter.softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("Request raw converters size", rawRqt.size(), is(5)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(RawBody.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(byte[].class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(Byte[].class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(File.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(ResourceFile.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Request mime converters size", mimeRqt.size(), is(4)))
                .softly(() -> assertThat("Request java type converters size", javaTypeRqt.size(), is(17)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Character.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Boolean.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Byte.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Integer.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Double.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Float.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Long.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Short.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(String.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Character.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Boolean.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Byte.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Integer.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Double.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Float.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Long.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Request java type converter", javaTypeRqt.get(Short.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response raw converters size", rawRsp.size(), is(5)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(RawBody.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(byte[].class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(Byte[].class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(File.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(ResourceFile.class), instanceOf(RawBodyTypeConverter.class)))
                .softly(() -> assertThat("Response mime converters size", mimeRsp.size(), is(4)))
                .softly(() -> assertThat("Response java type converters size", javaTypeRsp.size(), is(17)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Character.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Boolean.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Byte.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Integer.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Double.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Float.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Long.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Short.TYPE), instanceOf(JavaPrimitiveTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(String.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Character.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Boolean.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Byte.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Integer.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Double.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Float.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Long.class), instanceOf(JavaReferenceTypeConverter.class)))
                .softly(() -> assertThat("Response java type converter", javaTypeRsp.get(Short.class), instanceOf(JavaReferenceTypeConverter.class)))
        );
    }

    @Test
    @DisplayName("#toString()")
    public void test1639065949524() {
        final String result = getTestFactory().toString();
        assertThat("", result, is("" +
                                  "Converter factory: class veslo.BaseCoreUnitTest$TestsExtensionConverterFactory" +
                                  "\n\n" + REQUEST_CONVERTERS_INFO +
                                  "\n\n" + RESPONSE_CONVERTERS_INFO));
    }

    @Test
    @DisplayName("#getLogger() not null")
    public void test1639980524927() {
        assertThat(getTestFactory().getLogger(), notNullValue());
    }

    @Nested
    @DisplayName("#getRequestConverterFromCallAnnotation() method tests")
    public class GetRequestConverterFromAnnotationMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639943236693() {
            assertNPE(() -> getTestFactory().getRequestConverterFromCallAnnotation(null, AA, AA, RTF), "bodyClass");
            assertNPE(() -> getTestFactory().getRequestConverterFromCallAnnotation(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> getTestFactory().getRequestConverterFromCallAnnotation(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getRequestConverterFromCallAnnotation(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return null if RequestConverter annotation has processed class")
        public void test1639065949591() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, arrayOf(requestConverter), RTF);
            assertThat("Converter", converter, isA(RequestBodyConverter.class));
        }

        @Test
        @DisplayName("return null if RequestConverter annotation has not processed class")
        public void test1639065949601() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, Objects.class);
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, arrayOf(requestConverter), RTF);
            assertThat("Converter", converter, nullValue());
        }

        @Test
        @DisplayName("return null if Converters annotation has not processed class")
        public void test1639065949611() {
            final RequestConverter[] requestConverters = new RequestConverter[]{
                    getRequestConverter(TestConverter.class, Objects.class)
            };
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, requestConverters);
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, arrayOf(converters), RTF);
            assertThat("Converter", converter, nullValue());
        }

        @Test
        @DisplayName("return converter if Converters annotation does not contain classes for conversion")
        public void test1639065949634() {
            final RequestConverter[] requestConverters = arrayOf(getRequestConverter(TestConverter.class));
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, requestConverters);
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, arrayOf(converters), RTF);
            assertThat("Converter", converter, isA(RequestBodyConverter.class));
        }

        @Test
        @DisplayName("return converter if Converters annotation has RawDTO.class")
        public void test1639065949645() {
            final RequestConverter[] requestConverters = arrayOf(getRequestConverter(TestConverter.class, TestDTO.class));
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, requestConverters);
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, arrayOf(converters), RTF);
            assertThat("Converter", converter, isA(RequestBodyConverter.class));
        }

        @Test
        @DisplayName("return null if Converters annotation is empty (without specified @RequestConverter)")
        public void test1639065949624() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final RequestBodyConverter requestConverterFromAnnotation = getTestFactory()
                    .getRequestConverterFromCallAnnotation(OBJ_C, AA, arrayOf(converters), RTF);
            assertThat("Converter", requestConverterFromAnnotation, nullValue());
        }

        @Test
        @DisplayName("return null if annotations not present")
        public void test1639942661747() {
            final RequestBodyConverter converter = getTestFactory()
                    .getRequestConverterFromCallAnnotation(TestDTO.class, AA, AA, RTF);
            assertThat(converter, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException thrown if RequestConverter and Converters obtained at the same time")
        public void test1639065949656() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
            final Annotation[] annotations = {converters, requestConverter};
            assertThrow(() -> getTestFactory().getRequestConverterFromCallAnnotation(OBJ_C, AA, annotations, RTF))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("API method contains concurrent annotations.\n" +
                                     "Use only one of:\n" +
                                     " * interface veslo.client.converter.api.RequestConverter\n" +
                                     " * interface veslo.client.converter.api.Converters");
        }

    }

    @Nested
    @DisplayName("#getPackageRequestConverter() method tests")
    public class GetPackageRequestConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639942954995() {
            assertNPE(() -> getTestFactory().getPackageRequestConverter(null, AA, AA, RTF), "bodyClass");
            assertNPE(() -> getTestFactory().getPackageRequestConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> getTestFactory().getPackageRequestConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getPackageRequestConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return converter by supported package model")
        public void test1639065949751() {
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.registerPackageRequestConverter(new TestPackageConverter(), "internal.test.utils.client.model.pack");
            final RequestBodyConverter converter = factory
                    .getPackageRequestConverter(PackageDTO.class, AA, AA, RTF);
            assertThat("", converter, notNullValue());
        }

        @Test
        @DisplayName("Return null by unsupported package model")
        public void test1639065949761() {
            final RequestBodyConverter converter = getTestFactory()
                    .getPackageRequestConverter(NestedPackageDTO.class, AA, AA, RTF);
            assertThat("", converter, nullValue());
        }

        @Test
        @DisplayName("Return null if motel class is primitive")
        public void test1639943515743() {
            final RequestBodyConverter converter = getTestFactory()
                    .getPackageRequestConverter(Boolean.TYPE, AA, AA, RTF);
            assertThat("", converter, nullValue());
        }

        @Test
        @DisplayName("Return converter by wildcard '*'")
        public void test1645614892840() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "*");
            final RequestBodyConverter result = factory.getPackageRequestConverter(String.class, AA, AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard '*.converter'")
        public void test1645614895223() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "*.converter");
            final RequestBodyConverter result = factory.getPackageRequestConverter(this.getClass(), AA, AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard 'veslo.*.converter'")
        public void test1645614898191() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "veslo.*.converter");
            final RequestBodyConverter result = factory.getPackageRequestConverter(this.getClass(), AA, AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard 'veslo.*'")
        public void test1645614901842() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "veslo.*");
            final RequestBodyConverter result = factory.getPackageRequestConverter(this.getClass(), AA, AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return null by wildcard ''")
        public void test1645614905662() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "");
            final RequestBodyConverter result = factory.getPackageRequestConverter(this.getClass(), AA, AA, RTF);
            assertIsNull(result);
        }

    }

    @Nested
    @DisplayName("#getRawRequestConverter() method tests")
    public class GetRawRequestConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639943730640() {
            assertNPE(() -> getTestFactory().getRawRequestConverter(null, AA, AA, RTF), "bodyClass");
            assertNPE(() -> getTestFactory().getRawRequestConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> getTestFactory().getRawRequestConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getRawRequestConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return default Raw converters")
        public void test1639942879633() {
            assertThat(getTestFactory().getRawRequestConverter(RawBody.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawRequestConverter(Byte[].class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawRequestConverter(byte[].class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawRequestConverter(File.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawRequestConverter(ResourceFile.class, AA, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("Return null for unregistered type")
        public void test1639943158956() {
            assertThat(getTestFactory().getRawRequestConverter(OBJ_C, AA, AA, RTF), nullValue());
        }

    }

    @Nested
    @DisplayName("#getMimeRequestConverter() method tests")
    public class GetMimeRequestConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639969364968() {
            assertNPE(() -> getTestFactory().getMimeRequestConverter(null, AA, AA, RTF), "bodyClass");
            assertNPE(() -> getTestFactory().getMimeRequestConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> getTestFactory().getMimeRequestConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getMimeRequestConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return converter by text/plain mime type")
        public void test1639968946181() {
            final Annotation[] methodAnnotations = BaseCoreUnitTest.getContentTypeHeaderAnnotations(ContentTypeConstants.TEXT_PLAIN);
            final RequestBodyConverter result = getTestFactory().getMimeRequestConverter(OBJ_C, AA, methodAnnotations, RTF);
            assertThat(result, notNullValue());
        }

        @Test
        @DisplayName("return null if converter not registered with a random mime type")
        public void test1639969184440() {
            final Annotation[] methodAnnotations = getContentTypeHeaderAnnotations("mime/random");
            final RequestBodyConverter result = getTestFactory().getMimeRequestConverter(OBJ_C, AA, methodAnnotations, RTF);
            assertThat(result, nullValue());
        }

    }

    @Nested
    @DisplayName("#getJavaTypeRequestConverter() method tests")
    public class GetJavaTypeRequestConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639969372689() {
            assertNPE(() -> getTestFactory().getJavaTypeRequestConverter(null, AA, AA, RTF), "bodyClass");
            assertNPE(() -> getTestFactory().getJavaTypeRequestConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> getTestFactory().getJavaTypeRequestConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getJavaTypeRequestConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return default java type converters")
        public void test1639969426274() {
            assertThat(getTestFactory().getJavaTypeRequestConverter(Character.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Boolean.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Byte.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Integer.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Double.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Float.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Long.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Short.TYPE, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Character.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Boolean.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Byte.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Integer.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Double.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Float.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Long.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(Short.class, AA, AA, RTF), notNullValue());
            assertThat(getTestFactory().getJavaTypeRequestConverter(String.class, AA, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("Return null if converter not registered")
        public void test1639969520085() {
            assertThat(getTestFactory().getJavaTypeRequestConverter(Object.class, AA, AA, RTF), nullValue());
        }

    }

    @Nested
    @DisplayName("#getResponseBodyType() method tests")
    public class GetResponseBodyTypeMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639970352151() {
            assertNPE(() -> getTestFactory().getResponseBodyType(null), "type");
        }

        @Test
        @DisplayName("return String.class type if incoming type = IDualResponse<String, String>")
        public void test1639969701337() {
            assertThat(getTestFactory().getResponseBodyType(DUAL_RESPONSE_GENERIC_STRING_TYPE), is(String.class));
        }

        @Test
        @DisplayName("return IDualResponse.class type if incoming type == IDualResponse raw type (not ParameterizedType)")
        public void test1639969853342() {
            assertThat(getTestFactory().getResponseBodyType(DUAL_RESPONSE_RAW_TYPE), is(DualResponse.class));
        }

        @Test
        @DisplayName("return List.class type if incoming type == List type")
        public void test1639969896009() {
            assertThat(getTestFactory().getResponseBodyType(List.class), is(List.class));
        }

    }

    @Nested
    @DisplayName("#getRawResponseConverter() method tests")
    public class GetRawResponseConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639970510136() {
            assertNPE(() -> getTestFactory().getRawResponseConverter(null, AA, RTF), "bodyType");
            assertNPE(() -> getTestFactory().getRawResponseConverter(OBJ_C, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getRawResponseConverter(OBJ_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return default Raw converters")
        public void test1639970512630() {
            assertThat(getTestFactory().getRawResponseConverter(RawBody.class, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawResponseConverter(Byte[].class, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawResponseConverter(byte[].class, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawResponseConverter(File.class, AA, RTF), notNullValue());
            assertThat(getTestFactory().getRawResponseConverter(ResourceFile.class, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("Return null for unregistered type")
        public void test1639970517875() {
            assertThat(getTestFactory().getRawResponseConverter(OBJ_C, AA, RTF), nullValue());
        }

    }

    @Nested
    @DisplayName("#getPackageResponseConverter() method tests")
    public class GetPackageResponseConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639970585012() {
            assertNPE(() -> getTestFactory().getPackageResponseConverter(null, AA, RTF), "bodyType");
            assertNPE(() -> getTestFactory().getPackageResponseConverter(OBJ_C, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getPackageResponseConverter(OBJ_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return PackageDTO converter for genetic DualResponse<PackageDTO, PackageDTO>")
        public void test1639971180694() {
            assertThat(getTestFactory().getPackageResponseConverter(DUAL_RESPONSE_PACKAGE_DTO_TYPE, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return PackageDTO converter for genetic Map<String, PackageDTO>")
        public void test1639971384398() {
            assertThat(getTestFactory().getPackageResponseConverter(MAP_PACKAGE_DTO_TYPE, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return PackageDTO converter for genetic List<PackageDTO>")
        public void test1639971399772() {
            assertThat(getTestFactory().getPackageResponseConverter(LIST_PACKAGE_DTO_TYPE, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return null for raw genetic DualResponse")
        public void test1639971985808() {
            assertThat(getTestFactory().getPackageResponseConverter(DUAL_RESPONSE_RAW_TYPE, AA, RTF), nullValue());
        }

        @Test
        @DisplayName("return null for genetic List<String>")
        public void test1639972051954() {
            assertThat(getTestFactory().getPackageResponseConverter(STRING_LIST_T, AA, RTF), nullValue());
        }

        @Test
        @DisplayName("return null for genetic Map<String, String>")
        public void test1639972057628() {
            assertThat(getTestFactory().getPackageResponseConverter(STRING_MAP_T, AA, RTF), nullValue());
        }

        @Test
        @DisplayName("return null for primitive")
        public void test1639972159603() {
            assertThat(getTestFactory().getPackageResponseConverter(Boolean.TYPE, AA, RTF), nullValue());
        }

        @Test
        @DisplayName("Return converter by wildcard '*'")
        public void test1645614292210() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "*");
            final ResponseBodyConverter<?> result = factory.getPackageResponseConverter(String.class, AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard '*.converter'")
        public void test1645614632996() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "*.converter");
            final ResponseBodyConverter<?> result = factory.getPackageResponseConverter(this.getClass(), AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard 'veslo.*.converter'")
        public void test1645614805994() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "veslo.*.converter");
            final ResponseBodyConverter<?> result = factory.getPackageResponseConverter(this.getClass(), AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return converter by wildcard 'veslo.*'")
        public void test1645614837945() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "veslo.*");
            final ResponseBodyConverter<?> result = factory.getPackageResponseConverter(this.getClass(), AA, RTF);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Return null by wildcard ''")
        public void test1645614760460() {
            final TestsExtensionConverterFactory factory = getTestFactory();
            factory.registerPackageConverter(new TestPackageConverter(), "");
            final ResponseBodyConverter<?> result = factory.getPackageResponseConverter(this.getClass(), AA, RTF);
            assertIsNull(result);
        }

    }

    @Nested
    @DisplayName("#getMimeResponseConverter() method tests")
    public class GetMimeResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639972272144() {
            assertNPE(() -> getTestFactory().getMimeResponseConverter(null, null, AA, RTF), "bodyType");
            assertNPE(() -> getTestFactory().getMimeResponseConverter(null, OBJ_C, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getMimeResponseConverter(null, OBJ_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return converter for registered nullable body (content type not present)")
        public void test1639972946035() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerMimeConverter(TestConverter.INSTANCE, new ContentType(null));
            assertThat(testFactory.getMimeResponseConverter(null, OBJ_C, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return converter for registered body content type")
        public void test1639973115566() {
            final TestsExtensionConverterFactory testFactory = getTestFactory();
            testFactory.registerMimeConverter(TestConverter.INSTANCE, new ContentType(MediaType.get("foo/bar; charset=utf-8")));
            final ResponseBody responseBody = ResponseBody.create(MediaType.get("foo/bar; charset=utf-8"), "test");
            assertThat(testFactory.getMimeResponseConverter(responseBody, OBJ_C, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return null if content type not supported")
        public void test1639973336638() {
            final TestsExtensionConverterFactory testFactory = getTestFactory();
            final ResponseBody responseBody = ResponseBody.create(MediaType.get("foo/bar"), "test");
            assertThat(testFactory.getMimeResponseConverter(responseBody, OBJ_C, AA, RTF), nullValue());
        }

    }

    @Nested
    @DisplayName("#getJavaTypeResponseConverter() method tests")
    public class GetJavaTypeResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639973680228() {
            assertNPE(() -> getTestFactory().getJavaTypeResponseConverter(null, AA, RTF), "bodyType");
            assertNPE(() -> getTestFactory().getJavaTypeResponseConverter(OBJ_C, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getJavaTypeResponseConverter(OBJ_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return converter for registered ParameterizedType")
        public void test1639973733866() {
            final TestsExtensionConverterFactory testFactory = getTestFactory();
            testFactory.registerJavaTypeConverter(TestConverter.INSTANCE, List.class);
            assertThat(testFactory.getJavaTypeResponseConverter(LIST_PACKAGE_DTO_TYPE, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return converter for registered raw type")
        public void test1639973828920() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerJavaTypeConverter(TestConverter.INSTANCE, List.class);
            assertThat(testFactory.getJavaTypeResponseConverter(List.class, AA, RTF), notNullValue());
        }

        @Test
        @DisplayName("return null if converter not registered for incoming type")
        public void test1639973908980() {
            assertThat(getTestFactory().getJavaTypeResponseConverter(Object.class, AA, RTF), nullValue());
        }

    }

    @Nested
    @DisplayName("#getResponseConverterFromCallAnnotation() method tests")
    public class GetResponseConverterFromAnnotationMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639974240248() {
            assertNPE(() -> getTestFactory().getResponseConverterFromCallAnnotation(null, AA, RTF), "bodyType");
            assertNPE(() -> getTestFactory().getResponseConverterFromCallAnnotation(OBJ_C, null, RTF), "methodAnnotations");
            assertNPE(() -> getTestFactory().getResponseConverterFromCallAnnotation(OBJ_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return null if ResponseConverter annotation has processed class")
        public void test1639065949284() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final Converter<ResponseBody, ?> converter = getTestFactory()
                    .getResponseConverterFromCallAnnotation(TestDTO.class, arrayOf(responseConverter), RTF);
            assertThat("Converter", converter, isA(ResponseBodyConverter.class));
        }


        @Test
        @DisplayName("return null if ResponseConverter annotation has not processed class")
        public void test1639065949295() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, Objects.class);
            final Converter<ResponseBody, ?> converter = getTestFactory()
                    .getResponseConverterFromCallAnnotation(TestDTO.class, arrayOf(responseConverter), RTF);
            assertThat("Converter", converter, nullValue());
        }


        @Test
        @DisplayName("return null if Converters annotation has not processed class")
        public void test1639065949306() {
            final ResponseConverter[] responseConverters = new ResponseConverter[]{
                    getResponseConverter(TestConverter.class, Objects.class)
            };
            final Converters converters = BaseCoreUnitTest.getConverters(responseConverters, new RequestConverter[]{});
            final Converter<ResponseBody, ?> converter = getTestFactory()
                    .getResponseConverterFromCallAnnotation(TestDTO.class, arrayOf(converters), RTF);
            assertThat("Converter", converter, nullValue());
        }

        @Test
        @DisplayName("return null if Converters annotation is empty (without specified @ResponseConverter)")
        public void test1639065949319() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final Converter<ResponseBody, ?> responseConverterFromAnnotation = getTestFactory()
                    .getResponseConverterFromCallAnnotation(OBJ_C, arrayOf(converters), RTF);
            assertThat("Converter", responseConverterFromAnnotation, nullValue());
        }

        @Test
        @DisplayName("return converter if Converters annotation does not contain classes for conversion")
        public void test1639065949329() {
            final ResponseConverter[] responseConverters = new ResponseConverter[]{
                    getResponseConverter(TestConverter.class)
            };
            final Converters converters = BaseCoreUnitTest.getConverters(responseConverters, new RequestConverter[]{});
            final Converter<ResponseBody, ?> converter = getTestFactory()
                    .getResponseConverterFromCallAnnotation(TestDTO.class, arrayOf(converters), RTF);
            assertThat("Converter", converter, isA(ResponseBodyConverter.class));
        }

        @Test
        @DisplayName("return converter if Converters annotation has RawDTO.class")
        public void test1639065949342() {
            final ResponseConverter[] responseConverters = new ResponseConverter[]{
                    getResponseConverter(TestConverter.class, TestDTO.class)
            };
            final Converters converters = BaseCoreUnitTest.getConverters(responseConverters, new RequestConverter[]{});
            final Converter<ResponseBody, ?> converter = getTestFactory()
                    .getResponseConverterFromCallAnnotation(TestDTO.class, arrayOf(converters), RTF);
            assertThat("Converter", converter, isA(ResponseBodyConverter.class));
        }

        @Test
        @DisplayName("return null if annotations not present")
        public void test1639065949355() {
            final Converter<ResponseBody, ?> responseConverterFromAnnotation = getTestFactory()
                    .getResponseConverterFromCallAnnotation(OBJ_C, AA, RTF);
            assertThat("Converter", responseConverterFromAnnotation, nullValue());
        }

        @Test
        @DisplayName("ResponseConverter and Converters obtained at the same time")
        public void test1639065949364() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
            final Annotation[] annotations = {converters, responseConverter};
            assertThrow(() -> getTestFactory().getResponseConverterFromCallAnnotation(OBJ_C, annotations, RTF))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("API method contains concurrent annotations.\n" +
                                     "Use only one of:\n" +
                                     " * interface veslo.client.converter.api.ResponseConverter\n" +
                                     " * interface veslo.client.converter.api.Converters");
        }

    }

    @Nested
    @DisplayName("#getExtensionConverter() method tests")
    public class GetExtensionConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1640471514318() {
            assertNPE(() -> getTestFactory().getExtensionConverter(null, Object.class), "annotation");
            assertNPE(() -> getTestFactory().getExtensionConverter(getEndpointInfo(""), null), "bodyType");
        }

        @Test
        @DisplayName("return converter if converted classes not specified")
        public void test1640471516828() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
            final ExtensionConverter<?> converter = getTestFactory().getExtensionConverter(responseConverter, TestDTO.class);
            assertThat("TestConverter", converter, notNullValue());
            assertThat("TestConverter", converter, instanceOf(TestConverter.class));
        }

        @Test
        @DisplayName("return converter if converted classes specified (RawDTO.class)")
        public void test1640471519836() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final ExtensionConverter<?> converter = getTestFactory().getExtensionConverter(requestConverter, TestDTO.class);
            assertThat("TestConverter", converter, notNullValue());
            assertThat("TestConverter", converter, instanceOf(TestConverter.class));
        }

        @Test
        @DisplayName("return null if converted classes incompatible")
        public void test1640471523401() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final ExtensionConverter<?> converter = getTestFactory().getExtensionConverter(responseConverter, OBJ_C);
            assertThat("TestConverter", converter, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException being throw if converter=null")
        public void test1640471525715() {
            assertThrow(() -> getTestFactory().getExtensionConverter(null, OBJ_C)).assertNPE("annotation");
        }

        @Test
        @DisplayName("ConvertCallException being throw if bodyType=null")
        public void test1640471529047() {
            final RequestConverter converter = getRequestConverter(TestConverter.class, TestDTO.class);
            assertThrow(() -> getTestFactory().getExtensionConverter(converter, null)).assertNPE("bodyType");
        }

        @Test
        @DisplayName("ConvertCallException being throw if unsupported annotation")
        public void test1640471531152() {
            final Nullable nullable = new Nullable() {
                public Class<? extends Annotation> annotationType() {
                    return Nullable.class;
                }
            };
            assertThrow(() -> getTestFactory().getExtensionConverter(nullable, OBJ_C))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Received an unsupported annotation type: " + nullable.getClass());
        }

    }

    @Nested
    @DisplayName("#registerMimeConverter() method tests")
    public class RegisterMimeConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1640471587692() {
            assertNPE(() -> getTestFactory().registerMimeConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerMimeConverter(TestConverter.INSTANCE, null), "supportedContentTypes");
        }

        @Test
        @DisplayName("Register request/response MIME converter")
        public void test1640471589743() {
            final TestsExtensionConverterFactory testFactory = getTestFactory();
            final ContentType contentType = new ContentType("foo", "bar");
            testFactory.registerMimeConverter(TestConverter.INSTANCE, contentType);
            assertThat(testFactory.getMimeRequestConverters().get(contentType), notNullValue());
            assertThat(testFactory.getMimeResponseConverters().get(contentType), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerMimeRequestConverter() method tests")
    public class RegisterMimeRequestConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639974992833() {
            assertNPE(() -> getTestFactory().registerMimeRequestConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerMimeRequestConverter(TestConverter.INSTANCE, null), "supportedContentTypes");
            assertNPE(() -> getTestFactory().registerMimeRequestConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedContentType");
        }

        @Test
        @DisplayName("Register request MIME converter")
        public void test1639974577039() {
            final ContentType contentType = new ContentType("foo", "bar");
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerMimeRequestConverter(TestConverter.INSTANCE, contentType);
            assertThat(testFactory.getMimeRequestConverters().get(contentType), notNullValue());
            assertThat(testFactory.getMimeResponseConverters().get(contentType), nullValue());
        }

    }

    @Nested
    @DisplayName("#registerMimeResponseConverter() method tests")
    public class RegisterMimeResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639974997169() {
            assertNPE(() -> getTestFactory().registerMimeResponseConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerMimeResponseConverter(TestConverter.INSTANCE, null), "supportedContentTypes");
            assertNPE(() -> getTestFactory().registerMimeResponseConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedContentType");
        }

        @Test
        @DisplayName("Register response MIME converter")
        public void test1639975000187() {
            final ContentType contentType = new ContentType("foo", "bar");
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerMimeResponseConverter(TestConverter.INSTANCE, contentType);
            assertThat(testFactory.getMimeRequestConverters().get(contentType), nullValue());
            assertThat(testFactory.getMimeResponseConverters().get(contentType), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerRawConverter() method tests")
    public class RegisterRawConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975086685() {
            assertNPE(() -> getTestFactory().registerRawConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerRawConverter(TestConverter.INSTANCE, null), "supportedRawClasses");
        }

        @Test
        @DisplayName("Register request/response MIME converter")
        public void test1639975089092() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerRawConverter(TestConverter.INSTANCE, Object.class);
            assertThat(testFactory.getRawRequestConverters().get(Object.class), notNullValue());
            assertThat(testFactory.getRawResponseConverters().get(Object.class), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerRawRequestConverter() method tests")
    public class RegisterRawRequestConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975224503() {
            assertNPE(() -> getTestFactory().registerRawRequestConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerRawRequestConverter(TestConverter.INSTANCE, null), "supportedRawClasses");
            assertNPE(() -> getTestFactory().registerRawRequestConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedRawClass");
        }

        @Test
        @DisplayName("Register request raw type converter")
        public void test1639975221236() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerRawRequestConverter(TestConverter.INSTANCE, OBJ_C);
            assertThat(testFactory.getRawRequestConverters().get(OBJ_C), notNullValue());
            assertThat(testFactory.getRawResponseConverters().get(OBJ_C), nullValue());
        }

    }

    @Nested
    @DisplayName("#registerRawResponseConverter() method tests")
    public class RegisterRawResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975328522() {
            assertNPE(() -> getTestFactory().registerRawResponseConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerRawResponseConverter(TestConverter.INSTANCE, null), "supportedRawClasses");
            assertNPE(() -> getTestFactory().registerRawResponseConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedRawClass");
        }

        @Test
        @DisplayName("Register response raw type converter")
        public void test1639975215407() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerRawResponseConverter(TestConverter.INSTANCE, OBJ_C);
            assertThat(testFactory.getRawRequestConverters().get(OBJ_C), nullValue());
            assertThat(testFactory.getRawResponseConverters().get(OBJ_C), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerJavaTypeConverter() method tests")
    public class RegisterJavaTypeConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975489335() {
            assertNPE(() -> getTestFactory().registerJavaTypeConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerJavaTypeConverter(TestConverter.INSTANCE, null), "supportedJavaTypeClasses");
        }

        @Test
        @DisplayName("Register request/response java type converter")
        public void test1639975491594() {
            final TestsExtensionConverterFactory testFactory = getTestFactory();
            testFactory.registerJavaTypeConverter(TestConverter.INSTANCE, Object.class);
            assertThat(testFactory.getJavaTypeRequestConverters().get(Object.class), notNullValue());
            assertThat(testFactory.getJavaTypeResponseConverters().get(Object.class), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerJavaTypeRequestConverter() method tests")
    public class RegisterJavaTypeRequestConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975536231() {
            assertNPE(() -> getTestFactory().registerJavaTypeRequestConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerJavaTypeRequestConverter(TestConverter.INSTANCE, null), "supportedJavaTypeClasses");
            assertNPE(() -> getTestFactory().registerJavaTypeRequestConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedJavaTypeClass");
        }

        @Test
        @DisplayName("Register request java type type converter")
        public void test1639975538977() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerJavaTypeRequestConverter(TestConverter.INSTANCE, OBJ_C);
            assertThat(testFactory.getJavaTypeRequestConverters().get(OBJ_C), notNullValue());
            assertThat(testFactory.getJavaTypeResponseConverters().get(OBJ_C), nullValue());
        }

    }

    @Nested
    @DisplayName("#registerJavaTypeResponseConverter() method tests")
    public class RegisterJavaTypeResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639975635302() {
            assertNPE(() -> getTestFactory().registerJavaTypeResponseConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerJavaTypeResponseConverter(TestConverter.INSTANCE, null), "supportedJavaTypeClasses");
            assertNPE(() -> getTestFactory().registerJavaTypeResponseConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedJavaTypeClass");
        }

        @Test
        @DisplayName("Register response java type converter")
        public void test1639975637486() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerJavaTypeResponseConverter(TestConverter.INSTANCE, OBJ_C);
            assertThat(testFactory.getJavaTypeRequestConverters().get(OBJ_C), nullValue());
            assertThat(testFactory.getJavaTypeResponseConverters().get(OBJ_C), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerPackageConverter() method tests")
    public class RegisterPackageConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1640471568202() {
            assertNPE(() -> getTestFactory().registerPackageConverter(null, ""), "converter");
            assertNPE(() -> getTestFactory().registerPackageConverter(TestConverter.INSTANCE, (String) null), "supportedPackageName");
        }

        @Test
        @DisplayName("Register request/response package converter for package name")
        public void test1640471562938() {
            final String packageName = OBJ_C.getPackage().getName();
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerPackageConverter(TestConverter.INSTANCE, packageName);
            assertThat(testFactory.getPackageRequestConverters().get(packageName), notNullValue());
            assertThat(testFactory.getPackageResponseConverters().get(packageName), notNullValue());
        }

        @Test
        @DisplayName("Register request/response package converter for package")
        public void test1640471571997() {
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerPackageConverter(TestConverter.INSTANCE, OBJ_C.getPackage());
            final String packageName = OBJ_C.getPackage().getName();
            assertThat(testFactory.getPackageRequestConverters().get(packageName), notNullValue());
            assertThat(testFactory.getPackageResponseConverters().get(packageName), notNullValue());
        }

        @Test
        @DisplayName("Register request/response package converter for class package")
        public void test1640471575450() {
            final String packageName = OBJ_C.getPackage().getName();
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerPackageConverter(TestConverter.INSTANCE, OBJ_C);
            assertThat(testFactory.getPackageRequestConverters().get(packageName), notNullValue());
            assertThat(testFactory.getPackageResponseConverters().get(packageName), notNullValue());
        }

    }

    @Nested
    @DisplayName("#registerPackageRequestConverter() method tests")
    public class RegisterPackageRequestConverterConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639978157481() {
            assertNPE(() -> getTestFactory().registerPackageRequestConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerPackageRequestConverter(TestConverter.INSTANCE, null), "supportedPackageNames");
            assertNPE(() -> getTestFactory().registerPackageRequestConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedPackageName");
        }

        @Test
        @DisplayName("Register request package converter")
        public void test1639978160350() {
            final String name = OBJ_C.getPackage().getName();
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerPackageRequestConverter(TestConverter.INSTANCE, name);
            assertThat(testFactory.getPackageRequestConverters().get(name), notNullValue());
            assertThat(testFactory.getPackageResponseConverters().get(name), nullValue());
        }

    }

    @Nested
    @DisplayName("#registerPackageResponseConverter() method tests")
    public class RegisterPackageResponseConverterMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1640471545821() {
            assertNPE(() -> getTestFactory().registerPackageResponseConverter(null, arrayOf()), "converter");
            assertNPE(() -> getTestFactory().registerPackageResponseConverter(TestConverter.INSTANCE, null), "supportedPackageNames");
            assertNPE(() -> getTestFactory().registerPackageResponseConverter(TestConverter.INSTANCE, arrayOf(null, null)), "supportedPackageName");
        }

        @Test
        @DisplayName("Register response package converter")
        public void test1640471548754() {
            final String name = OBJ_C.getPackage().getName();
            final ExtensionConverterFactory testFactory = getTestFactory()
                    .registerPackageResponseConverter(TestConverter.INSTANCE, name);
            assertThat(testFactory.getPackageRequestConverters().get(name), nullValue());
            assertThat(testFactory.getPackageResponseConverters().get(name), notNullValue());
        }

    }

    @Nested
    @DisplayName("#getCallMethodAnnotationRequestConverters() method tests")
    public class GetAnnotatedRequestConvertersMethodTests {

        @Test
        @DisplayName("return empty map if annotations not present")
        public void test1639065949451() {
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(AA);
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(0));
        }

        @Test
        @DisplayName("return empty map if empty Converters annotation")
        public void test1639065949460() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(0));
        }

        @Test
        @DisplayName("return not empty map if Converters annotation contains ResponseConverter (without classes)")
        public void test1639065949471() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{<any model class>=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if Converters annotation contains ResponseConverter (RawDTO.class)")
        public void test1639065949485() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if ResponseConverter annotation contains ResponseConverter (without classes)")
        public void test1639065949499() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(new Annotation[]{requestConverter});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{<any model class>=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if ResponseConverter annotation contains ResponseConverter (RawDTO.class)")
        public void test1639065949512() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationRequestConverters(new Annotation[]{requestConverter});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
        }

    }

    @Nested
    @DisplayName("#getCallMethodAnnotationResponseConverters() method tests")
    public class GetAnnotatedResponseConvertersMethodTests {

        @Test
        @DisplayName("return empty map if annotations not present")
        public void test1639065949378() {
            final Map<String, Type> convertersMap = getTestFactory().getCallMethodAnnotationResponseConverters(AA);
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(0));
        }

        @Test
        @DisplayName("return empty map if empty Converters annottion")
        public void test1639065949387() {
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationResponseConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(0));
        }

        @Test
        @DisplayName("return not empty map if Converters annotation contains ResponseConverter (without classes)")
        public void test1639065949398() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationResponseConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{<any model class>=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if Converters annotation contains ResponseConverter (RawDTO.class)")
        public void test1639065949412() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final Converters converters = BaseCoreUnitTest.getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationResponseConverters(new Annotation[]{converters});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if ResponseConverter annotation contains ResponseConverter (without classes)")
        public void test1639065949426() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationResponseConverters(new Annotation[]{responseConverter});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{<any model class>=" + TestConverter.class + "}"));
        }

        @Test
        @DisplayName("return not empty map if ResponseConverter annotation contains ResponseConverter (RawDTO.class)")
        public void test1639065949439() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final Map<String, Type> convertersMap = getTestFactory()
                    .getCallMethodAnnotationResponseConverters(new Annotation[]{responseConverter});
            assertThat("Annotation converters map ", convertersMap, notNullValue());
            assertThat("Annotation converters map size", convertersMap.size(), is(1));
            assertThat("Annotation converters map", convertersMap.toString(),
                    is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
        }

    }

    @Nested
    @DisplayName("#getSupportedConvertersInfo() method tests")
    public class GetSupportedConvertersInfoMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639980415676() {
            assertNPE(() -> getTestFactory().getSupportedConvertersInfo(null, arrayOf()), "transportEvent");
            getTestFactory().getSupportedConvertersInfo(TransportEvent.REQUEST, null);
        }

        @Test
        @DisplayName("all Converters types present (REQUEST)")
        public void test1639065949534() {
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class, OBJ_C);
            final String info = getTestFactory().getSupportedConvertersInfo(TransportEvent.REQUEST, arrayOf(requestConverter));
            assertThat("", info, is(REQUEST_CONVERTERS_INFO_WITH_ANNOTATION));
        }

        @Test
        @DisplayName("all Converters types present (RESPONSE)")
        public void test1639065949542() {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class, OBJ_C);
            final String info = getTestFactory().getSupportedConvertersInfo(TransportEvent.RESPONSE, arrayOf(responseConverter));
            assertThat("", info, is(RESPONSE_CONVERTERS_INFO_WITH_ANNOTATION));
        }

        @Test
        @DisplayName("Converters not present (REQUEST)")
        public void test1639065949550() {
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.getModelAnnotationRequestConverters().clear();
            factory.getPackageRequestConverters().clear();
            factory.getRawRequestConverters().clear();
            factory.getMimeRequestConverters().clear();
            factory.getJavaTypeRequestConverters().clear();
            final String info = factory.getSupportedConvertersInfo(TransportEvent.REQUEST, AA);
            assertThat("", info, is("SUPPORTED REQUEST CONVERTERS:\n" +
                                    "Annotated converters: <absent>\n" +
                                    "Raw converters: <absent>\n" +
                                    "Package converters: <absent>\n" +
                                    "Content type converters: <absent>\n" +
                                    "Java type converters: <absent>"));
        }

        @Test
        @DisplayName("Converters not present (RESPONSE)")
        public void test1639065949567() {
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.getModelAnnotationResponseConverters().clear();
            factory.getPackageResponseConverters().clear();
            factory.getRawResponseConverters().clear();
            factory.getMimeResponseConverters().clear();
            factory.getJavaTypeResponseConverters().clear();
            final String info = factory.getSupportedConvertersInfo(TransportEvent.RESPONSE, AA);
            assertThat("", info, is("SUPPORTED RESPONSE CONVERTERS:\n" +
                                    "Annotated converters: <absent>\n" +
                                    "Raw converters: <absent>\n" +
                                    "Package converters: <absent>\n" +
                                    "Content type converters: <absent>\n" +
                                    "Java type converters: <absent>"));
        }

        @Test
        @DisplayName("IllegalArgumentException if callStage unsupported")
        public void test1639065949584() {
            assertThrow(() -> getTestFactory().getSupportedConvertersInfo(null, AA)).assertNPE("transportEvent");
        }

    }
}
