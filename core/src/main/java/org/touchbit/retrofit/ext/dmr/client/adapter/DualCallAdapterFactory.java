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

import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DualCallAdapterFactory extends CallAdapter.Factory {

    private final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer;
    private final Logger logger;

    public DualCallAdapterFactory() {
        this(LoggerFactory.getLogger(DualCallAdapterFactory.class), DualResponse::new);
    }

    public DualCallAdapterFactory(IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        this(LoggerFactory.getLogger(DualCallAdapterFactory.class), dualResponseConsumer);
    }

    public DualCallAdapterFactory(Logger logger) {
        this(logger, DualResponse::new);
    }

    public DualCallAdapterFactory(Logger logger, IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        Utils.parameterRequireNonNull(logger, "logger");
        Utils.parameterRequireNonNull(dualResponseConsumer, "dualResponseConsumer");
        this.dualResponseConsumer = dualResponseConsumer;
        this.logger = logger;
    }

    @Override
    @EverythingIsNonNull
    @SuppressWarnings({"rawtypes"})
    public CallAdapter<Object, IDualResponse<?, ?>> get(final Type rawType,
                                                        final Annotation[] methodAnnotations,
                                                        final Retrofit retrofit) {
        final ParameterizedType type = getParameterizedType(rawType);
        final String endpointInfo = getEndpointInfo(methodAnnotations);
        final Class successType = getRawType(getParameterUpperBound(0, type));
        final Class errorType = getRawType(getParameterUpperBound(1, type));
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
                                                                   final Class<?> successType,
                                                                   final Class<?> errorType,
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

            @Override
            @Nonnull
            public Type responseType() {
                return type;
            }

            @Override
            @EverythingIsNonNull
            public IDualResponse<?, ?> adapt(Call<Object> call) {
                final String finalInfo;
                if (endpointInfo.trim().isEmpty()) {
                    finalInfo = call.request().method() + " " + call.request().url();
                } else {
                    finalInfo = endpointInfo;
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
        if (type instanceof ParameterizedType) {
            Class<?> rawClass = (Class<?>) ((ParameterizedType) type).getRawType();
            if (IDualResponse.class.isAssignableFrom(rawClass)) {
                return (ParameterizedType) type;
            }
        }
        throw new IllegalArgumentException("API methods must return an implementation class of " +
                IDualResponse.class + "\nActual: " + type);
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
                                                @Nonnull final Class<?> successType,
                                                @Nonnull final Class<?> errorType,
                                                @Nonnull final String endpointInfo,
                                                @Nonnull final Annotation[] methodAnnotations,
                                                @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(call, "call");
        Utils.parameterRequireNonNull(successType, "successType");
        Utils.parameterRequireNonNull(errorType, "errorType");
        Utils.parameterRequireNonNull(endpointInfo, "endpointInfo");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        final Response<Object> response = getRetrofitResponse(call, successType, methodAnnotations);
        final Object errorDTO = getErrorDTO(response, errorType, methodAnnotations, retrofit);
        final Request request = call.request();
        return dualResponseConsumer.accept(request, response, errorDTO, endpointInfo, methodAnnotations);
    }

    @EverythingIsNonNull
    public Response<Object> getRetrofitResponse(final Call<Object> call,
                                                final Class<?> successType,
                                                final Annotation[] methodAnnotations) {
        Utils.parameterRequireNonNull(call, "call");
        Utils.parameterRequireNonNull(successType, "successType");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");

        final Response<Object> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new HttpCallException("Failed to make API call.\n" + e.getMessage() + "\n", e);
        }
        if (response.isSuccessful() && response.body() == null && successType.equals(RawBody.class)) {
            return Response.success(new RawBody((byte[]) null), response.raw());
        }
        return response;
    }

    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <DTO> DTO getErrorDTO(@Nonnull final Response<Object> response,
                                 @Nonnull final Class<DTO> errorType,
                                 @Nonnull final Annotation[] methodAnnotations,
                                 @Nonnull final Retrofit retrofit) {
        ResponseBody nullableResponseErrorBody = response.errorBody();
        if (nullableResponseErrorBody != null || RawBody.class.equals(errorType)) {
            try {
                return (DTO) retrofit.responseBodyConverter(errorType, methodAnnotations)
                        .convert(nullableResponseErrorBody);
            } catch (Exception e) {
                throw new HttpCallException("Failed to convert error body.", e);
            }
        }
        return null;
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

}
