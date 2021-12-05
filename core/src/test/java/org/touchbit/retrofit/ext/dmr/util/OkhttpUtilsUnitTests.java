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

package org.touchbit.retrofit.ext.dmr.util;

import okhttp3.*;
import okio.Buffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static internal.test.utils.asserter.ThrowableAsserter.assertUtilityClassException;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("OkhttpUtils class tests")
public class OkhttpUtilsUnitTests {

    @Test
    @DisplayName("OkhttpUtils is utility class")
    public void test1639065948117() {
        assertUtilityClassException(OkhttpUtils.class);
    }

    @Test
    @DisplayName("#bodyHasUnknownEncoding() return true if Content-Encoding: utf-8")
    public void test1639065948123() {
        final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "utf-8"));
        assertThat("utf-8 is unknown encoding", result, is(true));
    }

    @Test
    @DisplayName("#bodyHasUnknownEncoding() return false if Content-Encoding: identity")
    public void test1639065948130() {
        final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "identity"));
        assertThat("identity is unknown encoding", result, is(false));
    }

    @Test
    @DisplayName("#bodyHasUnknownEncoding() return false if Content-Encoding: gzip")
    public void test1639065948137() {
        final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "gzip"));
        assertThat("gzip is unknown encoding", result, is(false));
    }

    @Test
    @DisplayName("#bodyHasUnknownEncoding() return false if Content-Encoding not present")
    public void test1639065948144() {
        final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of());
        assertThat("unknown encoding", result, is(false));
    }

    @Test
    @DisplayName("#bodyHasUnknownEncoding() NPE if parameter 'headers' is null")
    public void test1639065948151() {
        assertThrow(() -> OkhttpUtils.bodyHasUnknownEncoding(null)).assertNPE("headers");
    }

    @Test
    @DisplayName("#isPlaintext() false if buffer contains not ISO control")
    public void test1639065948157() {
        Buffer buffer = new Buffer();
        buffer.write(new byte[]{(byte) 17});
        assertThat("", OkhttpUtils.isPlaintext(buffer), is(false));
    }

    @Test
    @DisplayName("#isPlaintext() true if buffer contains ISO controls")
    public void test1639065948165() {
        Buffer buffer = new Buffer();
        buffer.write(new byte[]{(byte) 28}); // ISO Control
        assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
        buffer = new Buffer();
        buffer.write(new byte[]{(byte) 46}); // Whitespace Control
        assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
        buffer = new Buffer();
        buffer.write("test1637759639496".getBytes()); // just in case
        assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
    }

    @Test
    @DisplayName("#isPlaintext() EOF -> false")
    public void test1639065948179() {
        final Buffer mock = mock(Buffer.class);
        when(mock.size()).thenThrow(new RuntimeException());
        assertThat("", OkhttpUtils.isPlaintext(mock), is(false));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if MediaType = null")
    public void test1639065948187() {
        assertThat("", OkhttpUtils.getCharset((MediaType) null), is(UTF_8));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if MediaType not contains charset")
    public void test1639065948193() {
        assertThat("", OkhttpUtils.getCharset(MediaType.get("a/b")), is(UTF_8));
    }

    @Test
    @DisplayName("#getCharset() return utf-16 if MediaType contains utf-16 charset")
    public void test1639065948199() {
        assertThat("", OkhttpUtils.getCharset(MediaType.get("a/b; charset=utf-16")), is(UTF_16));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if ResponseBody is null")
    public void test1639065948205() {
        assertThat("", OkhttpUtils.getCharset((ResponseBody) null), is(UTF_8));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if RequestBody is null")
    public void test1639065948211() {
        assertThat("", OkhttpUtils.getCharset((RequestBody) null), is(UTF_8));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if ResponseBody contains charset")
    public void test1639065948217() {
        final ResponseBody mock = mock(ResponseBody.class);
        when(mock.contentType()).thenReturn(MediaType.get("a/b; charset=utf-16"));
        assertThat("", OkhttpUtils.getCharset(mock), is(UTF_16));
    }

    @Test
    @DisplayName("#getCharset() return utf-8 if RequestBody contains charset")
    public void test1639065948225() {
        final RequestBody mock = mock(RequestBody.class);
        when(mock.contentType()).thenReturn(MediaType.get("a/b; charset=utf-16"));
        assertThat("", OkhttpUtils.getCharset(mock), is(UTF_16));
    }

    @Test
    @DisplayName("#responseToString() NPE if 'response' is null")
    public void test1639065948233() {
        assertThrow(() -> OkhttpUtils.responseToString(null)).assertNPE("response");
    }

    @Test
    @DisplayName("#responseToString() without body")
    public void test1639065948239() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("");
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of());
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 0 http://localhost/\n" +
                "Response headers: (absent)\n" +
                "Response body: (absent)\n"));
    }

    @Test
    @DisplayName("#responseToString() without body (HEAD request)")
    public void test1639065948255() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("HEAD");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("");
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of());
        when(response.body()).thenReturn(ResponseBody.create(null, ""));
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 0 http://localhost/\n" +
                "Response headers: (absent)\n" +
                "Response body: (absent)\n"));
    }

    @Test
    @DisplayName("#responseToString() with plain body")
    public void test1639065948273() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("OK");
        when(response.code()).thenReturn(200);
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of());
        when(response.body()).thenReturn(ResponseBody.create(MediaType.parse("text/plain"), "test1637768109234"));
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 200 OK http://localhost/\n" +
                "Response headers: (absent)\n" +
                "Response body: (17-byte body)\n" +
                "  test1637768109234\n"));
    }

    @Test
    @DisplayName("#responseToString() with empty body")
    public void test1639065948293() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("OK");
        when(response.code()).thenReturn(200);
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of());
        when(response.body()).thenReturn(ResponseBody.create(null, ""));
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 200 OK http://localhost/\n" +
                "Response headers: (absent)\n" +
                "Response body: (0-byte body)\n"));
    }

    @Test
    @DisplayName("#responseToString() with binary body")
    public void test1639065948312() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("OK");
        when(response.code()).thenReturn(200);
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of("Content-type", "application/octet-stream"));
        when(response.body()).thenReturn(ResponseBody.create(null, new byte[]{(byte) 17}));
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 200 OK http://localhost/\n" +
                "Response headers:\n" +
                "  Content-type: application/octet-stream\n" +
                "Response body: (binary 1-byte body omitted)\n"));
    }

    @Test
    @DisplayName("#responseToString() with Content-Length: -1 and unknown encoding")
    public void test1639065948332() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("OK");
        when(response.code()).thenReturn(200);
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of("Content-Encoding", "unknown"));
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.contentLength()).thenReturn(-1L);
        when(responseBody.bytes()).thenReturn("test1637770539680".getBytes());
        when(responseBody.source()).thenReturn(ResponseBody.create(null, "test1637770539680").source());
        when(response.body()).thenReturn(responseBody);
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 200 OK http://localhost/\n" +
                "Response headers:\n" +
                "  Content-Encoding: unknown\n" +
                "Response body: (encoded body omitted)\n"));
    }

    @Test
    @DisplayName("#responseToString() with gzip body")
    public void test1639065948356() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        final Response response = mock(Response.class);
        when(response.message()).thenReturn("OK");
        when(response.code()).thenReturn(200);
        when(response.request()).thenReturn(request);
        when(response.headers()).thenReturn(Headers.of("Content-Encoding", "gzip"));
        ByteArrayOutputStream baus = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baus);
        gzip.write("test1637770825775".getBytes(UTF_8));
        gzip.flush();
        gzip.close();
        when(response.body()).thenReturn(ResponseBody.create(null, baus.toByteArray()));
        final String result = OkhttpUtils.responseToString(response);
        assertThat("", result, is("" +
                "Response: 200 OK http://localhost/\n" +
                "Response headers:\n" +
                "  Content-Encoding: gzip\n" +
                "Response body: (17-byte body)\n" +
                "  test1637770825775\n"));
    }

    @Test
    @DisplayName("#requestToString() NPE if 'request' is null")
    public void test1639065948382() {
        assertThrow(() -> OkhttpUtils.requestToString(null)).assertNPE("request");
    }

    @Test
    @DisplayName("#requestToString() without body")
    public void test1639065948388() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        when(request.headers()).thenReturn(Headers.of());
        final String result = OkhttpUtils.requestToString(request);
        assertThat("", result, is("" +
                "Request: POST http://localhost/\n" +
                "Request headers: (absent)\n" +
                "Request body: (absent)\n"));
    }

    @Test
    @DisplayName("#requestToString() with plain body")
    public void test1639065948402() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        when(request.headers()).thenReturn(Headers.of("Content-Type", "text/plain"));
        when(request.body()).thenReturn(RequestBody.create(MediaType.parse("text/plain"), "test1637772023070"));
        final String result = OkhttpUtils.requestToString(request);
        assertThat("", result, is("" +
                "Request: POST http://localhost/\n" +
                "Request headers:\n" +
                "  Content-Type: text/plain\n" +
                "Request body:\n" +
                "  test1637772023070\n"));
    }

    @Test
    @DisplayName("#requestToString() with binary body")
    public void requestToString() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        when(request.headers()).thenReturn(Headers.of("Content-Type", "text/plain"));
        when(request.body()).thenReturn(RequestBody.create(null, new byte[]{(byte) 17}));
        final String result = OkhttpUtils.requestToString(request);
        assertThat("", result, is("" +
                "Request: POST http://localhost/\n" +
                "Request headers:\n" +
                "  Content-Type: text/plain\n" +
                "Request body: (binary 1-byte body omitted)\n"));
    }

    @Test
    @DisplayName("#requestToString() with unknown encoding")
    public void test1639065948435() throws Exception {
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        when(request.headers()).thenReturn(Headers.of("Content-Encoding", "unknown"));
        when(request.body()).thenReturn(RequestBody.create(null, "test1637770539680"));
        final String result = OkhttpUtils.requestToString(request);
        assertThat("", result, is("" +
                "Request: POST http://localhost/\n" +
                "Request headers:\n" +
                "  Content-Encoding: unknown\n" +
                "Request body: (encoded body omitted)\n"));
    }

    @Test
    @DisplayName("#requestToString() with duplex RequestBody")
    public void test1639065948451() throws Exception {
        final RequestBody mock = mock(RequestBody.class);
        when(mock.isDuplex()).thenReturn(true);
        final Request request = mock(Request.class);
        when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
        when(request.method()).thenReturn("POST");
        when(request.headers()).thenReturn(Headers.of());
        when(request.body()).thenReturn(mock);
        final String result = OkhttpUtils.requestToString(request);
        assertThat("", result, is("" +
                "Request: POST http://localhost/\n" +
                "Request headers: (absent)\n" +
                "Request body: (duplex request body omitted)\n"));
    }

}