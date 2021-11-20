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
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
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
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object value) {
                final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                final ObjectWriter objectWriter = objectMapper.writerFor((Class<?>) type);
                final MediaType mediaType = ConverterUtils.getMediaType(methodAnnotations);
                return wrap(() -> RequestBody.create(mediaType, objectWriter.writeValueAsBytes(value)));
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<T> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        return new ResponseBodyConverter<T>() {

            @Override
            @Nullable
            public T convert(@Nullable ResponseBody value) {
                if (value == null || value.contentLength() == 0) {
                    return null;
                }
                final ObjectReader objectReader = new ObjectMapper().readerFor((Class<?>) type);
                return wrap(() -> objectReader.readValue(value.bytes()));
            }

        };
    }

}
