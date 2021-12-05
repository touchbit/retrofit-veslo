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
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.PrimitiveConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class BaseCallAdapterFactory extends CallAdapter.Factory {

    protected final Logger logger;

    protected BaseCallAdapterFactory(Logger logger) {
        Utils.parameterRequireNonNull(logger, "logger");
        this.logger = logger;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected Object getSuccessfulResponseBody(final @Nonnull Response<?> response,
                                               final @Nonnull Type returnType,
                                               final @Nonnull Annotation[] annotations,
                                               final @Nonnull Retrofit retrofit) {
        final String typeName = Utils.getTypeName(returnType);
        logger.debug("Get successful response body model for type {}", typeName);
        try {
            final Object dto;
            final int code = response.code();
            if (code == 204 || code == 205 || response.body() == null) {
                logger.debug("Received 'No content' response. Forced conversion null body.");
                dto = retrofit.responseBodyConverter(returnType, annotations).convert(null);
            } else {
                dto = response.body();
            }
            logger.debug("Successful body is {}present", dto == null ? "not " : "");
            return dto;
        } catch (ConvertCallException e) {
            if (response.isSuccessful()) {
                throw e;
            }
        } catch (IOException e) {
            if (response.isSuccessful()) {
                throw new ConvertCallException("Error converting response body to type " + returnType.getTypeName(), e);
            }
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected Object getErrorResponseBody(final @Nonnull Response<?> response,
                                          final @Nonnull Type returnType,
                                          final @Nonnull Annotation[] annotations,
                                          final @Nonnull Retrofit retrofit) {
        logger.debug("Get error response body model for type {}", returnType);
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

    protected void checkPrimitiveConvertCall(final @Nonnull Type returnType, final @Nullable Object body) {
        logger.debug("Checking that the primitive is not null");
        if (returnType instanceof Class && ((Class<?>) returnType).isPrimitive() && body == null) {
            throw new PrimitiveConvertCallException(returnType);
        }
    }

}
