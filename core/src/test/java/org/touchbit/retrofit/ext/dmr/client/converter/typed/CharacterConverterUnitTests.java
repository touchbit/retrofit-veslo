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
@DisplayName("CharacterConverter.class unit tests")
public class CharacterConverterUnitTests extends BaseCoreUnitTest {

    private static final CharacterConverter CONVERTER = CharacterConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639667493606() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1639667496406() throws IOException {
            Character expected = Character.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(expected.toString()));
        }

        @Test
        @DisplayName("Convert body by real body primitive type")
        public void test1639667502269() throws IOException {
            char expected = Character.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(expected + ""));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != Character type")
        public void test1639667513559() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: java.lang.Character or char\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639667517869() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Character.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Character.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Character if response body present")
        public void test1639667520555() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final Character result = CONVERTER.responseBodyConverter(Character.class, AA, RTF).convert(responseBody);
            assertThat(result, is('1'));
        }

        @Test
        @DisplayName("return char if response body present")
        public void test1639667523290() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final char result = CONVERTER.responseBodyConverter(Character.TYPE, AA, RTF).convert(responseBody);
            assertThat(result, is('1'));
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1639667526447() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Character result = CONVERTER.responseBodyConverter(Character.class, AA, RTF).convert(responseBody);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639667529092() throws IOException {
            final Character result = CONVERTER.responseBodyConverter(Character.class, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException if response body = 'foobar'")
        public void test1639667531568() {
            final ResponseBody responseBody = ResponseBody.create(null, "foobar");
            assertThrow(() -> CONVERTER.responseBodyConverter(Character.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Character conversion error:\nexpected one character\nbut was 6");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if body = null and return type = primitive")
        public void test1639667534369() {
            assertThrow(() -> CONVERTER.responseBodyConverter(Character.TYPE, AA, RTF).convert(null))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: char");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639667537141() {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Character or char\n");
        }

    }

}
