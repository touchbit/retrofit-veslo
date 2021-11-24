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

public class OkHttpUtils {

    private OkHttpUtils() {
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
        return new Response.Builder()
                .request(getRequest())
                .headers(Headers.of("Content-Type", "text/plain", "X-Request-ID", "generated"))
                .body(ResponseBody.create(null, "generated"))
                .protocol(Protocol.HTTP_1_1)
                .message("TEST")
                .code(200)
                .build();
    }

    public static Request getRequest() {
        return getRequest(null, "generated");
    }

    public static Request getRequest(MediaType mediaType, String body) {
        return new Request.Builder()
                .addHeader("Content-Type", "text/plain")
                .addHeader("X-Request-ID", "generated")
                .post(getRequestBody(mediaType, body))
                .url("http://localhost")
                .build();
    }

    public static RequestBody getRequestBody(MediaType mediaType, String body) {
        return RequestBody.create(mediaType, body.getBytes());
    }

}
