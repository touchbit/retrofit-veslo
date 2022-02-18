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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.bean.template.TemplateReplaceAll;
import veslo.bean.template.TemplateSource;
import veslo.client.DualResponseAdapterFactoryClient;
import veslo.client.LoggedMockInterceptor;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;
import veslo.util.Utils;

import java.io.File;

import static internal.test.utils.client.MockInterceptor.*;
import static org.hamcrest.Matchers.is;
import static veslo.bean.template.TemplateSourceType.RESOURCE;
import static veslo.client.converter.api.ExtensionConverter.NULL_BODY_VALUE;

@SuppressWarnings("unused")
@DisplayName("JavaTypeAdapterFactory functional tests")
public class UniversalCallAdapterFactoryFuncTests extends BaseFuncTest {

    private static final Byte[] EMPTY_BYTES = new Byte[]{};
    private static final String STRING = "string";
    private static final Byte[] STRING_BYTES = Utils.toObjectByteArray(STRING);
    private static final String EMPTY_STRING = "";
    private static final Byte[] EMPTY_STRING_BYTES = Utils.toObjectByteArray(EMPTY_STRING);
    private static final String BLANK_STRING = "    ";
    private static final Byte[] BLANK_STRING_BYTES = Utils.toObjectByteArray(BLANK_STRING);
    private static final String Q_BLANK_STRING = "'" + BLANK_STRING + "'";
    private static final DualResponseAdapterFactoryClient CLIENT = buildClient(
            DualResponseAdapterFactoryClient.class,
            new LoggedMockInterceptor(),
            new UniversalCallAdapterFactory(),
            new ExtensionConverterFactory());

    @Nested
    @DisplayName("API method return Raw type")
    public class ReturnRawTypeConversionTests {

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = string")
        public void test1640470979765() {
            CLIENT.returnByteArray(OK, STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, STRING_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if unsuccessful status and body = string")
        public void test1640470982856() {
            CLIENT.returnByteArray(ERR, STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(ERR)
                            .assertErrBody(BaseUnitTest::assertIs, STRING_BYTES)
                            .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = <blank string>")
        public void test1640470985918() {
            CLIENT.returnByteArray(OK, BLANK_STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, BLANK_STRING_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body is empty (content-length=0)")
        public void test1640470989115() {
            CLIENT.returnByteArray(OK, EMPTY_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, EMPTY_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body not present (HTTP status 204 'no content')")
        public void test1640470991530() {
            CLIENT.returnByteArray(NO_CONTENT, NULL_BODY_VALUE)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(NO_CONTENT)
                            .assertSucBodyIsNull()
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("RawBody: return RawBody if body = string")
        public void test1640470994499() {
            RawBody exp = new RawBody(STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body = null")
        public void test1640470997484() {
            RawBody exp = RawBody.nullable();
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.empty())
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return RawBody if unsuccessful status and body = string")
        public void test1640471000069() {
            RawBody exp = new RawBody(STRING);
            CLIENT.returnRawBody(ERR, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, exp)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if unsuccessful status and body = null")
        public void test1640471002811() {
            RawBody exp = RawBody.nullable();
            CLIENT.returnRawBody(ERR, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.empty())
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return RawBody if body = <blank string>")
        public void test1640471005555() {
            RawBody exp = new RawBody(BLANK_STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body is empty (content-length=0)")
        public void test1640471008412() {
            RawBody exp = new RawBody(EMPTY_STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body not present (HTTP status 204 'no content')")
        public void test1640471011546() {
            CLIENT.returnRawBody(NO_CONTENT, RawBody.nullable()).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable())
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("File: return File value if body = string")
        public void test1640471013941() {
            final File file = new File("src/test/resources/not_empty.txt");
            final String expectedString = fileToString(file);
            CLIENT.returnFile(OK, file).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(expectedString)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return File value if unsuccessful status and body = string")
        public void test1640471016919() {
            CLIENT.returnFile(ERR, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertSucBodyIsNull()
                    .assertErrBody(body -> assertThat(fileToString(body), is(STRING))));
        }

        @Test
        @DisplayName("File: return File value if body = <blank string>")
        public void test1640471019427() {
            CLIENT.returnFile(OK, BLANK_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(BLANK_STRING)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return empty File if body is empty (content-length=0)")
        public void test1640471022681() {
            CLIENT.returnFile(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(EMPTY_STRING)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return empty File if body not present (HTTP status 204 'no content')")
        public void test1640471025143() {
            CLIENT.returnFile(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("ResourceFile: ConvertCallException for response")
        public void test1640471028080() {
            assertThrow(() -> CLIENT.returnResourceFile(OK, new ResourceFile("not_empty.txt")))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("It is forbidden to use the ResourceFile type to convert the response body.");
        }

    }

    @Nested
    @DisplayName("API method return reference java type")
    public class ReturnConversionTests {

        @Test
        @DisplayName("Object: ConverterNotFoundException")
        public void test1640471032510() {
            assertThrow(() -> CLIENT.returnObject(200, "test1638801870893"))
                    .assertClass(ConverterNotFoundException.class);
        }

        @Test
        @DisplayName("String: return String value if body = string")
        public void test1640471036071() {
            CLIENT.returnString(OK, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, STRING)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return String value if body = string")
        public void test1644945734479() {
            final Notes notes = new Notes().to("1").from("2").bodyContent("3");
            String expected = "" +
                    "<note>\n" +
                    "    <to>1</to>\n" +
                    "    <from>2</from>\n" +
                    "    <heading>Reminder</heading>\n" +
                    "    <body>\n" +
                    "        <content>3</content>\n" +
                    "        <status>DRAFT</status>\n" +
                    "    </body>\n" +
                    "</note>";
            CLIENT.returnString(OK, notes).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, expected)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return String if unsuccessful status and body = string")
        public void test1640471038861() {
            CLIENT.returnString(ERR, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertSucBodyIsNull()
                    .assertErrBody(BaseUnitTest::assertIs, STRING));
        }

        @Test
        @DisplayName("String: return String value if body = <blank string>")
        public void test1640471041238() {
            CLIENT.returnString(OK, BLANK_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, BLANK_STRING)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return null if body not present (HTTP status 204 'no content')")
        public void test1640471044212() {
            CLIENT.returnString(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return null if body is empty (content-length=0)")
        public void test1640471046754() {
            CLIENT.returnString(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: return Character if body = a")
        public void test1640471049456() {
            CLIENT.returnCharacter(OK, 'a').assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, 'a')
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: return Character if unsuccessful status and body = a")
        public void test1640471052284() {
            CLIENT.returnCharacter(ERR, 'a').assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, 'a')
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Character: return null if body not present (HTTP status 204 'no content')")
        public void test1640471055480() {
            CLIENT.returnCharacter(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: return null if body is empty (content-length=0)")
        public void test1640471058258() {
            CLIENT.returnCharacter(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: ConvertCallException if body = <blank string>")
        public void test1640471060270() {
            assertThrow(() -> CLIENT.returnCharacter(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Character conversion error:\n" +
                            "expected one character\n" +
                            "but was " + BLANK_STRING.length());
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = true")
        public void test1640471063017() {
            CLIENT.returnBoolean(OK, true).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, true)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = false")
        public void test1640471065285() {
            CLIENT.returnBoolean(OK, false).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, false)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return Boolean if unsuccessful status and body = false")
        public void test1640471068419() {
            CLIENT.returnBoolean(ERR, false).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, false)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return null if body not present (HTTP status 204 'no content')")
        public void test1640471071000() {
            CLIENT.returnBoolean(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return null if body is empty (content-length=0)")
        public void test1640471074063() {
            CLIENT.returnBoolean(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body = 1")
        public void test1640471076644() {
            assertThrow(() -> CLIENT.returnBoolean(OK, 1))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was '1'");
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body = <blank string>")
        public void test1640471079389() {
            assertThrow(() -> CLIENT.returnBoolean(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Byte: return Byte if body = " + Byte.MAX_VALUE)
        public void test1640471082228() {
            CLIENT.returnByte(OK, Byte.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Byte.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte: return Byte if unsuccessful status and body = " + Byte.MAX_VALUE)
        public void test1640471085043() {
            CLIENT.returnByte(ERR, Byte.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Byte.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Byte: return null if body not present (HTTP status 204 'no content')")
        public void test1640471088829() {
            CLIENT.returnByte(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte: return null if body is empty (content-length=0)")
        public void test1640471092108() {
            CLIENT.returnByte(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte: ConvertCallException if body = <blank string>")
        public void test1640471094931() {
            assertThrow(() -> CLIENT.returnByte(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Byte conversion error:\n" +
                            "expected byte in range -128...127\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Integer: return Integer if body = " + Integer.MAX_VALUE)
        public void test1640471098037() {
            CLIENT.returnInteger(OK, Integer.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Integer.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Integer: return Integer if unsuccessful status and body = " + Integer.MAX_VALUE)
        public void test1640471101158() {
            CLIENT.returnInteger(ERR, Integer.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Integer.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Integer: return null if body not present (HTTP status 204 'no content')")
        public void test1640471103941() {
            CLIENT.returnInteger(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Integer: return null if body is empty (content-length=0)")
        public void test1640471108730() {
            CLIENT.returnInteger(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Integer: ConvertCallException if body = <blank string>")
        public void test1640471111579() {
            assertThrow(() -> CLIENT.returnInteger(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Integer conversion error:\n" +
                            "expected integer number in range -2147483648...2147483647\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Double: return Double if body = " + Double.MAX_VALUE)
        public void test1640471114462() {
            CLIENT.returnDouble(OK, Double.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Double.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Double: return Double if unsuccessful status and body = " + Double.MAX_VALUE)
        public void test1640471117818() {
            CLIENT.returnDouble(ERR, Double.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Double.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Double: return null if body not present (HTTP status 204 'no content')")
        public void test1640471120217() {
            CLIENT.returnDouble(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Double: return null if body is empty (content-length=0)")
        public void test1640471123052() {
            CLIENT.returnDouble(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Double: ConvertCallException if body = '    ' (blank string)")
        public void test1640471125933() {
            assertThrow(() -> CLIENT.returnDouble(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Double conversion error:\n" +
                            "expected double number in range 4.9E-324...1.7976931348623157E308\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Float: return Float if body = " + Float.MAX_VALUE)
        public void test1640471128257() {
            CLIENT.returnFloat(OK, Float.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Float.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Float: return Float if unsuccessful status and body = " + Float.MAX_VALUE)
        public void test1640471131432() {
            CLIENT.returnFloat(ERR, Float.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Float.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Float: return null if body not present (HTTP status 204 'no content')")
        public void test1640471134385() {
            CLIENT.returnFloat(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Float: return null if body is empty (content-length=0)")
        public void test1640471136764() {
            CLIENT.returnFloat(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Float: ConvertCallException if body = '    ' (blank string)")
        public void test1640471139521() {
            assertThrow(() -> CLIENT.returnFloat(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Float conversion error:\n" +
                            "expected float number in range 1.4E-45...3.4028235E38\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Long: return Long if body = " + Long.MAX_VALUE)
        public void test1640471142619() {
            CLIENT.returnLong(OK, Long.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Long.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Long: return Long if unsuccessful status and body = " + Long.MAX_VALUE)
        public void test1640471144919() {
            CLIENT.returnLong(ERR, Long.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Long.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Long: return null if body not present (HTTP status 204 'no content')")
        public void test1640471147781() {
            CLIENT.returnLong(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Long: return null if body is empty (content-length=0)")
        public void test1640471150626() {
            CLIENT.returnLong(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Long: ConvertCallException if body = '    ' (blank string)")
        public void test1640471153403() {
            assertThrow(() -> CLIENT.returnLong(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Long conversion error:\n" +
                            "expected long number in range -9223372036854775808...9223372036854775807\n" +
                            "but was " + Q_BLANK_STRING);
        }

        @Test
        @DisplayName("Short: return Short if body = " + Short.MAX_VALUE)
        public void test1640471156788() {
            CLIENT.returnShort(OK, Short.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Short.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Short: return Short if unsuccessful status and body = " + Short.MAX_VALUE)
        public void test1640471159673() {
            CLIENT.returnShort(ERR, Short.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Short.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Short: return null if body not present (HTTP status 204 'no content')")
        public void test1640471162559() {
            CLIENT.returnShort(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Short: return null if body is empty (content-length=0)")
        public void test1640471165754() {
            CLIENT.returnShort(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Short: ConvertCallException if body = '    ' (blank string)")
        public void test1640471168910() {
            assertThrow(() -> CLIENT.returnShort(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Short conversion error:\n" +
                            "expected short number in range -32768...32767\n" +
                            "but was " + Q_BLANK_STRING);
        }

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = RESOURCE, path = "Notes.xml")
    public static final class Notes {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

}
