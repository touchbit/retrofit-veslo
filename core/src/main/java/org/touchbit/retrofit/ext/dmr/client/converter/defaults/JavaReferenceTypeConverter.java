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

import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.*;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Converter for reference java types:
 * Boolean.class, Byte.class, Char.class, Double.class, Float.class, Int.class, Long.class, Short.class, String.class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.12.2021
 */
@SuppressWarnings("rawtypes")
public class JavaReferenceTypeConverter implements ExtensionConverter {

    public static final JavaReferenceTypeConverter INSTANCE = new JavaReferenceTypeConverter();

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] paramAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(paramAnnotations, "parameterAnnotations");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (type.equals(String.class)) {
            return StringConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Character.class)) {
            return CharacterConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Boolean.class)) {
            return BooleanConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Byte.class)) {
            return ByteConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Integer.class)) {
            return IntegerConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Double.class)) {
            return DoubleConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Float.class)) {
            return FloatConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Long.class)) {
            return LongConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Short.class)) {
            return ShortConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, String.class, Character.class,
                Boolean.class, Byte.class, Integer.class, Double.class, Float.class, Long.class, Short.class);
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<?> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (type.equals(String.class)) {
            return StringConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Character.class)) {
            return CharacterConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Boolean.class)) {
            return BooleanConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Byte.class)) {
            return ByteConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Integer.class)) {
            return IntegerConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Double.class)) {
            return DoubleConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Float.class)) {
            return FloatConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Long.class)) {
            return LongConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Short.class)) {
            return ShortConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, String.class, Character.class, Boolean.class,
                Byte.class, Integer.class, Double.class, Float.class, Long.class, Short.class);
    }

}
