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
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A generic converter that supports IDualResponse and reference/primitive java types.
 * For example:
 * DualResponse<String, String> exampleMethodAPI(); -> {@link DualResponseCallAdapterFactory}
 * String exampleMethodAPI(); -> {@link JavaTypeCallAdapterFactory}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.12.2021
 */
public class UniversalCallAdapterFactory extends BaseCallAdapterFactory {

    /**
     * Default instance with {@link UniversalCallAdapterFactory} class logger
     */
    public static final UniversalCallAdapterFactory INSTANCE = new UniversalCallAdapterFactory();

    /**
     * Default constructor with this class logger
     */
    public UniversalCallAdapterFactory() {
        super(LoggerFactory.getLogger(UniversalCallAdapterFactory.class));
    }

    /**
     * @param logger - required Slf4J logger
     */
    protected UniversalCallAdapterFactory(final Logger logger) {
        super(logger);
    }

    /**
     * @param returnType        - called method return type
     * @param methodAnnotations - list of annotations for the called API method
     * @param retrofit          - HTTP client
     * @return {@link DualResponseCallAdapterFactory} or {@link JavaTypeCallAdapterFactory} depending on the return type
     */
    @Override
    @EverythingIsNonNull
    public CallAdapter<?, ?> get(final Type returnType, final Annotation[] methodAnnotations, final Retrofit retrofit) {
        final CallAdapter.Factory factory;
        if (ConvertUtils.isIDualResponse(returnType)) {
            factory = DualResponseCallAdapterFactory.INSTANCE;
        } else {
            factory = JavaTypeCallAdapterFactory.INSTANCE;
        }
        logger.debug("Used call adapter factory: {}", factory.getClass().getTypeName());
        return factory.get(returnType, methodAnnotations, retrofit);
    }

}
