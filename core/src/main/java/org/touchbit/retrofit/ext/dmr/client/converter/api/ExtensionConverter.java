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

package org.touchbit.retrofit.ext.dmr.client.converter.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ExtensionConverter<DTO> {

    String BODY_NULL_VALUE = "BODY_NULL_VALUE";

    /**
     * @param type                 - request method body type.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    RequestBodyConverter requestBodyConverter(Type type,
                                              Annotation[] parameterAnnotations,
                                              Annotation[] methodAnnotations,
                                              Retrofit retrofit);

    /**
     * @param type              - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    ResponseBodyConverter<DTO> responseBodyConverter(final Type type,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit);

    /**
     * Convert objects to and from their representation in HTTP.
     */
    interface RequestBodyConverter extends Converter<Object, RequestBody> {

        /**
         * Converting DTO model to their HTTP {@link RequestBody} representation
         *
         * @param body - DTO model
         * @return HTTP {@link RequestBody}
         */
        @Override
        @Nullable
        RequestBody convert(@Nonnull Object body) throws IOException;

    }

    /**
     * Convert objects to and from their representation in HTTP.
     */
    interface ResponseBodyConverter<DTO> extends Converter<ResponseBody, DTO> {

        /**
         * Converting HTTP {@link ResponseBody} to their {@link DTO} model representation
         *
         * @param body - HTTP {@link ResponseBody}
         * @return {@link DTO} model representation
         */
        @Override
        @Nullable
        DTO convert(@Nullable ResponseBody body) throws IOException;

    }

}
