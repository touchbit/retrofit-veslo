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

package org.touchbit.retrofit.ext.dmr.client.converter.defaults;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

public class StringConverter implements ExtensionConverter<String> {

    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                Objects.requireNonNull(body, "Parameter 'body' required");
                if (body instanceof String) {
                    final MediaType mediaType = ConverterUtils.getMediaType(methodAnnotations);
                    return RequestBody.create(mediaType, (String) body);
                }
                throw new ConverterUnsupportedTypeException(StringConverter.class, String.class, body.getClass());
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<String> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        return new ResponseBodyConverter<String>() {

            @Nullable
            @Override
            public String convert(@Nullable ResponseBody body) {
                if (body == null || body.contentLength() == 0) {
                    return null;
                }
                return wrap(() -> new String(body.bytes()));
            }

        };
    }

}
