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

package veslo;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.asserter.ThrowableRunnable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.client.JavaTypeAdapterFactoryClient;
import veslo.client.LoggedMockInterceptor;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;

import java.io.File;

import static internal.test.utils.client.MockInterceptor.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static veslo.client.converter.api.ExtensionConverter.NULL_BODY_VALUE;

@DisplayName("JavaTypeAdapterFactory functional tests")
public class JavaTypeCallAdapterFactoryFuncTests extends BaseUnitTest {

    private static final String STRING = "string";
    private static final byte[] STRING_BYTES = STRING.getBytes();
    private static final String EMPTY_STRING = "";
    private static final byte[] EMPTY_STRING_BYTES = EMPTY_STRING.getBytes();
    private static final String BLANK_STRING = "    ";
    private static final byte[] BLANK_STRING_BYTES = BLANK_STRING.getBytes();
    private static final String Q_BLANK_STRING = "'" + BLANK_STRING + "'";
    private static final JavaTypeAdapterFactoryClient CLIENT = DualResponseRetrofitClientUtils
            .create(JavaTypeAdapterFactoryClient.class,
                    "http://localhost",
                    new LoggedMockInterceptor(),
                    new UniversalCallAdapterFactory(),
                    new ExtensionConverterFactory());

    @Nested
    @DisplayName("API method return Raw type")
    public class ReturnRawTypeConversionTests {

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = string")
        public void test1639065953855() {
            assertThat(CLIENT.returnByteArrayReferenceType(OK, STRING_BYTES), is(STRING_BYTES));
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if unsuccessful status and body = string")
        public void test1639065953861() {
            assertThat(CLIENT.returnByteArrayReferenceType(ERR, STRING_BYTES), is(STRING_BYTES));
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = <blank string>")
        public void test1639065953867() {
            assertThat(CLIENT.returnByteArrayReferenceType(OK, BLANK_STRING_BYTES), is(BLANK_STRING_BYTES));
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body is empty (content-length=0)")
        public void test1639065953873() {
            assertThat(CLIENT.returnByteArrayReferenceType(OK, EMPTY_STRING_BYTES), is(EMPTY_STRING_BYTES));
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body not present (HTTP status 204 'no content')")
        public void test1639065953879() {
            assertThat(CLIENT.returnByteArrayReferenceType(NO_CONTENT, EMPTY_STRING_BYTES), nullValue());
        }

        @Test
        @DisplayName("RawBody: return RawBody value if body = string")
        public void test1639065953885() {
            RawBody expected = new RawBody(STRING);
            assertThat(CLIENT.returnRawBodyReferenceType(OK, expected), is(expected));
        }

        @Test
        @DisplayName("RawBody: return RawBody value if body = null")
        public void test1639065953892() {
            RawBody expected = RawBody.nullable();
            assertThat(CLIENT.returnRawBodyReferenceType(OK, expected), is(RawBody.empty()));
        }

        @Test
        @DisplayName("RawBody: return RawBody value if unsuccessful status and body = string")
        public void test1639065953899() {
            RawBody expected = new RawBody(STRING);
            assertThat(CLIENT.returnRawBodyReferenceType(ERR, expected), is(expected));
        }

        @Test
        @DisplayName("RawBody: return RawBody value if body = <blank string>")
        public void test1639065953906() {
            RawBody expected = new RawBody(BLANK_STRING);
            assertThat(CLIENT.returnRawBodyReferenceType(OK, expected), is(expected));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body is empty (content-length=0)")
        public void test1639065953913() {
            RawBody expected = new RawBody(EMPTY_STRING);
            assertThat(CLIENT.returnRawBodyReferenceType(OK, expected), is(expected));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body not present (HTTP status 204 'no content')")
        public void test1639065953920() {
            RawBody expected = RawBody.nullable();
            assertThat(CLIENT.returnRawBodyReferenceType(NO_CONTENT, expected), is(expected));
        }

        @Test
        @DisplayName("File: return File value if body = string")
        public void test1639065953927() {
            final File file = new File("src/test/resources/not_empty.txt");
            final String expectedString = fileToString(file);
            final File actual = CLIENT.returnFileReferenceType(OK, file);
            final String actualString = fileToString(actual);
            assertThat(actualString, is(expectedString));
        }

        @Test
        @DisplayName("File: return File value if unsuccessful status and body = string")
        public void test1639065953937() {
            final File actual = CLIENT.returnFileReferenceType(ERR, STRING);
            final String actualString = fileToString(actual);
            assertThat(actualString, is(STRING));
        }

        @Test
        @DisplayName("File: return File value if body = <blank string>")
        public void test1639065953945() {
            final File actual = CLIENT.returnFileReferenceType(OK, BLANK_STRING);
            final String actualString = fileToString(actual);
            assertThat(actualString, is(BLANK_STRING));
        }

        @Test
        @DisplayName("File: return empty File if body is empty (content-length=0)")
        public void test1639065953953() {
            final File actual = CLIENT.returnFileReferenceType(OK, EMPTY_STRING);
            final String actualString = fileToString(actual);
            assertThat(actualString, is(EMPTY_STRING));
        }

        @Test
        @DisplayName("File: return empty File if body not present (HTTP status 204 'no content')")
        public void test1639065953961() {
            final File actual = CLIENT.returnFileReferenceType(NO_CONTENT, NULL_BODY_VALUE);
            assertThat(actual, nullValue());
        }

        @Test
        @DisplayName("ResourceFile: ConvertCallException for response")
        public void test1639065953969() {
            assertThrow(() -> CLIENT.returnResourceFileReferenceType(OK, new ResourceFile("not_empty.txt")))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("It is forbidden to use the ResourceFile type to convert the response body.");
        }

    }

    @Nested
    @DisplayName("API method return reference java type")
    public class ReturnReferenceTypeConversionTests {

        @Test
        @DisplayName("Object: ConverterNotFoundException")
        public void test1639065953983() {
            assertThrow(() -> CLIENT.returnObjectReferenceType(200, "test1638801870893"))
                    .assertClass(ConverterNotFoundException.class);
        }

        @Test
        @DisplayName("String: return String value if body = string")
        public void test1639065953990() {
            assertThat(CLIENT.returnStringReferenceType(OK, "string"), is("string"));
        }

        @Test
        @DisplayName("String: return String if unsuccessful status and body = string")
        public void test1639065953996() {
            assertThat(CLIENT.returnStringReferenceType(ERR, "string"), is("string"));
        }

        @Test
        @DisplayName("String: return String value if body = <blank string>")
        public void test1639065954002() {
            assertThat(CLIENT.returnStringReferenceType(OK, BLANK_STRING), is(BLANK_STRING));
        }

        @Test
        @DisplayName("String: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954008() {
            assertThat(CLIENT.returnStringReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("String: return null if body is empty (content-length=0)")
        public void test1639065954014() {
            assertThat(CLIENT.returnStringReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Character: return Character if body = a")
        public void test1639065954020() {
            assertThat(CLIENT.returnCharacterReferenceType(OK, 'a'), is('a'));
        }

        @Test
        @DisplayName("Character: return Character if unsuccessful status and body = a")
        public void test1639065954026() {
            assertThat(CLIENT.returnCharacterReferenceType(ERR, 'a'), is('a'));
        }

        @Test
        @DisplayName("Character: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954032() {
            assertThat(CLIENT.returnCharacterReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Character: return null if body is empty (content-length=0)")
        public void test1639065954038() {
            assertThat(CLIENT.returnCharacterReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Character: ConvertCallException if body = <blank string>")
        public void test1639065954044() {
            assertThrow(() -> CLIENT.returnCharacterReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Character conversion error:\n" +
                            "expected one character\n" +
                            "but was " + BLANK_STRING.length());
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = true")
        public void test1639065954054() {
            assertThat(CLIENT.returnBooleanReferenceType(OK, true), is(true));
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = false")
        public void test1639065954060() {
            assertThat(CLIENT.returnBooleanReferenceType(OK, false), is(false));
        }

        @Test
        @DisplayName("Boolean: return Boolean if unsuccessful status and body = false")
        public void test1639065954066() {
            assertThat(CLIENT.returnBooleanReferenceType(ERR, false), is(false));
        }

        @Test
        @DisplayName("Boolean: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954072() {
            assertThat(CLIENT.returnBooleanReferenceType(NO_CONTENT, "asdasd"), nullValue());
        }

        @Test
        @DisplayName("Boolean: return null if body is empty (content-length=0)")
        public void test1639065954078() {
            assertThat(CLIENT.returnBooleanReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body = 1")
        public void test1639065954084() {
            assertThrow(() -> CLIENT.returnBooleanReferenceType(OK, 1))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was '1'");
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body = <blank string>")
        public void test1639065954094() {
            assertThrow(() -> CLIENT.returnBooleanReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Byte: return Byte if body = " + Byte.MAX_VALUE)
        public void test1639065954104() {
            assertThat(CLIENT.returnByteReferenceType(OK, Byte.MAX_VALUE), is(Byte.MAX_VALUE));
        }

        @Test
        @DisplayName("Byte: return Byte if unsuccessful status and body = " + Byte.MAX_VALUE)
        public void test1639065954110() {
            assertThat(CLIENT.returnByteReferenceType(ERR, Byte.MAX_VALUE), is(Byte.MAX_VALUE));
        }

        @Test
        @DisplayName("Byte: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954116() {
            assertThat(CLIENT.returnByteReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Byte: return null if body is empty (content-length=0)")
        public void test1639065954122() {
            assertThat(CLIENT.returnByteReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Byte: ConvertCallException if body = <blank string>")
        public void test1639065954128() {
            assertThrow(() -> CLIENT.returnByteReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Byte conversion error:\n" +
                            "expected byte in range -128...127\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Integer: return Integer if body = " + Integer.MAX_VALUE)
        public void test1639065954138() {
            assertThat(CLIENT.returnIntegerReferenceType(OK, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer: return Integer if unsuccessful status and body = " + Integer.MAX_VALUE)
        public void test1639065954144() {
            assertThat(CLIENT.returnIntegerReferenceType(ERR, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("Integer: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954150() {
            assertThat(CLIENT.returnIntegerReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Integer: return null if body is empty (content-length=0)")
        public void test1639065954156() {
            assertThat(CLIENT.returnIntegerReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Integer: ConvertCallException if body = <blank string>")
        public void test1639065954162() {
            assertThrow(() -> CLIENT.returnIntegerReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Integer conversion error:\n" +
                            "expected integer number in range -2147483648...2147483647\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Double: return Double if body = " + Double.MAX_VALUE)
        public void test1639065954172() {
            assertThat(CLIENT.returnDoubleReferenceType(OK, Double.MAX_VALUE), is(Double.MAX_VALUE));
        }

        @Test
        @DisplayName("Double: return Double if unsuccessful status and body = " + Double.MAX_VALUE)
        public void test1639065954178() {
            assertThat(CLIENT.returnDoubleReferenceType(ERR, Double.MAX_VALUE), is(Double.MAX_VALUE));
        }

        @Test
        @DisplayName("Double: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954184() {
            assertThat(CLIENT.returnDoubleReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Double: return null if body is empty (content-length=0)")
        public void test1639065954190() {
            assertThat(CLIENT.returnDoubleReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Double: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954196() {
            assertThrow(() -> CLIENT.returnDoubleReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Double conversion error:\n" +
                            "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Float: return Float if body = " + Float.MAX_VALUE)
        public void test1639065954206() {
            assertThat(CLIENT.returnFloatReferenceType(OK, Float.MAX_VALUE), is(Float.MAX_VALUE));
        }

        @Test
        @DisplayName("Float: return Float if unsuccessful status and body = " + Float.MAX_VALUE)
        public void test1639065954212() {
            assertThat(CLIENT.returnFloatReferenceType(ERR, Float.MAX_VALUE), is(Float.MAX_VALUE));
        }

        @Test
        @DisplayName("Float: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954218() {
            assertThat(CLIENT.returnFloatReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Float: return null if body is empty (content-length=0)")
        public void test1639065954224() {
            assertThat(CLIENT.returnFloatReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Float: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954230() {
            assertThrow(() -> CLIENT.returnFloatReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Float conversion error:\n" +
                            "expected float number in range 1.4E-45...3.4028235E38\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Long: return Long if body = " + Long.MAX_VALUE)
        public void test1639065954240() {
            assertThat(CLIENT.returnLongReferenceType(OK, Long.MAX_VALUE), is(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Long: return Long if unsuccessful status and body = " + Long.MAX_VALUE)
        public void test1639065954246() {
            assertThat(CLIENT.returnLongReferenceType(ERR, Long.MAX_VALUE), is(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Long: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954252() {
            assertThat(CLIENT.returnLongReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Long: return null if body is empty (content-length=0)")
        public void test1639065954258() {
            assertThat(CLIENT.returnLongReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Long: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954264() {
            assertThrow(() -> CLIENT.returnLongReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Long conversion error:\n" +
                            "expected long number in range -9223372036854775808...9223372036854775807\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Short: return Short if body = " + Short.MAX_VALUE)
        public void test1639065954274() {
            assertThat(CLIENT.returnShortReferenceType(OK, Short.MAX_VALUE), is(Short.MAX_VALUE));
        }

        @Test
        @DisplayName("Short: return Short if unsuccessful status and body = " + Short.MAX_VALUE)
        public void test1639065954280() {
            assertThat(CLIENT.returnShortReferenceType(ERR, Short.MAX_VALUE), is(Short.MAX_VALUE));
        }

        @Test
        @DisplayName("Short: return null if body not present (HTTP status 204 'no content')")
        public void test1639065954286() {
            assertThat(CLIENT.returnShortReferenceType(NO_CONTENT, ""), nullValue());
        }

        @Test
        @DisplayName("Short: return null if body is empty (content-length=0)")
        public void test1639065954292() {
            assertThat(CLIENT.returnShortReferenceType(OK, ""), nullValue());
        }

        @Test
        @DisplayName("Short: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954298() {
            assertThrow(() -> CLIENT.returnShortReferenceType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Short conversion error:\n" +
                            "expected short number in range -32768...32767\n" +
                            "but was " + Q_BLANK_STRING);
        }

    }

    @Nested
    @DisplayName("API method return primitive java type")
    public class ReturnPrimitiveTypeConversionTests {

        @Test
        @DisplayName("char: return char if body = a")
        public void test1639065954314() {
            assertThat(CLIENT.returnCharPrimitiveType(OK, 'a'), is('a'));
        }

        @Test
        @DisplayName("char: return char if unsuccessful status and body = a")
        public void test1639065954320() {
            assertThat(CLIENT.returnCharPrimitiveType(ERR, 'a'), is('a'));
        }

        @Test
        @DisplayName("char: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954326() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnCharPrimitiveType(NO_CONTENT, ""), Character.TYPE);
        }

        @Test
        @DisplayName("char: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954332() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnCharPrimitiveType(OK, ""), Character.TYPE);
        }

        @Test
        @DisplayName("char: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954338() {
            assertThrow(() -> CLIENT.returnCharPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Character conversion error:\n" +
                            "expected one character\n" +
                            "but was 4");
        }

        @Test
        @DisplayName("boolean: return boolean if body = true")
        public void test1639065954348() {
            assertThat(CLIENT.returnBooleanPrimitiveType(OK, true), is(true));
        }

        @Test
        @DisplayName("boolean: return boolean if body = false")
        public void test1639065954354() {
            assertThat(CLIENT.returnBooleanPrimitiveType(OK, false), is(false));
        }

        @Test
        @DisplayName("boolean: return boolean if unsuccessful status and body = true")
        public void test1639065954360() {
            assertThat(CLIENT.returnBooleanPrimitiveType(ERR, true), is(true));
        }

        @Test
        @DisplayName("boolean: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954366() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnBooleanPrimitiveType(NO_CONTENT, ""), Boolean.TYPE);
        }

        @Test
        @DisplayName("boolean: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954372() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnBooleanPrimitiveType(OK, ""), Boolean.TYPE);
        }

        @Test
        @DisplayName("boolean: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954378() {
            assertThrow(() -> CLIENT.returnBooleanPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("byte: return byte if body = " + Byte.MAX_VALUE)
        public void test1639065954388() {
            assertThat(CLIENT.returnBytePrimitiveType(OK, Byte.MAX_VALUE), is(Byte.MAX_VALUE));
        }

        @Test
        @DisplayName("byte: return byte if unsuccessful status and body = " + Byte.MAX_VALUE)
        public void test1639065954394() {
            assertThat(CLIENT.returnBytePrimitiveType(ERR, Byte.MAX_VALUE), is(Byte.MAX_VALUE));
        }

        @Test
        @DisplayName("byte: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954400() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnBytePrimitiveType(NO_CONTENT, ""), Byte.TYPE);
        }

        @Test
        @DisplayName("byte: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954406() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnBytePrimitiveType(OK, ""), Byte.TYPE);
        }

        @Test
        @DisplayName("byte: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954412() {
            assertThrow(() -> CLIENT.returnBytePrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Byte conversion error:\n" +
                            "expected byte in range -128...127\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("int: return int if body = " + Integer.MAX_VALUE)
        public void test1639065954422() {
            assertThat(CLIENT.returnIntPrimitiveType(OK, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("int: return int if unsuccessful status and body = " + Integer.MAX_VALUE)
        public void test1639065954428() {
            assertThat(CLIENT.returnIntPrimitiveType(ERR, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("int: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954434() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnIntPrimitiveType(NO_CONTENT, ""), Integer.TYPE);
        }

        @Test
        @DisplayName("int: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954440() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnIntPrimitiveType(OK, ""), Integer.TYPE);
        }

        @Test
        @DisplayName("int: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954446() {
            assertThrow(() -> CLIENT.returnIntPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Integer conversion error:\n" +
                            "expected integer number in range -2147483648...2147483647\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("double: return double if body = " + Double.MAX_VALUE)
        public void test1639065954456() {
            assertThat(CLIENT.returnDoublePrimitiveType(OK, Double.MAX_VALUE), is(Double.MAX_VALUE));
        }

        @Test
        @DisplayName("double: return double if unsuccessful status and body = " + Double.MAX_VALUE)
        public void test1639065954462() {
            assertThat(CLIENT.returnDoublePrimitiveType(ERR, Double.MAX_VALUE), is(Double.MAX_VALUE));
        }

        @Test
        @DisplayName("double: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954468() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnDoublePrimitiveType(NO_CONTENT, ""), Double.TYPE);
        }

        @Test
        @DisplayName("double: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954474() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnDoublePrimitiveType(OK, ""), Double.TYPE);
        }

        @Test
        @DisplayName("double: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954480() {
            assertThrow(() -> CLIENT.returnDoublePrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Double conversion error:\n" +
                            "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("float: return float if body = " + Float.MAX_VALUE)
        public void test1639065954490() {
            assertThat(CLIENT.returnFloatPrimitiveType(OK, Float.MAX_VALUE), is(Float.MAX_VALUE));
        }

        @Test
        @DisplayName("float: return float if unsuccessful status and body = " + Float.MAX_VALUE)
        public void test1639065954496() {
            assertThat(CLIENT.returnFloatPrimitiveType(ERR, Float.MAX_VALUE), is(Float.MAX_VALUE));
        }

        @Test
        @DisplayName("float: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954502() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnFloatPrimitiveType(NO_CONTENT, ""), Float.TYPE);
        }

        @Test
        @DisplayName("float: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954508() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnFloatPrimitiveType(OK, ""), Float.TYPE);
        }

        @Test
        @DisplayName("float: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954514() {
            assertThrow(() -> CLIENT.returnFloatPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Float conversion error:\n" +
                            "expected float number in range 1.4E-45...3.4028235E38\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("long: return long if body = " + Long.MAX_VALUE)
        public void test1639065954524() {
            assertThat(CLIENT.returnLongPrimitiveType(OK, Long.MAX_VALUE), is(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Long: return long if unsuccessful status and body = " + Long.MAX_VALUE)
        public void test1639065954530() {
            assertThat(CLIENT.returnLongPrimitiveType(ERR, Long.MAX_VALUE), is(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Long: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954536() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnLongPrimitiveType(NO_CONTENT, ""), Long.TYPE);
        }

        @Test
        @DisplayName("Long: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954542() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnLongPrimitiveType(OK, ""), Long.TYPE);
        }

        @Test
        @DisplayName("Long: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954548() {
            assertThrow(() -> CLIENT.returnLongPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Long conversion error:\n" +
                            "expected long number in range -9223372036854775808...9223372036854775807\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("short: return short if body = " + Short.MAX_VALUE)
        public void test1639065954558() {
            assertThat(CLIENT.returnShortPrimitiveType(OK, Short.MAX_VALUE), is(Short.MAX_VALUE));
        }

        @Test
        @DisplayName("Short: return short if unsuccessful status and  body = " + Short.MAX_VALUE)
        public void test1639065954564() {
            assertThat(CLIENT.returnShortPrimitiveType(ERR, Short.MAX_VALUE), is(Short.MAX_VALUE));
        }

        @Test
        @DisplayName("Short: ConvertCallException if body not present (HTTP status 204 'no content')")
        public void test1639065954570() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnShortPrimitiveType(NO_CONTENT, ""), Short.TYPE);
        }

        @Test
        @DisplayName("Short: ConvertCallException if body is empty (content-length=0)")
        public void test1639065954576() {
            assertPrimitiveConvertCallException(() -> CLIENT.returnShortPrimitiveType(OK, ""), Short.TYPE);
        }

        @Test
        @DisplayName("Short: ConvertCallException if body = '    ' (blank string)")
        public void test1639065954582() {
            assertThrow(() -> CLIENT.returnShortPrimitiveType(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Short conversion error:\n" +
                            "expected short number in range -32768...32767\n" +
                            "but was " + Q_BLANK_STRING);
        }

    }

    private void assertPrimitiveConvertCallException(ThrowableRunnable runnable, Class<?> type) {
        assertThrow(runnable)
                .assertClass(PrimitiveConvertCallException.class)
                .assertMessageIs("Cannot convert empty response body to primitive type: " + type);
    }

}
