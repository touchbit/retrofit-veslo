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

package org.touchbit.retrofit.ext.dmr.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Jackson 2 converter
 * <p>
 * Created by Oleg Shaburov on 08.11.2021
 * shaburov.o.a@gmail.com
 */
public class JacksonConverter<T> implements ExtensionConverter<T> {

    @Override
    @EverythingIsNonNull
    public <M> Converter<M, RequestBody> requestBodyConverter(final Type type,
                                                              final Annotation[] parameterAnnotations,
                                                              final Annotation[] methodAnnotations,
                                                              final Retrofit retrofit) {
        return new Converter<M, RequestBody>() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(M value) throws IOException {
                final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                final ObjectWriter objectWriter = objectMapper.writerFor((Class<?>) type);
                final MediaType mediaType = ConverterUtils.getMediaType(methodAnnotations);
                return RequestBody.create(mediaType, objectWriter.writeValueAsBytes(value));
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public Converter<ResponseBody, T> responseBodyConverter(final Type type,
                                                            final Annotation[] methodAnnotations,
                                                            final Retrofit retrofit) {
        return new Converter<ResponseBody, T>() {

            @Override
            @EverythingIsNonNull
            public T convert(ResponseBody value) throws IOException {
                final ObjectReader objectReader = new ObjectMapper().readerFor((Class<?>) type);
                return objectReader.readValue(value.bytes());
            }

        };
    }

}
