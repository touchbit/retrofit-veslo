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

package org.touchbit.retrofit.ext.dmr.client.converter;

import internal.test.utils.RetrofitUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import internal.test.utils.client.model.pack.nested.NestedPackageDTO;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaPrimitiveTypeConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaReferenceTypeConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ByteArrayConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.FileConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.RawBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ResourceFileConverter;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Converter;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import static internal.test.utils.TestUtils.array;
import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter.softlyAsserter;
import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.REQUEST;
import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.RESPONSE;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;
import static org.touchbit.retrofit.ext.dmr.client.model.ResourceFile.resourceToString;

@SuppressWarnings({"ConstantConditions", "SameParameterValue"})
@DisplayName("ExtensionConverterFactory tests")
public class ExtensionConverterFactoryUnitTests extends BaseCoreUnitTest {

    private static final String REQUEST_CONVERTERS_INFO = resourceToString("RequestConvertersInfo.txt");
    private static final String REQUEST_CONVERTERS_INFO_WITH_ANNOTATION = resourceToString("RequestConvertersInfoWithAnnotated.txt");
    private static final String RESPONSE_CONVERTERS_INFO = resourceToString("ResponseConvertersInfo.txt");
    private static final String RESPONSE_CONVERTERS_INFO_WITH_ANNOTATION = resourceToString("ResponseConvertersInfoWithAnnotated.txt");

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
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("Request raw converters size", rawRqt.size(), is(5)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(RawBody.class), instanceOf(RawBodyConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(Byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(File.class), instanceOf(FileConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(ResourceFile.class), instanceOf(ResourceFileConverter.class)))
                .softly(() -> assertThat("Request mime converters size", mimeRqt.size(), is(0)))
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
                .softly(() -> assertThat("Response raw converter", rawRsp.get(RawBody.class), instanceOf(RawBodyConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(Byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(File.class), instanceOf(FileConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(ResourceFile.class), instanceOf(ResourceFileConverter.class)))
                .softly(() -> assertThat("Response mime converters size", mimeRsp.size(), is(0)))
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

//    @DisplayName("Successfully converting ResponseBody to String.class by Content-Type header (mime)")
//    @ParameterizedTest(name = "Content-Type: {1}")
//    @MethodSource("testProvider1637426286255")
//    public void test1637426286255(Class<String> type, ContentType contentType, String body) throws IOException {
//        final Object dto = new ExtensionConverterFactory()
//                .responseBodyConverter(type, getContentTypeHeaderAnnotations(contentType), RTF)
//                .convert(ResponseBody.create(contentType.getMediaType(), body));
//        assertThat("ResponseBody", dto, is(body));
//    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class if body == IDualResponse<String, String>")
    public void test1639065949011() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(DUAL_RESPONSE_STRING_TYPE, getContentTypeHeaderAnnotations(TEXT_PLAIN), RTF)
                .convert(ResponseBody.create(TEXT_PLAIN.getMediaType(), "test1637431635085"));
        assertThat("ResponseBody", dto, is("test1637431635085"));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class if body == null and CT=NULL")
    public void test1639065949020() throws IOException {
        final Object result = new ExtensionConverterFactory()
                .responseBodyConverter(String.class, getContentTypeHeaderAnnotations(NULL), RTF)
                .convert(null);
        assertThat("ResponseBody", result, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw)")
    public void test1639065949029() throws IOException {
        RawBody expected = new RawBody("test1637429237836");
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, array(), null)
                .convert(ResponseBody.create(null, expected.string()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw)")
    public void test1639065949039() throws IOException {
        final String body = "test1637429413684";
        Byte[] expected = Utils.toObjectByteArray(body.getBytes());
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, array(), null)
                .convert(ResponseBody.create(null, body));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw)")
    public void test1639065949050() throws Exception {
        final String body = "test1637429592556";
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(File.class, array(), null)
                .convert(ResponseBody.create(null, body));
        assertThat("ResponseBody", dto, instanceOf(File.class));
        File file = (File) dto;
        final byte[] bytes = Files.readAllBytes(file.toPath());
        assertThat("File", new String(bytes), is(body));
    }

    @Test
    @DisplayName("ConvertCallException at converting ResponseBody to ResourceFile.class (raw)")
    public void test1639065949063() {
        final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                .responseBodyConverter(ResourceFile.class, array(), RTF)
                .convert(ResponseBody.create(null, "test1637429829752"));
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageContains("It is forbidden to use the ResourceFile type to convert the response body.");
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class")
    public void test1639065949074() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(String.class, array(), RTF)
                .convert(ResponseBody.create(null, "test1637430137761"));
        assertThat("ResponseBody", dto, is("test1637430137761"));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw) if body == null (expected AnyBody)")
    public void test1639065949083() throws IOException {
        RawBody expected = new RawBody((byte[]) null);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw) if body == null (expected null)")
    public void test1639065949093() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(File.class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw) if body == null (expected null)")
    public void test1639065949102() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->RawDTO using ResponseConverter annotation with RawDTO.class")
    public void test1639065949111() throws IOException {
        TestDTO expected = new TestDTO("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(TestDTO.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.data()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->AnyBody using ResponseConverter annotation with RawDTO.class")
    public void test1639065949122() throws IOException {
        RawBody expected = new RawBody("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.bytes()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->PackDTO using package converter")
    public void test1639065949133() throws IOException {
        PackageDTO expected = new PackageDTO("test1637679684417");
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.registerPackageResponseConverter(new TestPackageConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final Object dto = factory.responseBodyConverter(PackageDTO.class, array(), RTF)
                .convert(ResponseBody.create(null, expected.data()));
        assertThat("PackDTO", dto, is(expected));
    }

    @Test
    @DisplayName("#registerRawRequestConverter() NPE for the 'converter' parameter.")
    public void test1639065949144() {
        assertThrow(() -> new ExtensionConverterFactory().registerRawRequestConverter(null, OBJ_C))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#registerRawRequestConverter() NPE for the 'supportedRawClass' parameter.")
    public void test1639065949151() {
        assertThrow(() -> new ExtensionConverterFactory().registerRawRequestConverter(new RawBodyConverter(), new Class[]{null}))
                .assertNPE("supportedRawClass");
    }

    @Test
    @DisplayName("#registerMimeRequestConverter() NPE for the 'converter' parameter.")
    public void test1639065949158() {
        assertThrow(() -> new ExtensionConverterFactory().registerMimeRequestConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#registerMimeRequestConverter() NPE for the 'supportedContentType' parameter.")
    public void test1639065949165() {
        assertThrow(() -> new ExtensionConverterFactory().registerMimeRequestConverter(new RawBodyConverter(), new ContentType[]{null}))
                .assertNPE("supportedContentType");
    }

    @Test
    @DisplayName("#registerRawResponseConverter() NPE for the 'converter' parameter.")
    public void test1639065949172() {
        assertThrow(() -> new ExtensionConverterFactory().registerRawResponseConverter(null, OBJ_C))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#registerRawResponseConverter() NPE for the 'supportedRawClass' parameter.")
    public void test1639065949179() {
        assertThrow(() -> new ExtensionConverterFactory().registerRawResponseConverter(new RawBodyConverter(), new Class[]{null}))
                .assertNPE("supportedRawClass");
    }

    @Test
    @DisplayName("#registerMimeResponseConverter() NPE for the 'converter' parameter.")
    public void test1639065949186() {
        assertThrow(() -> new ExtensionConverterFactory().registerMimeResponseConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#registerMimeResponseConverter() NPE for the 'supportedContentType' parameter.")
    public void test1639065949193() {
        assertThrow(() -> new ExtensionConverterFactory().registerMimeResponseConverter(new RawBodyConverter(), new ContentType[]{null}))
                .assertNPE("supportedContentType");
    }

    @Test
    @DisplayName("#newInstance() successful return new instance if TestConverter.class")
    public void test1639065949200() {
        final ExtensionConverter<?> converter = TEST_FACTORY.newInstance(TestConverter.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class is null")
    public void test1639065949208() {
        assertThrow(() -> TEST_FACTORY.newInstance(null)).assertNPE("converterClass");
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class has privet constructor")
    public void test1639065949214() {
        assertThrow(() -> TEST_FACTORY.newInstance(PrivateConstructorConverter.class))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("" +
                        "Unable to create new instance of " + PrivateConstructorConverter.class + "\n" +
                        "See details below.")
                .assertCause(cause -> cause
                        .assertClass(IllegalAccessException.class)
                        .assertMessageContains(
                                "org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory ",
                                "not access a member of " + PrivateConstructorConverter.class,
                                "with modifiers \"private\"")
                );
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes not specified")
    public void test1639065949231() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(responseConverter, TestDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes specified (RawDTO.class)")
    public void test1639065949240() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(requestConverter, TestDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return null if converted classes incompatible")
    public void test1639065949249() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(responseConverter, OBJ_C);
        assertThat("TestConverter", converter, nullValue());
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if converter=null")
    public void test1639065949257() {
        assertThrow(() -> TEST_FACTORY.getExtensionConverter(null, OBJ_C)).assertNPE("annotation");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if bodyType=null")
    public void test1639065949263() {
        final RequestConverter converter = getRequestConverter(TestConverter.class, TestDTO.class);
        assertThrow(() -> TEST_FACTORY.getExtensionConverter(converter, null)).assertNPE("bodyType");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if unsupported annotation")
    public void test1639065949270() {
        final Nullable nullable = new Nullable() {
            public Class<? extends Annotation> annotationType() {
                return Nullable.class;
            }
        };
        assertThrow(() -> TEST_FACTORY.getExtensionConverter(nullable, OBJ_C))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Received an unsupported annotation type: " + nullable.getClass());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if ResponseConverter annotation has processed class")
    public void test1639065949284() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(TestDTO.class, new Annotation[]{responseConverter}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }


    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if ResponseConverter annotation has not processed class")
    public void test1639065949295() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, Objects.class);
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(TestDTO.class, new Annotation[]{responseConverter}, null);
        assertThat("Converter", converter, nullValue());
    }


    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if Converters annotation has not processed class")
    public void test1639065949306() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class, Objects.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(TestDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @ResponseConverter)")
    public void test1639065949319() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = TEST_FACTORY
                .getResponseConverterFromAnnotation(OBJ_C, array(converters), null);
        assertThat("Converter", responseConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return converter if Converters annotation does not contain classes for conversion")
    public void test1639065949329() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(TestDTO.class, array(converters), null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return converter if Converters annotation has RawDTO.class")
    public void test1639065949342() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class, TestDTO.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(TestDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if annotations not present")
    public void test1639065949355() {
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = TEST_FACTORY
                .getResponseConverterFromAnnotation(OBJ_C, array(), null);
        assertThat("Converter", responseConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() ConvertCallException thrown if " +
            "ResponseConverter and Converters obtained at the same time")
    public void test1639065949364() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Annotation[] annotations = {converters, responseConverter};
        assertThrow(() -> TEST_FACTORY.getResponseConverterFromAnnotation(OBJ_C, annotations, null))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("API method contains concurrent annotations.\n" +
                        "Use only one of:\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.Converters");
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return empty map if annotations not present")
    public void test1639065949378() {
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(array());
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return empty map if empty Converters annottion")
    public void test1639065949387() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (without classes)")
    public void test1639065949398() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Converters converters = getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (RawDTO.class)")
    public void test1639065949412() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final Converters converters = getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (without classes)")
    public void test1639065949426() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{responseConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (RawDTO.class)")
    public void test1639065949439() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{responseConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if annotations not present")
    public void test1639065949451() {
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(array());
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if empty Converters annotation")
    public void test1639065949460() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (without classes)")
    public void test1639065949471() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (RawDTO.class)")
    public void test1639065949485() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (without classes)")
    public void test1639065949499() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{requestConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if ResponseConverter annotation contains ResponseConverter (RawDTO.class)")
    public void test1639065949512() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
        final Map<String, Type> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{requestConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + TestDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#toString()")
    public void test1639065949524() {
        final String result = TEST_FACTORY.toString();
        assertThat("", result, is("" +
                "Converter factory: class org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest$TestsExtensionConverterFactory" +
                "\n\n" + REQUEST_CONVERTERS_INFO +
                "\n\n" + RESPONSE_CONVERTERS_INFO));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (REQUEST)")
    public void test1639065949534() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class, OBJ_C);
        final String info = TEST_FACTORY.getSupportedConvertersInfo(REQUEST, array(requestConverter));
        assertThat("", info, is(REQUEST_CONVERTERS_INFO_WITH_ANNOTATION));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (RESPONSE)")
    public void test1639065949542() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class, OBJ_C);
        final String info = TEST_FACTORY.getSupportedConvertersInfo(RESPONSE, array(responseConverter));
        assertThat("", info, is(RESPONSE_CONVERTERS_INFO_WITH_ANNOTATION));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() Converters not present (REQUEST)")
    public void test1639065949550() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.getPackageRequestConverters().clear();
        factory.getRawRequestConverters().clear();
        factory.getMimeRequestConverters().clear();
        factory.getJavaTypeRequestConverters().clear();
        final String info = factory.getSupportedConvertersInfo(REQUEST, array());
        assertThat("", info, is("SUPPORTED REQUEST CONVERTERS:\n" +
                "Annotated converters: <absent>\n" +
                "Raw converters: <absent>\n" +
                "Package converters: <absent>\n" +
                "Content type converters: <absent>\n" +
                "Java type converters: <absent>"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() Converters not present (RESPONSE)")
    public void test1639065949567() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.getPackageResponseConverters().clear();
        factory.getRawResponseConverters().clear();
        factory.getMimeResponseConverters().clear();
        factory.getJavaTypeResponseConverters().clear();
        final String info = factory.getSupportedConvertersInfo(RESPONSE, array());
        assertThat("", info, is("SUPPORTED RESPONSE CONVERTERS:\n" +
                "Annotated converters: <absent>\n" +
                "Raw converters: <absent>\n" +
                "Package converters: <absent>\n" +
                "Content type converters: <absent>\n" +
                "Java type converters: <absent>"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() IllegalArgumentException if callStage unsupported")
    public void test1639065949584() {
        assertThrow(() -> TEST_FACTORY.getSupportedConvertersInfo(null, array())).assertNPE("transportEvent");
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has processed class")
    public void test1639065949591() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(TestDTO.class, array(), array(requestConverter), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has not processed class")
    public void test1639065949601() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, Objects.class);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(TestDTO.class, array(), array(requestConverter), null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if Converters annotation has not processed class")
    public void test1639065949611() {
        final RequestConverter[] requestConverters = new RequestConverter[]{
                getRequestConverter(TestConverter.class, Objects.class)
        };
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(TestDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @RequestConverter)")
    public void test1639065949624() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final RequestBodyConverter requestConverterFromAnnotation = TEST_FACTORY
                .getRequestConverterFromAnnotation(OBJ_C, array(), array(converters), null);
        assertThat("Converter", requestConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation does not contain classes for conversion")
    public void test1639065949634() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(TestDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation has RawDTO.class")
    public void test1639065949645() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class, TestDTO.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(TestDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() ConvertCallException thrown if " +
            "RequestConverter and Converters obtained at the same time")
    public void test1639065949656() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
        final Annotation[] annotations = {converters, requestConverter};
        assertThrow(() -> TEST_FACTORY.getRequestConverterFromAnnotation(OBJ_C, array(), annotations, null))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("API method contains concurrent annotations.\n" +
                        "Use only one of:\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.Converters");
    }

    @Test
    @DisplayName("#registerPackageRequestConverter() Exception thrown if invalid package name")
    public void test1639065949670() {
        assertThrow(() -> TEST_FACTORY.registerPackageRequestConverter(new TestConverter(), "test1637674138465;"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Invalid package name: test1637674138465;");
    }

    @Test
    @DisplayName("#registerPackageResponseConverter() Exception thrown if invalid package name")
    public void test1639065949678() {
        assertThrow(() -> TEST_FACTORY.registerPackageResponseConverter(new TestConverter(), "test1637674262502;"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Invalid package name: test1637674262502;");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if bodyType = null")
    public void test1639065949686() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(null, array(), RTF)).assertNPE("bodyType");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if methodAnnotations = null")
    public void test1639065949692() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(OBJ_C, null, RTF)).assertNPE("methodAnnotations");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if retrofit = null")
    public void test1639065949698() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(OBJ_C, array(), null)).assertNPE("retrofit");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() if used package model return converter")
    public void test1639065949704() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.registerPackageResponseConverter(new TestPackageConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final Converter<ResponseBody, ?> converter = factory
                .getPackageResponseConverter(PackageDTO.class, array(), RTF);
        assertThat("", converter, notNullValue());
        assertThat("", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getPackageResponseConverter() if the model is out of the package return null")
    public void test1639065949715() {
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getPackageResponseConverter(NestedPackageDTO.class, array(), RTF);
        assertThat("", converter, nullValue());
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if bodyClass = null")
    public void test1639065949723() {
        assertNPE(() -> TEST_FACTORY.getPackageRequestConverter(null, array(), array(), RTF), "bodyClass");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if parameterAnnotations = null")
    public void test1639065949730() {
        assertNPE(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, null, array(), RTF), "parameterAnnotations");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if methodAnnotations = null")
    public void test1639065949737() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, array(), null, RTF))
                .assertNPE("methodAnnotations");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if retrofit = null")
    public void test1639065949744() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, array(), array(), null))
                .assertNPE("retrofit");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() get converter by supported package model")
    public void test1639065949751() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.registerPackageRequestConverter(new TestPackageConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final RequestBodyConverter converter = factory
                .getPackageRequestConverter(PackageDTO.class, array(), array(), RTF);
        assertThat("", converter, notNullValue());
    }

    @Test
    @DisplayName("#getPackageRequestConverter() get converter by unsupported package model")
    public void test1639065949761() {
        final RequestBodyConverter converter = TEST_FACTORY
                .getPackageRequestConverter(NestedPackageDTO.class, array(), array(), RTF);
        assertThat("", converter, nullValue());

    }

    private Annotation[] getContentTypeHeaderAnnotations(ContentType value) {
        return getContentTypeHeaderAnnotations(value.toString());
    }

    private Annotation[] getContentTypeHeaderAnnotations(String value) {
        return RetrofitUtils.getCallMethodAnnotations("Content-Type: " + value);
    }

}