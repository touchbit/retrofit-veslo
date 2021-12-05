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

package org.touchbit.retrofit.ext.dmr.client.converter.defaults;

import internal.test.utils.BaseUnitTest;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings({"ConstantConditions", "rawtypes"})
@DisplayName("JavaReferenceTypeConverter tests")
public class JavaPrimitiveTypeConverterUnitTests extends BaseUnitTest {

    private static final JavaPrimitiveTypeConverter CONVERTER = new JavaPrimitiveTypeConverter();
    private static final ResponseBodyConverter<?> RESPONSE_CONVERTER = CONVERTER.responseBodyConverter(OBJ_C, AA, RTF);

    private static ResponseBodyConverter<?> getResponseConverter(Class dtoClass) {
        return CONVERTER.responseBodyConverter(dtoClass, AA, RTF);
    }

    @Nested
    @DisplayName(".requestBodyConverter(Type, Annotation[], Retrofit) method")
    public class ResponseBodyConverterTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639065949825() {
            assertThrow(() -> CONVERTER.responseBodyConverter(null, AA, RTF)).assertNPE("type");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, null, RTF)).assertNPE("methodAnnotations");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, null)).assertNPE("retrofit");
        }

        @Nested
        @DisplayName(".convert(ResponseBody) method")
        public class ConvertMethodTests {

            @Test
            @DisplayName("return null if ResponseBody == null")
            public void test1639065949837() throws IOException {
                final Object result = RESPONSE_CONVERTER.convert(null);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Character.TYPE: Successful conversion if response body length = 1")
            public void test1639065949844() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "1");
                final Character result = (Character) getResponseConverter(PRIMITIVE_CHARACTER_C).convert(responseBody);
                assertThat("", result, is('1'));
            }

            @Test
            @DisplayName("Character.TYPE: ConvertCallException if response body is empty")
            public void test1639065949852() {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                assertThrow(() -> getResponseConverter(PRIMITIVE_CHARACTER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Character conversion error:\nexpected one character\nbut was 0");
            }

            @Test
            @DisplayName("Character.TYPE: ConvertCallException if response body length = 2")
            public void test1639065949861() {
                final ResponseBody responseBody = ResponseBody.create(null, "12");
                assertThrow(() -> getResponseConverter(PRIMITIVE_CHARACTER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Character conversion error:\nexpected one character\nbut was 2");
            }

            @Test
            @DisplayName("Boolean.TYPE: Successful conversion if response body = 'true'")
            public void test1639065949870() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "true");
                final Boolean result = (Boolean) getResponseConverter(PRIMITIVE_BOOLEAN_C).convert(responseBody);
                assertThat("", result, is(true));
            }

            @Test
            @DisplayName("Boolean.TYPE: Successful conversion if response body = 'false'")
            public void test1639065949878() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "false");
                final Boolean result = (Boolean) getResponseConverter(PRIMITIVE_BOOLEAN_C).convert(responseBody);
                assertThat("", result, is(false));
            }

            @Test
            @DisplayName("Boolean.TYPE: ConvertCallException if response body = 'foobar'")
            public void test1639065949886() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BOOLEAN_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was foobar");
            }

            @Test
            @DisplayName("Byte.TYPE: Successful conversion if response body = " + Byte.MIN_VALUE)
            public void test1639065949895() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MIN_VALUE);
                final Byte result = (Byte) getResponseConverter(PRIMITIVE_BYTE_C).convert(responseBody);
                assertThat("", result, is(Byte.MIN_VALUE));
            }

            @Test
            @DisplayName("Byte.TYPE: Successful conversion if response body = " + Byte.MAX_VALUE)
            public void test1639065949903() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MAX_VALUE);
                final Byte result = (Byte) getResponseConverter(PRIMITIVE_BYTE_C).convert(responseBody);
                assertThat("", result, is(Byte.MAX_VALUE));
            }

            @Test
            @DisplayName("Byte.TYPE: ConvertCallException if response body = foobar")
            public void test1639065949911() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BYTE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Byte conversion error:\nexpected byte in range -128...127\nbut was foobar");
            }

            @Test
            @DisplayName("Integer.TYPE: Successful conversion if response body = " + Integer.MIN_VALUE)
            public void test1639065949920() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MIN_VALUE);
                final Integer result = (Integer) getResponseConverter(PRIMITIVE_INTEGER_C).convert(responseBody);
                assertThat("", result, is(Integer.MIN_VALUE));
            }

            @Test
            @DisplayName("Integer.TYPE: Successful conversion if response body = " + Integer.MAX_VALUE)
            public void test1639065949928() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MAX_VALUE);
                final Integer result = (Integer) getResponseConverter(PRIMITIVE_INTEGER_C).convert(responseBody);
                assertThat("", result, is(Integer.MAX_VALUE));
            }

            @Test
            @DisplayName("Integer.TYPE: ConvertCallException if response body = foobar")
            public void test1639065949936() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_INTEGER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Integer conversion error:\n" +
                                "expected integer number in range -2147483648...2147483647\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Double.TYPE: Successful conversion if response body = " + Double.MIN_VALUE)
            public void test1639065949947() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MIN_VALUE);
                final Double result = (Double) getResponseConverter(PRIMITIVE_DOUBLE_C).convert(responseBody);
                assertThat("", result, is(Double.MIN_VALUE));
            }

            @Test
            @DisplayName("Double.TYPE: Successful conversion if response body = " + Double.MAX_VALUE)
            public void test1639065949955() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MAX_VALUE);
                final Double result = (Double) getResponseConverter(PRIMITIVE_DOUBLE_C).convert(responseBody);
                assertThat("", result, is(Double.MAX_VALUE));
            }

            @Test
            @DisplayName("Double.TYPE: ConvertCallException if response body = foobar")
            public void test1639065949963() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_DOUBLE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Double conversion error:\n" +
                                "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Float.TYPE: Successful conversion if response body = " + Float.MIN_VALUE)
            public void test1639065949974() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MIN_VALUE);
                final Float result = (Float) getResponseConverter(PRIMITIVE_FLOAT_C).convert(responseBody);
                assertThat("", result, is(Float.MIN_VALUE));
            }

            @Test
            @DisplayName("Float.TYPE: Successful conversion if response body = " + Float.MAX_VALUE)
            public void test1639065949982() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MAX_VALUE);
                final Float result = (Float) getResponseConverter(PRIMITIVE_FLOAT_C).convert(responseBody);
                assertThat("", result, is(Float.MAX_VALUE));
            }

            @Test
            @DisplayName("Float.TYPE: ConvertCallException if response body = foobar")
            public void test1639065949990() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_FLOAT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Float conversion error:\n" +
                                "expected float number in range 1.4E-45...3.4028235E38\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Long.TYPE: Successful conversion if response body = " + Long.MIN_VALUE)
            public void test1639065950001() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MIN_VALUE);
                final Long result = (Long) getResponseConverter(PRIMITIVE_LONG_C).convert(responseBody);
                assertThat("", result, is(Long.MIN_VALUE));
            }

            @Test
            @DisplayName("Long.TYPE: Successful conversion if response body = " + Long.MAX_VALUE)
            public void test1639065950009() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MAX_VALUE);
                final Long result = (Long) getResponseConverter(PRIMITIVE_LONG_C).convert(responseBody);
                assertThat("", result, is(Long.MAX_VALUE));
            }

            @Test
            @DisplayName("Long.TYPE: ConvertCallException if response body = foobar")
            public void test1639065950017() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_LONG_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Long conversion error:\n" +
                                "expected long number in range -9223372036854775808...9223372036854775807\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Short.TYPE: Successful conversion if response body = " + Short.MIN_VALUE)
            public void test1639065950028() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MIN_VALUE);
                final Short result = (Short) getResponseConverter(PRIMITIVE_SHORT_C).convert(responseBody);
                assertThat("", result, is(Short.MIN_VALUE));
            }

            @Test
            @DisplayName("Short.TYPE: Successful conversion if response body = " + Short.MAX_VALUE)
            public void test1639065950036() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MAX_VALUE);
                final Short result = (Short) getResponseConverter(PRIMITIVE_SHORT_C).convert(responseBody);
                assertThat("", result, is(Short.MAX_VALUE));
            }

            @Test
            @DisplayName("Short.TYPE: ConvertCallException if response body = foobar")
            public void test1639065950044() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_SHORT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Short conversion error:\n" +
                                "expected short number in range -32768...32767\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Object.class: ConvertCallException - unsupported type")
            public void test1639065950055() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(OBJ_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Object");
            }

            @Test
            @DisplayName("Set.class: ConvertCallException - unsupported type")
            public void test1639065950064() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(SET_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: interface java.util.Set");
            }

            @Test
            @DisplayName("List.class: ConvertCallException - unsupported type")
            public void test1639065950073() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(LIST_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: interface java.util.List");
            }

            @Test
            @DisplayName("Map.class: ConvertCallException - unsupported type")
            public void test1639065950082() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(MAP_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: interface java.util.Map");
            }

            @Test
            @DisplayName("String[].class: ConvertCallException - unsupported type")
            public void test1639065950091() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_ARRAY_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.String[]");
            }

            @Test
            @DisplayName("Boolean.class: ConvertCallException - unsupported type")
            public void test1639065950100() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BOOLEAN_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Boolean");
            }

            @Test
            @DisplayName("Byte.class: ConvertCallException - unsupported type")
            public void test1639065950109() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BYTE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Byte");
            }

            @Test
            @DisplayName("Character.class: ConvertCallException - unsupported type")
            public void test1639065950118() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(CHARACTER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Character");
            }

            @Test
            @DisplayName("Double.class: ConvertCallException - unsupported type")
            public void test1639065950127() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(DOUBLE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Double");
            }

            @Test
            @DisplayName("Float.class: ConvertCallException - unsupported type")
            public void test1639065950136() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(FLOAT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Float");
            }

            @Test
            @DisplayName("Integer.class: ConvertCallException - unsupported type")
            public void test1639065950145() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(INTEGER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Integer");
            }

            @Test
            @DisplayName("Long.class: ConvertCallException - unsupported type")
            public void test1639065950154() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(LONG_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Long");
            }

            @Test
            @DisplayName("Short.class: ConvertCallException - unsupported type")
            public void test1639065950163() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(SHORT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Short");
            }

            @Test
            @DisplayName("String.class: ConvertCallException - unsupported type")
            public void test1639065950172() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.String");
            }

        }

    }

}