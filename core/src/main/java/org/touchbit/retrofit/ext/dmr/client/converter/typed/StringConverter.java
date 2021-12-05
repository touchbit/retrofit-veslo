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
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Converter for {@link String} java type
 * <p>
 * Created by Oleg Shaburov on 05.12.2021
 * shaburov.o.a@gmail.com
 */
public class StringConverter implements ExtensionConverter<String> {

    public static final StringConverter INSTANCE = new StringConverter();

    /**
     * @param type                 - request method body type.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link Converter}
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            /**
             * Converts {@link String} to {@link RequestBody}
             *
             * @param body -
             * @return HTTP {@link RequestBody} or null if {@link ExtensionConverter#BODY_NULL_VALUE} present
             */
            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                Utils.parameterRequireNonNull(body, "body");
                if (body instanceof String) {
                    final String stringBody = (String) body;
                    if (BODY_NULL_VALUE.equals(stringBody)) {
                        return null;
                    }
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    return RequestBody.create(mediaType, stringBody);
                }
                throw new ConverterUnsupportedTypeException(RawBodyConverter.class, String.class, body.getClass());
            }
        };

    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<String> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        return ResponseBody::string;

    }

}
