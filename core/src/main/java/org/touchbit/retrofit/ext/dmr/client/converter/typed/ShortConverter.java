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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.exception.PrimitiveConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Reference/primitive Short java type converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
public class ShortConverter implements ExtensionConverter<Short> {

    public static final ShortConverter INSTANCE = new ShortConverter();

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            /**
             * @param body - Short body
             * @return HTTP {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                assertSupportedBodyType(INSTANCE, body, Short.class, Short.TYPE);
                Short aShort = (Short) body;
                final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                return RequestBody.create(mediaType, aShort.toString());
            }
        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<Short> responseBodyConverter(final Type type,
                                                              final Annotation[] methodAnnotations,
                                                              final Retrofit retrofit) {
        return new ResponseBodyConverter<Short>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return {@link Short}
             * @throws IOException body bytes not readable
             * @throws ConvertCallException inconvertible body
             * @throws PrimitiveConvertCallException primitive cannot be null
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Nullable
            @Override
            public Short convert(@Nullable ResponseBody responseBody) throws IOException {
                if (responseBody != null && responseBody.contentLength() != 0) {
                    assertSupportedBodyType(INSTANCE, type, Short.class, Short.TYPE);
                    final String body = responseBody.string();
                    try {
                        return Short.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Short conversion error:\n" +
                                "expected short number in range " + Short.MIN_VALUE + "..." + Short.MAX_VALUE + "\n" +
                                "but was '" + body + "'", e);
                    }
                }
                assertNotNullableBodyType(type);
                return null;
            }
        };
    }

}
