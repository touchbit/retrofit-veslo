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

package org.touchbit.retrofit.veslo.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.touchbit.retrofit.veslo.client.adapter.DualResponseCallAdapterFactory;
import org.touchbit.retrofit.veslo.client.adapter.IDualResponseConsumer;
import org.touchbit.retrofit.veslo.client.response.IDualResponse;
import org.touchbit.retrofit.veslo.util.Utils;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Factory for creating {@link CallAdapter} with support for Allure steps.
 * Overridden methods:
 * - getEndpointInfo() - get call info from allure {@link Description} annotation
 * - getCallAdapter() - wrapped in Allure step API call.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class AllureDualResponseCallAdapterFactory extends DualResponseCallAdapterFactory {

    public AllureDualResponseCallAdapterFactory() {
        super();
    }

    public AllureDualResponseCallAdapterFactory(IDualResponseConsumer<IDualResponse<?, ?>> consumer) {
        super(consumer);
    }

    public AllureDualResponseCallAdapterFactory(Logger logger) {
        super(logger);
    }

    public AllureDualResponseCallAdapterFactory(Logger logger, IDualResponseConsumer<IDualResponse<?, ?>> consumer) {
        super(logger, consumer);
    }

    /**
     * Method for getting an instance of the {@link CallAdapter} class
     * If there is no {@link Step} annotation and the stepInfo is full,
     * then the call will be wrapped in Allure step
     *
     * @param type              - called method return type
     * @param successType       - success DTO class
     * @param errorType         - error DTO class
     * @param stepInfo          - called method description
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return instance of {@link CallAdapter}
     */
    @Override
    @EverythingIsNonNull
    public CallAdapter<Object, IDualResponse<?, ?>> getCallAdapter(final ParameterizedType type,
                                                                   final Type successType,
                                                                   final Type errorType,
                                                                   final String stepInfo,
                                                                   final Annotation[] methodAnnotations,
                                                                   final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(successType, "successType");
        Utils.parameterRequireNonNull(errorType, "errorType");
        Utils.parameterRequireNonNull(stepInfo, "stepInfo");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        final Step annotation = Utils.getAnnotation(methodAnnotations, Step.class);
        if (annotation != null || stepInfo.trim().isEmpty()) {
            return super.getCallAdapter(type, successType, errorType, stepInfo, methodAnnotations, retrofit);
        }
        return Allure.step(stepInfo,
                () -> super.getCallAdapter(type, successType, errorType, stepInfo, methodAnnotations, retrofit));
    }

    /**
     * @param methodAnnotations - list of annotations for the called API method
     * @return - description of the called resource in detail from the {@link io.qameta.allure.Description} annotation
     */
    @Override
    @EverythingIsNonNull
    public String getEndpointInfo(Annotation[] methodAnnotations) {
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        final Description description = Utils.getAnnotation(methodAnnotations, Description.class);
        if (description != null) {
            return description.value().trim();
        }
        return "";
    }

}
