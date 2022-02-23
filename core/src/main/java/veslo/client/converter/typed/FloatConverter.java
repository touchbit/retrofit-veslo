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
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.PrimitiveConvertCallException;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * Reference/primitive Float java type converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
public class FloatConverter implements ExtensionConverter<Float> {

    /**
     * {@link FloatConverter} constant
     */
    public static final FloatConverter INSTANCE = new FloatConverter();

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
             * @param body - {@link Float} or float body
             * @return HTTP {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                assertSupportedBodyType(INSTANCE, body, Float.class, Float.TYPE);
                return createRequestBody(methodAnnotations, body.toString());
            }
        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<Float> responseBodyConverter(final Type type,
                                                              final Annotation[] methodAnnotations,
                                                              final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<Float>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return null if body == null or empty otherwise {@link Float} or float
             * @throws IOException body bytes not readable
             * @throws ConvertCallException inconvertible body
             * @throws PrimitiveConvertCallException primitive cannot be null
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Nullable
            @Override
            public Float convert(@Nullable ResponseBody responseBody) throws IOException {
                assertSupportedBodyType(INSTANCE, type, Float.class, Float.TYPE);
                final String body = copyBody(responseBody);
                if (body != null && body.length() != 0) {
                    try {
                        return Float.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Float conversion error:\n" +
                                "expected float number in range " + Float.MIN_VALUE + "..." + Float.MAX_VALUE + "\n" +
                                "but was '" + body + "'", e);
                    }
                }
                assertNotNullableBodyType(type);
                return null;
            }
        };
    }

}
