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

package org.touchbit.retrofit.ext.dmr.client.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponseBase;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Factory for creating {@link CallAdapter} with support {@link IDualResponse} type
 * <p>
 * Created by Oleg Shaburov on 11.12.2021
 * shaburov.o.a@gmail.com
 */
public class DualResponseCallAdapterFactory extends BaseCallAdapterFactory {

    /**
     * {@link IDualResponseConsumer} for constructor of {@link DualResponseBase} heirs
     */
    private final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer;

    /**
     * Default constructor with {@link DualResponse} return type handling
     */
    public DualResponseCallAdapterFactory() {
        this(LoggerFactory.getLogger(DualResponseCallAdapterFactory.class), DualResponse::new);
    }

    /**
     * @param dualResponseConsumer - {@link IDualResponseConsumer} for constructor of {@link DualResponseBase} heirs
     */
    public DualResponseCallAdapterFactory(IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        this(LoggerFactory.getLogger(DualResponseCallAdapterFactory.class), dualResponseConsumer);
    }

    /**
     * @param logger - required Slf4J logger
     */
    public DualResponseCallAdapterFactory(Logger logger) {
        this(logger, DualResponse::new);
    }

    /**
     * @param logger               - required Slf4J logger
     * @param dualResponseConsumer - {@link IDualResponseConsumer} for constructor of {@link DualResponseBase} heirs
     */
    public DualResponseCallAdapterFactory(Logger logger, IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        super(logger);
        Utils.parameterRequireNonNull(dualResponseConsumer, "dualResponseConsumer");
        this.dualResponseConsumer = dualResponseConsumer;
    }

    /**
     * @param returnType        - called method return type
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit-         - HTTP client
     * @return a call adapter for {@link IDualResponse} interface
     */
    @Override
    @EverythingIsNonNull
    public CallAdapter<Object, IDualResponse<?, ?>> get(final Type returnType,
                                                        final Annotation[] methodAnnotations,
                                                        final Retrofit retrofit) {
        logger.debug("Prepare API call\nAPI method annotations:{}", Utils.arrayToPrettyString(methodAnnotations));
        final ParameterizedType type = getParameterizedType(returnType);
        final String endpointInfo = getEndpointInfo(methodAnnotations);
        final Type successType = getParameterUpperBound(0, type);
        final Type errorType = getParameterUpperBound(1, type);
        return getCallAdapter(type, successType, errorType, endpointInfo, methodAnnotations, retrofit);
    }

    /**
     * Method for getting an instance of the {@link CallAdapter} class
     *
     * @param type              - called method return type
     * @param successType       - success DTO class
     * @param errorType         - error DTO class
     * @param endpointInfo      - called method description
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return instance of {@link CallAdapter}
     */
    @EverythingIsNonNull
    public CallAdapter<Object, IDualResponse<?, ?>> getCallAdapter(final ParameterizedType type,
                                                                   final Type successType,
                                                                   final Type errorType,
                                                                   final String endpointInfo,
                                                                   final Annotation[] methodAnnotations,
                                                                   final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(successType, "successType");
        Utils.parameterRequireNonNull(errorType, "errorType");
        Utils.parameterRequireNonNull(endpointInfo, "endpointInfo");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new CallAdapter<Object, IDualResponse<?, ?>>() {

            /**
             * @return see {@link CallAdapter#responseType()}
             */
            @Override
            @Nonnull
            public Type responseType() {
                return type;
            }

            /**
             * @param call - see {@link Call}
             * @return see {@link CallAdapter#adapt(Call)}
             */
            @Override
            @EverythingIsNonNull
            public IDualResponse<?, ?> adapt(Call<Object> call) {
                final String finalInfo;
                if (endpointInfo.trim().isEmpty()) {
                    finalInfo = call.request().method() + " " + call.request().url();
                } else {
                    finalInfo = endpointInfo.trim();
                }
                logger.info("API call: " + finalInfo);
                return getIDualResponse(call, successType, errorType, finalInfo, methodAnnotations, retrofit);
            }

        };
    }

    /**
     * Extracting {@link ParameterizedType} from wrapper class ({@link Type})
     * Checking that the wrapper class inherits from the IDualResponse interface
     *
     * @param type - the raw type of the response wrapper class
     * @return {@link ParameterizedType} of the wrapper class
     */
    public ParameterizedType getParameterizedType(Type type) {
        Utils.parameterRequireNonNull(type, "type");
        if (type instanceof ParameterizedType) {
            Class<?> rawClass = (Class<?>) ((ParameterizedType) type).getRawType();
            if (IDualResponse.class.isAssignableFrom(rawClass)) {
                return (ParameterizedType) type;
            }
        }
        throw new IllegalArgumentException("API method must return generic type of IDualResponse<SUC_DTO, ERR_DTO>" +
                "\nActual: " + type);
    }

    /**
     * A method for creating an instance of the DualResponse class
     * that implements the IDualResponse interface.
     *
     * @param call              - retrofit API resource call
     * @param errorType         - The DTO type representing the error model
     * @param endpointInfo      - description of the called resource in detail
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return {@link DualResponse}
     */
    public IDualResponse<?, ?> getIDualResponse(@Nonnull final Call<Object> call,
                                                @Nonnull final Type successType,
                                                @Nonnull final Type errorType,
                                                @Nonnull final String endpointInfo,
                                                @Nonnull final Annotation[] methodAnnotations,
                                                @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(call, "call");
        Utils.parameterRequireNonNull(successType, "successType");
        Utils.parameterRequireNonNull(errorType, "errorType");
        Utils.parameterRequireNonNull(endpointInfo, "endpointInfo");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        final Response<Object> response;
        try {
            logger.debug("Make an API call");
            response = call.execute();
        } catch (RuntimeException e) {
            logger.error("API call error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("API call error: {}", e.getMessage());
            throw new HttpCallException("Failed to make API call. See the reason below.", e);
        }
        logger.debug("API call completed successfully.");
        logger.debug("Define real values for the error/success response body");
        final Object sucDTO = getSuccessfulResponseBody(response, successType, methodAnnotations, retrofit);
        final Object errDTO = getErrorResponseBody(response, errorType, methodAnnotations, retrofit);
        final IDualResponse<?, ?> result = getDualResponseConsumer()
                .accept(sucDTO, errDTO, response.raw(), endpointInfo, methodAnnotations);
        logger.debug("IDualResponse created:\n{}", result);
        return result;
    }

    /**
     * @param methodAnnotations - list of annotations for the called API method
     * @return - description of the called resource in detail from the {@link EndpointInfo} annotation
     */
    @EverythingIsNonNull
    public String getEndpointInfo(Annotation[] methodAnnotations) {
        final EndpointInfo endpointInfo = Utils.getAnnotation(methodAnnotations, EndpointInfo.class);
        if (endpointInfo == null || endpointInfo.value() == null) {
            return "";
        }
        return endpointInfo.value().trim();
    }

    /**
     * @return {@link IDualResponseConsumer} for constructor of {@link DualResponseBase} heirs
     */
    public IDualResponseConsumer<IDualResponse<?, ?>> getDualResponseConsumer() {
        return dualResponseConsumer;
    }

}
