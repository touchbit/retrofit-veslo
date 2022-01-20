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

package veslo.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.BriefAssertionError;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"EqualsWithItself", "ConstantConditions", "StringOperationCanBeSimplified", "EqualsBetweenInconvertibleTypes"})
@DisplayName("RawBody model tests")
public class RawBodyUnitTests {

    private static final String BLANK_BODY_STRING = "   ";
    private static final byte[] BLANK_BODY_BYTES = BLANK_BODY_STRING.getBytes();
    private static final String EMPTY_BODY_STRING = "";
    private static final byte[] EMPTY_BODY_BYTES = EMPTY_BODY_STRING.getBytes();

    @Test
    @DisplayName("Successfully instantiating nullable RawBody class with empty constructor")
    public void test1639981400649() {
        RawBody dto = new RawBody();
        assertThat("RawBody.getBody()", dto.bytes(), nullValue());
        assertThat("RawBody.getBody()", dto.string(), nullValue());
        assertThat("RawBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.isNullBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.hashCode(), is(0));
    }

    @Test
    @DisplayName("Successfully instantiating RawBody class if data is empty string")
    public void test1639065951875() {
        RawBody dto = new RawBody(EMPTY_BODY_STRING);
        assertThat("RawBody.getBody()", dto.bytes(), is(EMPTY_BODY_BYTES));
        assertThat("RawBody.getBody()", dto.string(), is(EMPTY_BODY_STRING));
        assertThat("RawBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.isNullBody(), is(false));
        assertThat("RawBody.isNullBody()", dto.hashCode(), greaterThan(0));
    }

    @Test
    @DisplayName("Successfully instantiating RawBody class if data is blank string")
    public void test1639065951886() {
        RawBody dto = new RawBody(BLANK_BODY_STRING);
        assertThat("RawBody.getBody()", dto.bytes(), is(BLANK_BODY_BYTES));
        assertThat("RawBody.getBody()", dto.string(), is(BLANK_BODY_STRING));
        assertThat("RawBody.isEmptyBody()", dto.isEmptyBody(), is(false));
        assertThat("RawBody.isNullBody()", dto.isNullBody(), is(false));
        assertThat("RawBody.isNullBody()", dto.hashCode(), greaterThan(0));
    }

    @Test
    @DisplayName("Successfully instantiating RawBody class if data is null string")
    public void test1639065951897() {
        RawBody dto = new RawBody((String) null);
        assertThat("RawBody.getBody()", dto.bytes(), nullValue());
        assertThat("RawBody.getBody()", dto.string(), nullValue());
        assertThat("RawBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.isNullBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.hashCode(), is(0));
    }

    @Test
    @DisplayName("Successfully instantiating RawBody class if data is null byte array")
    public void test1639065951908() {
        RawBody dto = new RawBody((byte[]) null);
        assertThat("RawBody.getBody()", dto.bytes(), nullValue());
        assertThat("RawBody.getBody()", dto.string(), nullValue());
        assertThat("RawBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.isNullBody(), is(true));
        assertThat("RawBody.isNullBody()", dto.hashCode(), is(0));
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): An error occurs if the body is blank")
    public void test1639065951919() {
        assertThrow(() -> new RawBody(BLANK_BODY_BYTES).assertBodyIsEmpty())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is empty byte array\n" +
                        "     but: was array length '" + BLANK_BODY_STRING.length() + "'\n");
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): no error if body = <empty string>")
    public void test1639065951930() {
        new RawBody(EMPTY_BODY_STRING).assertBodyIsEmpty();
    }

    @Test
    @DisplayName("assertBodyIsEmpty(): no error if body = <empty byte array>")
    public void test1639065951936() {
        new RawBody(EMPTY_BODY_BYTES).assertBodyIsEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): An error occurs if the body is empty")
    public void test1639065951942() {
        assertThrow(() -> new RawBody(EMPTY_BODY_BYTES).assertBodyIsNotEmpty())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is not empty byte array\n" +
                        "     but: was []\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): An error occurs if the body is null")
    public void test1639065951953() {
        assertThrow(() -> new RawBody((byte[]) null).assertBodyIsNotEmpty())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is not empty byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <not blank string>")
    public void test1639065951964() {
        new RawBody("test1637479938602").assertBodyIsNotEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <not empty byte array>")
    public void test1639065951970() {
        new RawBody("test1635887040120".getBytes()).assertBodyIsNotEmpty();
    }

    @Test
    @DisplayName("assertBodyIsNotNull(): An error occurs if the body is null")
    public void test1639065951976() {
        assertThrow(() -> new RawBody((byte[]) null).assertBodyIsNotNull())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <blank string>")
    public void test1639065951987() {
        new RawBody(BLANK_BODY_STRING).assertBodyIsNotNull();
    }

    @Test
    @DisplayName("assertBodyIsNotEmpty(): no error if body = <empty string>")
    public void test1639065951993() {
        new RawBody(EMPTY_BODY_BYTES).assertBodyIsNotNull();
    }

    @Test
    @DisplayName("assertBodyIsNull(): An error occurs if the body is empty string")
    public void test1639065951999() {
        assertThrow(() -> new RawBody(EMPTY_BODY_STRING).assertBodyIsNull())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is null\n" +
                        "     but: was array length '0'\n");
    }

    @Test
    @DisplayName("assertBodyIsNull(): An error occurs if the body is blank string")
    public void test1639065952010() {
        assertThrow(() -> new RawBody(BLANK_BODY_STRING).assertBodyIsNull())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is null\n" +
                        "     but: was array length '3'\n");
    }

    @Test
    @DisplayName("assertBodyIsNull(): no error if body = null")
    public void test1639065952021() {
        new RawBody((byte[]) null).assertBodyIsNull();
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if the body is null")
    public void test1639065952027() {
        assertThrow(() -> new RawBody((byte[]) null).assertStringBodyContains("test1637481386260"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if the body is empty")
    public void test1639065952038() {
        assertThrow(() -> new RawBody(EMPTY_BODY_BYTES).assertStringBodyContains("test1637481450008"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Response body\n" +
                        "Expected: contains 'test1637481450008'\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): An error occurs if expected value in another case")
    public void test1639065952050() {
        assertThrow(() -> new RawBody("test1637481553477").assertStringBodyContains("TEST1637481553477"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Response body\n" +
                        "Expected: contains 'TEST1637481553477'\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): no error if full match")
    public void test1639065952062() {
        new RawBody("test1637481630982").assertStringBodyContains("test1637481630982");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): no error if partial match")
    public void test1639065952068() {
        new RawBody("test1637481653432").assertStringBodyContains("1637481653432");
    }

    @Test
    @DisplayName("assertBodyContainsStrings(): several expected values")
    public void test1639065952074() {
        new RawBody("test1637481806904").assertStringBodyContains("test", "1637481806904");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): An error occurs if the body is null")
    public void test1639065952080() {
        assertThrow(() -> new RawBody((byte[]) null).assertStringBodyContainsIgnoreCase("test1637482417487"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): An error occurs if the body is empty")
    public void test1639065952091() {
        assertThrow(() -> new RawBody(EMPTY_BODY_BYTES).assertStringBodyContainsIgnoreCase("test1637482421528"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Response body\n" +
                        "Expected: contains 'test1637482421528' (ignore case)\n" +
                        "     but: does not contain\n");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if different case")
    public void test1639065952103() {
        new RawBody("test1637482424480").assertStringBodyContainsIgnoreCase("TEST1637482424480");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if full match")
    public void test1639065952109() {
        new RawBody("test1637482427644").assertStringBodyContainsIgnoreCase("test1637482427644");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): no error if partial match")
    public void test1639065952115() {
        new RawBody("test1637482430003").assertStringBodyContainsIgnoreCase("1637482430003");
    }

    @Test
    @DisplayName("assertBodyContainsIgnoreCaseStrings(): several expected values")
    public void test1639065952121() {
        new RawBody("test1637482432552").assertStringBodyContainsIgnoreCase("test", "1637482432552");
    }

    @Test
    @DisplayName("assertStringBodyIs(): An error occurs if the body is null")
    public void test1639065952127() {
        assertThrow(() -> new RawBody((byte[]) null).assertStringBodyIs("test1637483049766"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertStringBodyIs(): An error occurs if different case")
    public void test1639065952137() {
        assertThrow(() -> new RawBody("test1637483021598").assertStringBodyIs("TEST1637483021598"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: 'TEST1637483021598'\n" +
                        "     but: was 'test1637483021598'\n");
    }

    @Test
    @DisplayName("assertStringBodyIs(): no error if full match")
    public void test1639065952147() {
        new RawBody("test1637483157892").assertStringBodyIs("test1637483157892");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): An error occurs if the body is null")
    public void test1639065952153() {
        assertThrow(() -> new RawBody((byte[]) null).assertStringBodyIsIgnoreCase("test1637483049766"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: is byte array\n" +
                        "     but: was null\n");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): An error occurs if no match")
    public void test1639065952163() {
        assertThrow(() -> new RawBody("test1637483306882").assertStringBodyIsIgnoreCase("test"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("Response body\n" +
                        "Expected: 'test' (ignore case)\n" +
                        "     but: was 'test1637483306882'\n");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): no error if different case")
    public void test1639065952173() {
        new RawBody("test1637483230878").assertStringBodyIsIgnoreCase("TEST1637483230878");
    }

    @Test
    @DisplayName("assertStringBodyIsIgnoreCase(): no error if full match")
    public void test1639065952179() {
        new RawBody("test1637483233620").assertStringBodyIsIgnoreCase("test1637483233620");
    }

    @Test
    @DisplayName("equals() = true if same objects")
    public void test1639065952185() {
        final RawBody rawBody = new RawBody("test1637483554877");
        assertThat("", rawBody.equals(rawBody), is(true));
    }

    @Test
    @DisplayName("equals() = true if null (body and compared object)")
    public void test1639065952192() {
        final RawBody rawBody = new RawBody((byte[]) null);
        assertThat("", rawBody.equals(null), is(true));
    }

    @Test
    @DisplayName("equals() = false if compared object != RawBody")
    public void test1639065952199() {
        final RawBody rawBody = new RawBody("");
        assertThat("", rawBody.equals(new String()), is(false));
    }

    @Test
    @DisplayName("equals() = false if compared object = null")
    public void test1639065952206() {
        final RawBody rawBody = new RawBody("");
        assertThat("", rawBody.equals(null), is(false));
    }

    @Test
    @DisplayName("equals() = true if same data (body and compared object)")
    public void test1639065952213() {
        final RawBody rawBody1 = new RawBody("test1637483839968");
        final RawBody rawBody2 = new RawBody("test1637483839968");
        assertThat("", rawBody1.equals(rawBody2), is(true));
    }

}