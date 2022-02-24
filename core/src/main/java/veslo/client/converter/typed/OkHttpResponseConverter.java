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

package veslo.client.converter.typed;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.adapter.JavaTypeCallAdapterFactory;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * {@link Response} java type converter
 * The converter does not convert anything and always returns null .
 * The raw response is returned in the {@link JavaTypeCallAdapterFactory#get(Type, Annotation[], Retrofit)} method.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 16.12.2021
 */
public class OkHttpResponseConverter implements ExtensionConverter<Object> {

    /**
     * {@link OkHttpResponseConverter} constant
     */
    public static final OkHttpResponseConverter INSTANCE = new OkHttpResponseConverter();

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
             * @param body - request body
             * @return nothing (always throws an exception)
             * @throws ConverterUnsupportedTypeException unsupported body type ({@link Response})
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                throw new ConvertCallException("Type " + Response.class.getName() + " cannot be used to make requests.");
            }

        };
    }

    /**
     * The converter does not convert anything and always returns null .
     * The raw response is returned in the {@link JavaTypeCallAdapterFactory#get(Type, Annotation[], Retrofit)} method.
     *
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    @SuppressWarnings("java:S5443")
    public ResponseBodyConverter<Object> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<Object>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return null if body == null otherwise {@link File} if body present or empty
             */
            @Override
            @Nullable
            public Object convert(@Nullable ResponseBody responseBody) {
                assertSupportedBodyType(INSTANCE, type, Response.class);
                return responseBody;
            }

        };
    }
}
