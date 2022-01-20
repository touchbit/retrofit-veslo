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

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static internal.test.utils.OkHttpTestUtils.getRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
public class RetrofitTestUtils {

    private RetrofitTestUtils() {
    }

    public static Retrofit retrofit(CallAdapter.Factory callAdapterFactory, Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .baseUrl("http://localhost")
                .build();
    }

    public static Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://localhost")
                .build();
    }

    public static Call getCall(int code, Object body) {
        final Request request = getRequest();
        final Response response = getResponse(code, body);
        return getCall(request, response);
    }

    public static Call getCall(Request request, Response<?> response) {
        Call call = mock(Call.class);
        try {
            when(call.request()).thenReturn(request);
            when(call.execute()).thenReturn(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return call;
    }

    public static Response getResponse(int code, Object body) {
        if (code >= 200 && code <= 299) {
            return Response.success(code, body);
        }
        final ResponseBody rawResponseBody;
        if (body == null) {
            rawResponseBody = mock(ResponseBody.class);
        } else {
            rawResponseBody = ResponseBody.create(MediaType.get("application/json"), String.valueOf(body));
        }
        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
                .body(rawResponseBody)
                .request(getRequest())
                .code(code)
                .protocol(Protocol.HTTP_1_1)
                .message("TEST")
                .build();
        return Response.error(rawResponseBody, rawResponse);
    }

    public static Annotation[] getCallMethodAnnotations(final String... headers) {
        List<Annotation> result = new ArrayList<>();
        result.add(getHeadersAnnotation(headers));
        result.add(getPostAnnotation(UUID.randomUUID().toString()));
        return result.toArray(new Annotation[]{});
    }

    public static Headers getHeadersAnnotation(final String... headers) {
        return new Headers() {
            public Class<? extends Annotation> annotationType() {
                return Headers.class;
            }

            public String[] value() {
                return headers;
            }
        };
    }

    public static POST getPostAnnotation(String url) {
        return new POST() {
            public Class<? extends Annotation> annotationType() {
                return POST.class;
            }

            public String value() {
                return url;
            }
        };
    }

}
