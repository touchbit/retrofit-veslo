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

package org.touchbit.retrofit.ext.dmr.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"EqualsWithItself", "ConstantConditions", "StringOperationCanBeSimplified", "EqualsBetweenInconvertibleTypes"})
@DisplayName("AnyBody model tests")
public class AnyBodyUnitTests {

    private static final String BLANK_BODY_STRING = "   ";
    private static final byte[] BLANK_BODY_BYTES = BLANK_BODY_STRING.getBytes();
    private static final String EMPTY_BODY_STRING = "";
    private static final byte[] EMPTY_BODY_BYTES = EMPTY_BODY_STRING.getBytes();

    @Test
    @DisplayName("Successfully instantiating AnyBody class if data is empty string")
    public void test1635619789030() {
        AnyBody dto = new AnyBody(EMPTY_BODY_STRING);
        assertThat("AnyBody.getBody()", dto.bytes(), is(EMPTY_BODY_BYTES));
        assertThat("AnyBody.getBody()", dto.string(), is(EMPTY_BODY_STRING));
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("AnyBody.isNullBody()", dto.isNullBody(), is(false));
        assertThat("AnyBody.isNullBody()", dto.hashCode(), greaterThan(0));
    }

    @Test
    @DisplayName("Successfully instantiating AnyBody class if data is blank string")
    public void test1635620281699() {
        AnyBody dto = new AnyBody(BLANK_BODY_STRING);
        assertThat("AnyBody.getBody()", dto.bytes(), is(BLANK_BODY_BYTES));
        assertThat("AnyBody.getBody()", dto.string(), is(BLANK_BODY_STRING));
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(false));
        assertThat("AnyBody.isNullBody()", dto.isNullBody(), is(false));
        assertThat("AnyBody.isNullBody()", dto.hashCode(), greaterThan(0));
    }

    @Test
    @DisplayName("Successfully instantiating AnyBody class if data is null string")
    public void test1635886245565() {
        AnyBody dto = new AnyBody((String) null);
        assertThat("AnyBody.getBody()", dto.bytes(), nullValue());
        assertThat("AnyBody.getBody()", dto.string(), nullValue());
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("AnyBody.isNullBody()", dto.isNullBody(), is(true));
        assertThat("AnyBody.isNullBody()", dto.hashCode(), is(0));
    }

    @Test
    @DisplayName("Successfully instantiating AnyBody class if data is null byte array")
    public void test1637479501031() {
        AnyBody dto = new AnyBody((byte[]) null);
        assertThat("AnyBody.getBody()", dto.bytes(), nullValue());
        assertThat("AnyBody.getBody()", dto.string(), nullValue());
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("AnyBody.isNullBody()", dto.isNullBody(), is(true));
        assertThat("AnyBody.isNullBody()", dto.hashCode(), is(0));
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): An error occurs if the body is blank")
    public void test1635886327262() {
        assertThrow(() -> new AnyBody(BLANK_BODY_BYTES).assertBodyIsEmpty())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is empty byte array\n" +
                        "     but: was array length '" + BLANK_BODY_STRING.length() + "'\n");
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): no error if body = <empty string>")
    public void test1637479578838() {
        new AnyBody(EMPTY_BODY_STRING).assertBodyIsEmpty();
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): no error if body = <empty byte array>")
    public void test1637479640678() {
        new AnyBody(EMPTY_BODY_BYTES).assertBodyIsEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): An error occurs if the body is empty")
    public void test1637479897397() {
        assertThrow(() -> new AnyBody(EMPTY_BODY_BYTES).assertBodyIsNotEmpty())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is not empty byte array\n" +
                        "     but: was []\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): An error occurs if the body is null")
    public void test1637480057727() {
        assertThrow(() -> new AnyBody((byte[]) null).assertBodyIsNotEmpty())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is not empty byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <not blank string>")
    public void test1637479938602() {
        new AnyBody("test1637479938602").assertBodyIsNotEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <not empty byte array>")
    public void test1635887040120() {
        new AnyBody("test1635887040120".getBytes()).assertBodyIsNotEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotNull(): An error occurs if the body is null")
    public void test1637480200215() {
        assertThrow(() -> new AnyBody((byte[]) null).assertBodyIsNotNull())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <blank string>")
    public void test1637480323225() {
        new AnyBody(BLANK_BODY_STRING).assertBodyIsNotNull();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <empty string>")
    public void test1637480336285() {
        new AnyBody(EMPTY_BODY_BYTES).assertBodyIsNotNull();
    }

    @Test
    @DisplayName("assertBodyIsNull(): An error occurs if the body is empty string")
    public void test1637480386236() {
        assertThrow(() -> new AnyBody(EMPTY_BODY_STRING).assertBodyIsNull())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is null\n" +
                        "     but: was array length '0'\n");
    }

    @Test
    @DisplayName("assertBodyIsNull(): An error occurs if the body is blank string")
    public void test1637480417679() {
        assertThrow(() -> new AnyBody(BLANK_BODY_STRING).assertBodyIsNull())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is null\n" +
                        "     but: was array length '3'\n");
    }

    @Test
    @DisplayName("assertBodyIsNull(): no error if body = null")
    public void test1637480509582() {
        new AnyBody((byte[]) null).assertBodyIsNull();
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if the body is null")
    public void test1637481386260() {
        assertThrow(() -> new AnyBody((byte[]) null).assertStringBodyContains("test1637481386260"))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if the body is empty")
    public void test1637481450008() {
        assertThrow(() -> new AnyBody(EMPTY_BODY_BYTES).assertStringBodyContains("test1637481450008"))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "The response contains the following errors:\n" +
                        "Response body\n" +
                        "Expected: contains 'test1637481450008'\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if expected value in another case")
    public void test1637481553477() {
        assertThrow(() -> new AnyBody("test1637481553477").assertStringBodyContains("TEST1637481553477"))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "The response contains the following errors:\n" +
                        "Response body\n" +
                        "Expected: contains 'TEST1637481553477'\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): no error if full match")
    public void test1637481630982() {
        new AnyBody("test1637481630982").assertStringBodyContains("test1637481630982");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): no error if partial match")
    public void test1637481653432() {
        new AnyBody("test1637481653432").assertStringBodyContains("1637481653432");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): several expected values")
    public void test1637481806904() {
        new AnyBody("test1637481806904").assertStringBodyContains("test", "1637481806904");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): An error occurs if the body is null")
    public void test1637482417487() {
        assertThrow(() -> new AnyBody((byte[]) null).assertStringBodyContainsIgnoreCase("test1637482417487"))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): An error occurs if the body is empty")
    public void test1637482421528() {
        assertThrow(() -> new AnyBody(EMPTY_BODY_BYTES).assertStringBodyContainsIgnoreCase("test1637482421528"))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "The response contains the following errors:\n" +
                        "Response body\n" +
                        "Expected: contains 'test1637482421528' (ignore case)\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if different case")
    public void test1637482424480() {
        new AnyBody("test1637482424480").assertStringBodyContainsIgnoreCase("TEST1637482424480");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if full match")
    public void test1637482427644() {
        new AnyBody("test1637482427644").assertStringBodyContainsIgnoreCase("test1637482427644");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if partial match")
    public void test1637482430003() {
        new AnyBody("test1637482430003").assertStringBodyContainsIgnoreCase("1637482430003");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): several expected values")
    public void test1637482432552() {
        new AnyBody("test1637482432552").assertStringBodyContainsIgnoreCase("test", "1637482432552");
    }

    @Test
    @DisplayName("assertStringBodyIs(): An error occurs if the body is null")
    public void test1637483049766() {
        assertThrow(() -> new AnyBody((byte[]) null).assertStringBodyIs("test1637483049766"))
                .assertClass(AssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertStringBodyIs(): An error occurs if different case")
    public void test1637483021598() {
        assertThrow(() -> new AnyBody("test1637483021598").assertStringBodyIs("TEST1637483021598"))
                .assertClass(AssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: 'TEST1637483021598'\n" +
                        "     but: was 'test1637483021598'\n");
    }

    @Test
    @DisplayName("assertStringBodyIs(): no error if full match")
    public void test1637483157892() {
        new AnyBody("test1637483157892").assertStringBodyIs("test1637483157892");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): An error occurs if the body is null")
    public void test1637483228563() {
        assertThrow(() -> new AnyBody((byte[]) null).assertStringBodyIsIgnoreCase("test1637483049766"))
                .assertClass(AssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): An error occurs if no match")
    public void test1637483306882() {
        assertThrow(() -> new AnyBody("test1637483306882").assertStringBodyIsIgnoreCase("test"))
                .assertClass(AssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: 'test' (ignore case)\n" +
                        "     but: was 'test1637483306882'\n");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): no error if different case")
    public void test1637483230878() {
        new AnyBody("test1637483230878").assertStringBodyIsIgnoreCase("TEST1637483230878");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): no error if full match")
    public void test1637483233620() {
        new AnyBody("test1637483233620").assertStringBodyIsIgnoreCase("test1637483233620");
    }

    @Test
    @DisplayName("equals() = true if same objects")
    public void test1637483554877() {
        final AnyBody anyBody = new AnyBody("test1637483554877");
        assertThat("", anyBody.equals(anyBody), is(true));
    }

    @Test
    @DisplayName("equals() = true if null (body and compared object)")
    public void test1637483617976() {
        final AnyBody anyBody = new AnyBody((byte[]) null);
        assertThat("", anyBody.equals(null), is(true));
    }

    @Test
    @DisplayName("equals() = false if compared object != AnyBody")
    public void test1637483724496() {
        final AnyBody anyBody = new AnyBody("");
        assertThat("", anyBody.equals(new String()), is(false));
    }

    @Test
    @DisplayName("equals() = false if compared object = null")
    public void test1637483797632() {
        final AnyBody anyBody = new AnyBody("");
        assertThat("", anyBody.equals(null), is(false));
    }

    @Test
    @DisplayName("equals() = true if same data (body and compared object)")
    public void test1637483839968() {
        final AnyBody anyBody1 = new AnyBody("test1637483839968");
        final AnyBody anyBody2 = new AnyBody("test1637483839968");
        assertThat("", anyBody1.equals(anyBody2), is(true));
    }

}
