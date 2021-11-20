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

package org.touchbit.retrofit.ext.dmr.client.converter.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ThrowableRunnable;
import org.touchbit.retrofit.ext.dmr.util.ThrowableSupplier;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ExtensionConverter<DTO> {

    /**
     * @param type                 - request method body type.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    RequestBodyConverter requestBodyConverter(Type type,
                                              Annotation[] parameterAnnotations,
                                              Annotation[] methodAnnotations,
                                              Retrofit retrofit);

    /**
     * @param type              - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    ResponseBodyConverter<DTO> responseBodyConverter(Type type,
                                                     Annotation[] methodAnnotations,
                                                     Retrofit retrofit);

    default void wrap(ThrowableRunnable runnable) {
        try {
            runnable.execute();
        } catch (Throwable e) {
            throw new ConvertCallException("An error occurred while converting. See the reasons below.", e);
        }
    }

    default <A> A wrap(ThrowableSupplier<A> supplier) {
        try {
            return supplier.execute();
        } catch (Throwable e) {
            throw new ConvertCallException("An error occurred while converting. See the reasons below.", e);
        }
    }

    interface RequestBodyConverter extends Converter<Object, RequestBody> {

        @Override
        @EverythingIsNonNull
        RequestBody convert(Object value);

    }

    interface ResponseBodyConverter<DTO> extends Converter<ResponseBody, DTO> {

        @Override
        @Nullable
        DTO convert(@Nullable ResponseBody value);

    }

}
