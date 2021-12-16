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
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.exception.PrimitiveConvertCallException;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("ConstantConditions")
@DisplayName("ShortConverter.class unit tests")
public class ShortConverterUnitTests extends BaseCoreUnitTest {

    private static final ShortConverter CONVERTER = ShortConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639672664284() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1639672667160() throws IOException {
            Short expected = Short.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("Convert body by real body primitive type")
        public void test1639672671315() throws IOException {
            short expected = Short.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != Short type")
        public void test1639672674266() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert("foo"))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.String\n" +
                            "Expected: java.lang.Short or short\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639672677437() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Short.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Short.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Short if response body present")
        public void test1639672700278() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final Short result = CONVERTER.responseBodyConverter(Short.class, AA, RTF).convert(responseBody);
            assertThat(result, is((short) 1));
        }

        @Test
        @DisplayName("return short if response body present")
        public void test1639672703314() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final short result = CONVERTER.responseBodyConverter(Short.TYPE, AA, RTF).convert(responseBody);
            assertThat(result, is((short) 1));
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1639672705640() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Short result = CONVERTER.responseBodyConverter(Short.class, AA, RTF).convert(responseBody);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639672708556() throws IOException {
            final Short result = CONVERTER.responseBodyConverter(Short.class, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException if response body = 'foobar'")
        public void test1639672711843() {
            final ResponseBody responseBody = ResponseBody.create(null, "foobar");
            assertThrow(() -> CONVERTER.responseBodyConverter(Short.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Short conversion error:\n" +
                            "expected short number in range -32768...32767\n" +
                            "but was 'foobar'");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if body = null and return type = primitive")
        public void test1639672715113() {
            assertThrow(() -> CONVERTER.responseBodyConverter(Short.TYPE, AA, RTF).convert(null))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: short");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639672717856() {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Short or short\n");
        }

    }

}
