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

import internal.test.utils.OkHttpUtils;
import internal.test.utils.RetrofitUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.*;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterNotFoundException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import org.touchbit.retrofit.ext.test.PackConverter;
import org.touchbit.retrofit.ext.test.PrivateConverter;
import org.touchbit.retrofit.ext.test.TestConverter;
import org.touchbit.retrofit.ext.test.TestsExtensionConverterFactory;
import org.touchbit.retrofit.ext.test.model.RawDTO;
import org.touchbit.retrofit.ext.test.model.pack.PackDTO;
import org.touchbit.retrofit.ext.test.model.pack.nested.NestedPackDTO;
import retrofit2.Converter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static internal.test.utils.TestUtils.array;
import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter.softlyAsserter;
import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.REQUEST;
import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.RESPONSE;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;

@SuppressWarnings({"ConstantConditions", "rawtypes", "SameParameterValue"})
@DisplayName("ExtensionConverterFactory tests")
public class ExtensionConverterFactoryUnitTests {

    private static final TestsExtensionConverterFactory TEST_FACTORY = new TestsExtensionConverterFactory();
    private static final Retrofit RTF = RetrofitUtils.retrofit();
    private static final Class<Object> OBJ_C = Object.class;
    private static final String TEST_FACTORY_REQUEST_INFO = "SUPPORTED REQUEST CONVERTERS:\n" +
            "Annotated converters: <absent>\n" +
            "Raw converters:\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ByteArrayConverter\n" +
            "    class java.lang.Byte[]\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.FileConverter\n" +
            "    class java.io.File\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.RawBodyConverter\n" +
            "    class org.touchbit.retrofit.ext.dmr.client.model.RawBody\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ResourceFileConverter\n" +
            "    class org.touchbit.retrofit.ext.dmr.client.model.ResourceFile\n" +
            "Package converters:\n" +
            "org.touchbit.retrofit.ext.test.PackConverter\n" +
            "    org.touchbit.retrofit.ext.test.model.pack\n" +
            "Content type converters:\n" +
            "org.touchbit.retrofit.ext.test.TestConverter\n" +
            "    text/plain\n" +
            "Java type converters:\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaPrimitiveTypeConverter\n" +
            "    boolean\n" +
            "    byte\n" +
            "    char\n" +
            "    double\n" +
            "    float\n" +
            "    int\n" +
            "    long\n" +
            "    short\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaReferenceTypeConverter\n" +
            "    java.lang.Boolean\n" +
            "    java.lang.Byte\n" +
            "    java.lang.Character\n" +
            "    java.lang.Double\n" +
            "    java.lang.Float\n" +
            "    java.lang.Integer\n" +
            "    java.lang.Long\n" +
            "    java.lang.Short\n" +
            "    java.lang.String";

    private static final String TEST_FACTORY_RESPONSE_INFO = "SUPPORTED RESPONSE CONVERTERS:\n" +
            "Annotated converters: <absent>\n" +
            "Raw converters:\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ByteArrayConverter\n" +
            "    class java.lang.Byte[]\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.FileConverter\n" +
            "    class java.io.File\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.RawBodyConverter\n" +
            "    class org.touchbit.retrofit.ext.dmr.client.model.RawBody\n" +
            "Package converters:\n" +
            "org.touchbit.retrofit.ext.test.PackConverter\n" +
            "    org.touchbit.retrofit.ext.test.model.pack\n" +
            "Content type converters:\n" +
            "org.touchbit.retrofit.ext.test.TestConverter\n" +
            "    text/plain\n" +
            "Java type converters:\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaPrimitiveTypeConverter\n" +
            "    boolean\n" +
            "    byte\n" +
            "    char\n" +
            "    double\n" +
            "    float\n" +
            "    int\n" +
            "    long\n" +
            "    short\n" +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaReferenceTypeConverter\n" +
            "    java.lang.Boolean\n" +
            "    java.lang.Byte\n" +
            "    java.lang.Character\n" +
            "    java.lang.Double\n" +
            "    java.lang.Float\n" +
            "    java.lang.Integer\n" +
            "    java.lang.Long\n" +
            "    java.lang.Short\n" +
            "    java.lang.String";

    private static Stream<Arguments> testProvider1637422599548() {
        return Stream.of(
                Arguments.of(String.class, TEXT_PLAIN.toString(), randomUUID().toString(), TEXT_PLAIN_UTF8.getMediaType()),
                Arguments.of(String.class, TEXT_PLAIN_UTF8.toString(), randomUUID().toString(), TEXT_PLAIN_UTF8.getMediaType()),
                Arguments.of(String.class, TEXT_HTML.toString(), randomUUID().toString(), TEXT_HTML_UTF8.getMediaType()),
                Arguments.of(String.class, TEXT_HTML_UTF8.toString(), randomUUID().toString(), TEXT_HTML_UTF8.getMediaType())
        );
    }

    private static Stream<Arguments> testProvider1637426286255() {
        return Stream.of(
                Arguments.of(String.class, TEXT_PLAIN, randomUUID().toString()),
                Arguments.of(String.class, TEXT_PLAIN_UTF8, randomUUID().toString()),
                Arguments.of(String.class, TEXT_HTML, randomUUID().toString()),
                Arguments.of(String.class, TEXT_HTML_UTF8, randomUUID().toString()),
                Arguments.of(String.class, APP_FORM_URLENCODED, randomUUID().toString()),
                Arguments.of(String.class, APP_FORM_URLENCODED_UTF8, randomUUID().toString())
        );
    }

    @Test
    @DisplayName("Default converters initialization")
    public void test1637421751464() {
        ExtensionConverterFactory factory = new ExtensionConverterFactory();
        final Map<Class<?>, ExtensionConverter<?>> rawRqt = factory.getRawRequestConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRqt = factory.getMimeRequestConverters();
        final Map<Class<?>, ExtensionConverter<?>> javaTypeRqt = factory.getJavaTypeRequestConverters();
        final Map<Class<?>, ExtensionConverter<?>> rawRsp = factory.getRawResponseConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRsp = factory.getMimeResponseConverters();
        final Map<Class<?>, ExtensionConverter<?>> javaTypeRsp = factory.getJavaTypeResponseConverters();
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("Request raw converters size", rawRqt.size(), is(4)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(RawBody.class), instanceOf(RawBodyConverter.class)))
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
                .softly(() -> assertThat("Response raw converters size", rawRsp.size(), is(3)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(RawBody.class), instanceOf(RawBodyConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(Byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(File.class), instanceOf(FileConverter.class)))
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

    @DisplayName("Successfully converting String.class to RequestBody by Content-Type header (mime)")
    @ParameterizedTest(name = "Content-Type: {1}")
    @MethodSource("testProvider1637422599548")
    public void test1637422599548(Class<String> type, String cT, Object body, MediaType expMT) throws IOException {
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(type, array(), getContentTypeHeaderAnnotations(cT), RTF)
                .convert(body);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.contentType()", requestBody.contentType(), is(expMT));
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is(body));
    }

    @Test
    @DisplayName("Successfully converting AnyBody.class to RequestBody by type (raw)")
    public void test1637428451229() throws IOException {
        final RawBody expected = new RawBody("test1637428451229");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(RawBody.class, array(), array(), null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428451229"));
    }

    @Test
    @DisplayName("Successfully converting Byte[].class to RequestBody by type (raw)")
    public void test1637428566604() throws IOException {
        final Byte[] expected = Utils.toObjectByteArray("test1637428566604".getBytes());
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(RawBody.class, array(), array(), null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428566604"));
    }

    @Test
    @DisplayName("Successfully converting File.class to RequestBody by type (raw)")
    public void test1637428660061() throws IOException {
        final File file = new File("src/test/resources/test/data/test1637428660061.txt");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(RawBody.class, array(), array(), null)
                .convert(file);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428660061"));
    }

    @Test
    @DisplayName("Successfully converting ResourceFile.class to RequestBody by type (raw)")
    public void test1637428785169() throws IOException {
        final ResourceFile file = new ResourceFile("test/data/test1637428785169.txt");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(RawBody.class, array(), array(), null)
                .convert(file);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428785169"));
    }

    @Test
    @DisplayName("Successfully converting String->RequestBody using java type Converter")
    public void test1637430252094() throws IOException {
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(RawBody.class, array(), array(), RTF)
                .convert("test1637430252094");
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637430252094"));
    }

    @Test
    @DisplayName("Successfully converting RawDTO->RequestBody using RequestConverter annotation with RawDTO.class")
    public void test1637670751294() throws IOException {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final RequestBody dto = new ExtensionConverterFactory()
                .requestBodyConverter(RawDTO.class, array(), array(requestConverter), RTF)
                .convert(new RawDTO("test1637670751294"));
        assertThat("RequestBody", dto, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(dto), is("test1637670751294"));
    }

    @Test
    @DisplayName("Successfully converting AnyBody->RequestBody using RequestConverter annotation with RawDTO.class")
    public void test1637670825343() throws IOException {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final RequestBody dto = TEST_FACTORY.requestBodyConverter(RawBody.class, array(), array(responseConverter), RTF)
                .convert(new RawBody("test1637641171302"));
        assertThat("RequestBody", dto, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(dto), is("test1637641171302"));
    }

    @Test
    @DisplayName("Successfully converting PackDTO->RequestBody using package converter")
    public void test1637687959905() throws IOException {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageRequestConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final RequestBody dto =  factory
                .requestBodyConverter(RawBody.class, array(), array(), RTF)
                .convert(new PackDTO("test1637687959905"));
        assertThat("RequestBody", dto, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(dto), is("test1637687959905"));
    }

    @DisplayName("Successfully converting ResponseBody to String.class by Content-Type header (mime)")
    @ParameterizedTest(name = "Content-Type: {1}")
    @MethodSource("testProvider1637426286255")
    public void test1637426286255(Class<String> type, ContentType contentType, String body) throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(type, getContentTypeHeaderAnnotations(contentType), RTF)
                .convert(ResponseBody.create(contentType.getMediaType(), body));
        assertThat("ResponseBody", dto, is(body));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class if body == IDualResponse<String, String>")
    public void test1637431635085() throws IOException {
        final Type type = TestClient.getStringGenericReturnType();
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(type, getContentTypeHeaderAnnotations(TEXT_PLAIN), RTF)
                .convert(ResponseBody.create(TEXT_PLAIN.getMediaType(), "test1637431635085"));
        assertThat("ResponseBody", dto, is("test1637431635085"));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class if body == null and CT=NULL")
    public void test1637431342933() throws IOException {
        final Object result = new ExtensionConverterFactory()
                .responseBodyConverter(String.class, getContentTypeHeaderAnnotations(NULL), RTF)
                .convert(null);
        assertThat("ResponseBody", result, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw)")
    public void test1637429237836() throws IOException {
        RawBody expected = new RawBody("test1637429237836");
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, array(), null)
                .convert(ResponseBody.create(null, expected.string()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw)")
    public void test1637429413684() throws IOException {
        final String body = "test1637429413684";
        Byte[] expected = Utils.toObjectByteArray(body.getBytes());
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, array(), null)
                .convert(ResponseBody.create(null, body));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw)")
    public void test1637429592556() throws Exception {
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
    public void test1637429829752() {
        final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                .responseBodyConverter(ResourceFile.class, array(), RTF)
                .convert(ResponseBody.create(null, "test1637429829752"));
        assertThrow(runnable)
                .assertClass(ConverterNotFoundException.class)
                .assertMessageContains("Converter not found");
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class")
    public void test1637430137761() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(String.class, array(), RTF)
                .convert(ResponseBody.create(null, "test1637430137761"));
        assertThat("ResponseBody", dto, is("test1637430137761"));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw) if body == null (expected AnyBody)")
    public void test1637430944283() throws IOException {
        RawBody expected = new RawBody((byte[]) null);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw) if body == null (expected null)")
    public void test1637431055137() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(File.class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw) if body == null (expected null)")
    public void test1637431161233() throws IOException {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, array(), RTF)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->RawDTO using ResponseConverter annotation with RawDTO.class")
    public void test1637569961286() throws IOException {
        RawDTO expected = new RawDTO("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawDTO.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.data()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->AnyBody using ResponseConverter annotation with RawDTO.class")
    public void test1637641171302() throws IOException {
        RawBody expected = new RawBody("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawBody.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.bytes()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->PackDTO using package converter")
    public void test1637679684417() throws IOException {
        PackDTO expected = new PackDTO("test1637679684417");
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageResponseConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final Object dto = factory.responseBodyConverter(PackDTO.class, array(), RTF)
                .convert(ResponseBody.create(null, expected.data()));
        assertThat("PackDTO", dto, is(expected));
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'converter' parameter.")
    public void test1637431872063() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(null, OBJ_C))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'supportedRawClass' parameter.")
    public void test1637432122823() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(new RawBodyConverter(), new Class[]{null}))
                .assertNPE("supportedRawClass");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'converter' parameter.")
    public void test1637432216675() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'supportedContentType' parameter.")
    public void test1637432248690() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(new RawBodyConverter(), new ContentType[]{null}))
                .assertNPE("supportedContentType");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432305518() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(null, OBJ_C))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'supportedRawClass' parameter.")
    public void test1637432309181() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(new RawBodyConverter(), new Class[]{null}))
                .assertNPE("supportedRawClass");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432312342() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'supportedContentType' parameter.")
    public void test1637432315305() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(new RawBodyConverter(), new ContentType[]{null}))
                .assertNPE("supportedContentType");
    }

    @Test
    @DisplayName("#newInstance() successful return new instance if TestConverter.class")
    public void test1637647202884() {
        final ExtensionConverter<?> converter = TEST_FACTORY.newInstance(TestConverter.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class is null")
    public void test1637647341032() {
        assertThrow(() -> TEST_FACTORY.newInstance(null)).assertNPE("converterClass");
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class has privet constructor")
    public void test1637647541211() {
        assertThrow(() -> TEST_FACTORY.newInstance(PrivateConverter.class))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("" +
                        "Unable to create new instance of class org.touchbit.retrofit.ext.test.PrivateConverter\n" +
                        "See details below.")
                .assertCause(cause -> cause
                        .assertClass(IllegalAccessException.class)
                        .assertMessageContains(
                                "org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory ",
                                "not access a member of class org.touchbit.retrofit.ext.test.PrivateConverter",
                                "with modifiers \"private\"")
                );
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes not specified")
    public void test1637647704141() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(responseConverter, RawDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes specified (RawDTO.class)")
    public void test1637648856550() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(requestConverter, RawDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return null if converted classes incompatible")
    public void test1637648901620() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final ExtensionConverter<?> converter = TEST_FACTORY.getExtensionConverter(responseConverter, OBJ_C);
        assertThat("TestConverter", converter, nullValue());
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if converter=null")
    public void test1637649395407() {
        assertThrow(() -> TEST_FACTORY.getExtensionConverter(null, OBJ_C)).assertNPE("annotation");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if bodyClass=null")
    public void test1637649414228() {
        final RequestConverter converter = getRequestConverter(TestConverter.class, RawDTO.class);
        assertThrow(() -> TEST_FACTORY.getExtensionConverter(converter, null)).assertNPE("bodyClass");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if unsupported annotation")
    public void test1637669472817() {
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
    public void test1637663426755() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{responseConverter}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }


    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if ResponseConverter annotation has not processed class")
    public void test1637663216813() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, Objects.class);
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{responseConverter}, null);
        assertThat("Converter", converter, nullValue());
    }


    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if Converters annotation has not processed class")
    public void test1637663040441() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class, Objects.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @ResponseConverter)")
    public void test1637641699864() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = TEST_FACTORY
                .getResponseConverterFromAnnotation(OBJ_C, array(converters), null);
        assertThat("Converter", responseConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return converter if Converters annotation does not contain classes for conversion")
    public void test1637641271823() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, array(converters), null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return converter if Converters annotation has RawDTO.class")
    public void test1637643574390() {
        final ResponseConverter[] responseConverters = new ResponseConverter[]{
                getResponseConverter(TestConverter.class, RawDTO.class)
        };
        final Converters converters = getConverters(responseConverters, new RequestConverter[]{});
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if annotations not present")
    public void test1637661882814() {
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = TEST_FACTORY
                .getResponseConverterFromAnnotation(OBJ_C, array(), null);
        assertThat("Converter", responseConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() ConvertCallException thrown if " +
            "ResponseConverter and Converters obtained at the same time")
    public void test1637662013770() {
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
    public void test1637645424991() {
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(array());
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return empty map if empty Converters annottion")
    public void test1637645605023() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (without classes)")
    public void test1637645675279() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Converters converters = getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (RawDTO.class)")
    public void test1637645920175() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Converters converters = getConverters(new ResponseConverter[]{responseConverter}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (without classes)")
    public void test1637646119589() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{responseConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (RawDTO.class)")
    public void test1637646163150() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{responseConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if annotations not present")
    public void test1637646367646() {
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(array());
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if empty Converters annotation")
    public void test1637646381640() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (without classes)")
    public void test1637646459376() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if Converters annottion contains ResponseConverter (RawDTO.class)")
    public void test1637646481357() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{requestConverter});
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{converters});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (without classes)")
    public void test1637646497084() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class);
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{requestConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{<any model class>=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return not empty map " +
            "if ResponseConverter annottion contains ResponseConverter (RawDTO.class)")
    public void test1637646536281() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final Map<String, Class<?>> convertersMap = TEST_FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{requestConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#toString()")
    public void test1637663604069() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageRequestConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        factory.addPackageResponseConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final String result = factory.toString();
        assertThat("", result, is("" +
                "Converter factory: class org.touchbit.retrofit.ext.test.TestsExtensionConverterFactory" +
                "\n\n" + TEST_FACTORY_REQUEST_INFO +
                "\n\n" + TEST_FACTORY_RESPONSE_INFO));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (RESPONSE)")
    public void test1637663823967() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageRequestConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        factory.addPackageResponseConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final String info = factory.getSupportedConvertersInfo(RESPONSE, array(responseConverter));
        assertThat("", info, is(TEST_FACTORY_RESPONSE_INFO.replace("Annotated converters: <absent>\n",
                "Annotated converters:\n" +
                        TestConverter.class.getTypeName() +
                        "\n    " + RawDTO.class.getTypeName() + "\n")));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (REQUEST)")
    public void test1637664434980() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class, OBJ_C);
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageRequestConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        factory.addPackageResponseConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final String info = factory.getSupportedConvertersInfo(REQUEST, array(requestConverter));
        assertThat("", info, is(TEST_FACTORY_REQUEST_INFO.replace("Annotated converters: <absent>\n",
                "Annotated converters:\n" +
                        TestConverter.class.getTypeName() +
                        "\n    " + OBJ_C.getTypeName() +
                        "\n    " + RawDTO.class.getTypeName() + "\n")));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() Converters not present (REQUEST)")
    public void test1637665351779() {
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
    public void test1637665499864() {
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
    public void test1637665542849() {
        assertThrow(() -> TEST_FACTORY.getSupportedConvertersInfo(null, array())).assertNPE("transportEvent");
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has processed class")
    public void test1637670097335() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(requestConverter), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has not processed class")
    public void test1637670057963() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, Objects.class);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(requestConverter), null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if Converters annotation has not processed class")
    public void test1637669851050() {
        final RequestConverter[] requestConverters = new RequestConverter[]{
                getRequestConverter(TestConverter.class, Objects.class)
        };
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @RequestConverter)")
    public void test1637670150695() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final RequestBodyConverter requestConverterFromAnnotation = TEST_FACTORY
                .getRequestConverterFromAnnotation(OBJ_C, array(), array(converters), null);
        assertThat("Converter", requestConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation does not contain classes for conversion")
    public void test1637670201642() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation has RawDTO.class")
    public void test1637670239992() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class, RawDTO.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = TEST_FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() ConvertCallException thrown if " +
            "RequestConverter and Converters obtained at the same time")
    public void test1637670290990() {
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
    @DisplayName("#addPackageRequestConverter() Exception thrown if invalid package name")
    public void test1637674138465() {
        assertThrow(() -> TEST_FACTORY.addPackageRequestConverter(new TestConverter(), "test1637674138465;"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Invalid package name: test1637674138465;");
    }

    @Test
    @DisplayName("#addPackageResponseConverter() Exception thrown if invalid package name")
    public void test1637674262502() {
        assertThrow(() -> TEST_FACTORY.addPackageResponseConverter(new TestConverter(), "test1637674262502;"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Invalid package name: test1637674262502;");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if bodyClass = null")
    public void test1637678506954() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(null, array(), RTF)).assertNPE("bodyClass");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if methodAnnotations = null")
    public void test1637678754965() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(OBJ_C, null, RTF)).assertNPE("methodAnnotations");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() Exception thrown if retrofit = null")
    public void test1637678781984() {
        assertThrow(() -> TEST_FACTORY.getPackageResponseConverter(OBJ_C, array(), null)).assertNPE("retrofit");
    }

    @Test
    @DisplayName("#getPackageResponseConverter() if used package model return converter")
    public void test1637678810491() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageResponseConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final Converter<ResponseBody, ?> converter = factory
                .getPackageResponseConverter(PackDTO.class, array(), RTF);
        assertThat("", converter, notNullValue());
        assertThat("", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getPackageResponseConverter() if the model is out of the package return null")
    public void test1637679190494() {
        final Converter<ResponseBody, ?> converter = TEST_FACTORY
                .getPackageResponseConverter(NestedPackDTO.class, array(), RTF);
        assertThat("", converter, nullValue());
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if bodyClass = null")
    public void test1637687324412() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(null, array(), array(), RTF))
                .assertNPE("bodyClass");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if parameterAnnotations = null")
    public void test1637687524356() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, null, array(), RTF))
                .assertNPE("parameterAnnotations");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if methodAnnotations = null")
    public void test1637687530741() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, array(), null, RTF))
                .assertNPE("methodAnnotations");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() Exception thrown if retrofit = null")
    public void test1637687537421() {
        assertThrow(() -> TEST_FACTORY.getPackageRequestConverter(OBJ_C, array(), array(), null))
                .assertNPE("retrofit");
    }

    @Test
    @DisplayName("#getPackageRequestConverter() get converter by supported package model")
    public void test1637687600834() {
        final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
        factory.addPackageRequestConverter(new PackConverter(), "org.touchbit.retrofit.ext.test.model.pack");
        final RequestBodyConverter converter = factory
                .getPackageRequestConverter(PackDTO.class, array(), array(), RTF);
        assertThat("", converter, notNullValue());
    }

    @Test
    @DisplayName("#getPackageRequestConverter() get converter by unsupported package model")
    public void test1637687751629() {
        final RequestBodyConverter converter = TEST_FACTORY
                .getPackageRequestConverter(NestedPackDTO.class, array(), array(), RTF);
        assertThat("", converter, nullValue());

    }

    private Converters getConverters(ResponseConverter[] responseConverters,
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

    private ResponseConverter getResponseConverter(Class<? extends ExtensionConverter> converter,
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
            public Class<? extends ExtensionConverter> converter() {
                return converter;
            }
        };
    }

    private RequestConverter getRequestConverter(Class<? extends ExtensionConverter> converter,
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
            public Class<? extends ExtensionConverter> converter() {
                return converter;
            }
        };
    }

    private Annotation[] getContentTypeHeaderAnnotations(ContentType value) {
        return getContentTypeHeaderAnnotations(value.toString());
    }

    private Annotation[] getContentTypeHeaderAnnotations(String value) {
        return RetrofitUtils.getCallMethodAnnotations("Content-Type: " + value);
    }

    private interface TestClient {

        DualResponse<String, String> string();

        static Type getStringGenericReturnType() {
            try {
                return TestClient.class.getDeclaredMethod("string").getGenericReturnType();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
