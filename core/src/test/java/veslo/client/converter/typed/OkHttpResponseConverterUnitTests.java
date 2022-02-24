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
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;

import java.io.IOException;

@SuppressWarnings("ConstantConditions")
@DisplayName("OkHttpResponseConverter.class unit tests")
public class OkHttpResponseConverterUnitTests extends BaseCoreUnitTest {

    private static final OkHttpResponseConverter CONVERTER = OkHttpResponseConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645693723439() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("ConvertCallException if body != Response type")
        public void test1645693874296() {
            final Response response = OkHttpTestUtils.getResponse();
            assertThrow(() -> CONVERTER.requestBodyConverter(Response.class, AA, AA, RTF).convert(response))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Type okhttp3.Response cannot be used to make requests.");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645693745074() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return null if response body present")
        public void test1645693749035() throws IOException {
            final String expected = "test";
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final Object result = CONVERTER.responseBodyConverter(Response.class, AA, RTF).convert(responseBody);
            assertIsNull(result);
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1645693751742() throws IOException {
            final String expected = "";
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final Object result = CONVERTER.responseBodyConverter(Response.class, AA, RTF).convert(responseBody);
            assertIsNull(result);
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1645694004500() throws IOException {
            final String expected = "";
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final Object result = CONVERTER.responseBodyConverter(Response.class, AA, RTF).convert(responseBody);
            assertIsNull(result);
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1645693782658() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: okhttp3.Response\n");
        }

    }

}
