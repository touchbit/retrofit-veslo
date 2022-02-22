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

package veslo.client.converter.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConverterUnsupportedTypeException;
import veslo.PrimitiveConvertCallException;
import veslo.util.ConvertUtils;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static veslo.constant.ParameterNameConstants.*;

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

    /**
     * @param bodyType - DTO java type
     * @throws PrimitiveConvertCallException if bodyType is primitive and cannot be null
     */
    @EverythingIsNonNull
    default void assertNotNullableBodyType(Type bodyType) {
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        if (bodyType instanceof Class && ((Class<?>) bodyType).isPrimitive()) {
            throw new PrimitiveConvertCallException(bodyType);
        }
    }

    /**
     * @param methodAnnotations - API client called method annotations
     * @param body              - request string body
     * @return {@link RequestBody} with content-type from methodAnnotations
     */
    @EverythingIsNonNull
    default RequestBody createRequestBody(final Annotation[] methodAnnotations, String body) {
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(body, BODY_PARAMETER);
        return createRequestBody(methodAnnotations, body.getBytes());
    }

    /**
     * @param methodAnnotations - API client called method annotations
     * @param body              - request byte array body
     * @return {@link RequestBody} with content-type from methodAnnotations
     */
    @EverythingIsNonNull
    default RequestBody createRequestBody(final Annotation[] methodAnnotations, byte[] body) {
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(body, BODY_PARAMETER);
        final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
        return RequestBody.create(mediaType, body);
    }

    /**
     * @param converter     - {@link ExtensionConverter} for exception info
     * @param body          - convertable body
     * @param expectedTypes - list of expected (supported) types
     * @throws ConverterUnsupportedTypeException if the body type does not match the expected types
     */
    @EverythingIsNonNull
    default void assertSupportedBodyType(ExtensionConverter<?> converter, Object body, Type... expectedTypes) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(body, BODY_PARAMETER);
        Utils.parameterRequireNonNull(expectedTypes, EXPECTED_TYPES_PARAMETER);
        final Class<?> bodyType = body.getClass();
        assertSupportedBodyType(converter, bodyType, expectedTypes);
    }

    /**
     * @param converter     - {@link ExtensionConverter} for exception info
     * @param bodyType      - convertable body type
     * @param expectedTypes - list of expected (supported) types
     * @throws ConverterUnsupportedTypeException if the body type does not match the expected types
     */
    @EverythingIsNonNull
    default void assertSupportedBodyType(ExtensionConverter<?> converter, Type bodyType, Type... expectedTypes) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(expectedTypes, EXPECTED_TYPES_PARAMETER);
        if (!Arrays.asList(expectedTypes).contains(bodyType)) {
            throw new ConverterUnsupportedTypeException(converter.getClass(), bodyType, expectedTypes);
        }
    }

    /**
     * @param body - request body
     * @return true if body == NULL_BODY_VALUE
     */
    default boolean isForceNullBodyValue(@Nullable Object body) {
        return isForceNullBodyValue(String.valueOf(body));
    }

    /**
     * @param body - request body
     * @return true if body == NULL_BODY_VALUE
     */
    default boolean isForceNullBodyValue(@Nullable String body) {
        return NULL_BODY_VALUE.equals(body);
    }

    /**
     * @param body - request body
     * @return true if body == NULL_JSON_VALUE
     */
    default boolean isForceNullJsonValue(@Nullable Object body) {
        return isForceNullJsonValue(String.valueOf(body));
    }

    /**
     * @param body - request body
     * @return true if body == NULL_JSON_VALUE
     */
    default boolean isForceNullJsonValue(@Nullable String body) {
        return NULL_JSON_VALUE.equals(body);
    }

    @Nullable
    default String copyBody(@Nullable ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return null;
        }
        try {
            final BufferedSource source = responseBody.source();
            //noinspection ConstantConditions
            if (source == null) {
                return null;
            }
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            final MediaType mediaType = responseBody.contentType();
            if (mediaType != null) {
                final Charset charset = mediaType.charset();
                if (charset != null) {
                    return buffer.clone().readString(charset);
                }
            }
            return buffer.clone().readString(StandardCharsets.UTF_8);
        } catch (IllegalStateException ignore) {
            // ignore NoContentResponseBody.source() runtime exception
            // if http status code = 204 or 205 and Content-Length = -1 or 0
            return null;
        }
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
