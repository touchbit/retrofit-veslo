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

package org.touchbit.retrofit.ext.dmr.client.converter.ext;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class AnyBodyConverter implements ExtensionConverter<AnyBody> {

    @Override
    @EverythingIsNonNull
    public <M> Converter<M, RequestBody> requestBodyConverter(final Type type,
                                                              final Annotation[] parameterAnnotations,
                                                              final Annotation[] methodAnnotations,
                                                              final Retrofit retrofit) {
        return new Converter<M, RequestBody>() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(M value) {
                if (value instanceof AnyBody) {
                    final AnyBody anyBody = (AnyBody) value;
                    final MediaType mediaType = ConverterUtils.getMediaType(methodAnnotations);
                    return RequestBody.create(mediaType, anyBody.getBytes());
                }
                throw new ConverterUnsupportedTypeException(AnyBodyConverter.class, AnyBody.class, value.getClass());
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public Converter<ResponseBody, AnyBody> responseBodyConverter(final Type type,
                                                                  final Annotation[] methodAnnotations,
                                                                  final Retrofit retrofit) {
        return new Converter<ResponseBody, AnyBody>() {

            @Override
            @EverythingIsNonNull
            public AnyBody convert(ResponseBody value) throws IOException {
                return new AnyBody(value.bytes());
            }

        };
    }

}
