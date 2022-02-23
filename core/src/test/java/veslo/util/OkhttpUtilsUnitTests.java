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

package veslo.util;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.*;
import okio.Buffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static internal.test.utils.OkHttpTestUtils.getRequest;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("OkhttpUtils class tests")
public class OkhttpUtilsUnitTests extends BaseCoreUnitTest {

    @Nested
    @DisplayName("#requestToString() method tests")
    public class RequestToStringMethodTests {

        @Test
        @DisplayName("NPE if 'request' is null")
        public void test1639065948382() {
            assertThrow(() -> OkhttpUtils.requestToString(null)).assertNPE("request");
        }

        @Test
        @DisplayName("without body")
        public void test1639065948388() throws Exception {
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of());
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (absent)"));
        }

        @Test
        @DisplayName("with plain body")
        public void test1639065948402() throws Exception {
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of("Content-Type", "text/plain"));
            when(request.body()).thenReturn(RequestBody.create(MediaType.parse("text/plain"), "test1637772023070"));
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (17-byte body)\n  test1637772023070\n"));
        }

        @Test
        @DisplayName("with binary body")
        public void requestToString() throws Exception {
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of("Content-Type", "text/plain"));
            when(request.body()).thenReturn(RequestBody.create(null, new byte[]{(byte) 17}));
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (binary 1-byte body omitted)"));
        }

        @Test
        @DisplayName("with unknown encoding")
        public void test1639065948435() throws Exception {
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of("Content-Encoding", "unknown"));
            when(request.body()).thenReturn(RequestBody.create(null, "test1637770539680"));
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (encoded body omitted)"));
        }

        @Test
        @DisplayName("with duplex RequestBody")
        public void test1639065948451() throws Exception {
            final RequestBody mock = mock(RequestBody.class);
            when(mock.isDuplex()).thenReturn(true);
            when(mock.contentLength()).thenReturn(10L);
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of());
            when(request.body()).thenReturn(mock);
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (duplex request body omitted)"));
        }

        @Test
        @DisplayName("with duplex && content length == 0")
        public void test1639986665183() throws IOException {
            final RequestBody mock = mock(RequestBody.class);
            when(mock.isDuplex()).thenReturn(true);
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            when(request.method()).thenReturn("POST");
            when(request.headers()).thenReturn(Headers.of());
            when(request.body()).thenReturn(mock);
            final String result = OkhttpUtils.requestToString(request);
            assertThat("", result, containsString("Body: (absent)"));
        }


    }

    @Nested
    @DisplayName("#responseToString() method tests")
    public class ResponseToStringMethodTests {

        @Test
        @DisplayName("NPE if 'response' is null")
        public void test1639065948233() {
            assertThrow(() -> OkhttpUtils.responseToString(null)).assertNPE("response");
        }

        @Test
        @DisplayName("without body")
        public void test1639065948239() throws Exception {
            final Request request = mock(Request.class);
            when(request.url()).thenReturn(HttpUrl.get("http://localhost"));
            final Response response = mock(Response.class);
            when(response.message()).thenReturn("");
            when(response.request()).thenReturn(request);
            when(response.headers()).thenReturn(Headers.of());
            final String result = OkhttpUtils.responseToString(response);
            assertThat("", result, containsString("Body: (absent)"));
        }

        @Test
        @DisplayName("without body (HEAD request)")
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
            assertThat("", result, containsString("Body: (absent)"));
        }

        @Test
        @DisplayName("with plain body")
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
            assertThat("", result, containsString("Body: (17-byte body)\n  test1637768109234"));
        }

        @Test
        @DisplayName("with empty body")
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
            assertThat("", result, containsString("Body: (0-byte body)"));
        }

        @Test
        @DisplayName("with binary body")
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
            assertThat("", result, containsString("Body: (binary 1-byte body omitted)"));
        }

        @Test
        @DisplayName("with Content-Length: -1 and unknown encoding")
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
            when(responseBody.bytes()).thenReturn("test1637770539680" .getBytes());
            when(responseBody.source()).thenReturn(ResponseBody.create(null, "test1637770539680").source());
            when(response.body()).thenReturn(responseBody);
            final String result = OkhttpUtils.responseToString(response);
            assertThat("", result, containsString("Body: (encoded body omitted)\n"));
        }

        @Test
        @DisplayName("with gzip body")
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
            gzip.write("test1637770825775" .getBytes(UTF_8));
            gzip.flush();
            gzip.close();
            when(response.body()).thenReturn(ResponseBody.create(null, baus.toByteArray()));
            final String result = OkhttpUtils.responseToString(response);
            assertThat("", result, containsString("Body: (17-byte body)"));
        }

    }

    @Nested
    @DisplayName("#getRequestHeaders() method tests")
    public class GetRequestHeadersMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639986006458() {
            assertNPE(() -> OkhttpUtils.getRequestHeaders(null), "request");
        }

        @Test
        @DisplayName("Get headers if RequestBody == null")
        public void test1639986008895() throws IOException {
            final Request request = new Request.Builder()
                    .addHeader("X-Request-ID", "generated")
                    .get()
                    .url("http://localhost")
                    .build();
            final Headers headers = OkhttpUtils.getRequestHeaders(request);
            assertThat(headers, notNullValue());
            assertThat(headers.get("Content-Type"), nullValue());
            assertThat(headers.get("Content-Length"), nullValue());
        }

        @Test
        @DisplayName("Get headers if body media type == text/plain && header Content-Type == null")
        public void test1639986011793() throws IOException {
            final Request request = new Request.Builder()
                    .addHeader("X-Request-ID", "generated")
                    .post(RequestBody.create(MediaType.get("text/plain"), "test"))
                    .url("http://localhost")
                    .build();
            final Headers headers = OkhttpUtils.getRequestHeaders(request);
            assertThat(headers, notNullValue());
            assertThat(headers.get("Content-Type"), is("text/plain; charset=utf-8"));
            assertThat(headers.get("Content-Length"), is("4"));
        }

        @Test
        @DisplayName("Get headers if body media type == null && header Content-Type == null")
        public void test1639986015416() throws IOException {
            final Request request = new Request.Builder()
                    .addHeader("X-Request-ID", "generated")
                    .post(RequestBody.create(null, "test"))
                    .url("http://localhost")
                    .build();
            final Headers headers = OkhttpUtils.getRequestHeaders(request);
            assertThat(headers, notNullValue());
            assertThat(headers.get("Content-Type"), nullValue());
            assertThat(headers.get("Content-Length"), is("4"));
        }

        @Test
        @DisplayName("Ignore body values if Content-Type && Content-Length headers present")
        public void test1639986019309() throws IOException {
            final Request request = new Request.Builder()
                    .addHeader("Content-Type", "foo/bar")
                    .addHeader("Content-Length", "100500")
                    .addHeader("X-Request-ID", "generated")
                    .post(RequestBody.create(null, "test"))
                    .url("http://localhost")
                    .build();
            final Headers headers = OkhttpUtils.getRequestHeaders(request);
            assertThat(headers, notNullValue());
            assertThat(headers.get("Content-Type"), is("foo/bar"));
            assertThat(headers.get("Content-Length"), is("100500"));
        }

    }

    @Nested
    @DisplayName("#getResponseHeaders() method tests")
    public class GetResponseHeadersMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639984662473() {
            assertNPE(() -> OkhttpUtils.getResponseHeaders(null), "response");
        }

        @Test
        @DisplayName("Get headers if ResponseBody == null")
        public void test1639984743021() {
            final Response response = OkHttpTestUtils.getResponse((String) null);
            final Headers responseHeaders = OkhttpUtils.getResponseHeaders(response);
            assertThat(responseHeaders, notNullValue());
            assertThat(responseHeaders.get("Content-Type"), nullValue());
            assertThat(responseHeaders.get("Content-Length"), nullValue());
        }

        @Test
        @DisplayName("Get headers if body media type == text/plain && header Content-Type == null")
        public void test1639984979678() {
            final Response response = new Response.Builder()
                    .request(getRequest())
                    .body(ResponseBody.create(MediaType.get("text/plain"), "test"))
                    .protocol(Protocol.HTTP_1_1)
                    .message("TEST")
                    .code(200)
                    .build();
            final Headers responseHeaders = OkhttpUtils.getResponseHeaders(response);
            assertThat(responseHeaders, notNullValue());
            assertThat(responseHeaders.get("Content-Type"), is("text/plain; charset=utf-8"));
            assertThat(responseHeaders.get("Content-Length"), is("4"));
        }

        @Test
        @DisplayName("Get headers if body media type == null && header Content-Type == null")
        public void test1639985456835() {
            final Response response = new Response.Builder()
                    .request(getRequest())
                    .body(ResponseBody.create(null, "test"))
                    .protocol(Protocol.HTTP_1_1)
                    .message("TEST")
                    .code(200)
                    .build();
            final Headers responseHeaders = OkhttpUtils.getResponseHeaders(response);
            assertThat(responseHeaders, notNullValue());
            assertThat(responseHeaders.get("Content-Type"), nullValue());
            assertThat(responseHeaders.get("Content-Length"), is("4"));
        }

        @Test
        @DisplayName("Ignore body values if Content-Type && Content-Length headers present")
        public void test1639985790789() {
            final Response response = new Response.Builder()
                    .request(getRequest())
                    .protocol(Protocol.HTTP_1_1)
                    .headers(Headers.of("Content-Type", "foo/bar", "Content-Length", "100500"))
                    .body(ResponseBody.create(MediaType.get("text/plain"), "test"))
                    .message("TEST")
                    .code(200)
                    .build();
            final Headers responseHeaders = OkhttpUtils.getResponseHeaders(response);
            assertThat(responseHeaders, notNullValue());
            assertThat(responseHeaders.get("Content-Type"), is("foo/bar"));
            assertThat(responseHeaders.get("Content-Length"), is("100500"));
        }

    }

    @Nested
    @DisplayName("#getCharset() method tests")
    public class GetCharsetMethodTests {

        @Test
        @DisplayName("return utf-8 if MediaType = null")
        public void test1639065948187() {
            assertThat("", OkhttpUtils.getCharset((MediaType) null), is(UTF_8));
        }

        @Test
        @DisplayName("return utf-8 if MediaType not contains charset")
        public void test1639065948193() {
            assertThat("", OkhttpUtils.getCharset(MediaType.get("a/b")), is(UTF_8));
        }

        @Test
        @DisplayName("return utf-16 if MediaType contains utf-16 charset")
        public void test1639065948199() {
            assertThat("", OkhttpUtils.getCharset(MediaType.get("a/b; charset=utf-16")), is(UTF_16));
        }

        @Test
        @DisplayName("return utf-8 if ResponseBody is null")
        public void test1639065948205() {
            assertThat("", OkhttpUtils.getCharset((ResponseBody) null), is(UTF_8));
        }

        @Test
        @DisplayName("return utf-8 if RequestBody is null")
        public void test1639065948211() {
            assertThat("", OkhttpUtils.getCharset((RequestBody) null), is(UTF_8));
        }

        @Test
        @DisplayName("return utf-8 if ResponseBody contains charset")
        public void test1639065948217() {
            final ResponseBody mock = mock(ResponseBody.class);
            when(mock.contentType()).thenReturn(MediaType.get("a/b; charset=utf-16"));
            assertThat("", OkhttpUtils.getCharset(mock), is(UTF_16));
        }

        @Test
        @DisplayName("return utf-8 if RequestBody contains charset")
        public void test1639065948225() {
            final RequestBody mock = mock(RequestBody.class);
            when(mock.contentType()).thenReturn(MediaType.get("a/b; charset=utf-16"));
            assertThat("", OkhttpUtils.getCharset(mock), is(UTF_16));
        }

    }

    @Nested
    @DisplayName("#isPlaintext() method tests")
    public class IsPlaintextMethodTests {

        @Test
        @DisplayName("false if buffer contains not ISO control")
        public void test1639065948157() {
            Buffer buffer = new Buffer();
            buffer.write(new byte[]{(byte) 17});
            assertThat("", OkhttpUtils.isPlaintext(buffer), is(false));
        }

        @Test
        @DisplayName("true if buffer contains ISO controls")
        public void test1639065948165() {
            Buffer buffer = new Buffer();
            buffer.write(new byte[]{(byte) 28}); // ISO Control
            assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
            buffer = new Buffer();
            buffer.write(new byte[]{(byte) 46}); // Whitespace Control
            assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
            buffer = new Buffer();
            buffer.write("test1637759639496" .getBytes()); // just in case
            assertThat("", OkhttpUtils.isPlaintext(buffer), is(true));
        }

        @Test
        @DisplayName("EOF -> false")
        public void test1639065948179() {
            final Buffer mock = mock(Buffer.class);
            when(mock.size()).thenThrow(new RuntimeException());
            assertThat("", OkhttpUtils.isPlaintext(mock), is(false));
        }

    }

    @Nested
    @DisplayName("#bodyHasUnknownEncoding() method tests")
    public class BodyHasUnknownEncodingMethodTests {

        @Test
        @DisplayName("return true if Content-Encoding: utf-8")
        public void test1639065948123() {
            final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "utf-8"));
            assertThat("utf-8 is unknown encoding", result, is(true));
        }

        @Test
        @DisplayName("return false if Content-Encoding: identity")
        public void test1639065948130() {
            final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "identity"));
            assertThat("identity is unknown encoding", result, is(false));
        }

        @Test
        @DisplayName("return false if Content-Encoding: gzip")
        public void test1639065948137() {
            final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of("Content-Encoding", "gzip"));
            assertThat("gzip is unknown encoding", result, is(false));
        }

        @Test
        @DisplayName("return false if Content-Encoding not present")
        public void test1639065948144() {
            final boolean result = OkhttpUtils.bodyHasUnknownEncoding(Headers.of());
            assertThat("unknown encoding", result, is(false));
        }

        @Test
        @DisplayName("NPE if parameter 'headers' is null")
        public void test1639065948151() {
            assertThrow(() -> OkhttpUtils.bodyHasUnknownEncoding(null)).assertNPE("headers");
        }

    }

}