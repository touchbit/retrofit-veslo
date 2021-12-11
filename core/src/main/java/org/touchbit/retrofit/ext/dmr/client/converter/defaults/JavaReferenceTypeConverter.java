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
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.io.IOException;
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
public class JavaReferenceTypeConverter extends JavaTypeConverterBase {

    public static final JavaReferenceTypeConverter INSTANCE = new JavaReferenceTypeConverter();

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
            @Nullable
            @Override
            public Object convert(@Nullable ResponseBody responseBody) throws IOException {
                if (responseBody == null || responseBody.contentLength() == 0) {
                    return null;
                }
                final String body = responseBody.string();
                if (type.equals(String.class)) {
                    return body;
                } else if (type.equals(Character.class)) {
                    final int length = body.length();
                    if (length != 1) {
                        throw new ConvertCallException("Character conversion error:\n" +
                                "expected one character\nbut was " + length);
                    }
                    return body.charAt(0);
                } else if (type.equals(Boolean.class)) {
                    if (body.equalsIgnoreCase("false") || body.equalsIgnoreCase("true")) {
                        return Boolean.valueOf(body);
                    }
                    throw new ConvertCallException("Boolean conversion error:\n" +
                            "expected true/false\n" +
                            "but was " + exceptionBodyValue(body));
                } else if (type.equals(Byte.class)) {
                    try {
                        return Byte.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Byte conversion error:\n" +
                                "expected byte in range " + Byte.MIN_VALUE + "..." + Byte.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Integer.class)) {
                    try {
                        return Integer.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Integer conversion error:\n" +
                                "expected integer number in range " + Integer.MIN_VALUE + "..." + Integer.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Double.class)) {
                    try {
                        return Double.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Double conversion error:\n" +
                                "expected double number in range " + Double.MIN_VALUE + "..." + Double.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Float.class)) {
                    try {
                        return Float.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Float conversion error:\n" +
                                "expected float number in range " + Float.MIN_VALUE + "..." + Float.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Long.class)) {
                    try {
                        return Long.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Long conversion error:\n" +
                                "expected long number in range " + Long.MIN_VALUE + "..." + Long.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else if (type.equals(Short.class)) {
                    try {
                        return Short.valueOf(body);
                    } catch (Exception e) {
                        throw new ConvertCallException("Short conversion error:\n" +
                                "expected short number in range " + Short.MIN_VALUE + "..." + Short.MAX_VALUE + "\n" +
                                "but was " + exceptionBodyValue(body), e);
                    }
                } else {
                    throw new ConvertCallException("Received an unsupported type for conversion: " + getTypeName(type));
                }
            }
        };
    }

}
