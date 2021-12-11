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
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A class with built-in soft checks for standard response headers.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.11.2021
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class HeadersAsserter implements IHeadersAsserter {

    public static final String H_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String H_CONNECTION = "Connection";
    public static final String H_CONTENT_TYPE = "Content-Type";
    public static final String H_ETAG = "Etag";
    public static final String H_KEEP_ALIVE = "Keep-Alive";
    public static final String H_SERVER = "Server";
    public static final String H_SET_COOKIE = "Set-Cookie";
    public static final String H_CONTENT_ENCODING = "Content-Encoding";
    public static final String H_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String H_VARY = "Vary";

    public static final String GZIP_ENCODING = "gzip";
    public static final String COMPRESS_ENCODING = "compress";
    public static final String DEFLATE_ENCODING = "deflate";
    public static final String BR_ENCODING = "br";
    public static final String CHUNKED_ENCODING = "chunked";

    private final List<Throwable> errors = new ArrayList<>();
    private final Headers headers;

    public HeadersAsserter(final @Nonnull Headers headers) {
        this.headers = headers;
    }

    public HeadersAsserter accessControlAllowOriginIsPresent() {
        return assertHeaderIsPresent(H_ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public HeadersAsserter accessControlAllowOriginNotPresent() {
        return assertHeaderNotPresent(H_ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public HeadersAsserter accessControlAllowOriginIs(final String expected) {
        return assertHeaderIs(H_ACCESS_CONTROL_ALLOW_ORIGIN, expected);
    }

    public HeadersAsserter accessControlAllowOriginContains(final String expected) {
        return assertHeaderContains(H_ACCESS_CONTROL_ALLOW_ORIGIN, expected);
    }

    public HeadersAsserter varyIsPresent() {
        return assertHeaderIsPresent(H_VARY);
    }

    public HeadersAsserter varyNotPresent() {
        return assertHeaderNotPresent(H_VARY);
    }

    public HeadersAsserter varyIs(final String expected) {
        return assertHeaderIs(H_VARY, expected);
    }

    public HeadersAsserter varyContains(final String expected) {
        return assertHeaderContains(H_VARY, expected);
    }

    public HeadersAsserter setCookieIsPresent() {
        return assertHeaderIsPresent(H_SET_COOKIE);
    }

    public HeadersAsserter setCookieNotPresent() {
        return assertHeaderNotPresent(H_SET_COOKIE);
    }

    public HeadersAsserter setCookieIs(final String expected) {
        return assertHeaderIs(H_SET_COOKIE, expected);
    }

    public HeadersAsserter setCookieContains(final String expected) {
        return assertHeaderContains(H_SET_COOKIE, expected);
    }

    public HeadersAsserter serverIsPresent() {
        return assertHeaderIsPresent(H_SERVER);
    }

    public HeadersAsserter serverNotPresent() {
        return assertHeaderNotPresent(H_SERVER);
    }

    public HeadersAsserter serverIs(final String expected) {
        return assertHeaderIs(H_SERVER, expected);
    }

    public HeadersAsserter serverContains(final String expected) {
        return assertHeaderContains(H_SERVER, expected);
    }

    public HeadersAsserter keepAliveIsPresent() {
        return assertHeaderIsPresent(H_KEEP_ALIVE);
    }

    public HeadersAsserter keepAliveNotPresent() {
        return assertHeaderNotPresent(H_KEEP_ALIVE);
    }

    public HeadersAsserter keepAliveIs(final String expected) {
        return assertHeaderIs(H_KEEP_ALIVE, expected);
    }

    public HeadersAsserter keepAliveContains(final String expected) {
        return assertHeaderContains(H_KEEP_ALIVE, expected);
    }

    public HeadersAsserter connectionIsPresent() {
        return assertHeaderIsPresent(H_CONNECTION);
    }

    public HeadersAsserter connectionNotPresent() {
        return assertHeaderNotPresent(H_CONNECTION);
    }

    public HeadersAsserter connectionIs(final String expected) {
        return assertHeaderIs(H_CONNECTION, expected);
    }

    public HeadersAsserter connectionContains(final String expected) {
        return assertHeaderContains(H_CONNECTION, expected);
    }

    public HeadersAsserter contentEncodingIsPresent() {
        return assertHeaderIsPresent(H_CONTENT_ENCODING);
    }

    public HeadersAsserter contentEncodingNotPresent() {
        return assertHeaderNotPresent(H_CONTENT_ENCODING);
    }

    public HeadersAsserter contentEncodingIs(final String expected) {
        return assertHeaderIs(H_CONTENT_ENCODING, expected);
    }

    public HeadersAsserter transferEncodingIsPresent() {
        return assertHeaderIsPresent(H_TRANSFER_ENCODING);
    }

    public HeadersAsserter transferEncodingNotPresent() {
        return assertHeaderNotPresent(H_TRANSFER_ENCODING);
    }

    public HeadersAsserter transferEncodingIs(final String expected) {
        return assertHeaderIs(H_TRANSFER_ENCODING, expected);
    }

    public HeadersAsserter eTagIsPresent() {
        return assertHeaderIsPresent(H_ETAG);
    }

    public HeadersAsserter eTagNotPresent() {
        return assertHeaderNotPresent(H_ETAG);
    }

    public HeadersAsserter contentTypeIsPresent() {
        return assertHeaderIsPresent(H_CONTENT_TYPE);
    }

    public HeadersAsserter contentTypeNotPresent() {
        return assertHeaderNotPresent(H_CONTENT_TYPE);
    }

    public HeadersAsserter contentTypeIs(final String expected) {
        return assertHeaderIs(H_CONTENT_TYPE, expected);
    }

    public HeadersAsserter contentTypeContains(final String expected) {
        return assertHeaderContains(H_CONTENT_TYPE, expected);
    }

    public HeadersAsserter assertHeaderNotPresent(final String headerName) {
        final String actual = headers.get(headerName);
        if (actual != null) {
            this.errors.add(getAssertionError("Response header '" + headerName + "'", "not present", null, actual));
        }
        return this;
    }

    public HeadersAsserter assertHeaderIsPresent(final String headerName) {
        final String actual = headers.get(headerName);
        if (actual == null) {
            this.errors.add(getAssertionError("Response header '" + headerName + "'", "is present", null, null));
        }
        return this;
    }

    public HeadersAsserter assertHeaderIs(final String headerName, final @Nonnull String expected) {
        final String actual = headers.get(headerName);
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            this.errors.add(getAssertionError("Response header '" + headerName + "'", "is", expected, actual));
        }
        return this;
    }

    public HeadersAsserter assertHeaderContains(final String headerName, final @Nonnull String expected) {
        final String actual = headers.get(headerName);
        if (actual == null || !actual.trim().toLowerCase().contains(expected.toLowerCase())) {
            this.errors.add(getAssertionError("Response header '" + headerName + "'", "contains", expected, actual));
        }
        return this;
    }

    @Nonnull
    @Override
    public List<Throwable> getErrors() {
        return this.errors;
    }

    @Override
    @EverythingIsNonNull
    public void addErrors(@Nonnull List<Throwable> throwableList) {
        this.errors.addAll(throwableList);
    }

    public HeadersAsserter blame() {
        close();
        return this;
    }

    protected AssertionError getAssertionError(final String reason,
                                               final String condition,
                                               final String expected,
                                               final String actual) {
        return new AssertionError(reason + "\n" +
                "Expected: " + condition + (expected == null ? "" : " '" + expected + "'") + "\n" +
                "  Actual: " + actual);
    }

}
