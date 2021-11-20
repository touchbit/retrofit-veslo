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

package org.touchbit.retrofit.ext.dmr.asserter;

import okhttp3.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
@DisplayName("ResponseAsserter tests")
public class ResponseAsserterUnitTests {

    @Test
    @DisplayName("NPE if constructor argument is null ")
    public void test1637375056691() {
        assertThrow(() -> new ResponseAsserter<>(null))
                .assertClass(NullPointerException.class)
                .assertMessageIs("Response required");
    }

    @Test
    @DisplayName("#close() without AssertionError if no errors")
    public void test1637375132619() {
        final IDualResponse response = mock(IDualResponse.class);
        new ResponseAsserter<>(response).close();
    }

    @Test
    @DisplayName("#close() with AssertionError if has errors")
    public void test1637375230127() {
        final IDualResponse response = mock(IDualResponse.class);
        final ResponseAsserter responseAsserter = new ResponseAsserter<>(response);
        responseAsserter.addErrors(new RuntimeException("test1637375230127"));
        assertThrow(responseAsserter::close)
                .assertClass(AssertionError.class)
                .assertMessageIs("The response contains the following errors:\ntest1637375230127");
    }

    @Test
    @DisplayName("#getErrors() and #addError(Throwable)")
    public void test1637375375437() {
        final IDualResponse response = mock(IDualResponse.class);
        final ResponseAsserter responseAsserter = new ResponseAsserter<>(response);
        assertResponseAsserterContainErrors(responseAsserter, 0);
        responseAsserter.addErrors(new RuntimeException());
        assertResponseAsserterContainErrors(responseAsserter, 1);
    }

    @Test
    @DisplayName("#getResponse() not null")
    public void test1637375692562() {
        final IDualResponse response = mock(IDualResponse.class);
        final ResponseAsserter responseAsserter = new ResponseAsserter<>(response);
        assertThat("ResponseAsserter.getResponse()", responseAsserter.getResponse(), notNullValue());
    }

    @Test
    @DisplayName("#assertHeaders(Consumer) NPE if Consumer is null")
    public void test1637375747466() {
        final IDualResponse response = mock(IDualResponse.class);
        final ResponseAsserter responseAsserter = new ResponseAsserter<>(response);
        assertThrow(() -> responseAsserter.assertHeaders(null))
                .assertClass(NullPointerException.class)
                .assertMessageIs("HeadersAsserter consumer required");
    }

    @Test
    @DisplayName("#assertHeaders(Consumer) AssertionError is not thrown if headers no errors (Closeable)")
    public void test1637375913878() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHeaders()).thenReturn(Headers.of());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response)
                .assertHeaders(HeadersAsserter::contentTypeNotPresent);
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertHeaders(Consumer) AssertionError is not thrown if headers has errors (Closeable)")
    public void test1637377238768() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHeaders()).thenReturn(Headers.of());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response)
                .assertHeaders(HeadersAsserter::connectionIsPresent);
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertSuccessfulResponse(int, Consumer) positive")
    public void test1637377600770() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getSuccessfulDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertSuccessfulResponse(200, body -> assertThat("", body, notNullValue()));
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertSuccessfulResponse(int, Consumer) negative")
    public void test1637381003323() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(500);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getSuccessfulDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        assertThrow(() -> asserter.assertSuccessfulResponse(200, body -> assertThat("", body, notNullValue())))
                .assertClass(AssertionError.class)
                .assertMessageIs("The response contains the following errors:\n" +
                        "Received unsuccessful HTTP status code.\n" +
                        "Expected: in range 200..299\n" +
                        "  Actual: was 500\n" +
                        "\n" +
                        "Can't get a successful DTO model. Response body is null.");
    }


    @Test
    @DisplayName("#assertSuccessfulBody(Consumer) positive")
    public void test1637379205239() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getSuccessfulDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertSuccessfulBody(body -> assertThat("", body, notNullValue()));
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertSuccessfulBody(Consumer) negative")
    public void test1637380841700() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(500);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getSuccessfulDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        assertThrow(() -> asserter.assertSuccessfulBody(body -> assertThat("", body, notNullValue())))
                .assertClass(AssertionError.class)
                .assertMessageIs("The response contains the following errors:\n" +
                        "Received unsuccessful HTTP status code.\n" +
                        "Expected: in range 200..299\n" +
                        "  Actual: was 500\n" +
                        "\n" +
                        "Can't get a successful DTO model. Response body is null.");
    }

    @Test
    @DisplayName("#assertIsSuccessfulResponse() positive")
    public void test1637379315054() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.isSuccessful()).thenReturn(true);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertIsSuccessfulResponse();
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertIsSuccessfulResponse() negative")
    public void test1637379359463() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.isSuccessful()).thenReturn(false);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertIsSuccessfulResponse();
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertSuccessfulDtoNotNull() positive")
    public void test1637381195499() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getSuccessfulDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertSuccessfulDtoNotNull();
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertSuccessfulDtoNotNull() negative")
    public void test1637381198515() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getSuccessfulDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertSuccessfulDtoNotNull();
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertErrorResponse(int, Consumer) positive")
    public void test1637382483874() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(400);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getErrorDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertErrorResponse(400, body -> assertThat("", body, notNullValue()));
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertErrorResponse(int, Consumer) negative")
    public void test1637382486990() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getErrorDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        assertThrow(() -> asserter.assertErrorResponse(500, body -> assertThat("", body, notNullValue())))
                .assertClass(AssertionError.class)
                .assertMessageIs("The response contains the following errors:\n" +
                        "Received successful HTTP status code.\n" +
                        "Expected: in range 300..599\n" +
                        "  Actual: was 200\n" +
                        "\n" +
                        "Can't get a error DTO model. Response body is null.");
    }

    @Test
    @DisplayName("#assertErrorBody(Consumer) positive")
    public void test1637383064502() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(400);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getErrorDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertErrorBody(body -> assertThat("", body, notNullValue()));
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertErrorBody(Consumer) negative")
    public void test1637383087995() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        when(response.getErrorDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        assertThrow(() -> asserter.assertErrorBody(body -> assertThat("", body, notNullValue())))
                .assertClass(AssertionError.class)
                .assertMessageIs("The response contains the following errors:\n" +
                        "Received successful HTTP status code.\n" +
                        "Expected: in range 300..599\n" +
                        "  Actual: was 200\n" +
                        "\n" +
                        "Can't get a error DTO model. Response body is null.");
    }

    @Test
    @DisplayName("#assertIsErrorResponse() positive")
    public void test1637383184390() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.isSuccessful()).thenReturn(false);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertIsErrorResponse();
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertIsErrorResponse() negative")
    public void test1637383195795() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.isSuccessful()).thenReturn(true);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertIsErrorResponse();
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertErrorDtoNotNull() positive")
    public void test1637383409067() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getErrorDTO()).thenReturn(new Object());
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertErrorDtoNotNull();
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertErrorDtoNotNull() negative")
    public void test1637383416953() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getErrorDTO()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertErrorDtoNotNull();
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertHttpStatusCodeIs() positive")
    public void test1637383491500() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(200);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertHttpStatusCodeIs(200);
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertHttpStatusCodeIs() negative")
    public void test1637383509805() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusCode()).thenReturn(500);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertHttpStatusCodeIs(200);
        assertResponseAsserterContainErrors(asserter, 1);
    }

    @Test
    @DisplayName("#assertHttpStatusMessageIs() positive")
    public void test1637385229255() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusMessage()).thenReturn("ok");
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertHttpStatusMessageIs("OK");
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertHttpStatusMessageIs() positive (null)")
    public void test1637385283639() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusMessage()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertHttpStatusMessageIs(null);
        assertResponseAsserterContainErrors(asserter, 0);
    }

    @Test
    @DisplayName("#assertHttpStatusMessageIs() negative")
    public void test1637385286986() {
        final IDualResponse<Object, Object> response = mock(IDualResponse.class);
        when(response.getHttpStatusMessage()).thenReturn(null);
        final ResponseAsserter<Object, Object> asserter = new ResponseAsserter<>(response);
        asserter.assertHttpStatusMessageIs("OK");
        assertResponseAsserterContainErrors(asserter, 1);
    }

    private void assertResponseAsserterContainErrors(ResponseAsserter<?, ?> ra, int errorsCount) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> assertThat("ResponseAsserter.getErrors()", ra.getErrors(), notNullValue()));
            asserter.softly(() -> assertThat("ResponseAsserter.getErrors()", ra.getErrors().size(), is(errorsCount)));
        }
    }

}
