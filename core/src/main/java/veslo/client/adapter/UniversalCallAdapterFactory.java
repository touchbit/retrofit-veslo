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

package veslo.client.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.HttpCallException;
import veslo.client.EndpointInfo;
import veslo.client.response.BaseDualResponse;
import veslo.client.response.DualResponse;
import veslo.client.response.IDualResponse;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;
import static veslo.constant.SonarRuleConstants.SONAR_GENERIC_WILDCARD_TYPES;

/**
 * Factory for creating {@link CallAdapter} with support {@link IDualResponse} type
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.12.2021
 */
@SuppressWarnings(SONAR_GENERIC_WILDCARD_TYPES)
public class UniversalCallAdapterFactory extends JavaTypeCallAdapterFactory {

    public static final UniversalCallAdapterFactory INSTANCE = new UniversalCallAdapterFactory();

    /**
     * {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    private final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer;

    /**
     * Default constructor with {@link DualResponse} return type handling
     */
    public UniversalCallAdapterFactory() {
        //noinspection ConstantConditions (idea inspection bug)
        this(LoggerFactory.getLogger(UniversalCallAdapterFactory.class), DualResponse::new);
    }

    /**
     * @param dualResponseConsumer - {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    @EverythingIsNonNull
    public UniversalCallAdapterFactory(final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        this(LoggerFactory.getLogger(UniversalCallAdapterFactory.class), dualResponseConsumer);
    }

    /**
     * @param logger - required Slf4J logger
     */
    @EverythingIsNonNull
    public UniversalCallAdapterFactory(final Logger logger) {
        //noinspection ConstantConditions (idea inspection bug)
        this(logger, DualResponse::new);
    }

    /**
     * @param logger               - required Slf4J logger
     * @param dualResponseConsumer - {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    @EverythingIsNonNull
    public UniversalCallAdapterFactory(final Logger logger,
                                       final IDualResponseConsumer<IDualResponse<?, ?>> dualResponseConsumer) {
        super(logger);
        Utils.parameterRequireNonNull(dualResponseConsumer, DUAL_RESPONSE_CONSUMER_PARAMETER);
        this.dualResponseConsumer = dualResponseConsumer;
    }

    /**
     * @param returnType        - called method return type
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return a call adapter for {@link IDualResponse} interface
     */
    @Override
    @EverythingIsNonNull
    public CallAdapter<Object, Object> get(final Type returnType,
                                           final Annotation[] methodAnnotations,
                                           final Retrofit retrofit) {
        if (Utils.isIDualResponse(returnType)) {
            final String annotations = Utils.arrayToPrettyString(methodAnnotations);
            logger.debug("Prepare API call\nAPI method annotations:{}", annotations);
            final ParameterizedType type = getParameterizedType(returnType);
            final String endpointInfo = getEndpointInfo(methodAnnotations);
            final Type successType = getParameterUpperBound(0, type);
            final Type errorType = getParameterUpperBound(1, type);
            return getCallAdapter(type, successType, errorType, endpointInfo, methodAnnotations, retrofit);
        } else {
            return super.get(returnType, methodAnnotations, retrofit);
        }
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
    public CallAdapter<Object, Object> getCallAdapter(final ParameterizedType type,
                                                      final Type successType,
                                                      final Type errorType,
                                                      final String endpointInfo,
                                                      final Annotation[] methodAnnotations,
                                                      final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(successType,  SUCCESS_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(errorType, ERROR_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(endpointInfo, ENDPOINT_INFO_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new CallAdapter<Object, Object>() {

            /**
             * @return @see {@link CallAdapter#responseType()}
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
                logger.info("API call: {}", finalInfo);
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
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
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
        Utils.parameterRequireNonNull(call, CALL_PARAMETER);
        Utils.parameterRequireNonNull(successType,  SUCCESS_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(errorType, ERROR_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(endpointInfo, ENDPOINT_INFO_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final Response<Object> response;
        try {
            logger.debug("Make an API call");
            response = call.execute();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpCallException("Failed to make API call. See the reason below.", e);
        }
        logger.debug("API call completed successfully.");
        logger.debug("Define real values for the error/success response body");
        final Object sucDTO;
        if (successType == Void.TYPE || successType == Void.class) {
            sucDTO = null;
        } else {
            sucDTO = getSuccessfulResponseBody(response, successType, methodAnnotations, retrofit);
        }
        final Object errDTO;
        if (errorType == Void.TYPE || errorType == Void.class) {
            errDTO = null;
        } else {
            errDTO = getErrorResponseBody(response, errorType, methodAnnotations, retrofit);
        }
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
     * @return {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    public IDualResponseConsumer<IDualResponse<?, ?>> getDualResponseConsumer() {
        return dualResponseConsumer;
    }

}
