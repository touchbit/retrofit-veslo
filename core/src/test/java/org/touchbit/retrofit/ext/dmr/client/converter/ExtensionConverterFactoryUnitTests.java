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
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.*;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterNotFoundException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import org.touchbit.retrofit.ext.test.PrivateConverter;
import org.touchbit.retrofit.ext.test.PublicExtensionConverterFactory;
import org.touchbit.retrofit.ext.test.TestConverter;
import org.touchbit.retrofit.ext.test.model.RawDTO;
import retrofit2.Converter;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter.softlyAsserter;
import static org.touchbit.retrofit.ext.dmr.client.CallStage.REQUEST;
import static org.touchbit.retrofit.ext.dmr.client.CallStage.RESPONSE;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;

@SuppressWarnings({"ConstantConditions", "rawtypes", "SameParameterValue"})
@DisplayName("ExtensionConverterFactory tests")
public class ExtensionConverterFactoryUnitTests {

    private static final PublicExtensionConverterFactory FACTORY = new PublicExtensionConverterFactory();

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
        final Byte[] expected = Utils.toObjectByteArray("test1637428566604".getBytes());
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
                .assertClass(ConverterNotFoundException.class)
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
        Byte[] expected = Utils.toObjectByteArray(body.getBytes());
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
                .assertClass(ConverterNotFoundException.class)
                .assertMessageContains("Converter not found");
    }

    @Test
    @DisplayName("ConvertCallException at converting ResponseBody to String.class (raw)")
    public void test1637430137761() {
        final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                .responseBodyConverter(String.class, new Annotation[]{}, null)
                .convert(ResponseBody.create(null, "test1637430137761"));
        assertThrow(runnable)
                .assertClass(ConverterNotFoundException.class)
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
    @DisplayName("Successfully converting ResponseBody->RawDTO using ResponseConverter annotation with RawDTO.class")
    public void test1637569961286() {
        RawDTO expected = new RawDTO("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(RawDTO.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.data()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting ResponseBody->AnyBody using ResponseConverter annotation with RawDTO.class")
    public void test1637641171302() {
        AnyBody expected = new AnyBody("test1637569961286");
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Object dto = new ExtensionConverterFactory()
                .responseBodyConverter(AnyBody.class, new Annotation[]{responseConverter}, null)
                .convert(ResponseBody.create(null, expected.bytes()));
        assertThat("ResponseBody", dto, is(expected));
    }

    @Test
    @DisplayName("Successfully converting RawDTO->RequestBody using RequestConverter annotation with RawDTO.class")
    public void test1637670751294() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final RequestBody dto = new ExtensionConverterFactory()
                .requestBodyConverter(RawDTO.class, array(), array(requestConverter), null)
                .convert(new RawDTO("test1637670751294"));
        assertThat("ResponseBody", dto, notNullValue());
    }

    @Test
    @DisplayName("Successfully converting AnyBody->RequestBody using RequestConverter annotation with RawDTO.class")
    public void test1637670825343() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final RequestBody dto = new ExtensionConverterFactory()
                .requestBodyConverter(AnyBody.class, array(), array(responseConverter), null)
                .convert(new AnyBody("test1637641171302"));
        assertThat("ResponseBody", dto, notNullValue());
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'converter' parameter.")
    public void test1637431872063() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(null, Object.class))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addRawRequestConverter() NPE for the 'rawClass' parameter.")
    public void test1637432122823() {
        assertThrow(() -> new ExtensionConverterFactory().addRawRequestConverter(new StringConverter(), new Class[]{null}))
                .assertNPE("rawClass");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'converter' parameter.")
    public void test1637432216675() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addMimeRequestConverter() NPE for the 'rawClass' parameter.")
    public void test1637432248690() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeRequestConverter(new StringConverter(), new ContentType[]{null}))
                .assertNPE("contentType");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432305518() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(null, Object.class))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addRawResponseConverter() NPE for the 'bodyClass' parameter.")
    public void test1637432309181() {
        assertThrow(() -> new ExtensionConverterFactory().addRawResponseConverter(new StringConverter(), new Class[]{null}))
                .assertNPE("bodyClass");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'converter' parameter.")
    public void test1637432312342() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(null, TEXT_HTML))
                .assertNPE("converter");
    }

    @Test
    @DisplayName("#addMimeResponseConverter() NPE for the 'rawClass' parameter.")
    public void test1637432315305() {
        assertThrow(() -> new ExtensionConverterFactory().addMimeResponseConverter(new StringConverter(), new ContentType[]{null}))
                .assertNPE("contentType");
    }

    @Test
    @DisplayName("#newInstance() successful return new instance if TestConverter.class")
    public void test1637647202884() {
        final ExtensionConverter<?> converter = FACTORY.newInstance(TestConverter.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class is null")
    public void test1637647341032() {
        assertThrow(() -> FACTORY.newInstance(null)).assertNPE("converterClass");
    }

    @Test
    @DisplayName("#newInstance() ConvertCallException being throw if class has privet constructor")
    public void test1637647541211() {
        assertThrow(() -> FACTORY.newInstance(PrivateConverter.class))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("" +
                        "Unable to create new instance of class org.touchbit.retrofit.ext.test.PrivateConverter\n" +
                        "See details below.")
                .assertCause(cause -> cause
                        .assertClass(IllegalAccessException.class)
                        .assertMessageIs("" +
                                "class org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory " +
                                "cannot access a member of class org.touchbit.retrofit.ext.test.PrivateConverter " +
                                "with modifiers \"private\"")
                );
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes not specified")
    public void test1637647704141() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final ExtensionConverter<?> converter = FACTORY.getExtensionConverter(responseConverter, RawDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return converter if converted classes specified (RawDTO.class)")
    public void test1637648856550() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final ExtensionConverter<?> converter = FACTORY.getExtensionConverter(requestConverter, RawDTO.class);
        assertThat("TestConverter", converter, notNullValue());
        assertThat("TestConverter", converter, instanceOf(TestConverter.class));
    }

    @Test
    @DisplayName("#getExtensionConverter() return null if converted classes incompatible")
    public void test1637648901620() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final ExtensionConverter<?> converter = FACTORY
                .getExtensionConverter(responseConverter, Object.class);
        assertThat("TestConverter", converter, nullValue());
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if converter=null")
    public void test1637649395407() {
        assertThrow(() -> FACTORY.getExtensionConverter(null, Object.class)).assertNPE("annotation");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if bodyClass=null")
    public void test1637649414228() {
        final RequestConverter converter = getRequestConverter(TestConverter.class, RawDTO.class);
        assertThrow(() -> FACTORY.getExtensionConverter(converter, null)).assertNPE("bodyClass");
    }

    @Test
    @DisplayName("#getExtensionConverter() ConvertCallException being throw if unsupported annotation")
    public void test1637669472817() {
        final Nullable nullable = new Nullable() {
            public Class<? extends Annotation> annotationType() {
                return Nullable.class;
            }
        };
        assertThrow(() -> FACTORY.getExtensionConverter(nullable, Object.class))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Received an unsupported annotation type: " + nullable.getClass());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if ResponseConverter annotation has processed class")
    public void test1637663426755() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final Converter<ResponseBody, ?> converter = FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{responseConverter}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }


    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if ResponseConverter annotation has not processed class")
    public void test1637663216813() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, Objects.class);
        final Converter<ResponseBody, ?> converter = FACTORY
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
        final Converter<ResponseBody, ?> converter = FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @ResponseConverter)")
    public void test1637641699864() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = FACTORY
                .getResponseConverterFromAnnotation(Object.class, new Annotation[]{converters}, null);
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
        final Converter<ResponseBody, ?> converter = FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{converters}, null);
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
        final Converter<ResponseBody, ?> converter = FACTORY
                .getResponseConverterFromAnnotation(RawDTO.class, new Annotation[]{converters}, null);
        assertThat("Converter", converter, isA(ResponseBodyConverter.class));
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() " +
            "return null if annotations not present")
    public void test1637661882814() {
        final Converter<ResponseBody, ?> responseConverterFromAnnotation = FACTORY
                .getResponseConverterFromAnnotation(Object.class, new Annotation[]{}, null);
        assertThat("Converter", responseConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getResponseConverterFromAnnotation() ConvertCallException thrown if " +
            "ResponseConverter and Converters obtained at the same time")
    public void test1637662013770() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class);
        final Annotation[] annotations = {converters, responseConverter};
        assertThrow(() -> FACTORY.getResponseConverterFromAnnotation(Object.class, annotations, null))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("API method contains concurrent annotations.\n" +
                        "Use only one of:\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.Converters");
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return empty map if annotations not present")
    public void test1637645424991() {
        final Map<String, Class<?>> convertersMap = FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnResponseConverters() return empty map if empty Converters annottion")
    public void test1637645605023() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
                .getAnnotatedResponseConverters(new Annotation[]{responseConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if annotations not present")
    public void test1637646367646() {
        final Map<String, Class<?>> convertersMap = FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnRequestConverters() return empty map if empty Converters annotation")
    public void test1637646381640() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
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
        final Map<String, Class<?>> convertersMap = FACTORY
                .getAnnotatedRequestConverters(new Annotation[]{requestConverter});
        assertThat("Annotation converters map ", convertersMap, notNullValue());
        assertThat("Annotation converters map size", convertersMap.size(), is(1));
        assertThat("Annotation converters map", convertersMap.toString(),
                is("{" + RawDTO.class.getName() + "=" + TestConverter.class + "}"));
    }

    @Test
    @DisplayName("#toString()")
    public void test1637663604069() {
        final String result = FACTORY.toString();
        assertThat("", result, containsString("Converter factory: "));
        assertThat("", result, containsString("Annotated converters: <absent>\n"));
        assertThat("", result, containsString("Raw converters:\n"));
        assertThat("", result, containsString("Content type converters:\n"));
        assertThat("", result, containsString("SUPPORTED " + REQUEST + " CONVERTERS:\n"));
        assertThat("", result, containsString("SUPPORTED " + RESPONSE + " CONVERTERS:\n"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (RESPONSE)")
    public void test1637663823967() {
        final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, RawDTO.class);
        final String info = new PublicExtensionConverterFactory()
                .getSupportedConvertersInfo(RESPONSE, array(responseConverter));
        assertThat("", info, is("SUPPORTED RESPONSE CONVERTERS:\n" +
                "Annotated converters:\n" +
                "org.touchbit.retrofit.ext.test.TestConverter\n" +
                "    org.touchbit.retrofit.ext.test.model.RawDTO\n" +
                "\n" +
                "Raw converters:\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.AnyBodyConverter\n" +
                "    class org.touchbit.retrofit.ext.dmr.client.model.AnyBody\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ByteArrayConverter\n" +
                "    class java.lang.Byte[]\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.FileConverter\n" +
                "    class java.io.File\n" +
                "\n" +
                "Content type converters:\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.StringConverter\n" +
                "    application/x-www-form-urlencoded\n" +
                "    application/x-www-form-urlencoded; charset=utf-8\n" +
                "    text/html\n" +
                "    text/html; charset=utf-8\n" +
                "    text/plain\n" +
                "    text/plain; charset=utf-8"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() all Converters types present (REQUEST)")
    public void test1637664434980() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class, Object.class);
        final String info = new PublicExtensionConverterFactory()
                .getSupportedConvertersInfo(REQUEST, array(requestConverter));
        assertThat("", info, is("SUPPORTED REQUEST CONVERTERS:\n" +
                "Annotated converters:\n" +
                "org.touchbit.retrofit.ext.test.TestConverter\n" +
                "    java.lang.Object\n" +
                "    org.touchbit.retrofit.ext.test.model.RawDTO\n" +
                "\n" +
                "Raw converters:\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.AnyBodyConverter\n" +
                "    class org.touchbit.retrofit.ext.dmr.client.model.AnyBody\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ByteArrayConverter\n" +
                "    class java.lang.Byte[]\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.FileConverter\n" +
                "    class java.io.File\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.ResourceFileConverter\n" +
                "    class org.touchbit.retrofit.ext.dmr.client.model.ResourceFile\n" +
                "\n" +
                "Content type converters:\n" +
                "org.touchbit.retrofit.ext.dmr.client.converter.defaults.StringConverter\n" +
                "    text/html\n" +
                "    text/html; charset=utf-8\n" +
                "    text/plain\n" +
                "    text/plain; charset=utf-8"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() Converters not present (REQUEST)")
    public void test1637665351779() {
        final PublicExtensionConverterFactory factory = new PublicExtensionConverterFactory();
        factory.getRawRequestConverters().clear();
        factory.getRawResponseConverters().clear();
        factory.getMimeRequestConverters().clear();
        factory.getMimeResponseConverters().clear();
        final String info = factory.getSupportedConvertersInfo(REQUEST, array());
        assertThat("", info, is("SUPPORTED REQUEST CONVERTERS:\n" +
                "Annotated converters: <absent>\n" +
                "\n" +
                "Raw converters: <absent>\n" +
                "\n" +
                "Content type converters: <absent>"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() Converters not present (RESPONSE)")
    public void test1637665499864() {
        final PublicExtensionConverterFactory factory = new PublicExtensionConverterFactory();
        factory.getRawRequestConverters().clear();
        factory.getRawResponseConverters().clear();
        factory.getMimeRequestConverters().clear();
        factory.getMimeResponseConverters().clear();
        final String info = factory.getSupportedConvertersInfo(RESPONSE, array());
        assertThat("", info, is("SUPPORTED RESPONSE CONVERTERS:\n" +
                "Annotated converters: <absent>\n" +
                "\n" +
                "Raw converters: <absent>\n" +
                "\n" +
                "Content type converters: <absent>"));
    }

    @Test
    @DisplayName("#getSupportedConvertersInfo() IllegalArgumentException if callStage unsupported")
    public void test1637665542849() {
        assertThrow(() -> FACTORY.getSupportedConvertersInfo(null, array())).assertNPE("callStage");
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has processed class")
    public void test1637670097335() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, RawDTO.class);
        final RequestBodyConverter converter = FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(requestConverter), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if RequestConverter annotation has not processed class")
    public void test1637670057963() {
        final RequestConverter requestConverter = getRequestConverter(TestConverter.class, Objects.class);
        final RequestBodyConverter converter = FACTORY
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
        final RequestBodyConverter converter = FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return null if Converters annotation is empty (without specified @RequestConverter)")
    public void test1637670150695() {
        final Converters converters = getConverters(new ResponseConverter[]{}, new RequestConverter[]{});
        final RequestBodyConverter requestConverterFromAnnotation = FACTORY
                .getRequestConverterFromAnnotation(Object.class, array(), array(converters), null);
        assertThat("Converter", requestConverterFromAnnotation, nullValue());
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation does not contain classes for conversion")
    public void test1637670201642() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = FACTORY
                .getRequestConverterFromAnnotation(RawDTO.class, array(), array(converters), null);
        assertThat("Converter", converter, isA(RequestBodyConverter.class));
    }

    @Test
    @DisplayName("#getRequestConverterFromAnnotation() " +
            "return converter if Converters annotation has RawDTO.class")
    public void test1637670239992() {
        final RequestConverter[] requestConverters = array(getRequestConverter(TestConverter.class, RawDTO.class));
        final Converters converters = getConverters(new ResponseConverter[]{}, requestConverters);
        final RequestBodyConverter converter = FACTORY
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
        assertThrow(() -> FACTORY.getRequestConverterFromAnnotation(Object.class, array(), annotations, null))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("API method contains concurrent annotations.\n" +
                        "Use only one of:\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter\n" +
                        " * interface org.touchbit.retrofit.ext.dmr.client.converter.api.Converters");
    }

    @SafeVarargs
    private final <C> C[] array(C... annotations) {
        return annotations;
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
            public Class<?>[] bodyClass() {
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
            public Class<?>[] bodyClass() {
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

}
