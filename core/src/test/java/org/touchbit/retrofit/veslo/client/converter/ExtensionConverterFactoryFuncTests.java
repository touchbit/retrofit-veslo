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

package org.touchbit.retrofit.veslo.client.converter;

import internal.test.utils.OkHttpTestUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import org.touchbit.retrofit.veslo.client.converter.api.Converters;
import org.touchbit.retrofit.veslo.client.converter.api.RequestConverter;
import org.touchbit.retrofit.veslo.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.veslo.client.model.RawBody;
import org.touchbit.retrofit.veslo.client.model.ResourceFile;
import org.touchbit.retrofit.veslo.exception.ConvertCallException;
import org.touchbit.retrofit.veslo.exception.ConverterNotFoundException;
import org.touchbit.retrofit.veslo.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;

import static internal.test.utils.OkHttpTestUtils.requestBodyToString;
import static internal.test.utils.TestUtils.array;
import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.veslo.client.header.ContentTypeConstants.NULL;
import static org.touchbit.retrofit.veslo.client.header.ContentTypeConstants.TEXT_PLAIN;

@SuppressWarnings("ConstantConditions")
@DisplayName("ExtensionConverterFactory functional tests")
public class ExtensionConverterFactoryFuncTests extends BaseCoreUnitTest {

    private static final ExtensionConverterFactory FACTORY = new ExtensionConverterFactory();

    @Nested
    @DisplayName("Request body conversation tests")
    public class RequestBodyConverterTests {

        @Test
        @DisplayName("Successfully converting TestDTO.class using @Converters annotation")
        public void test1639065948758() throws IOException {
            final TestDTO dto = new TestDTO("test1638725788474");
            final Converters converters = getConverters(getRequestConverter(TestConverter.class));
            final RequestBody requestBody = getTestFactory()
                    .requestBodyConverter(TestDTO.class, AA, array(converters), RTF).convert(dto);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.contentType()", requestBody.contentType(), nullValue());
            assertThat("RequestBody.toString()", requestBodyToString(requestBody), is(dto.toString()));
        }

        @Test
        @DisplayName("Successfully converting TestDTO using @RequestConverter annotation")
        public void test1639065948769() throws IOException {
            final TestDTO dto = new TestDTO("test1637670751294");
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final RequestBody requestBody = getTestFactory()
                    .requestBodyConverter(TestDTO.class, AA, array(requestConverter), RTF).convert(dto);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", requestBodyToString(requestBody), is("test1637670751294"));
        }

        @Test
        @DisplayName("Successfully converting String.class to RequestBody")
        public void test1639065948779() throws IOException {
            String body = "test1637422599548";
            final RequestBody requestBody = FACTORY.requestBodyConverter(STRING_T, AA, AA, RTF).convert(body);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.contentType()", requestBody.contentType(), nullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is(body));
        }

        @Test
        @DisplayName("Successfully converting RawBody.class to RequestBody by type (raw)")
        public void test1639065948789() throws IOException {
            final RawBody expected = new RawBody("test1637428451229");
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(expected);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428451229"));
        }

        @Test
        @DisplayName("Successfully converting Byte[].class to RequestBody by type (raw)")
        public void test1639065948800() throws IOException {
            final Byte[] expected = Utils.toObjectByteArray("test1637428566604" .getBytes());
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(expected);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428566604"));
        }

        @Test
        @DisplayName("Successfully converting File.class to RequestBody by type (raw)")
        public void test1639065948811() throws IOException {
            final File file = new File("src/test/resources/test/data/test1637428660061.txt");
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(file);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428660061"));
        }

        @Test
        @DisplayName("Successfully converting ResourceFile.class to RequestBody by type (raw)")
        public void test1639065948822() throws IOException {
            final ResourceFile file = new ResourceFile("test/data/test1637428785169.txt");
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(file);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428785169"));
        }

        @Test
        @DisplayName("Successfully converting String->RequestBody using java type Converter")
        public void test1639065948833() throws IOException {
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert("test1637430252094");
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637430252094"));
        }

        @Test
        @DisplayName("Successfully converting RawBody->RequestBody using RequestConverter annotation with RawDTO.class")
        public void test1639065948843() throws IOException {
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final RequestBody dto = getTestFactory().requestBodyConverter(RawBody.class, array(), array(responseConverter), RTF)
                    .convert(new RawBody("test1637641171302"));
            assertThat("RequestBody", dto, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(dto), is("test1637641171302"));
        }

        @Test
        @DisplayName("Successfully converting PackDTO->RequestBody using package converter")
        public void test1639065948853() throws IOException {
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.registerPackageRequestConverter(new TestPackageConverter(), "internal.test.utils.client.model.pack");
            final RequestBody dto = factory
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(new PackageDTO("test1637687959905"));
            assertThat("RequestBody", dto, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(dto), is("test1637687959905"));
        }

        @Test
        @DisplayName("ConverterNotFoundException if body type == Object")
        public void test1639980930213() {
            final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                    .requestBodyConverter(Object.class, array(), array(), RTF)
                    .convert(new Object());
            assertThrow(runnable)
                    .assertClass(ConverterNotFoundException.class)
                    .assertMessageContains("Converter not found\n" +
                            "Transport event: REQUEST\n" +
                            "Content-Type: null\n" +
                            "DTO type: class java.lang.Object\n" +
                            "\n" +
                            "SUPPORTED REQUEST CONVERTERS:\n");
        }

    }

    @Nested
    @DisplayName("Response body conversation tests")
    public class ResponseBodyConverterTests {

        @Test
        @DisplayName("Successfully converting ResponseBody to String.class if body == IDualResponse<String, String>")
        public void test1639065949011() throws IOException {
            final Object dto = new ExtensionConverterFactory()
                    .responseBodyConverter(DUAL_RESPONSE_GENERIC_STRING_TYPE, getContentTypeHeaderAnnotations(TEXT_PLAIN), RTF)
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
        @DisplayName("Successfully converting ResponseBody to RawBody.class (raw)")
        public void test1639065949029() throws IOException {
            RawBody expected = new RawBody("test1637429237836");
            final Object dto = new ExtensionConverterFactory()
                    .responseBodyConverter(RawBody.class, array(), RTF)
                    .convert(ResponseBody.create(null, expected.string()));
            assertThat("ResponseBody", dto, is(expected));
        }

        @Test
        @DisplayName("Successfully converting ResponseBody to Byte[].class (raw)")
        public void test1639065949039() throws IOException {
            final String body = "test1637429413684";
            Byte[] expected = Utils.toObjectByteArray(body.getBytes());
            final Object dto = new ExtensionConverterFactory()
                    .responseBodyConverter(Byte[].class, array(), RTF)
                    .convert(ResponseBody.create(null, body));
            assertThat("ResponseBody", dto, is(expected));
        }

        @Test
        @DisplayName("Successfully converting ResponseBody to File.class (raw)")
        public void test1639065949050() throws Exception {
            final String body = "test1637429592556";
            final Object dto = new ExtensionConverterFactory()
                    .responseBodyConverter(File.class, array(), RTF)
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
        @DisplayName("Successfully converting ResponseBody to RawBody.class (raw) if body == null (expected RawBody)")
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
                    .responseBodyConverter(TestDTO.class, new Annotation[]{responseConverter}, RTF)
                    .convert(ResponseBody.create(null, expected.data()));
            assertThat("ResponseBody", dto, is(expected));
        }

        @Test
        @DisplayName("Successfully converting ResponseBody->RawBody using ResponseConverter annotation with RawDTO.class")
        public void test1639065949122() throws IOException {
            RawBody expected = new RawBody("test1637569961286");
            final ResponseConverter responseConverter = getResponseConverter(TestConverter.class, TestDTO.class);
            final Object dto = new ExtensionConverterFactory()
                    .responseBodyConverter(RawBody.class, new Annotation[]{responseConverter}, RTF)
                    .convert(ResponseBody.create(null, expected.bytes()));
            assertThat("ResponseBody", dto, is(expected));
        }

        @Test
        @DisplayName("Successfully converting ResponseBody->PackDTO using package converter")
        public void test1639065949133() throws IOException {
            PackageDTO expected = new PackageDTO("test1637679684417");
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.registerPackageResponseConverter(new TestPackageConverter(), "internal.test.utils.client.model.pack");
            final Object dto = factory.responseBodyConverter(PackageDTO.class, array(), RTF)
                    .convert(ResponseBody.create(null, expected.data()));
            assertThat("PackDTO", dto, is(expected));
        }

        @Test
        @DisplayName("ConverterNotFoundException if body type == Object")
        public void test1639942326488() {
            final ResponseBody responseBody = ResponseBody.create(null, "test1637430137761");
            final ThrowableRunnable runnable = () -> new ExtensionConverterFactory()
                    .responseBodyConverter(Object.class, array(), RTF)
                    .convert(responseBody);
            assertThrow(runnable)
                    .assertClass(ConverterNotFoundException.class)
                    .assertMessageContains("Converter not found\n" +
                            "Transport event: RESPONSE\n" +
                            "Content-Type: null\n" +
                            "DTO type: class java.lang.Object\n" +
                            "\n" +
                            "SUPPORTED RESPONSE CONVERTERS:\n");
        }

    }

}