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
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings({"ConstantConditions", "rawtypes"})
@DisplayName("JavaReferenceTypeConverter tests")
public class JavaReferenceTypeConverterUnitTests extends BaseUnitTest {

    private static final JavaReferenceTypeConverter CONVERTER = new JavaReferenceTypeConverter();
    private static final ResponseBodyConverter<?> RESPONSE_CONVERTER = CONVERTER.responseBodyConverter(STRING_C, AA, RTF);
    private static final String REPLACE = "REPLACE";
    private static final String UNSUPPORTED_TYPE_MSG = "Unsupported type for converter " +
            "org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaReferenceTypeConverter\n" +
            "Received: " + REPLACE + "\n";

    private static ResponseBodyConverter<?> getResponseConverter(Class dtoClass) {
        return CONVERTER.responseBodyConverter(dtoClass, AA, RTF);
    }

    @Nested
    @DisplayName(".requestBodyConverter(Type, Annotation[], Retrofit) method")
    public class ResponseBodyConverterTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639065950321() {
            assertThrow(() -> CONVERTER.responseBodyConverter(null, AA, RTF)).assertNPE("type");
            assertThrow(() -> CONVERTER.responseBodyConverter(STRING_C, null, RTF)).assertNPE("methodAnnotations");
            assertThrow(() -> CONVERTER.responseBodyConverter(STRING_C, AA, null)).assertNPE("retrofit");
        }

        @Nested
        @DisplayName(".convert(ResponseBody) method")
        public class ConvertMethodTests {

            @Test
            @DisplayName("return null if ResponseBody == null")
            public void test1639065950333() throws IOException {
                final Object result = RESPONSE_CONVERTER.convert(null);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("String.class: return null if response body is empty")
            public void test1639065950340() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final String result = (String) getResponseConverter(STRING_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("String.class: return String if response body present")
            public void test1639065950348() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "test1638617489426");
                final String result = (String) getResponseConverter(STRING_C).convert(responseBody);
                assertThat("", result, is("test1638617489426"));
            }

            @Test
            @DisplayName("Character.class: return Character if response body length = 1")
            public void test1639065950356() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "1");
                final Character result = (Character) getResponseConverter(CHARACTER_C).convert(responseBody);
                assertThat("", result, is('1'));
            }

            @Test
            @DisplayName("Character.class: return null if response body is empty")
            public void test1639065950364() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Character result = (Character) getResponseConverter(CHARACTER_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Character.class: ConvertCallException if response body length = 2")
            public void test1639065950373() {
                final ResponseBody responseBody = ResponseBody.create(null, "12");
                assertThrow(() -> getResponseConverter(CHARACTER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Character conversion error:\nexpected one character\nbut was 2");
            }

            @Test
            @DisplayName("Boolean.class: return Boolean if response body = 'true'")
            public void test1639065950382() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "true");
                final Boolean result = (Boolean) getResponseConverter(BOOLEAN_C).convert(responseBody);
                assertThat("", result, is(true));
            }

            @Test
            @DisplayName("Boolean.class: return Boolean if response body = 'false'")
            public void test1639065950390() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "false");
                final Boolean result = (Boolean) getResponseConverter(BOOLEAN_C).convert(responseBody);
                assertThat("", result, is(false));
            }

            @Test
            @DisplayName("Boolean.class: ConvertCallException if response body = 'foobar'")
            public void test1639065950398() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BOOLEAN_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was 'foobar'");
            }

            @Test
            @DisplayName("Boolean.class: return null if response body is empty")
            public void test1639170869833() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Boolean result = (Boolean) getResponseConverter(BOOLEAN_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Byte.class: return Byte if response body = " + Byte.MIN_VALUE)
            public void test1639065950407() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MIN_VALUE);
                final Byte result = (Byte) getResponseConverter(BYTE_C).convert(responseBody);
                assertThat("", result, is(Byte.MIN_VALUE));
            }

            @Test
            @DisplayName("Byte.class: return null if response body is empty")
            public void test1639170899698() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Byte result = (Byte) getResponseConverter(BYTE_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Byte.class: return Byte if response body = " + Byte.MAX_VALUE)
            public void test1639065950415() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MAX_VALUE);
                final Byte result = (Byte) getResponseConverter(BYTE_C).convert(responseBody);
                assertThat("", result, is(Byte.MAX_VALUE));
            }

            @Test
            @DisplayName("Byte.class: ConvertCallException if response body = foobar")
            public void test1639065950423() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BYTE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Byte conversion error:\nexpected byte in range -128...127\nbut was 'foobar'");
            }

            @Test
            @DisplayName("Integer.class: return Integer if response body = " + Integer.MIN_VALUE)
            public void test1639065950432() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MIN_VALUE);
                final Integer result = (Integer) getResponseConverter(INTEGER_C).convert(responseBody);
                assertThat("", result, is(Integer.MIN_VALUE));
            }

            @Test
            @DisplayName("Integer.class: return Integer if response body = " + Integer.MAX_VALUE)
            public void test1639065950440() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MAX_VALUE);
                final Integer result = (Integer) getResponseConverter(INTEGER_C).convert(responseBody);
                assertThat("", result, is(Integer.MAX_VALUE));
            }

            @Test
            @DisplayName("Integer.class: return null if response body is empty")
            public void test1639171066420() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Object result = getResponseConverter(INTEGER_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Integer.class: ConvertCallException if response body = foobar")
            public void test1639065950448() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(INTEGER_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Integer conversion error:\n" +
                                "expected integer number in range -2147483648...2147483647\n" +
                                "but was 'foobar'");
            }

            @Test
            @DisplayName("Double.class: return Double if response body = " + Double.MIN_VALUE)
            public void test1639065950459() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MIN_VALUE);
                final Double result = (Double) getResponseConverter(DOUBLE_C).convert(responseBody);
                assertThat("", result, is(Double.MIN_VALUE));
            }

            @Test
            @DisplayName("Double.class: return Double if response body = " + Double.MAX_VALUE)
            public void test1639065950467() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MAX_VALUE);
                final Double result = (Double) getResponseConverter(DOUBLE_C).convert(responseBody);
                assertThat("", result, is(Double.MAX_VALUE));
            }

            @Test
            @DisplayName("Double.class: return null if response body is empty")
            public void test1639170962903() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Object result = getResponseConverter(DOUBLE_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Double.class: ConvertCallException if response body = foobar")
            public void test1639065950475() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(DOUBLE_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Double conversion error:\n" +
                                "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                                "but was 'foobar'");
            }

            @Test
            @DisplayName("Float.class: return Float if response body = " + Float.MIN_VALUE)
            public void test1639065950486() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MIN_VALUE);
                final Float result = (Float) getResponseConverter(FLOAT_C).convert(responseBody);
                assertThat("", result, is(Float.MIN_VALUE));
            }

            @Test
            @DisplayName("Float.class: return Float if response body = " + Float.MAX_VALUE)
            public void test1639065950494() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MAX_VALUE);
                final Float result = (Float) getResponseConverter(FLOAT_C).convert(responseBody);
                assertThat("", result, is(Float.MAX_VALUE));
            }

            @Test
            @DisplayName("Float.class: return null if response body is empty")
            public void test1639170994612() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Object result = getResponseConverter(FLOAT_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Float.class: ConvertCallException if response body = foobar")
            public void test1639065950502() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(FLOAT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Float conversion error:\n" +
                                "expected float number in range 1.4E-45...3.4028235E38\n" +
                                "but was 'foobar'");
            }

            @Test
            @DisplayName("Long.class: return Long if response body = " + Long.MIN_VALUE)
            public void test1639065950513() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MIN_VALUE);
                final Long result = (Long) getResponseConverter(LONG_C).convert(responseBody);
                assertThat("", result, is(Long.MIN_VALUE));
            }

            @Test
            @DisplayName("Long.class: return Long if response body = " + Long.MAX_VALUE)
            public void test1639065950521() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MAX_VALUE);
                final Long result = (Long) getResponseConverter(LONG_C).convert(responseBody);
                assertThat("", result, is(Long.MAX_VALUE));
            }

            @Test
            @DisplayName("Long.class: return null if response body is empty")
            public void test1639171105927() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Object result = getResponseConverter(LONG_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Long.class: ConvertCallException if response body = foobar")
            public void test1639065950529() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(LONG_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Long conversion error:\n" +
                                "expected long number in range -9223372036854775808...9223372036854775807\n" +
                                "but was 'foobar'");
            }

            @Test
            @DisplayName("Short.class: return Short if response body = " + Short.MIN_VALUE)
            public void test1639065950540() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MIN_VALUE);
                final Short result = (Short) getResponseConverter(SHORT_C).convert(responseBody);
                assertThat("", result, is(Short.MIN_VALUE));
            }

            @Test
            @DisplayName("Short.class: return Short if response body = " + Short.MAX_VALUE)
            public void test1639065950548() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MAX_VALUE);
                final Short result = (Short) getResponseConverter(SHORT_C).convert(responseBody);
                assertThat("", result, is(Short.MAX_VALUE));
            }

            @Test
            @DisplayName("Short.class: return null if response body is empty")
            public void test1639171075292() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final Object result = getResponseConverter(SHORT_C).convert(responseBody);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("Short.class: ConvertCallException if response body = foobar")
            public void test1639065950556() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(SHORT_C).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Short conversion error:\n" +
                                "expected short number in range -32768...32767\n" +
                                "but was 'foobar'");
            }

            @Test
            @DisplayName("Object.class: ConvertCallException - unsupported type")
            public void test1639065950567() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(OBJ_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "java.lang.Object"));
            }

            @Test
            @DisplayName("Set.class: ConvertCallException - unsupported type")
            public void test1639065950577() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(SET_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "java.util.Set"));
            }

            @Test
            @DisplayName("List.class: ConvertCallException - unsupported type")
            public void test1639065950586() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(LIST_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "java.util.List"));
            }

            @Test
            @DisplayName("Map.class: ConvertCallException - unsupported type")
            public void test1639065950595() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(MAP_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "java.util.Map"));
            }

            @Test
            @DisplayName("String[].class: ConvertCallException - unsupported type")
            public void test1639065950604() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_ARRAY_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "java.lang.String[]"));
            }

            @Test
            @DisplayName("Boolean.TYPE: ConvertCallException - unsupported type")
            public void test1639065950613() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BOOLEAN_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "boolean"));
            }

            @Test
            @DisplayName("Byte.TYPE: ConvertCallException - unsupported type")
            public void test1639065950622() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BYTE_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "byte"));
            }

            @Test
            @DisplayName("Character.TYPE: ConvertCallException - unsupported type")
            public void test1639065950631() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_CHARACTER_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "char"));
            }

            @Test
            @DisplayName("Double.TYPE: ConvertCallException - unsupported type")
            public void test1639065950640() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_DOUBLE_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "double"));
            }

            @Test
            @DisplayName("Float.TYPE: ConvertCallException - unsupported type")
            public void test1639065950649() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_FLOAT_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "float"));
            }

            @Test
            @DisplayName("Integer.TYPE: ConvertCallException - unsupported type")
            public void test1639065950658() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_INTEGER_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "int"));
            }

            @Test
            @DisplayName("Long.TYPE: ConvertCallException - unsupported type")
            public void test1639065950667() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_LONG_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "long"));
            }

            @Test
            @DisplayName("Short.TYPE: ConvertCallException - unsupported type")
            public void test1639065950676() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_SHORT_C).convert(responseBody))
                        .assertClass(ConverterUnsupportedTypeException.class)
                        .assertMessageContains(UNSUPPORTED_TYPE_MSG.replace(REPLACE, "short"));
            }

        }

    }

}