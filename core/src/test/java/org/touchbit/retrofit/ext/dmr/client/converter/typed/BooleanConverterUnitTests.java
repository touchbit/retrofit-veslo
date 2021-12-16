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
@DisplayName("BooleanConverter.class unit tests")
public class BooleanConverterUnitTests extends BaseCoreUnitTest {

    private static final BooleanConverter CONVERTER = BooleanConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639661523956() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1639661296139() throws IOException {
            Boolean expected = Boolean.TRUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is("true"));
        }

        @Test
        @DisplayName("Convert body by real body primitive type")
        public void test1639661655853() throws IOException {
            boolean expected = true;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is("true"));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != boolean type")
        public void test1639661732374() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: java.lang.Boolean or boolean\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639661909791() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Boolean if response body = 'true'")
        public void test1639661960519() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            final Boolean result = CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody);
            assertThat(result, is(true));
        }

        @Test
        @DisplayName("return Boolean if response body = 'false'")
        public void test1639662028467() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "false");
            final Boolean result = CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody);
            assertThat(result, is(false));
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1639662090358() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Boolean result = CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639662120077() throws IOException {
            final Boolean result = CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }


        @Test
        @DisplayName("ConvertCallException if response body = 'foobar'")
        public void test1639662092726() {
            final ResponseBody responseBody = ResponseBody.create(null, "foobar");
            assertThrow(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was 'foobar'");
        }

        @Test
        @DisplayName("ConvertCallException if response body = '1'")
        public void test1639662231270() {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            assertThrow(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was '1'");
        }

        @Test
        @DisplayName("ConvertCallException if response body = '0'")
        public void test1639662238142() {
            final ResponseBody responseBody = ResponseBody.create(null, "0");
            assertThrow(() -> CONVERTER.responseBodyConverter(BOOLEAN_C, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was '0'");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if body = null and return type = primitive")
        public void test1639662330630() {
            assertThrow(() -> CONVERTER.responseBodyConverter(Boolean.TYPE, AA, RTF).convert(null))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: boolean");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != boolean type")
        public void test1639662419053() {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Boolean or boolean\n");
        }

    }

}
