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

package veslo.client.converter.api;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.ConverterUnsupportedTypeException;
import veslo.PrimitiveConvertCallException;
import veslo.client.converter.typed.BooleanConverter;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.is;
import static veslo.client.converter.api.ExtensionConverter.NULL_BODY_VALUE;
import static veslo.client.converter.api.ExtensionConverter.NULL_JSON_VALUE;

@SuppressWarnings({"ConstantConditions", "ConfusingArgumentToVarargsMethod"})
@DisplayName("ExtensionConverter.class tests")
public class ExtensionConverterUnitTests extends BaseCoreUnitTest {

    @Nested
    @DisplayName("#assertNotNullableBodyType() method tests")
    public class AssertNotNullableBodyTypeMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639683650161() {
            assertNPE(() -> TEST_CONVERTER.assertNotNullableBodyType(null), "bodyType");
        }

        @Test
        @DisplayName("No exception if reference class")
        public void test1639683736333() {
            TEST_CONVERTER.assertNotNullableBodyType(Boolean.class);
        }

        @Test
        @DisplayName("No exception if ParameterizedType")
        public void test1639683813516() {
            TEST_CONVERTER.assertNotNullableBodyType(DUAL_RESPONSE_GENERIC_STRING_TYPE);
        }

        @Test
        @DisplayName("PrimitiveConvertCallException if primitive type")
        public void test1639683852833() {
            assertThrow(() -> TEST_CONVERTER.assertNotNullableBodyType(Boolean.TYPE))
                    .assertClass(PrimitiveConvertCallException.class)
                    .assertMessageIs("Cannot convert empty response body to primitive type: boolean");
        }

    }

    @Nested
    @DisplayName("#createRequestBody(Annotation[], String) method tests")
    public class FirstCreateRequestBodyMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639684290432() {
            assertNPE(() -> TEST_CONVERTER.createRequestBody(null, ""), "methodAnnotations");
            assertNPE(() -> TEST_CONVERTER.createRequestBody(arrayOf(), (String) null), "body");
        }

        @Test
        @DisplayName("return RequestBody")
        public void test1639684287787() {
            final RequestBody requestBody = TEST_CONVERTER.createRequestBody(arrayOf(), "test");
            final String act = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(act, is("test"));
        }

    }

    @Nested
    @DisplayName("#createRequestBody(Annotation[], byte[]) method tests")
    public class SecondCreateRequestBodyMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639684284123() {
            assertNPE(() -> TEST_CONVERTER.createRequestBody(null, ""), "methodAnnotations");
            assertNPE(() -> TEST_CONVERTER.createRequestBody(arrayOf(), (String) null), "body");
        }

        @Test
        @DisplayName("return RequestBody")
        public void test1639684281041() {
            final RequestBody requestBody = TEST_CONVERTER.createRequestBody(arrayOf(), "test" .getBytes());
            final String act = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(act, is("test"));
        }

    }

    @Nested
    @DisplayName("#assertSupportedBodyType(ExtensionConverter, Object, Type...)  method tests")
    public class FirstAssertSupportedBodyTypeMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639684275121() {
            final BooleanConverter c = BooleanConverter.INSTANCE;
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(null, "", STRING_C), "converter");
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(c, (Object) null, STRING_C), "body");
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(c, "", null), "expectedTypes");
        }

        @Test
        @DisplayName("No exception if types matched")
        public void test1639684599528() {
            TEST_CONVERTER.assertSupportedBodyType(BooleanConverter.INSTANCE, "", STRING_C);
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if types matched")
        public void test1639684667300() {
            assertThrow(() -> TEST_CONVERTER.assertSupportedBodyType(BooleanConverter.INSTANCE, "", BOOLEAN_C))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            BooleanConverter.INSTANCE.getClass().getTypeName() + "\n" +
                            "Received: java.lang.String\n" +
                            "Expected: java.lang.Boolean\n");
        }

    }

    @Nested
    @DisplayName("#assertSupportedBodyType(ExtensionConverter, Type, Type...) method tests")
    public class SecondAssertSupportedBodyTypeMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639684277693() {
            final BooleanConverter c = BooleanConverter.INSTANCE;
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(null, STRING_C, STRING_C), "converter");
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(c, null, STRING_C), "bodyType");
            assertNPE(() -> TEST_CONVERTER.assertSupportedBodyType(c, STRING_C, null), "expectedTypes");
        }

        @Test
        @DisplayName("No exception if types matched")
        public void test1639684621647() {
            TEST_CONVERTER.assertSupportedBodyType(BooleanConverter.INSTANCE, STRING_C, STRING_C);
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if types matched")
        public void test1639684671094() {
            assertThrow(() -> TEST_CONVERTER.assertSupportedBodyType(BooleanConverter.INSTANCE, STRING_C, BOOLEAN_C))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            BooleanConverter.INSTANCE.getClass().getTypeName() + "\n" +
                            "Received: java.lang.String\n" +
                            "Expected: java.lang.Boolean\n");
        }

    }

    @Nested
    @DisplayName("#isForceNullBodyValue(Object) method tests")
    public class FirstIsForceNullBodyValueMethodTests {

        @Test
        @DisplayName("return true if body == NULL_BODY_VALUE")
        public void test1639684929554() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue((Object) NULL_BODY_VALUE);
            assertThat(result, is(true));
        }

        @Test
        @DisplayName("return false if body == foobar")
        public void test1639685019101() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue((Object) "foobar");
            assertThat(result, is(false));
        }

        @Test
        @DisplayName("return false if body == null")
        public void test1639685049394() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue((Object) null);
            assertThat(result, is(false));
        }

    }

    @Nested
    @DisplayName("#isForceNullBodyValue(String) method tests")
    public class SecondIsForceNullBodyValueMethodTests {

        @Test
        @DisplayName("return true if body == NULL_BODY_VALUE")
        public void test1639685081748() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue(NULL_BODY_VALUE);
            assertThat(result, is(true));
        }

        @Test
        @DisplayName("return false if body == foobar")
        public void test1639685084216() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue("foobar");
            assertThat(result, is(false));
        }

        @Test
        @DisplayName("return false if body == null")
        public void test1639685087079() {
            final boolean result = TEST_CONVERTER.isForceNullBodyValue(null);
            assertThat(result, is(false));
        }

    }

    @Nested
    @DisplayName("#isForceNullJsonValue(Object) method tests")
    public class FirstIsForceNullJsonValueMethodTests {

        @Test
        @DisplayName("return true if body == NULL_JSON_VALUE")
        public void test1640471598667() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue((Object) NULL_JSON_VALUE);
            assertThat(result, is(true));
        }

        @Test
        @DisplayName("return false if body == foobar")
        public void test1640471600917() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue((Object) "foobar");
            assertThat(result, is(false));
        }

        @Test
        @DisplayName("return false if body == null")
        public void test1640471603876() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue((Object) null);
            assertThat(result, is(false));
        }

    }

    @Nested
    @DisplayName("#isForceNullJsonValue(String) method tests")
    public class SecondIsForceNullJsonValueMethodTests {

        @Test
        @DisplayName("return true if body == NULL_JSON_VALUE")
        public void test1639685180949() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue(NULL_JSON_VALUE);
            assertThat(result, is(true));
        }

        @Test
        @DisplayName("return false if body == foobar")
        public void test1639685183809() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue("foobar");
            assertThat(result, is(false));
        }

        @Test
        @DisplayName("return false if body == null")
        public void test1639685186128() {
            final boolean result = TEST_CONVERTER.isForceNullJsonValue(null);
            assertThat(result, is(false));
        }

    }

}
