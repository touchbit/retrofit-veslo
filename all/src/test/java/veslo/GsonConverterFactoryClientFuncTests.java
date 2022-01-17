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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.client.GsonDualConverterFactoryClient;
import veslo.client.LoggedMockInterceptor;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.gson.ErrDTO;
import veslo.client.gson.SucDTO;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;
import veslo.util.Utils;

import java.io.File;
import java.util.*;

import static internal.test.utils.client.MockInterceptor.*;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static veslo.client.converter.api.ExtensionConverter.NULL_BODY_VALUE;

@DisplayName("JacksonDualConverterFactory.class functional tests")
public class GsonConverterFactoryClientFuncTests extends BaseFuncTest {

    private static final Byte[] EMPTY_BYTES = new Byte[]{};
    private static final String STRING = "string";
    private static final String JSON_STRING = "\"string\"";
    private static final Byte[] STRING_BYTES = Utils.toObjectByteArray(STRING);
    private static final String EMPTY_STRING = "";
    private static final String EMPTY_JSON_STRING = "\"\"";
    private static final String BLANK_STRING = "    ";
    private static final String BLANK_JSON_STRING = "\"    \"";
    private static final Byte[] BLANK_STRING_BYTES = Utils.toObjectByteArray(BLANK_STRING);
    private static final GsonDualConverterFactoryClient CLIENT = buildClient(
            GsonDualConverterFactoryClient.class,
            new LoggedMockInterceptor(),
            new UniversalCallAdapterFactory(),
            new GsonConverterFactory());

    @Nested
    @DisplayName("API method return Raw type")
    public class ReturnRawTypeConversionTests {

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = string")
        public void test1640471190412() {
            CLIENT.returnByteArray(OK, STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, STRING_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if unsuccessful status and body = string")
        public void test1640471194242() {
            CLIENT.returnByteArray(ERR, STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(ERR)
                            .assertErrBody(BaseUnitTest::assertIs, STRING_BYTES)
                            .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return Byte[] value if body = <blank string>")
        public void test1640471200918() {
            CLIENT.returnByteArray(OK, BLANK_STRING_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, BLANK_STRING_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body is empty (content-length=0)")
        public void test1640471202981() {
            CLIENT.returnByteArray(OK, EMPTY_BYTES)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(OK)
                            .assertSucBody(BaseUnitTest::assertIs, EMPTY_BYTES)
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte[]: return empty Byte[] if body not present (HTTP status 204 'no content')")
        public void test1640471205082() {
            CLIENT.returnByteArray(NO_CONTENT, NULL_BODY_VALUE)
                    .assertResponse(asserter -> asserter
                            .assertHttpStatusCodeIs(NO_CONTENT)
                            .assertSucBodyIsNull()
                            .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("RawBody: return RawBody if body = string")
        public void test1640471209728() {
            RawBody exp = new RawBody(STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body = null")
        public void test1640471212031() {
            RawBody exp = RawBody.nullable();
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.empty())
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return RawBody if unsuccessful status and body = string")
        public void test1640471214353() {
            RawBody exp = new RawBody(STRING);
            CLIENT.returnRawBody(ERR, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, exp)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if unsuccessful status and body = null")
        public void test1640471217228() {
            RawBody exp = RawBody.nullable();
            CLIENT.returnRawBody(ERR, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.empty())
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return RawBody if body = <blank string>")
        public void test1640471219155() {
            RawBody exp = new RawBody(BLANK_STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body is empty (content-length=0)")
        public void test1640471223765() {
            RawBody exp = new RawBody(EMPTY_STRING);
            CLIENT.returnRawBody(OK, exp).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, exp)
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("RawBody: return empty RawBody if body not present (HTTP status 204 'no content')")
        public void test1640471226091() {
            CLIENT.returnRawBody(NO_CONTENT, RawBody.nullable()).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBody(BaseUnitTest::assertIs, RawBody.nullable())
                    .assertErrBody(BaseUnitTest::assertIs, RawBody.nullable()));
        }

        @Test
        @DisplayName("File: return File value if body = string")
        public void test1640471228791() {
            final File file = new File("src/test/resources/not_empty.txt");
            final String expectedString = fileToString(file);
            CLIENT.returnFile(OK, file).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(expectedString)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return File value if unsuccessful status and body = string")
        public void test1640471231545() {
            CLIENT.returnFile(ERR, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertSucBodyIsNull()
                    .assertErrBody(body -> assertThat(fileToString(body), is(JSON_STRING))));
        }

        @Test
        @DisplayName("File: return File value if body = <blank string>")
        public void test1640471235215() {
            CLIENT.returnFile(OK, BLANK_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(BLANK_JSON_STRING)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return empty File if body is empty (content-length=0)")
        public void test1640471237583() {
            CLIENT.returnFile(OK, EMPTY_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(fileToString(body), is(EMPTY_JSON_STRING)))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("File: return empty File if body not present (HTTP status 204 'no content')")
        public void test1640471239645() {
            CLIENT.returnFile(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("ResourceFile: ConvertCallException for response")
        public void test1640471244167() {
            assertThrow(() -> CLIENT.returnResourceFile(OK, new ResourceFile("not_empty.txt")))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("It is forbidden to use the ResourceFile type to convert the response body.");
        }

    }

    @SuppressWarnings("rawtypes")
    @Nested
    @DisplayName("API method return reference java type")
    public class ReturnConversionTests {

        @Test
        @DisplayName("Object: return Object if body present (Jackson converter by content type)")
        public void test1640471247184() {
            CLIENT.returnObject(OK, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, STRING)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List<DTO>: return List<SucDTO> if body = [SucDTO]")
        public void test1640471249476() {
            final SucDTO dto = new SucDTO().setMsg("test1639143742989");
            List<SucDTO> expected = Collections.singletonList(dto);
            CLIENT.returnListJacksonModel(OK, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> {
                        assertThat(body.size(), is(1));
                        assertThat(body.get(0).getMsg(), is(expected.get(0).getMsg()));
                    })
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List<DTO>: return empty List<SucDTO> if body = []")
        public void test1640471252838() {
            List<SucDTO> expected = new ArrayList<>();
            CLIENT.returnListJacksonModel(OK, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, expected)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List<DTO>: return List<ErrDTO> if unsuccessful status and body = [ErrDTO]")
        public void test1640471256780() {
            final ErrDTO dto = new ErrDTO().setCode(1010);
            List<ErrDTO> expected = Collections.singletonList(dto);
            CLIENT.returnListJacksonModel(ERR, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(body -> {
                        assertThat(body.size(), is(1));
                        assertThat(body.get(0).getCode(), is(expected.get(0).getCode()));
                    })
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("List<DTO>: ConvertCallException if body = <blank string>")
        public void test1640471260092() {
            assertThrow(() -> CLIENT.returnListJacksonModel(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type" +
                            " java.util.List<veslo.client.gson.SucDTO>\n" +
                            "java.lang.IllegalStateException: " +
                            "Expected BEGIN_ARRAY but was STRING at line 1 column 2 path $");
        }

        @Test
        @DisplayName("List<DTO>: return null if body not present (HTTP status 204 'no content')")
        public void test1640471262462() {
            CLIENT.returnListJacksonModel(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List<SucDTO>: ConvertCallException if body is empty (content-length=0)")
        public void test1640471264492() {
            assertThrow(() -> CLIENT.returnListJacksonModel(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type " +
                            "java.util.List<veslo.client.gson.SucDTO>\n" +
                            "java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 2 path $");
        }

        @Test
        @DisplayName("List: return List<SucDTO> if body = [SucDTO]")
        public void test1640471268900() {
            final SucDTO dto = new SucDTO().setMsg("test1639143742989");
            List<SucDTO> expected = Collections.singletonList(dto);
            CLIENT.returnRawList(OK, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> {
                        assertThat(body.size(), is(1));
                        assertThat(body.get(0), instanceOf(Map.class));
                        assertThat(body.get(0).toString(), is("{msg=test1639143742989}"));
                    })
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List: return empty List<Map> if body = []")
        public void test1640471271744() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("code", 1010);
            List<Map> expected = Collections.singletonList(dto);
            CLIENT.returnRawList(OK, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(body.toString(), is(expected.toString())))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("List: return List<Map> if unsuccessful status and body = [Map]")
        public void test1640471274041() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("code", 1010);
            List<Map> expected = Collections.singletonList(dto);
            CLIENT.returnRawList(ERR, expected).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(body -> {
                        assertThat(body.size(), is(1));
                        assertThat(body.get(0), instanceOf(Map.class));
                        assertThat(body.get(0).toString(), is("{code=1010}"));
                    })
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Map<String, Object>: return Map if body = {msg:foobar}")
        public void test1640471278404() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("msg", "foobar");
            CLIENT.returnMapStringObject(OK, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, dto)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Map<String, Object>: return empty Map if body = {}")
        public void test1640471281151() {
            Map<String, Object> dto = new HashMap<>();
            CLIENT.returnMapStringObject(OK, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, dto)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Map<String, Object>: return Map if unsuccessful status and body = {msg:foobar}")
        public void test1640471283451() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("msg", "foobar");
            CLIENT.returnMapStringObject(ERR, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, dto)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Map: return Map if body = {msg:foobar}")
        public void test1640471286191() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("msg", "foobar");
            CLIENT.returnRawMap(OK, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, dto)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Map: return empty Map if body = {}")
        public void test1640471290521() {
            Map<String, Object> dto = new HashMap<>();
            CLIENT.returnRawMap(OK, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, dto)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Map: return Map if unsuccessful status and body = {msg:foobar}")
        public void test1640471292410() {
            Map<String, Object> dto = new HashMap<>();
            dto.put("msg", "foobar");
            CLIENT.returnRawMap(ERR, dto).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, dto)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Jackson DTO: return SucDTO if body = present")
        public void test1640471294451() {
            final SucDTO sucDTO = new SucDTO().setMsg("foobar");
            CLIENT.returnJacksonModel(OK, sucDTO).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(body -> assertThat(body.getMsg(), is(sucDTO.getMsg())))
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Jackson DTO: return ErrDTO if unsuccessful status and body = present")
        public void test1640471300650() {
            final ErrDTO errDTO = new ErrDTO().setCode(1020);
            CLIENT.returnJacksonModel(ERR, errDTO).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(body -> assertThat(body.getCode(), is(errDTO.getCode())))
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Jackson DTO: return null if body body not present (HTTP status 204 'no content')")
        public void test1640471302713() {
            CLIENT.returnJacksonModel(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return String value if body = string")
        public void test1640471304866() {
            CLIENT.returnString(OK, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, STRING)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return String if unsuccessful status and body = string")
        public void test1640471306863() {
            CLIENT.returnString(ERR, STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertSucBodyIsNull()
                    .assertErrBody(BaseUnitTest::assertIs, STRING));
        }

        @Test
        @DisplayName("String: return String value if body = <blank string>")
        public void test1640471311205() {
            CLIENT.returnString(OK, BLANK_STRING).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, BLANK_STRING)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return null if body not present (HTTP status 204 'no content')")
        public void test1640471313279() {
            CLIENT.returnString(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("String: return null if body is empty (content-length=0)")
        public void test1640471315642() {
            CLIENT.returnString(OK, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: return Character if body = a")
        public void test1640471317759() {
            CLIENT.returnCharacter(OK, 'a').assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, 'a')
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: return Character if unsuccessful status and body = a")
        public void test1640471322743() {
            CLIENT.returnCharacter(ERR, 'a').assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, 'a')
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Character: return null if body not present (HTTP status 204 'no content')")
        public void test1640471325104() {
            CLIENT.returnCharacter(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Character: ConvertCallException if body is empty (content-length=0)")
        public void test1640471328390() {
            assertThrow(() -> CLIENT.returnCharacter(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Character\n" +
                            "Expecting character, got: ");
        }

        @Test
        @DisplayName("Character: ConvertCallException if body = <blank string>")
        public void test1640471331929() {
            assertThrow(() -> CLIENT.returnCharacter(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Character\n" +
                            "Expecting character, got:     ");
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = true")
        public void test1640471336942() {
            CLIENT.returnBoolean(OK, true).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, true)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return Boolean if body = false")
        public void test1640471339201() {
            CLIENT.returnBoolean(OK, false).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, false)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return Boolean if unsuccessful status and body = false")
        public void test1640471341700() {
            CLIENT.returnBoolean(ERR, false).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, false)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: return null if body not present (HTTP status 204 'no content')")
        public void test1640471343971() {
            CLIENT.returnBoolean(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body is empty (content-length=0)")
        public void test1640471348836() {
            assertThrow(() -> CLIENT.returnBoolean(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Boolean\n" +
                            "Expected a boolean but was: ''");
        }

        @Test
        @DisplayName("Boolean: return true if body = 1")
        public void test1640471351051() {
            CLIENT.returnBoolean(OK, 1).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, true)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Boolean: ConvertCallException if body = <blank string>")
        public void test1640471353213() {
            assertThrow(() -> CLIENT.returnBoolean(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\n" +
                            "Response body not convertible to type class java.lang.Boolean\n" +
                            "Expected a boolean but was: '    '");
        }

        @Test
        @DisplayName("Byte: return Byte if body = " + Byte.MAX_VALUE)
        public void test1640471355558() {
            CLIENT.returnByte(OK, Byte.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Byte.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte: return Byte if unsuccessful status and body = " + Byte.MAX_VALUE)
        public void test1640471360453() {
            CLIENT.returnByte(ERR, Byte.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Byte.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Byte: return null if body not present (HTTP status 204 'no content')")
        public void test1640471362738() {
            CLIENT.returnByte(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Byte: ConvertCallException if body is empty (content-length=0)")
        public void test1640471365706() {
            assertThrow(() -> CLIENT.returnByte(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Byte\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Byte: ConvertCallException if body = <blank string>")
        public void test1640471368083() {
            assertThrow(() -> CLIENT.returnByte(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Byte\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Integer: return Integer if body = " + Integer.MAX_VALUE)
        public void test1640471374009() {
            CLIENT.returnInteger(OK, Integer.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Integer.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Integer: return Integer if unsuccessful status and body = " + Integer.MAX_VALUE)
        public void test1640471376468() {
            CLIENT.returnInteger(ERR, Integer.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Integer.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Integer: return null if body not present (HTTP status 204 'no content')")
        public void test1640471379010() {
            CLIENT.returnInteger(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Integer: ConvertCallException if body is empty (content-length=0)")
        public void test1640471381231() {
            assertThrow(() -> CLIENT.returnInteger(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Integer\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Integer: ConvertCallException if body = <blank string>")
        public void test1640471386957() {
            assertThrow(() -> CLIENT.returnInteger(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Integer\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Double: return Double if body = " + Double.MAX_VALUE)
        public void test1640471389416() {
            CLIENT.returnDouble(OK, Double.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Double.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Double: return Double if unsuccessful status and body = " + Double.MAX_VALUE)
        public void test1640471391836() {
            CLIENT.returnDouble(ERR, Double.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Double.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Double: return null if body not present (HTTP status 204 'no content')")
        public void test1640471394598() {
            CLIENT.returnDouble(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Double: ConvertCallException if body is empty (content-length=0)")
        public void test1640471399384() {
            assertThrow(() -> CLIENT.returnDouble(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Double\n" +
                            "empty String");
        }

        @Test
        @DisplayName("Double: ConvertCallException if body = '    ' (blank string)")
        public void test1640471401681() {
            assertThrow(() -> CLIENT.returnDouble(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Double\n" +
                            "empty String");
        }

        @Test
        @DisplayName("Float: return Float if body = " + Float.MAX_VALUE)
        public void test1640471404099() {
            CLIENT.returnFloat(OK, Float.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Float.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Float: return Float if unsuccessful status and body = " + Float.MAX_VALUE)
        public void test1640471406257() {
            CLIENT.returnFloat(ERR, Float.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Float.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Float: return null if body not present (HTTP status 204 'no content')")
        public void test1640471411061() {
            CLIENT.returnFloat(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Float: ConvertCallException if body is empty (content-length=0)")
        public void test1640471413577() {
            assertThrow(() -> CLIENT.returnFloat(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Float\n" +
                            "empty String");
        }

        @Test
        @DisplayName("Float: ConvertCallException if body = '    ' (blank string)")
        public void test1640471416428() {
            assertThrow(() -> CLIENT.returnFloat(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Float\n" +
                            "empty String");
        }

        @Test
        @DisplayName("Long: return Long if body = " + Long.MAX_VALUE)
        public void test1640471419266() {
            CLIENT.returnLong(OK, Long.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Long.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Long: return Long if unsuccessful status and body = " + Long.MAX_VALUE)
        public void test1640471424433() {
            CLIENT.returnLong(ERR, Long.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Long.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Long: return null if body not present (HTTP status 204 'no content')")
        public void test1640471426861() {
            CLIENT.returnLong(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Long: ConvertCallException if body is empty (content-length=0)")
        public void test1640471429169() {
            assertThrow(() -> CLIENT.returnLong(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Long\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Long: ConvertCallException if body = '    ' (blank string)")
        public void test1640471431393() {
            assertThrow(() -> CLIENT.returnLong(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Long\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Short: return Short if body = " + Short.MAX_VALUE)
        public void test1640471436297() {
            CLIENT.returnShort(OK, Short.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(OK)
                    .assertSucBody(BaseUnitTest::assertIs, Short.MAX_VALUE)
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Short: return Short if unsuccessful status and body = " + Short.MAX_VALUE)
        public void test1640471438696() {
            CLIENT.returnShort(ERR, Short.MAX_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(ERR)
                    .assertErrBody(BaseUnitTest::assertIs, Short.MAX_VALUE)
                    .assertSucBodyIsNull());
        }

        @Test
        @DisplayName("Short: return null if body not present (HTTP status 204 'no content')")
        public void test1640471441097() {
            CLIENT.returnShort(NO_CONTENT, NULL_BODY_VALUE).assertResponse(asserter -> asserter
                    .assertHttpStatusCodeIs(NO_CONTENT)
                    .assertSucBodyIsNull()
                    .assertErrBodyIsNull());
        }

        @Test
        @DisplayName("Short: ConvertCallException if body is empty (content-length=0)")
        public void test1640471445645() {
            assertThrow(() -> CLIENT.returnShort(OK, EMPTY_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Short\n" +
                            "java.lang.NumberFormatException: empty String");
        }

        @Test
        @DisplayName("Short: ConvertCallException if body = '    ' (blank string)")
        public void test1640471449113() {
            assertThrow(() -> CLIENT.returnShort(OK, BLANK_STRING))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("\nResponse body not convertible to type class java.lang.Short\n" +
                            "java.lang.NumberFormatException: empty String");
        }

    }
}