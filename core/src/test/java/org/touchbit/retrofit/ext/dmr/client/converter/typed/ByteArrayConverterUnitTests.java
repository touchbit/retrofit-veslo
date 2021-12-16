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

package org.touchbit.retrofit.ext.dmr.client.converter.typed;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

@SuppressWarnings("ConstantConditions")
@DisplayName("ByteArrayConverter.class unit tests")
public class ByteArrayConverterUnitTests extends BaseCoreUnitTest {

    private static final ByteArrayConverter CONVERTER = ByteArrayConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639663456575() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference array type")
        public void test1639663479573() throws IOException {
            final String expected = "test";
            final Byte[] body = Utils.toObjectByteArray(expected);
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(body);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("Convert body by real body primitive array type")
        public void test1639663530556() throws IOException {
            final String expected = "test";
            final byte[] body = expected.getBytes();
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(body);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != Byte[]")
        public void test1639663568425() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: java.lang.Byte[] or byte[]\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639663925930() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Byte[] if response body present and type = Byte[].class")
        public void test1639663950638() throws IOException {
            final byte[] expected = "test" .getBytes();
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final Object result = CONVERTER.responseBodyConverter(Byte[].class, AA, RTF).convert(responseBody);
            assertThat(result, instanceOf(Byte[].class));
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("return byte[] if response body present and type = byte[].class")
        public void test1639664296631() throws IOException {
            final byte[] expected = "test" .getBytes();
            final ResponseBody responseBody = ResponseBody.create(null, expected);
            final Object result = CONVERTER.responseBodyConverter(byte[].class, AA, RTF).convert(responseBody);
            assertThat(result, instanceOf(byte[].class));
            assertThat(result, is("test" .getBytes()));
        }

        @Test
        @DisplayName("return byte array if response body is empty")
        public void test1639664011559() throws IOException {
            final byte[] expected = "" .getBytes();
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Object result = CONVERTER.responseBodyConverter(Byte[].class, AA, RTF).convert(responseBody);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639664018809() throws IOException {
            final Object result = CONVERTER.responseBodyConverter(Byte[].class, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != boolean type")
        public void test1639665541105() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Byte[] or byte[]\n");
        }

    }

}
