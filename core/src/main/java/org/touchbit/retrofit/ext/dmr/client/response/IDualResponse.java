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

package org.touchbit.retrofit.ext.dmr.client.response;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Common generic interface for response API wrapper classes.
 * Contains the deserialized response body from the server, which can be
 * represented by two DTO models (error/success) depending on {@link IDualResponse#getHttpStatusCode()}
 * <p>
 * Created by Oleg Shaburov on 03.11.2021
 * shaburov.o.a@gmail.com
 */
@SuppressWarnings("unused")
public interface IDualResponse<SUCCESSFUL_DTO, ERROR_DTO> {

    /**
     * @return okhttp3 {@link Request}
     */
    Request getRawRequest();

    /**
     * @return retrofit {@link Response}
     */
    Response<SUCCESSFUL_DTO> getResponse();

    /**
     * @return error DTO model
     */
    @Nullable
    ERROR_DTO getErrorDTO();

    /**
     * @return description of the called resource in detail
     */
    String getEndpointInfo();

    /**
     * @return list of annotations for the called API method
     */
    Annotation[] getCallAnnotations();

    /**
     * @return successful DTO model {@link SUCCESSFUL_DTO}
     */
    default SUCCESSFUL_DTO getSuccessfulDTO() {
        return getResponse().body();
    }

    /**
     * @return okhttp3 response headers
     */
    default Headers getHeaders() {
        Headers headers = getResponse().headers();
        ResponseBody body = getRawResponse().body();
        if (body != null) {
            MediaType mediaType = body.contentType();
            if (mediaType != null) {
                return headers.newBuilder().add("Content-Type", mediaType.toString()).build();
            }
        }
        return headers;
    }

    /**
     * @return HTTP response status code
     */
    default int getHttpStatusCode() {
        return getResponse().code();
    }

    /**
     * @return HTTP response status message
     */
    default String getHttpStatusMessage() {
        return getResponse().message();
    }

    /**
     * @return okHttp3 response {@link okhttp3.Response}
     */
    default okhttp3.Response getRawResponse() {
        return getResponse().raw();
    }

    /**
     * @return true if {@link IDualResponse#getHttpStatusCode()} is in the range [200..299].
     */
    default boolean isSuccessful() {
        return getResponse().isSuccessful();
    }

}
