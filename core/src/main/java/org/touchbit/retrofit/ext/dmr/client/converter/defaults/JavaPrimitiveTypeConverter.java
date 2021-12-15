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
 * Converter for primitive java types: boolean, byte, char, double, float, int, long, short
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.12.2021
 */
@SuppressWarnings("rawtypes")
public class JavaPrimitiveTypeConverter implements ExtensionConverter {

    public static final JavaPrimitiveTypeConverter INSTANCE = new JavaPrimitiveTypeConverter();

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
        if (type.equals(Character.TYPE)) {
            return CharacterConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Boolean.TYPE)) {
            return BooleanConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Byte.TYPE)) {
            return ByteConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Integer.TYPE)) {
            return IntegerConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Double.TYPE)) {
            return DoubleConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Float.TYPE)) {
            return FloatConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Long.TYPE)) {
            return LongConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Short.TYPE)) {
            return ShortConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, Character.TYPE, Boolean.TYPE, Byte.TYPE,
                Integer.TYPE, Double.TYPE, Float.TYPE, Long.TYPE, Short.TYPE);
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
        if (type.equals(Character.TYPE)) {
            return CharacterConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Boolean.TYPE)) {
            return BooleanConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Byte.TYPE)) {
            return ByteConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Integer.TYPE)) {
            return IntegerConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Double.TYPE)) {
            return DoubleConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Float.TYPE)) {
            return FloatConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Long.TYPE)) {
            return LongConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Short.TYPE)) {
            return ShortConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, Character.TYPE, Boolean.TYPE,
                Byte.TYPE, Integer.TYPE, Double.TYPE, Float.TYPE, Long.TYPE, Short.TYPE);
    }

}
