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

package internal.test.utils.client;

import okhttp3.*;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static okhttp3.Protocol.HTTP_1_1;

public class MockInterceptor implements Interceptor {

    public static final Integer OK = 200;
    public static final Integer NO_CONTENT = 204;
    public static final Integer ERR = 500;
    protected final Logger logger = LoggerFactory.getLogger(MockInterceptor.class);

    @Override
    @SuppressWarnings("NullableProblems")
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        logRequest(request);
        final HttpUrl url = request.url();
        final MediaType mediaType;
        if (request.body() != null) {
            mediaType = request.body().contentType();
        } else {
            mediaType = MediaType.get("text/plain");
        }
        final String code = url.queryParameter("status");
        final Buffer buffer = new Buffer();
        final RequestBody requestBody = request.body();
        final String body;
        final ResponseBody responseBody;
        final Headers headers;
        if (NO_CONTENT.toString().equals(code)) {
            responseBody = ResponseBody.create(mediaType, 0, new Buffer());
            headers = request.headers();
        } else {
            if (requestBody != null) {
                try {
                    requestBody.writeTo(buffer);
                } catch (IOException e) {
                    throw new RuntimeException("Mock implementation error", e);
                }
                body = buffer.readUtf8();
            } else {
                body = "";
            }
            responseBody = ResponseBody.create(mediaType, body);
            headers = request.headers().newBuilder().set("Content-Length", "" + body.length()).build();
        }

        final Response response = new Response.Builder()
                .request(request)
                .protocol(HTTP_1_1)
                .headers(headers)
                .message("Mocked response")
                .code(Integer.parseInt(code != null ? code : "200"))
                .body(responseBody)
                .build();
        logResponse(response);
        return response;
    }

    public void logRequest(Request request) throws IOException {

    }

    public void logResponse(Response response) throws IOException {

    }

}
