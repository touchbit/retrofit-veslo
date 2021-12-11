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

import internal.test.utils.OkHttpTestUtils;
import internal.test.utils.client.model.TestDTO;
import internal.test.utils.client.model.pack.PackageDTO;
import okhttp3.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.util.Utils;

import java.io.File;
import java.io.IOException;

import static internal.test.utils.OkHttpTestUtils.requestBodyToString;
import static internal.test.utils.TestUtils.array;
import static org.hamcrest.Matchers.*;

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
            final RequestBody requestBody = getRequestBodyConverter(TestDTO.class, converters).convert(dto);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.contentType()", requestBody.contentType(), nullValue());
            assertThat("RequestBody.toString()", requestBodyToString(requestBody), is(dto.toString()));
        }

        @Test
        @DisplayName("Successfully converting TestDTO using @RequestConverter annotation")
        public void test1639065948769() throws IOException {
            final TestDTO dto = new TestDTO("test1637670751294");
            final RequestConverter requestConverter = getRequestConverter(TestConverter.class, TestDTO.class);
            final RequestBody requestBody = getRequestBodyConverter(TestDTO.class, requestConverter).convert(dto);
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
                    .requestBodyConverter(RawBody.class, array(), array(), null)
                    .convert(expected);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428451229"));
        }

        @Test
        @DisplayName("Successfully converting Byte[].class to RequestBody by type (raw)")
        public void test1639065948800() throws IOException {
            final Byte[] expected = Utils.toObjectByteArray("test1637428566604" .getBytes());
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), null)
                    .convert(expected);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428566604"));
        }

        @Test
        @DisplayName("Successfully converting File.class to RequestBody by type (raw)")
        public void test1639065948811() throws IOException {
            final File file = new File("src/test/resources/test/data/test1637428660061.txt");
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), null)
                    .convert(file);
            assertThat("RequestBody", requestBody, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(requestBody), is("test1637428660061"));
        }

        @Test
        @DisplayName("Successfully converting ResourceFile.class to RequestBody by type (raw)")
        public void test1639065948822() throws IOException {
            final ResourceFile file = new ResourceFile("test/data/test1637428785169.txt");
            final RequestBody requestBody = new ExtensionConverterFactory()
                    .requestBodyConverter(RawBody.class, array(), array(), null)
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
            final RequestBody dto = TEST_FACTORY.requestBodyConverter(RawBody.class, array(), array(responseConverter), RTF)
                    .convert(new RawBody("test1637641171302"));
            assertThat("RequestBody", dto, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(dto), is("test1637641171302"));
        }

        @Test
        @DisplayName("Successfully converting PackDTO->RequestBody using package converter")
        public void test1639065948853() throws IOException {
            final TestsExtensionConverterFactory factory = new TestsExtensionConverterFactory();
            factory.registerPackageRequestConverter(new TestPackageConverter(), "org.touchbit.retrofit.ext.test.model.pack");
            final RequestBody dto = factory
                    .requestBodyConverter(RawBody.class, array(), array(), RTF)
                    .convert(new PackageDTO("test1637687959905"));
            assertThat("RequestBody", dto, notNullValue());
            assertThat("RequestBody.toString()", OkHttpTestUtils.requestBodyToString(dto), is("test1637687959905"));
        }

    }

}