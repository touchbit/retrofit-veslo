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
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import org.touchbit.retrofit.ext.dmr.exception.UtilityClassException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("DuplicatedCode")
public class OkhttpUtils {

    /**
     * Copied and modified
     * Source Code: https://github.com/square/okhttp/tree/parent-3.14.9
     * Source Code Copyright (C) 2015 Square, Inc.
     * Licensed under the Apache License, Version 2.0;
     *
     * @param request - {@link okhttp3.Request} (not nullable)
     * @return - string representation of {@link okhttp3.Request}
     * @throws IOException - no comments
     */
    @Nonnull
    public static String requestToString(@Nonnull final Request request) throws IOException {
        Utils.parameterRequireNonNull(request, "request");
        final StringJoiner resultMessage = new StringJoiner("\n");
        final RequestBody requestBody = request.body();
        final boolean hasRequestBody = requestBody != null && requestBody.contentLength() != 0;
        resultMessage.add("REQUEST:\n" + request.method() + ' ' + request.url());
        final Headers headers = getRequestHeaders(request);
        if (headers.toMultimap().isEmpty()) {
            resultMessage.add("Headers: (absent)");
        } else {
            resultMessage.add("Headers:");
            resultMessage.add("  " + headers.toString().trim().replaceAll("\n", "\n  "));
        }
        if (!hasRequestBody) {
            resultMessage.add("Body: (absent)");
        } else if (bodyHasUnknownEncoding(headers)) {
            resultMessage.add("Body: (encoded body omitted)");
        } else if (requestBody.isDuplex()) {
            resultMessage.add("Body: (duplex request body omitted)");
        } else {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = getCharset(requestBody);
            if (isPlaintext(buffer)) {
                resultMessage.add("Body: (" + buffer.size() + "-byte body)");
                resultMessage.add("  " + buffer.readString(charset).replace("\n", "\n  "));
            } else {
                resultMessage.add("Body: (binary " + requestBody.contentLength() + "-byte body omitted)");
            }
        }
        return resultMessage.add("").toString();
    }

    /**
     * Copied and modified
     * Source Code: https://github.com/square/okhttp/tree/parent-3.14.9
     * Source Code Copyright (C) 2015 Square, Inc.
     * Licensed under the Apache License, Version 2.0;
     *
     * @param response - {@link okhttp3.Response} (not nullable)
     * @return - string representation of {@link okhttp3.Response}
     * @throws IOException - no comments
     */
    @Nonnull
    public static String responseToString(final Response response) throws IOException {
        Utils.parameterRequireNonNull(response, "response");
        final StringJoiner resultMessage = new StringJoiner("\n");
        final ResponseBody responseBody = response.body();
        final boolean hasResponseBody = responseBody != null;
        final String startMessage = "RESPONSE:\n" + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url();
        resultMessage.add(startMessage);
        final Headers responseHeaders = getResponseHeaders(response);
        if (responseHeaders.toMultimap().isEmpty()) {
            resultMessage.add("Headers: (absent)");
        } else {
            resultMessage.add("Headers:");
            resultMessage.add("  " + responseHeaders.toString().trim().replaceAll("\n", "\n  "));
        }
        if (!hasResponseBody || !HttpHeaders.hasBody(response)) {
            resultMessage.add("Body: (absent)");
        } else if (bodyHasUnknownEncoding(responseHeaders)) {
            resultMessage.add("Body: (encoded body omitted)");
        } else {
            final BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            if ("gzip".equalsIgnoreCase(responseHeaders.get("Content-Encoding"))) {
                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                }
            }
            Charset charset = getCharset(responseBody);
            if (isPlaintext(buffer)) {
                final String body = buffer.clone().readString(charset);
                resultMessage.add("Body: (" + buffer.size() + "-byte body)");
                if (body.length() > 0) {
                    resultMessage.add("  " + body.replace("\n", "\n  "));
                }
            } else {
                resultMessage.add("Body: (binary " + buffer.size() + "-byte body omitted)");
            }
        }
        return resultMessage.add("").toString();
    }

    public static Headers getRequestHeaders(@Nonnull final Request request) throws IOException {
        final Headers headers = request.headers();
        final RequestBody body = request.body();
        if (body == null) {
            return headers;
        }
        final Headers.Builder builder = headers.newBuilder();
        final MediaType mediaType = body.contentType();
        if (headers.get("Content-Type") == null && mediaType != null) {
            builder.add("Content-Type", mediaType.toString());
        }
        if (headers.get("Content-Length") == null) {
            builder.add("Content-Length", String.valueOf(body.contentLength()));
        }
        return builder.build();
    }

    public static Headers getResponseHeaders(@Nonnull final Response response) {
        final Headers headers = response.headers();
        final ResponseBody body = response.body();
        if (body == null) {
            return headers;
        }
        final Headers.Builder builder = headers.newBuilder();
        final MediaType mediaType = body.contentType();
        if (headers.get("Content-Type") == null && mediaType != null) {
            builder.add("Content-Type", mediaType.toString());
        }
        if (headers.get("Content-Length") == null) {
            builder.add("Content-Length", String.valueOf(body.contentLength()));
        }
        return builder.build();
    }

    /**
     * @param requestBody - nullable {@link RequestBody}
     * @return extracted {@link Charset} from {@link RequestBody} or UTF-8 by default
     */
    @Nonnull
    public static Charset getCharset(@Nullable RequestBody requestBody) {
        return requestBody == null ? UTF_8 : getCharset(requestBody.contentType());
    }

    /**
     * @param responseBody - nullable {@link ResponseBody}
     * @return extracted {@link Charset} from {@link ResponseBody} or UTF-8 by default
     */
    @Nonnull
    public static Charset getCharset(@Nullable ResponseBody responseBody) {
        return responseBody == null ? UTF_8 : getCharset(responseBody.contentType());
    }

    /**
     * @param mediaType - nullable {@link MediaType}
     * @return extracted {@link Charset} from {@link MediaType} or UTF-8 by default
     */
    @Nonnull
    public static Charset getCharset(@Nullable MediaType mediaType) {
        final Charset charset = (mediaType == null) ? UTF_8 : mediaType.charset();
        return (charset == null) ? UTF_8 : charset;
    }

    /**
     * @param buffer - byte buffer (okio)
     * @return - true if buffer is present and contains only unicode code points
     */
    public static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = Math.min(buffer.size(), 64L);
            buffer.copyTo(prefix, 0L, byteCount);
            for (int i = 0; i < 16 && !prefix.exhausted(); ++i) {
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**§
     * @param headers - okhttp headers multimap
     * @return true if 'Content-Encoding' header is present and has no 'identity' or 'gzip' value
     */
    public static boolean bodyHasUnknownEncoding(@Nonnull Headers headers) {
        Utils.parameterRequireNonNull(headers, "headers");
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null &&
                !contentEncoding.equalsIgnoreCase("identity") &&
                !contentEncoding.equalsIgnoreCase("gzip");
    }

    private OkhttpUtils() {
        throw new UtilityClassException();
    }

}
