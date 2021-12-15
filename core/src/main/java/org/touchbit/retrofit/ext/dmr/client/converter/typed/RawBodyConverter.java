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
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class RawBodyConverter implements ExtensionConverter<RawBody> {

    public static final RawBodyConverter INSTANCE = new RawBodyConverter();

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
                Utils.parameterRequireNonNull(body, "body");
                if (body instanceof RawBody) {
                    final RawBody rawBody = (RawBody) body;
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    if (rawBody.isNullBody()) {
                        return RequestBody.create(mediaType, new byte[]{});
                    }
                    return RequestBody.create(mediaType, rawBody.bytes());
                }
                throw new ConverterUnsupportedTypeException(RawBodyConverter.class, body.getClass(), RawBody.class);
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<RawBody> responseBodyConverter(final Type type,
                                                                final Annotation[] methodAnnotations,
                                                                final Retrofit retrofit) {
        return new ResponseBodyConverter<RawBody>() {

            @Override
            @Nonnull
            public RawBody convert(@Nullable ResponseBody body) throws IOException {
                if (body == null) {
                    return RawBody.nullable();
                }
                return new RawBody(body.bytes());
            }

        };
    }

}
