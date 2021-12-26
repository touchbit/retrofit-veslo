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

package org.touchbit.retrofit.veslo.client.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.exception.ConvertCallException;
import org.touchbit.retrofit.veslo.exception.HttpCallException;
import org.touchbit.retrofit.veslo.exception.PrimitiveConvertCallException;
import org.touchbit.retrofit.veslo.util.Utils;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Base extension call adapter factory {@link CallAdapter.Factory}
 * Implements the basic methods for getting the response body model
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.12.2021
 */
public class JavaTypeCallAdapterFactory extends CallAdapter.Factory {

    /**
     * Slf4J logger
     */
    protected final Logger logger;

    /**
     * Default constructor
     */
    public JavaTypeCallAdapterFactory() {
        this(LoggerFactory.getLogger(JavaTypeCallAdapterFactory.class));
    }

    /**
     * @param logger - required Slf4J logger
     */
    @EverythingIsNonNull
    public JavaTypeCallAdapterFactory(final Logger logger) {
        Utils.parameterRequireNonNull(logger, "logger");
        this.logger = logger;
    }

    /**
     * The returned CallAdapter seeks to convert the response body
     * to the specified return type, regardless of the HTTP status.
     *
     * @param returnType        - called method return type
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return a call adapter for the specified return type
     */
    @Override
    @EverythingIsNonNull
    public CallAdapter<Object, Object> get(final Type returnType,
                                           final Annotation[] methodAnnotations,
                                           final Retrofit retrofit) {
        return new CallAdapter<Object, Object>() {

            /**
             * @return see {@link CallAdapter#responseType()}
             */
            @Override
            @Nonnull
            public Type responseType() {
                return returnType;
            }

            /**
             * @param call - see {@link Call}
             * @return see {@link CallAdapter#adapt(Call)}
             */
            @Override
            public Object adapt(final @Nonnull Call<Object> call) {
                logger.info("API call: " + call.request().method() + " " + call.request().url());
                try {
                    final Response<Object> response = call.execute();
                    final Object dto;
                    logger.debug("Retrieving the response body.");
                    if (response.isSuccessful()) {
                        dto = getSuccessfulResponseBody(response, returnType, methodAnnotations, retrofit);
                    } else {
                        dto = getErrorResponseBody(response, returnType, methodAnnotations, retrofit);
                    }
                    checkPrimitiveConvertCall(returnType, dto);
                    logger.debug("Response body is {}present for type: {}", dto == null ? "not " : "", returnType);
                    //noinspection ConstantConditions
                    return dto;
                } catch (IOException e) {
                    logger.error("Failed to make API call.", e);
                    throw new HttpCallException("Failed to make API call.\n" + e.getMessage() + "\n", e);
                }
            }
        };
    }

    /**
     * Force calls a typed converter if HTTP status code 204/205 or {@link Response#body()} return null
     * This ensures that the request body is processed correctly for the {@link Type}.
     * If an exception was thrown and method {@link Response#isSuccessful()} returned false,
     * then null will be returned without throwing an exception.
     *
     * @param response    - Retrofit call response
     * @param returnType  - Successful model {@link Type}
     * @param annotations - list of annotations for the called API method
     * @param retrofit    - HTTP client
     * @return Successful model
     * @throws ConvertCallException {@link ConvertCallException} If the body of a successful response is not convertible
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected Object getSuccessfulResponseBody(final @Nonnull Response<?> response,
                                               final @Nonnull Type returnType,
                                               final @Nonnull Annotation[] annotations,
                                               final @Nonnull Retrofit retrofit) {
        Utils.parameterRequireNonNull(response, "response");
        Utils.parameterRequireNonNull(returnType, "returnType");
        Utils.parameterRequireNonNull(annotations, "annotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (returnType == Void.TYPE || returnType == Void.class) {
            return null;
        }
        final String typeName = Utils.getTypeName(returnType);
        logger.debug("Get successful response body model for type {}", typeName);
        try {
            final Object dto;
            final int code = response.code();
            if (code == 204 || code == 205 || response.body() == null) {
                logger.debug("Received 'No content' response. Forced conversion null body.");
                dto = retrofit.responseBodyConverter(returnType, annotations).convert(null);
            } else {
                logger.debug("The existing response body was used.");
                dto = response.body();
            }
            logger.debug("Successful body is {}present", dto == null ? "not " : "");
            return dto;
        } catch (ConvertCallException e) {
            if (response.isSuccessful()) {
                throw e;
            }
        } catch (Exception e) {
            if (response.isSuccessful()) {
                throw new ConvertCallException("Error converting response body to type " + returnType.getTypeName(), e);
            }
        }
        return null;
    }

    /**
     * Converting  {@link Response#errorBody()} to an error model {@link Type}.
     * If an exception was thrown and method {@link Response#isSuccessful()} returned true,
     * then null will be returned without throwing an exception.
     *
     * @param response    - Retrofit call response
     * @param returnType  - Error model {@link Type}
     * @param annotations - list of annotations for the called API method
     * @param retrofit    - HTTP client
     * @return Error model
     * @throws ConvertCallException If the body of a error response is not convertible
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected Object getErrorResponseBody(final @Nonnull Response<?> response,
                                          final @Nonnull Type returnType,
                                          final @Nonnull Annotation[] annotations,
                                          final @Nonnull Retrofit retrofit) {
        Utils.parameterRequireNonNull(response, "response");
        Utils.parameterRequireNonNull(returnType, "returnType");
        Utils.parameterRequireNonNull(annotations, "annotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        logger.debug("Get error response body model for type {}", returnType);
        if (returnType == Void.TYPE || returnType == Void.class) {
            return null;
        }
        Object dto = null;
        try {
            dto = retrofit.responseBodyConverter(returnType, annotations).convert(response.errorBody());
        } catch (ConvertCallException e) {
            if (!response.isSuccessful()) {
                throw e;
            }
        } catch (IOException e) {
            if (!response.isSuccessful()) {
                throw new ConvertCallException("Error converting response body to type " + returnType.getTypeName(), e);
            }
        }
        logger.debug("Error body is {}present", dto == null ? "not " : "");
        return dto;
    }

    /**
     * @param returnType - model type
     * @param body       - model value
     * @throws PrimitiveConvertCallException if returnType is primitive and body is null
     */
    protected void checkPrimitiveConvertCall(final @Nonnull Type returnType, final @Nullable Object body) {
        logger.debug("Checking that the primitive is not null");
        if (returnType instanceof Class && ((Class<?>) returnType).isPrimitive() && body == null) {
            throw new PrimitiveConvertCallException(returnType);
        }
    }

}
