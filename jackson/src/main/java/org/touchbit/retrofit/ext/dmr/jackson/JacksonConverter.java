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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;

/**
 * Jackson 2 converter
 * <p>
 * Created: 08.11.2021
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 */
public class JacksonConverter<T> implements ExtensionConverter<T> {

    public static final String JSON_NULL_VALUE = "JSON_NULL_VALUE";

    private final ObjectMapper requestObjectMapper;
    private final ObjectMapper responseObjectMapper;

    public JacksonConverter() {
        this(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT),
                new ObjectMapper().enable(FAIL_ON_NULL_FOR_PRIMITIVES, ACCEPT_EMPTY_STRING_AS_NULL_OBJECT));
    }

    public JacksonConverter(ObjectMapper requestObjectMapper, ObjectMapper responseObjectMapper) {
        this.requestObjectMapper = requestObjectMapper;
        this.responseObjectMapper = responseObjectMapper;
    }

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

            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                Utils.parameterRequireNonNull(body, "body");
                final ObjectMapper objectMapper = getRequestObjectMapper();
                final JavaType javaType = objectMapper.constructType(type);
                final ObjectWriter objectWriter = objectMapper.writerFor(javaType);
                final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                try {
                    if (BODY_NULL_VALUE.equals(body)) {
                        return null;
                    } else if (JSON_NULL_VALUE.equals(body)) {
                        return RequestBody.create(mediaType, objectWriter.writeValueAsBytes(null));
                    } else {
                        return RequestBody.create(mediaType, objectWriter.writeValueAsBytes(body));
                    }
                } catch (Exception e) {
                    throw new ConvertCallException("Body not convertible to JSON. Body " + body.getClass(), e);
                }
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<T> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new ResponseBodyConverter<T>() {

            @Override
            @Nullable
            public T convert(@Nullable ResponseBody body) {
                if (body == null || body.contentLength() == 0) {
                    return null;
                }
                try {
                    final ObjectMapper objectMapper = getResponseObjectMapper();
                    final JavaType javaType = objectMapper.constructType(type);
                    return objectMapper.readerFor(javaType).readValue(body.string());
                } catch (Exception e) {
                    throw new ConvertCallException("\nResponse body not convertible to type " +
                            type + "\n" + e.getMessage(), e);
                }
            }
        };
    }

    public ObjectMapper getRequestObjectMapper() {
        return requestObjectMapper;
    }

    public ObjectMapper getResponseObjectMapper() {
        return responseObjectMapper;
    }

}
