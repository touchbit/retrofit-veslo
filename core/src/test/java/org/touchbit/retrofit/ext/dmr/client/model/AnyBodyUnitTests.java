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
import static org.hamcrest.Matchers.is;

@DisplayName("AnyBody model tests")
public class AnyBodyUnitTests {

    private static final String BLANK_BODY_STRING = "   ";
    private static final byte[] BLANK_BODY_BYTES = BLANK_BODY_STRING.getBytes();
    private static final String EMPTY_BODY_STRING = "";
    private static final byte[] EMPTY_BODY_BYTES = EMPTY_BODY_STRING.getBytes();

    @Test
    @DisplayName("Successfully getting empty body if data is empty")
    public void test1635619789030() {
        AnyBody dto = new AnyBody("".getBytes());
        assertThat("AnyBody.getBody()", dto.bytes(), is(new byte[]{}));
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(true));
        assertThat("AnyBody.toString()", dto.string(), is(EMPTY_BODY_STRING));
    }

    @Test
    @DisplayName("Successfully getting body if data is present")
    public void test1635620281699() {
        AnyBody dto = new AnyBody("test1635620281699".getBytes());
        assertThat("AnyBody.body", dto.string(), is("test1635620281699"));
        assertThat("AnyBody.isEmptyBody()", dto.isEmptyBody(), is(false));
        assertThat("AnyBody.toString()", dto.string(), is("test1635620281699"));
    }

    @Test
    @DisplayName("AnyBody#assertBodyIsEmpty(): No error if body is empty")
    public void test1635886245565() {
        new AnyBody(EMPTY_BODY_BYTES).assertBodyIsEmpty();
    }

    @Test
    @DisplayName("AnyBody#assertBodyIsEmpty(): An error occurs if the body is blank")
    public void test1635886327262() {
        assertThrow(() -> new AnyBody(BLANK_BODY_BYTES).assertBodyIsEmpty())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is empty byte array\n" +
                        "     but: was array length '" + BLANK_BODY_STRING.length() + "'\n");
    }

    @Test
    @DisplayName("AnyBody#assertBodyIsNotEmpty(): No error if body is not empty")
    public void test1635887040120() {
        new AnyBody(BLANK_BODY_BYTES).assertBodyIsNotEmpty();
    }

    @Test
    @DisplayName("AnyBody#assertBodyIsNotEmpty(): An error occurs if the body is empty")
    public void test1635887070608() {
        assertThrow(() -> new AnyBody(EMPTY_BODY_BYTES).assertBodyIsNotEmpty())
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Response body\n" +
                        "Expected: is not empty byte array\n" +
                        "     but: was []\n");
    }

}
