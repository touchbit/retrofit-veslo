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

package org.touchbit.retrofit.ext.dmr.jackson;

import internal.test.utils.TestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.adapter.DualCallAdapterFactory;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import org.touchbit.retrofit.ext.dmr.jackson.cli.MockClient;
import org.touchbit.retrofit.ext.dmr.jackson.cli.model.UserDTO;

import static internal.test.utils.MockInterceptor.SUCCESS_CODE_NO_CONTENT;
import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Jackson converter functional tests")
public class JacksonFuncTests {

    protected static final MockClient MOCK_CLIENT = TestClient
            .build(MockClient.class, new DualCallAdapterFactory(), new JacksonDualConverterFactory());

    @Test
    @DisplayName("Validate and get the success text/plain string body with success HTTP status code")
    public void test1634821009703() {
        MOCK_CLIENT.textPlainString(200, "test1634821009703")
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertSuccessfulBody(body -> assertThat("Success body content", body, is("test1634821009703"))));
    }

    @Test
    @DisplayName("Validate and get the error text/plain string body with wrong HTTP status code")
    public void test1634821324193() {
        MOCK_CLIENT.textPlainString(500, "test1634821324193")
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(500)
                        .assertErrorBody(body -> assertThat("Success body content", body, is("test1634821324193"))));
    }

    @Test
    @DisplayName("Validate and get the success application/json string body with success HTTP status code")
    public void test1635881709958() {
        MOCK_CLIENT.applicationJsonString(200, "test1635881709958")
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertSuccessfulBody(body -> assertThat("Success body content", body, is("test1635881709958"))));
    }

    @Test
    @DisplayName("Validate and get the error application/json string body with wrong HTTP status code")
    public void test1635883177203() {
        MOCK_CLIENT.applicationJsonString(400, "test1635883177203")
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(400)
                        .assertErrorBody(body -> assertThat("Success body content", body, is("test1635883177203"))));
    }

    @Test
    @DisplayName("Validate success text/plain AnyBody DTO with success HTTP status code and empty body")
    public void test1635885848809() {
        MOCK_CLIENT.textPlainAnyBody(200, new AnyBody("".getBytes()))
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertSuccessfulBody(body -> assertThat("Success body content", body.string(), emptyString())));
    }

    @Test
    @DisplayName("Validate success text/plain AnyBody DTO with success HTTP status code and filled body")
    public void test1635886715203() {
        MOCK_CLIENT.textPlainAnyBody(200, new AnyBody("1".getBytes()))
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertSuccessfulBody(body -> assertThat("Success body content", body.string(), is("1"))));
    }

    @Test
    @DisplayName("Exception occurred if getting unprocessed IDualResponse generic type (int)")
    public void test1635888162581() {
        assertThrow(() -> MOCK_CLIENT.unprocessedDTO(200, 100))
                .assertClass(HttpCallException.class)
                .assertMessageContains("Failed to make API call.")
                .assertCause(cause -> cause
                        .assertClass(ConvertCallException.class)
                        .assertMessageContains("Converter not found", "and DTO type java.lang.Integer."));
    }

    @Test
    @DisplayName("Validate and get the success FormUrlEncoded string body with success HTTP status code")
    public void test1636389446342() {
        MOCK_CLIENT.formUrlEncodedTextPlainString(200, "test1636389446342")
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertHttpStatusMessageIs("Mocked response")
                        .assertHeaders(headers -> headers
                                .contentTypeIs("application/x-www-form-urlencoded; charset=utf-8"))
                        .assertSuccessfulBody(body -> assertThat("Body content", body, is("method=test1636389446342"))));
    }

    @Test
    @DisplayName("Validate success application/json jackson DTO with success HTTP status code and filled body")
    public void test1636910681764() {
        UserDTO expected = new UserDTO().setFirstName("Modest").setLastName("Modestovich");
        MOCK_CLIENT.applicationJsonJacksonDTO(200, expected)
                .assertResponse(response -> response
                        .assertHttpStatusCodeIs(200)
                        .assertSuccessfulBody(body -> assertUserDTO(body, expected)));
    }

    @Test
    @DisplayName("Successfully retrieving the AnyBody model if response body = null (successful DTO)")
    public void test1636926325040() {
        MOCK_CLIENT.textPlainAnyBody(SUCCESS_CODE_NO_CONTENT, "No Content").getSuccessfulDTO().assertBodyIsNull();
    }

    private void assertUserDTO(UserDTO actual, UserDTO expected) {
        assertThat("UserDTO not null", actual, notNullValue());
        assertThat("UserDTO.firstName", actual.getFirstName(), is(expected.getFirstName()));
        assertThat("UserDTO.lastName", actual.getLastName(), is(expected.getLastName()));
    }

}
