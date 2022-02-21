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

package veslo.client.converter.typed;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("ConstantConditions")
@DisplayName("FileConverter tests")
public class FileConverterUnitTests extends BaseCoreUnitTest {

    private static final FileConverter CONVERTER = FileConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639668892322() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1637466272864() throws IOException {
            final String expected = "test1637466272864";
            final File file = new File("src/test/resources/test/data/test1637466272864.txt");
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, arrayOf(), arrayOf(), RTF).convert(file);
            final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat("Body", actual, is(expected));
        }

        @Test
        @DisplayName("ConvertCallException if file not exists")
        public void test1639669359068() {
            final File file = new File("src/test_foo_bar");
            assertThrow(() -> CONVERTER.requestBodyConverter(FILE_C, arrayOf(), arrayOf(), RTF).convert(file))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Request body file not exists: src/test_foo_bar");
        }

        @Test
        @DisplayName("ConvertCallException if file is a directory")
        public void test1639669398095() {
            final File file = new File("src");
            assertThrow(() -> CONVERTER.requestBodyConverter(FILE_C, arrayOf(), arrayOf(), RTF).convert(file))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Request body file is not a readable file: src");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != File type")
        public void test1639669417891() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: java.io.File\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639669545521() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return File if response body present")
        public void test1639669554897() throws IOException {
            final String expected = "test";
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final File result = CONVERTER.responseBodyConverter(FILE_C, arrayOf(), RTF).convert(responseBody);
            final byte[] resultData = Files.readAllBytes(result.toPath());
            assertThat("Body", resultData, is("test" .getBytes()));
        }

        @Test
        @DisplayName("return File if response body is empty")
        public void test1639669659531() throws IOException {
            final String expected = "";
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final File result = CONVERTER.responseBodyConverter(FILE_C, arrayOf(), RTF).convert(responseBody);
            final byte[] resultData = Files.readAllBytes(result.toPath());
            assertThat("Body", resultData, is("" .getBytes()));
        }

        @Test
        @DisplayName("return null if response body == null")
        public void test1639669773834() throws IOException {
            final File result = CONVERTER.responseBodyConverter(FILE_C, arrayOf(), RTF).convert(null);
            assertThat("Body", result, nullValue());
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639669795774() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.io.File\n");
        }

    }

}
