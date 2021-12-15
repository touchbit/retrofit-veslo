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
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.exception.PrimitiveConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

public interface ExtensionConverter<DTO> {

    String NULL_JSON_VALUE = "NULL_JSON_VALUE";
    String NULL_BODY_VALUE = "NULL_BODY_VALUE";

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

    @EverythingIsNonNull
    default void assertNotNullableBodyType(Type bodyType) {
        Utils.parameterRequireNonNull(bodyType, "bodyType");
        if (bodyType instanceof Class && ((Class<?>) bodyType).isPrimitive()) {
            throw new PrimitiveConvertCallException(bodyType);
        }
    }

    @EverythingIsNonNull
    default void assertSupportedBodyType(ExtensionConverter<?> converter, Object body, Type... expectedTypes) {
        Utils.parameterRequireNonNull(body, "body");
        Utils.parameterRequireNonNull(expectedTypes, "expectedTypes");
        final Class<?> bodyType = body.getClass();
        assertSupportedBodyType(converter, bodyType, expectedTypes);
    }

    default void assertSupportedBodyType(ExtensionConverter<?> converter, Type bodyType, Type... expectedTypes) {
        Utils.parameterRequireNonNull(bodyType, "bodyType");
        Utils.parameterRequireNonNull(expectedTypes, "expectedTypes");
        if (!Arrays.asList(expectedTypes).contains(bodyType)) {
            throw new ConverterUnsupportedTypeException(converter.getClass(), bodyType, expectedTypes);
        }
    }

    default boolean isForceNullBodyValue(@Nullable Object body) {
        return isForceNullBodyValue(String.valueOf(body));
    }

    default boolean isForceNullBodyValue(@Nullable String body) {
        return NULL_BODY_VALUE.equals(body);
    }

    default boolean isForceNullJsonValue(@Nullable Object body) {
        return isForceNullJsonValue(String.valueOf(body));
    }

    default boolean isForceNullJsonValue(@Nullable String body) {
        return NULL_JSON_VALUE.equals(body);
    }

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
