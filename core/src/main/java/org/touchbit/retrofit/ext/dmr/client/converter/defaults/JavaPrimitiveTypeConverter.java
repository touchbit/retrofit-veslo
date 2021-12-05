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

import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.PrimitiveConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Converter for primitive java types: boolean, byte, char, double, float, int, long, short
 * <p>
 * Created by Oleg Shaburov on 05.12.2021
 * shaburov.o.a@gmail.com
 */
public class JavaPrimitiveTypeConverter extends JavaTypeConverterBase {

    public static final JavaPrimitiveTypeConverter INSTANCE = new JavaPrimitiveTypeConverter();

    /**
     * @param type              - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link Converter}
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<?> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        return new ResponseBodyConverter<Object>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return - Converted value
             * @throws IOException if ResponseBody data not readable
             */
            @Nonnull
            @Override
            public Object convert(@Nullable ResponseBody responseBody) throws IOException {
                if (responseBody == null || responseBody.contentLength() == 0) {
                    throw new PrimitiveConvertCallException(type);
                }
                final String body = responseBody.string();
                if (type.equals(Character.TYPE)) {
                    final int length = body.length();
                    if (length != 1) {
                        throw new ConvertCallException("Character conversion error:\n" +
                                "expected one character\nbut was " + length);
                    }
                    return body.charAt(0);
                } else if (type.equals(Boolean.TYPE)) {
                    if (body.equalsIgnoreCase("false") || body.equalsIgnoreCase("true")) {
                        return Boolean.valueOf(body);
                    }
                    throw new ConvertCallException("Boolean conversion error:\n" +
                            "expected true/false\nbut was " + exceptionBodyValue(body));
                } else if (type.equals(Byte.TYPE)) {
                    try {
                        return Byte.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Byte conversion error:\nexpected byte in range " +
                                Byte.MIN_VALUE + "..." + Byte.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Integer.TYPE)) {
                    try {
                        return Integer.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Integer conversion error:\nexpected integer number in range " +
                                Integer.MIN_VALUE + "..." + Integer.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Double.TYPE)) {
                    try {
                        return Double.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Double conversion error:\nexpected double number in range " +
                                Double.MIN_VALUE + "..." + Double.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Float.TYPE)) {
                    try {
                        return Float.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Float conversion error:\nexpected float number in range " +
                                Float.MIN_VALUE + "..." + Float.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Long.TYPE)) {
                    try {
                        return Long.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Long conversion error:\nexpected long number in range " +
                                Long.MIN_VALUE + "..." + Long.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Short.TYPE)) {
                    try {
                        return Short.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Short conversion error:\nexpected short number in range " +
                                Short.MIN_VALUE + "..." + Short.MAX_VALUE + "\nbut was " + exceptionBodyValue(body), e);
                    }
                } else {
                    throw new ConvertCallException("Received an unsupported type for conversion: " + getTypeName(type));
                }
            }
        };
    }

}
