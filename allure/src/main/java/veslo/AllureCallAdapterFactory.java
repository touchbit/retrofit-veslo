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

package veslo;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.EndpointInfo;
import veslo.client.adapter.IDualResponseConsumer;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.response.BaseDualResponse;
import veslo.client.response.IDualResponse;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
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
public class AllureCallAdapterFactory extends UniversalCallAdapterFactory {

    /**
     * Default constructor with {@link AResponse} return type handling
     */
    public AllureCallAdapterFactory() {
        //noinspection ConstantConditions (idea inspection bug)
        super(LoggerFactory.getLogger(AllureCallAdapterFactory.class), AResponse::new);
    }

    /**
     * @param consumer - {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    @EverythingIsNonNull
    public AllureCallAdapterFactory(IDualResponseConsumer<IDualResponse<?, ?>> consumer) {
        super(LoggerFactory.getLogger(AllureCallAdapterFactory.class), consumer);
    }

    /**
     * @param logger - required Slf4J logger
     */
    @EverythingIsNonNull
    public AllureCallAdapterFactory(Logger logger) {
        //noinspection ConstantConditions (idea inspection bug)
        super(logger, AResponse::new);
    }

    /**
     * @param logger   - required Slf4J logger
     * @param consumer - {@link IDualResponseConsumer} for constructor of {@link BaseDualResponse} heirs
     */
    @EverythingIsNonNull
    public AllureCallAdapterFactory(Logger logger, IDualResponseConsumer<IDualResponse<?, ?>> consumer) {
        super(logger, consumer);
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
        final Step step = Utils.getAnnotation(methodAnnotations, Step.class);
        if (step != null) {
            return super.get(returnType, methodAnnotations, retrofit);
        }
        final CallAdapter<Object, Object> adapter = super.get(returnType, methodAnnotations, retrofit);
        //noinspection L101
        if (adapter == null) {
            throw new ConvertCallException("Missing CallAdapter for model " + returnType);
        }
        return new CallAdapter<Object, Object>() {

            /**
             * @return see {@link CallAdapter#responseType()}
             */
            @Override
            @Nonnull
            public Type responseType() {
                return adapter.responseType();
            }

            /**
             * @param call - see {@link Call}
             * @return see {@link CallAdapter#adapt(Call)}
             */
            @Override
            public Object adapt(final @Nonnull Call<Object> call) {
                final String endpointInfo = getEndpointInfo(methodAnnotations);
                if (endpointInfo == null || endpointInfo.trim().isEmpty()) {
                    return Allure.step("API call: no description", () -> {
                        Allure.addAttachment("ALLURE_ERROR", "Use annotations to describe the called API method:\n - " +
                                Description.class + "\n - " + EndpointInfo.class + "\n\n" +
                                "The @Step annotation value is ignored because it is expected that the step description " +
                                "will be provided using the aspectj library.");
                        return adapter.adapt(call);
                    });
                } else {
                    return Allure.step(endpointInfo, () -> adapter.adapt(call));
                }
            }
        };
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
        final String endpointInfo = super.getEndpointInfo(methodAnnotations);
        if (endpointInfo != null && !endpointInfo.trim().isEmpty()) {
            return endpointInfo;
        }
        return "";
    }

}
