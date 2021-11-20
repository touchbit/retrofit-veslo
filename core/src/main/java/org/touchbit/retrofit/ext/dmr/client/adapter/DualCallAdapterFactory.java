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
import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.IDualResponseConsumer;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
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
import java.util.Objects;
import java.util.StringJoiner;

public class DualCallAdapterFactory extends CallAdapter.Factory {

    private final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer;

    public DualCallAdapterFactory() {
        this(DualResponse::new);
    }

    public DualCallAdapterFactory(IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        this.dualResponseConsumer = dualResponseConsumer;
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

        return new CallAdapter<Object, IDualResponse<?, ?>>() {

            @Override
            public ParameterizedType responseType() {
                return type;
            }

            @Override
            public IDualResponse<?, ?> adapt(Call<Object> call) {
                return getIDualResponse(call, successType, errorType, endpointInfo, methodAnnotations, retrofit);
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
        Objects.requireNonNull(call, "Parameter 'call' cannot be null.");
        Objects.requireNonNull(successType, "Parameter 'successType' cannot be null.");
        Objects.requireNonNull(errorType, "Parameter 'errorType' cannot be null.");
        Objects.requireNonNull(endpointInfo, "Parameter 'endpointInfo' cannot be null.");
        Objects.requireNonNull(methodAnnotations, "Parameter 'methodAnnotations' cannot be null.");
        Objects.requireNonNull(retrofit, "Parameter 'retrofit' cannot be null.");

        final Response<Object> response = getRetrofitResponse(call, successType, methodAnnotations);
        final Object errorDTO = getErrorDTO(response, errorType, methodAnnotations, retrofit);
        final Request request = call.request();
        return dualResponseConsumer.accept(request, response, errorDTO, endpointInfo, methodAnnotations);
    }

    @EverythingIsNonNull
    public Response<Object> getRetrofitResponse(final Call<Object> call,
                                                final Class<?> successType,
                                                final Annotation[] methodAnnotations) {
        Objects.requireNonNull(call, "Parameter 'call' cannot be null.");
        Objects.requireNonNull(successType, "Parameter 'successType' cannot be null.");
        Objects.requireNonNull(methodAnnotations, "Parameter 'methodAnnotations' cannot be null.");

        final Response<Object> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            StringJoiner sj = new StringJoiner("\n * ", "List of annotations of the method:\n * ", "");
            for (Annotation methodAnnotation : methodAnnotations) {
                sj.add(methodAnnotation.toString());
            }
            throw new HttpCallException("Failed to make API call.\n" + e.getMessage() + "\n" + sj, e);
        }
        if (response.isSuccessful() && response.body() == null && successType.equals(AnyBody.class)) {
            return Response.success(new AnyBody((byte[]) null), response.raw());
        }
        return response;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <DTO> DTO getErrorDTO(@Nonnull final Response<Object> response,
                                 @Nonnull final Class<DTO> errorType,
                                 @Nonnull final Annotation[] methodAnnotations,
                                 @Nonnull final Retrofit retrofit) {
        ResponseBody responseErrorBody = response.errorBody();
        if (responseErrorBody != null || AnyBody.class.equals(errorType)) {
            try {
                //responseErrorBody could be null. NPE does not arise. Suppress inspection.
                //noinspection ConstantConditions
                return (DTO) retrofit.responseBodyConverter(errorType, methodAnnotations).convert(responseErrorBody);
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
        EndpointInfo endpointInfo = null;
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof EndpointInfo) {
                endpointInfo = (EndpointInfo) annotation;
            }
        }
        if (endpointInfo != null && endpointInfo.value() != null) {
            return endpointInfo.value().trim();
        }
        return "";
    }

}
