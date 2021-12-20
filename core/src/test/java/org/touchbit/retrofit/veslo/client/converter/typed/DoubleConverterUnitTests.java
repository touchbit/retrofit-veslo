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

package org.touchbit.retrofit.veslo.client.converter.typed;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import org.touchbit.retrofit.veslo.exception.ConvertCallException;
import org.touchbit.retrofit.veslo.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.veslo.exception.PrimitiveConvertCallException;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("ConstantConditions")
@DisplayName("DoubleConverter.class unit tests")
public class DoubleConverterUnitTests extends BaseCoreUnitTest {

    private static final DoubleConverter CONVERTER = DoubleConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639668043358() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1639668045901() throws IOException {
            Double expected = Double.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("Convert body by real body primitive type")
        public void test1639668048483() throws IOException {
            double expected = Double.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != Double type")
        public void test1639668051483() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: java.lang.Double or double\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639668054667() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Double.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Double.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Double if response body present")
        public void test1639668057582() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final Double result = CONVERTER.responseBodyConverter(Double.class, AA, RTF).convert(responseBody);
            assertThat(result, is((double) 1));
        }

        @Test
        @DisplayName("return double if response body present")
        public void test1639668060356() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final double result = CONVERTER.responseBodyConverter(Double.TYPE, AA, RTF).convert(responseBody);
            assertThat(result, is((double) 1));
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1639668062954() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Double result = CONVERTER.responseBodyConverter(Double.class, AA, RTF).convert(responseBody);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639668065745() throws IOException {
            final Double result = CONVERTER.responseBodyConverter(Double.class, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException if response body = 'foobar'")
        public void test1639668068303() {
            final ResponseBody responseBody = ResponseBody.create(null, "foobar");
            assertThrow(() -> CONVERTER.responseBodyConverter(Double.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Double conversion error:\n" +
                            "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                            "but was 'foobar'");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if body = null and return type = primitive")
        public void test1639668070921() {
            assertThrow(() -> CONVERTER.responseBodyConverter(Double.TYPE, AA, RTF).convert(null))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: double");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639668073525() {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Double or double\n");
        }

    }

}
