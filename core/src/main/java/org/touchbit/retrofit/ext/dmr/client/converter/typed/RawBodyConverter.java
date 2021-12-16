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

package org.touchbit.retrofit.ext.dmr.client.converter.typed;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * {@link RawBody} java type converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 16.12.2021
 */
public class RawBodyConverter implements ExtensionConverter<RawBody> {

    public static final RawBodyConverter INSTANCE = new RawBodyConverter();

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(parameterAnnotations, "parameterAnnotations");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new RequestBodyConverter() {

            /**
             * @param body - {@link RawBody}
             * @return {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                assertSupportedBodyType(INSTANCE, body, RawBody.class);
                final RawBody rawBody = (RawBody) body;
                if (rawBody.isNullBody()) {
                    return null;
                }
                return createRequestBody(methodAnnotations, rawBody.bytes());
            }

        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<RawBody> responseBodyConverter(final Type type,
                                                                final Annotation[] methodAnnotations,
                                                                final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new ResponseBodyConverter<RawBody>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return {@link RawBody}
             * @throws IOException                       body bytes not readable
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @Nonnull
            public RawBody convert(@Nullable ResponseBody responseBody) throws IOException {
                if (responseBody == null) {
                    return RawBody.nullable();
                }
                assertSupportedBodyType(INSTANCE, type, RawBody.class);
                return new RawBody(responseBody.bytes());
            }

        };
    }

}
