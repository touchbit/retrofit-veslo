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
import org.touchbit.retrofit.ext.dmr.exception.HttpCallException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class JavaTypeCallAdapterFactory extends BaseCallAdapterFactory {

    public JavaTypeCallAdapterFactory() {
        super(LoggerFactory.getLogger(JavaTypeCallAdapterFactory.class));
    }

    public JavaTypeCallAdapterFactory(Logger logger) {
        super(logger);
    }

    @Override
    @EverythingIsNonNull
    public CallAdapter<Object, Object> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new CallAdapter<Object, Object>() {

            @Override
            @Nonnull
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(@Nonnull Call<Object> call) {
                logger.info("API call: " + call.request().method() + " " + call.request().url());
                try {
                    final Response<Object> response = call.execute();
                    final Object dto;
                    logger.debug("Retrieving the response body.");
                    if (response.isSuccessful()) {
                        dto = getSuccessfulResponseBody(response, returnType, annotations, retrofit);
                    } else {
                        dto = getErrorResponseBody(response, returnType, annotations, retrofit);
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

}
