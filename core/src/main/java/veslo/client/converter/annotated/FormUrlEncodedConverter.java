/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo.client.converter.annotated;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.bean.urlencoded.FormUrlEncoded;
import veslo.bean.urlencoded.FormUrlEncodedMapper;
import veslo.bean.urlencoded.IFormUrlEncodedMapper;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * Converter for {@link FormUrlEncoded} annotated classes
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 21.02.2022
 */
public class FormUrlEncodedConverter implements ExtensionConverter<Object> {

    /**
     * {@link FormUrlEncodedConverter} constant
     */
    public static final FormUrlEncodedConverter INSTANCE = new FormUrlEncodedConverter();

    /**
     * {@link IFormUrlEncodedMapper} mapper implementation
     */
    private final IFormUrlEncodedMapper mapper;

    /**
     * Default constructor with {@link FormUrlEncodedMapper} by default
     */
    public FormUrlEncodedConverter() {
        this(FormUrlEncodedMapper.INSTANCE);
    }

    /**
     * @param mapper - {@link IFormUrlEncodedMapper} mapper implementation
     */
    public FormUrlEncodedConverter(final IFormUrlEncodedMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new RequestBodyConverter() {

            /**
             * @param body - annotated classes {@link FormUrlEncoded}
             * @return {@link RequestBody}
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                return createRequestBody(methodAnnotations, mapper.marshal(body));
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
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<Object>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             */
            @Override
            public Object convert(@Nullable ResponseBody responseBody) throws IOException {
                final String body = copyBody(responseBody);
                if (body == null) {
                    return null;
                }
                final Class<?> modelType;
                if (type instanceof Class) {
                    modelType = (Class<?>) type;
                } else {
                    final ParameterizedType parameterizedType = (ParameterizedType) type;
                    modelType = (Class<?>) parameterizedType.getRawType();
                }
                return mapper.unmarshal(modelType, body);
            }

        };
    }
}
