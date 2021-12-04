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
import java.lang.reflect.Type;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("ConstantConditions")
@DisplayName("JavaReferenceTypeConverter tests")
public class JavaReferenceTypeConverterUnitTests extends BaseUnitTest {

    private static final JavaReferenceTypeConverter CONVERTER = new JavaReferenceTypeConverter();
    private static final ResponseBodyConverter<?> RESPONSE_CONVERTER = CONVERTER.responseBodyConverter(OBJ_T, AA, RTF);

    private static ResponseBodyConverter<?> getResponseConverter(Type dtoClass) {
        return CONVERTER.responseBodyConverter(dtoClass, AA, RTF);
    }

    @Nested
    @DisplayName(".requestBodyConverter(Type, Annotation[], Retrofit) method")
    public class ResponseBodyConverterTests {

        @Test
        @DisplayName("All parameters required")
        public void test1638717993279() {
            assertThrow(() -> CONVERTER.responseBodyConverter(null, AA, RTF)).assertNPE("type");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_T, null, RTF)).assertNPE("methodAnnotations");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_T, AA, null)).assertNPE("retrofit");
        }

        @Nested
        @DisplayName(".convert(ResponseBody) method")
        public class ConvertMethodTests {

            @Test
            @DisplayName("return null if ResponseBody == null")
            public void test1638616672953() throws IOException {
                final Object result = RESPONSE_CONVERTER.convert(null);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("String.class: Successful conversion if response body is empty")
            public void test1638617079498() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                final String result = (String) getResponseConverter(STRING_T).convert(responseBody);
                assertThat("", result, emptyString());
            }

            @Test
            @DisplayName("String.class: Successful conversion if response body present")
            public void test1638617489426() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "test1638617489426");
                final String result = (String) getResponseConverter(STRING_T).convert(responseBody);
                assertThat("", result, is("test1638617489426"));
            }

            @Test
            @DisplayName("Character.class: Successful conversion if response body length = 1")
            public void test1638625116624() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "1");
                final Character result = (Character) getResponseConverter(CHARACTER_T).convert(responseBody);
                assertThat("", result, is('1'));
            }

            @Test
            @DisplayName("Character.class: ConvertCallException if response body is empty")
            public void test1638625290221() {
                final ResponseBody responseBody = ResponseBody.create(null, "");
                assertThrow(() -> getResponseConverter(CHARACTER_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Character conversion error:\nexpected one character\nbut was 0");
            }

            @Test
            @DisplayName("Character.class: ConvertCallException if response body length = 2")
            public void test1638625382890() {
                final ResponseBody responseBody = ResponseBody.create(null, "12");
                assertThrow(() -> getResponseConverter(CHARACTER_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Character conversion error:\nexpected one character\nbut was 2");
            }

            @Test
            @DisplayName("Boolean.class: Successful conversion if response body = 'true'")
            public void test1638625449533() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "true");
                final Boolean result = (Boolean) getResponseConverter(BOOLEAN_T).convert(responseBody);
                assertThat("", result, is(true));
            }

            @Test
            @DisplayName("Boolean.class: Successful conversion if response body = 'false'")
            public void test1638625471296() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "false");
                final Boolean result = (Boolean) getResponseConverter(BOOLEAN_T).convert(responseBody);
                assertThat("", result, is(false));
            }

            @Test
            @DisplayName("Boolean.class: ConvertCallException if response body = 'foobar'")
            public void test1638625484331() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BOOLEAN_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Boolean conversion error:\nexpected true/false\nbut was foobar");
            }

            @Test
            @DisplayName("Byte.class: Successful conversion if response body = " + Byte.MIN_VALUE)
            public void test1638625823038() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MIN_VALUE);
                final Byte result = (Byte) getResponseConverter(BYTE_T).convert(responseBody);
                assertThat("", result, is(Byte.MIN_VALUE));
            }

            @Test
            @DisplayName("Byte.class: Successful conversion if response body = " + Byte.MAX_VALUE)
            public void test1638625965099() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Byte.MAX_VALUE);
                final Byte result = (Byte) getResponseConverter(BYTE_T).convert(responseBody);
                assertThat("", result, is(Byte.MAX_VALUE));
            }

            @Test
            @DisplayName("Byte.class: ConvertCallException if response body = foobar")
            public void test1638626234393() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(BYTE_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Byte conversion error:\nexpected byte in range -128...127\nbut was foobar");
            }

            @Test
            @DisplayName("Integer.class: Successful conversion if response body = " + Integer.MIN_VALUE)
            public void test1638627653003() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MIN_VALUE);
                final Integer result = (Integer) getResponseConverter(INTEGER_T).convert(responseBody);
                assertThat("", result, is(Integer.MIN_VALUE));
            }

            @Test
            @DisplayName("Integer.class: Successful conversion if response body = " + Integer.MAX_VALUE)
            public void test1638627729363() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Integer.MAX_VALUE);
                final Integer result = (Integer) getResponseConverter(INTEGER_T).convert(responseBody);
                assertThat("", result, is(Integer.MAX_VALUE));
            }

            @Test
            @DisplayName("Integer.class: ConvertCallException if response body = foobar")
            public void test1638627826989() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(INTEGER_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Integer conversion error:\n" +
                                "expected integer number in range -2147483648...2147483647\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Double.class: Successful conversion if response body = " + Double.MIN_VALUE)
            public void test1638628032700() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MIN_VALUE);
                final Double result = (Double) getResponseConverter(DOUBLE_T).convert(responseBody);
                assertThat("", result, is(Double.MIN_VALUE));
            }

            @Test
            @DisplayName("Double.class: Successful conversion if response body = " + Double.MAX_VALUE)
            public void test1638628035133() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Double.MAX_VALUE);
                final Double result = (Double) getResponseConverter(DOUBLE_T).convert(responseBody);
                assertThat("", result, is(Double.MAX_VALUE));
            }

            @Test
            @DisplayName("Double.class: ConvertCallException if response body = foobar")
            public void test1638628040548() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(DOUBLE_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Double conversion error:\n" +
                                "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Float.class: Successful conversion if response body = " + Float.MIN_VALUE)
            public void test1638628402774() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MIN_VALUE);
                final Float result = (Float) getResponseConverter(FLOAT_T).convert(responseBody);
                assertThat("", result, is(Float.MIN_VALUE));
            }

            @Test
            @DisplayName("Float.class: Successful conversion if response body = " + Float.MAX_VALUE)
            public void test1638628406233() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Float.MAX_VALUE);
                final Float result = (Float) getResponseConverter(FLOAT_T).convert(responseBody);
                assertThat("", result, is(Float.MAX_VALUE));
            }

            @Test
            @DisplayName("Float.class: ConvertCallException if response body = foobar")
            public void test1638628408811() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(FLOAT_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Float conversion error:\n" +
                                "expected float number in range 1.4E-45...3.4028235E38\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Long.class: Successful conversion if response body = " + Long.MIN_VALUE)
            public void test1638628532855() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MIN_VALUE);
                final Long result = (Long) getResponseConverter(LONG_T).convert(responseBody);
                assertThat("", result, is(Long.MIN_VALUE));
            }

            @Test
            @DisplayName("Long.class: Successful conversion if response body = " + Long.MAX_VALUE)
            public void test1638628535578() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Long.MAX_VALUE);
                final Long result = (Long) getResponseConverter(LONG_T).convert(responseBody);
                assertThat("", result, is(Long.MAX_VALUE));
            }

            @Test
            @DisplayName("Long.class: ConvertCallException if response body = foobar")
            public void test1638628539886() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(LONG_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Long conversion error:\n" +
                                "expected long number in range -9223372036854775808...9223372036854775807\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Short.class: Successful conversion if response body = " + Short.MIN_VALUE)
            public void test1638628598612() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MIN_VALUE);
                final Short result = (Short) getResponseConverter(SHORT_T).convert(responseBody);
                assertThat("", result, is(Short.MIN_VALUE));
            }

            @Test
            @DisplayName("Short.class: Successful conversion if response body = " + Short.MAX_VALUE)
            public void test1638628602451() throws IOException {
                final ResponseBody responseBody = ResponseBody.create(null, "" + Short.MAX_VALUE);
                final Short result = (Short) getResponseConverter(SHORT_T).convert(responseBody);
                assertThat("", result, is(Short.MAX_VALUE));
            }

            @Test
            @DisplayName("Short.class: ConvertCallException if response body = foobar")
            public void test1638628604794() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(SHORT_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Short conversion error:\n" +
                                "expected short number in range -32768...32767\n" +
                                "but was foobar");
            }

            @Test
            @DisplayName("Object.class: ConvertCallException - unsupported type")
            public void test1638628667544() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(OBJ_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.Object");
            }


            @Test
            @DisplayName("Set.class: ConvertCallException - unsupported type")
            public void test1638711777768() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_SET_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: java.util.Set<java.lang.String>");
            }

            @Test
            @DisplayName("List.class: ConvertCallException - unsupported type")
            public void test1638711777769() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_LIST_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: java.util.List<java.lang.String>");
            }

            @Test
            @DisplayName("Map.class: ConvertCallException - unsupported type")
            public void test1638711780317() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_MAP_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: " +
                                "java.util.Map<java.lang.String, java.lang.String>");
            }

            @Test
            @DisplayName("String[].class: ConvertCallException - unsupported type")
            public void test1638711783200() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(STRING_ARRAY_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: class java.lang.String[]");
            }

            @Test
            @DisplayName("Boolean.TYPE: ConvertCallException - unsupported type")
            public void test1638712367612() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BOOLEAN_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: boolean");
            }

            @Test
            @DisplayName("Byte.TYPE: ConvertCallException - unsupported type")
            public void test1638712440716() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_BYTE_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: byte");
            }

            @Test
            @DisplayName("Character.TYPE: ConvertCallException - unsupported type")
            public void test1638712443258() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_CHARACTER_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: char");
            }

            @Test
            @DisplayName("Double.TYPE: ConvertCallException - unsupported type")
            public void test1638712445324() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_DOUBLE_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: double");
            }

            @Test
            @DisplayName("Float.TYPE: ConvertCallException - unsupported type")
            public void test1638712447337() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_FLOAT_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: float");
            }

            @Test
            @DisplayName("Integer.TYPE: ConvertCallException - unsupported type")
            public void test1638712450404() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_INTEGER_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: int");
            }

            @Test
            @DisplayName("Long.TYPE: ConvertCallException - unsupported type")
            public void test1638712453473() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_LONG_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: long");
            }

            @Test
            @DisplayName("Short.TYPE: ConvertCallException - unsupported type")
            public void test1638712510824() {
                final ResponseBody responseBody = ResponseBody.create(null, "foobar");
                assertThrow(() -> getResponseConverter(PRIMITIVE_SHORT_T).convert(responseBody))
                        .assertClass(ConvertCallException.class)
                        .assertMessageIs("Received an unsupported type for conversion: short");
            }

        }

    }

}
