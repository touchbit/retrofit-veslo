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
import internal.test.utils.ThrowableRunnable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.*;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Stream;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter.softlyAsserter;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;

@SuppressWarnings("ConstantConditions")
@DisplayName("ExtensionConverterFactory tests")
public class ExtensionConverterFactoryUnitTests {

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

    private static Annotation[] getContentTypeHeaderAnnotations(ContentType value) {
        return getContentTypeHeaderAnnotations(value.toString());
    }

    private static Annotation[] getContentTypeHeaderAnnotations(String value) {
        return RetrofitUtils.getCallMethodAnnotations("Content-Type: " + value);
    }

    @Test
    @DisplayName("Default converters initialization")
    public void test1637421751464() {
        ExtensionConverterFactory factory = new ExtensionConverterFactory();
        final Map<Class<?>, ExtensionConverter<?>> rawRqt = factory.getRawRequestConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRqt = factory.getMimeRequestConverters();
        final Map<Class<?>, ExtensionConverter<?>> rawRsp = factory.getRawResponseConverters();
        final Map<ContentType, ExtensionConverter<?>> mimeRsp = factory.getMimeResponseConverters();
        softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("Request raw converters size", rawRqt.size(), is(4)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(AnyBody.class), instanceOf(AnyBodyConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(Byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(File.class), instanceOf(FileConverter.class)))
                .softly(() -> assertThat("Request raw converter", rawRqt.get(ResourceFile.class), instanceOf(ResourceFileConverter.class)))
                .softly(() -> assertThat("Request mime converters size", mimeRqt.size(), is(4)))
                .softly(() -> assertThat("Request mime converter", mimeRqt.get(TEXT_PLAIN), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Request mime converter", mimeRqt.get(TEXT_PLAIN_UTF8), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Request mime converter", mimeRqt.get(TEXT_HTML), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Request mime converter", mimeRqt.get(TEXT_HTML_UTF8), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response raw converters size", rawRsp.size(), is(3)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(AnyBody.class), instanceOf(AnyBodyConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(Byte[].class), instanceOf(ByteArrayConverter.class)))
                .softly(() -> assertThat("Response raw converter", rawRsp.get(File.class), instanceOf(FileConverter.class)))
                .softly(() -> assertThat("Response mime converters size", mimeRsp.size(), is(6)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(TEXT_PLAIN), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(TEXT_PLAIN_UTF8), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(TEXT_HTML), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(TEXT_HTML_UTF8), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(APP_FORM_URLENCODED), instanceOf(StringConverter.class)))
                .softly(() -> assertThat("Response mime converter", mimeRsp.get(APP_FORM_URLENCODED_UTF8), instanceOf(StringConverter.class)))
        );
    }

    @DisplayName("Successfully converting String.class to RequestBody by Content-Type header (mime)")
    @ParameterizedTest(name = "Content-Type: {1}")
    @MethodSource("testProvider1637422599548")
    public void test1637422599548(Class<String> type, String cT, Object body, MediaType expMT) {
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(type, new Annotation[]{}, getContentTypeHeaderAnnotations(cT), null)
                .convert(body);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.contentType()", requestBody.contentType(), is(expMT));
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is(body));
    }

    @Test
    @DisplayName("Successfully converting AnyBody.class to RequestBody by type (raw)")
    public void test1637428451229() {
        final AnyBody expected = new AnyBody("test1637428451229");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(AnyBody.class, new Annotation[]{}, new Annotation[]{}, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428451229"));
    }

    @Test
    @DisplayName("Successfully converting Byte[].class to RequestBody by type (raw)")
    public void test1637428566604() {
        final Byte[] expected = ConverterUtils.toObjectByteArray("test1637428566604".getBytes());
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(AnyBody.class, new Annotation[]{}, new Annotation[]{}, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428566604"));
    }

    @Test
    @DisplayName("Successfully converting File.class to RequestBody by type (raw)")
    public void test1637428660061() {
        final File file = new File("src/test/resources/test/data/test1637428660061.txt");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(AnyBody.class, new Annotation[]{}, new Annotation[]{}, null)
                .convert(file);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428660061"));
    }

    @Test
    @DisplayName("Successfully converting ResourceFile.class to RequestBody by type (raw)")
    public void test1637428785169() {
        final ResourceFile file = new ResourceFile("test/data/test1637428785169.txt");
        final RequestBody requestBody = new ExtensionConverterFactory()
                .requestBodyConverter(AnyBody.class, new Annotation[]{}, new Annotation[]{}, null)
                .convert(file);
        assertThat("RequestBody", requestBody, notNullValue());
        assertThat("RequestBody.toString()", OkHttpUtils.requestBodyToString(requestBody), is("test1637428785169"));
    }

    @Test
    @DisplayName("ConvertCallException at converting String.class to RequestBody by type (raw)")
    public void test1637430252094() {
        final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                .requestBodyConverter(String.class, new Annotation[]{}, new Annotation[]{}, null)
                .convert("test1637430252094");
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageContains("Converter not found");
    }

    @DisplayName("Successfully converting ResponseBody to String.class by Content-Type header (mime)")
    @ParameterizedTest(name = "Content-Type: {1}")
    @MethodSource("testProvider1637426286255")
    public void test1637426286255(Class<String> type, ContentType contentType, String body) {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(type, getContentTypeHeaderAnnotations(contentType), null)
                .convert(ResponseBody.create(contentType.getMediaType(), body));
        assertThat("ResponseBody", dto, is(body));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class if body == IDualResponse<String, String>")
    public void test1637431635085() {
        DualResponse<String, String> response = new DualResponse<>(null, null, null, null, null);
        final Type type = response.getClass().getGenericSuperclass();
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(type, getContentTypeHeaderAnnotations(TEXT_PLAIN), null)
                .convert(ResponseBody.create(TEXT_PLAIN.getMediaType(), "test1637431635085"));
        assertThat("ResponseBody", dto, is("test1637431635085"));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to String.class (mime) if body == null (expected null)")
    public void test1637431342933() {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(String.class, getContentTypeHeaderAnnotations(NULL), null)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw)")
    public void test1637429237836() {
        AnyBody expected = new AnyBody("test1637429237836");
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(AnyBody.class, new Annotation[]{}, null)
                .convert(ResponseBody.create(null, expected.string()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw)")
    public void test1637429413684() {
        final String body = "test1637429413684";
        Byte[] expected = ConverterUtils.toObjectByteArray(body.getBytes());
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, new Annotation[]{}, null)
                .convert(ResponseBody.create(null, body));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw)")
    public void test1637429592556() throws Exception {
        final String body = "test1637429592556";
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(File.class, new Annotation[]{}, null)
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
                .responseBodyConverter(ResourceFile.class, new Annotation[]{}, null)
                .convert(ResponseBody.create(null, "test1637429829752"));
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageContains("Converter not found");
    }

    @Test
    @DisplayName("ConvertCallException at converting ResponseBody to String.class (raw)")
    public void test1637430137761() {
        final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                .responseBodyConverter(String.class, new Annotation[]{}, null)
                .convert(ResponseBody.create(null, "test1637430137761"));
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageContains("Converter not found");
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to AnyBody.class (raw) if body == null (expected AnyBody)")
    public void test1637430944283() {
        AnyBody expected = new AnyBody((byte[]) null);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(AnyBody.class, new Annotation[]{}, null)
                .convert(null);
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to File.class (raw) if body == null (expected null)")
    public void test1637431055137() {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(File.class, new Annotation[]{}, null)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("Successfully converting ResponseBody to Byte[].class (raw) if body == null (expected null)")
    public void test1637431161233() {
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(Byte[].class, new Annotation[]{}, null)
                .convert(null);
        assertThat("ResponseBody", dto, nullValue());
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'converter' parameter.")
    public void test1637431872063() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(null, Object.class))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'converter' cannot be null.");
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'rawClass' parameter.")
    public void test1637432122823() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(new StringConverter(), new Class[]{null}))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'rawClass' cannot be null.");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'converter' parameter.")
    public void test1637432216675() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(null, TEXT_HTML))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'converter' cannot be null.");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'rawClass' parameter.")
    public void test1637432248690() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(new StringConverter(), new ContentType[]{null}))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'contentType' cannot be null.");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432305518() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(null, Object.class))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'converter' cannot be null.");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'bodyClass' parameter.")
    public void test1637432309181() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(new StringConverter(), new Class[]{null}))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'bodyClass' cannot be null.");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432312342() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(null, TEXT_HTML))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'converter' cannot be null.");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'rawClass' parameter.")
    public void test1637432315305() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(new StringConverter(), new ContentType[]{null}))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Parameter 'contentType' cannot be null.");
    }

}
