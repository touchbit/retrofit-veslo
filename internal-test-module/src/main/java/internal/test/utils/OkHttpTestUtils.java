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

package internal.test.utils;

import okhttp3.*;
import okio.Buffer;

public class OkHttpTestUtils {

    private OkHttpTestUtils() {
    }

    public static String requestBodyToString(RequestBody requestBody) {
        try {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Response getResponse() {
        return getResponse("generated");
    }

    public static Response getResponse(Request request) {
        return getResponse(request, "generated");
    }

    public static Response getResponse(Request request, Headers headers) {
        return getResponse(request, "generated", headers);
    }

    public static Response getResponse(String body) {
        return getResponse(body, 200);
    }

    public static Response getResponse(Request request, String body) {
        return getResponse(request, body, 200);
    }

    public static Response getResponse(Request request, String body, Headers headers) {
        return getResponse(request, body, 200, headers);
    }

    public static Response getResponse(int status) {
        return getResponse("body", status);
    }

    public static Response getResponse(Request request, int status) {
        return getResponse(request, "body", status);
    }

    public static Response getResponse(String body, int status) {
        return getResponse(body, status, body == null ? null : MediaType.get("text/plain"));
    }

    public static Response getResponse(Request request, String body, int status) {
        return getResponse(request, body, status, body == null ? null : MediaType.get("text/plain"));
    }

    public static Response getResponse(Request request, String body, int status, Headers headers) {
        return getResponse(request, body, status, body == null ? null : MediaType.get("text/plain"), headers);
    }

    public static Response getResponse(String body, int status, MediaType mediaType) {
        return getResponse(getRequest(), body, status, mediaType);
    }

    public static Response getResponse(Request request, String body, int status, MediaType mediaType) {
        final Headers.Builder hb = new Headers.Builder();
        if (mediaType != null) {
            hb.add("Content-Type", mediaType.toString());
        }
        if (body != null) {
            hb.add("Content-Length", body.length() + "");
        }
        hb.add("X-Request-ID", "generated");
        return getResponse(request, body, status, mediaType, hb.build());
    }

    public static Response getResponse(Request request, String body, int status, MediaType mediaType, Headers headers) {
        return new Response.Builder()
                .request(request)
                .headers(headers)
                .body(body == null ? null : ResponseBody.create(mediaType, body))
                .protocol(Protocol.HTTP_1_1)
                .message("TEST")
                .code(status)
                .build();
    }

    public static Request getRequest() {
        return getRequest("http://localhost", null, "generated", Headers.of());
    }

    public static Request getRequest(String url, String... headerNamesAndValues) {
        return getRequest(url, null, "generated", Headers.of(headerNamesAndValues));
    }

    public static Request getRequest(MediaType mediaType, String body) {
        return getRequest("http://localhost", mediaType, body, Headers.of("Content-Type", "text/plain", "X-Request-ID", "generated"));
    }

    public static Request getRequest(String url, MediaType mediaType, String body, Headers headers) {
        return new Request.Builder()
                .headers(headers)
                .post(getRequestBody(mediaType, body))
                .url(url)
                .build();
    }

    public static RequestBody getRequestBody(MediaType mediaType, String body) {
        return RequestBody.create(mediaType, body.getBytes());
    }

}
