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

package org.touchbit.retrofit.veslo.client.converter.typed;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.veslo.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.veslo.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.veslo.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Reference/primitive byte array converter (raw body)
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 16.12.2021
 */
public class ByteArrayConverter implements ExtensionConverter<Object> {

    public static final ByteArrayConverter INSTANCE = new ByteArrayConverter();

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
             * @param body - Byte[] or byte[]
             * @return {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                assertSupportedBodyType(INSTANCE, body, Byte[].class, byte[].class);
                final byte[] bytes = Utils.toPrimitiveByteArray(body);
                return createRequestBody(methodAnnotations, bytes);
            }

        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<Object> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new ResponseBodyConverter<Object>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return null if body == null otherwise Byte[] or byte[] if body present or empty
             * @throws IOException                       body bytes not readable
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @Nullable
            public Object convert(@Nullable ResponseBody responseBody) throws IOException {
                assertSupportedBodyType(INSTANCE, type, Byte[].class, byte[].class);
                final String body = copyBody(responseBody);
                if (body == null) {
                    return null;
                }
                final byte[] bytes = body.getBytes();
                if (type.equals(Byte[].class)) {
                    return Utils.toObjectByteArray(bytes);
                }
                return bytes;
            }

        };

    }

}
