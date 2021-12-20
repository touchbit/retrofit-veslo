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
@DisplayName("LongConverter.class unit tests")
public class LongConverterUnitTests extends BaseCoreUnitTest {

    private static final LongConverter CONVERTER = LongConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639671917047() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert body by real body reference type")
        public void test1639671920344() throws IOException {
            Long expected = Long.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("Convert body by real body primitive type")
        public void test1639671923079() throws IOException {
            long expected = Long.MAX_VALUE;
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(expected);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(String.valueOf(expected)));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != Long type")
        public void test1639671926001() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert("foo"))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.String\n" +
                            "Expected: java.lang.Long or long\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639671929306() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return Long if response body present")
        public void test1639671931916() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final Long result = CONVERTER.responseBodyConverter(Long.class, AA, RTF).convert(responseBody);
            assertThat(result, is(1L));
        }

        @Test
        @DisplayName("return long if response body present")
        public void test1639671934816() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "1");
            final long result = CONVERTER.responseBodyConverter(Long.TYPE, AA, RTF).convert(responseBody);
            assertThat(result, is(1L));
        }

        @Test
        @DisplayName("return null if response body is empty")
        public void test1639671937283() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, "");
            final Long result = CONVERTER.responseBodyConverter(Long.class, AA, RTF).convert(responseBody);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("return null if response body is null")
        public void test1639671939843() throws IOException {
            final Long result = CONVERTER.responseBodyConverter(Long.class, AA, RTF).convert(null);
            assertThat(result, nullValue());
        }

        @Test
        @DisplayName("ConvertCallException if response body = 'foobar'")
        public void test1639671942749() {
            final ResponseBody responseBody = ResponseBody.create(null, "foobar");
            assertThrow(() -> CONVERTER.responseBodyConverter(Long.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Long conversion error:\n" +
                            "expected long number in range -9223372036854775808...9223372036854775807\n" +
                            "but was 'foobar'");
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if body = null and return type = primitive")
        public void test1639671945884() {
            assertThrow(() -> CONVERTER.responseBodyConverter(Long.TYPE, AA, RTF).convert(null))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: long");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639671948640() {
            final ResponseBody responseBody = ResponseBody.create(null, "true");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: java.lang.Long or long\n");
        }

    }

}
