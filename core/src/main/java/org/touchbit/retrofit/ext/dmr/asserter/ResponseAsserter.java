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

import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER extends IHeadersAsserter>
        extends ResponseAsserterBase<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> {

    private final HEADERS_ASSERTER headersAsserter;

    public ResponseAsserter(final @Nonnull IDualResponse<SUC_DTO, ERR_DTO> response, final HEADERS_ASSERTER headersAsserter) {
        super(response);
        this.headersAsserter = headersAsserter;
    }

    @Override
    @EverythingIsNonNull
    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertHeaders(final Consumer<HEADERS_ASSERTER> consumer) {
        Utils.parameterRequireNonNull(consumer, "consumer");
        consumer.accept(headersAsserter);
        addErrors(headersAsserter.getErrors());
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertSucResponse(final int expectedStatusCode,
                                                                                  final Consumer<SUC_DTO> consumer) {
        return assertSucBody(consumer).assertHttpStatusCodeIs(expectedStatusCode);
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertSucBody(final Consumer<SUC_DTO> consumer) {
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual == null) {
            assertSucBodyNotNull().blame();
        }
        softly(() -> consumer.accept(actual));
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertSucBody(final BiConsumer<SUC_DTO, SUC_DTO> consumer,
                                                                              final SUC_DTO expected) {
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual == null) {
            assertSucBodyNotNull().blame();
        }
        softly(() -> consumer.accept(actual, expected));
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertIsSucResponse() {
        if (!getResponse().isSuccessful()) {
            addErrors(new AssertionError("Received unsuccessful HTTP status code.\n" +
                    "Expected: in range 200..299\n" +
                    "  Actual: was " + getResponse().getHttpStatusCode()));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertSucBodyNotNull() {
        if (getResponse().getSucDTO() == null) {
            addErrors(new AssertionError("Successful body\n" +
                    "Expected: is not null\n" +
                    "  Actual: null"));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertSucBodyIsNull() {
        final SUC_DTO sucDTO = getResponse().getSucDTO();
        if (sucDTO != null) {
            addErrors(new AssertionError("Successful body\n" +
                    "Expected: is null\n" +
                    "  Actual: " + sucDTO));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertErrResponse(final int expectedStatusCode,
                                                                                  final Consumer<ERR_DTO> consumer) {
        return assertErrBody(consumer).assertHttpStatusCodeIs(expectedStatusCode);
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertErrBody(final Consumer<ERR_DTO> consumer) {
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual == null) {
            assertErrBodyNotNull().blame();
        }
        softly(() -> consumer.accept(actual));
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertErrBody(final BiConsumer<ERR_DTO, ERR_DTO> consumer,
                                                                              final ERR_DTO expected) {
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual == null) {
            assertErrBodyNotNull().blame();
        }
        softly(() -> consumer.accept(actual, expected));
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertIsErrResponse() {
        if (getResponse().isSuccessful()) {
            addErrors(new AssertionError("Received successful HTTP status code.\n" +
                    "Expected: in range 300..599\n" +
                    "  Actual: was " + getResponse().getHttpStatusCode()));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertErrBodyNotNull() {
        if (getResponse().getErrDTO() == null) {
            addErrors(new AssertionError("Error body\n" +
                    "Expected: is not null\n" +
                    "  Actual: null"));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertErrBodyIsNull() {
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual != null) {
            addErrors(new AssertionError("Error body\n" +
                    "Expected: is null\n" +
                    "  Actual: " + actual));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertHttpStatusCodeIs(final int expected) {
        int actual = getResponse().getHttpStatusCode();
        if (actual != expected) {
            addErrors(new AssertionError("HTTP status code\n" +
                    "Expected: is  " + expected + "\n" +
                    "  Actual: was " + actual));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> assertHttpStatusMessageIs(final String expected) {
        final String actual = getResponse().getHttpStatusMessage();
        if (actual == null && expected == null) {
            return this;
        }
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            addErrors(new AssertionError("HTTP status message\n" +
                    "Expected: is  " + expected + "\n" +
                    "  Actual: was " + actual));
        }
        return this;
    }

    public ResponseAsserter<SUC_DTO, ERR_DTO, HEADERS_ASSERTER> blame() {
        super.close();
        return this;
    }

}
