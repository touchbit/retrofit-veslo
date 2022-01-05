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

package veslo.util;

import retrofit2.internal.EverythingIsNonNull;
import veslo.UtilityClassException;
import veslo.client.response.IDualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public class Utils {

    /**
     * Utility class
     */
    private Utils() {
        throw new UtilityClassException();
    }

    /**
     * @param parameter     - checked parameter
     * @param parameterName - checked parameter name
     * @throws NullPointerException if parameter is null
     */
    public static void parameterRequireNonNull(Object parameter, String parameterName) {
        Objects.requireNonNull(parameter, "Parameter '" + parameterName + "' is required and cannot be null.");
    }

    /**
     * @param annotations - list of annotations
     * @param expected    - annotation class
     * @param <A>         - annotation generic type
     * @return first annotation from annotation list by expected class
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <A extends Annotation> A getAnnotation(@Nullable final Annotation[] annotations,
                                                         @Nullable final Class<A> expected) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(expected)) {
                    return (A) annotation;
                }
            }
        }
        return null;
    }

    /**
     * @param data - {@link String}
     * @return {@link Byte[]}
     */
    @EverythingIsNonNull
    public static Byte[] toObjectByteArray(String data) {
        Utils.parameterRequireNonNull(data, "data");
        return toObjectByteArray(data.getBytes());
    }

    /**
     * @param bytes - byte[]
     * @return {@link Byte[]}
     */
    @EverythingIsNonNull
    public static Byte[] toObjectByteArray(byte[] bytes) {
        Utils.parameterRequireNonNull(bytes, "bytes");
        Byte[] result = new Byte[bytes.length];
        Arrays.setAll(result, n -> bytes[n]);
        return result;
    }

    /**
     * @param bytes - {@link Byte[]} or {@link byte[]} object
     * @return byte[]
     */
    @EverythingIsNonNull
    public static byte[] toPrimitiveByteArray(Object bytes) {
        Utils.parameterRequireNonNull(bytes, "bytes");
        if (bytes instanceof Byte[]) {
            return toPrimitiveByteArray((Byte[]) bytes);
        }
        if (bytes instanceof byte[]) {
            return (byte[]) bytes;
        }
        throw new IllegalArgumentException("Received unsupported type: " + bytes.getClass() + "\n" +
                "Expected: " + Byte[].class.getTypeName() + " or " + byte[].class.getTypeName());
    }

    /**
     * @param bytes - {@link Byte[]}
     * @return byte[]
     */
    @EverythingIsNonNull
    public static byte[] toPrimitiveByteArray(Byte[] bytes) {
        Utils.parameterRequireNonNull(bytes, "bytes");
        byte[] primitiveArray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            primitiveArray[i] = bytes[i];
        }
        return primitiveArray;
    }

    /**
     * @param type - nullable java Type
     * @return "null" or type name
     */
    @Nonnull
    public static String getTypeName(@Nullable Type type) {
        return type == null ? "null" : type.getTypeName();
    }

    /**
     * @param object - nullable object
     * @return @return "null" or class type name
     */
    @Nonnull
    public static String getTypeName(@Nullable Object object) {
        return object == null ? "null" : getTypeName(object.getClass());
    }

    /**
     * For example: "API method annotations:" + Utils.arrayToPrettyString(getCallAnnotations())
     * * API method annotations:
     * *   @retrofit2.http.POST("/api/mock/application/json/Object")
     * *   @retrofit2.http.Headers({"Content-Type: application/json"})
     *
     * @param objects - nullable object array
     * @return empty string or list of objects converted to string with leading "\n  " delimiter
     */
    public static String arrayToPrettyString(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return "";
        }
        String delimiter = "\n  ";
        return delimiter + Arrays.stream(objects)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter))
                .trim();
    }

    public static boolean isIDualResponse(final Type type) {
        if (type instanceof ParameterizedType) {
            Class<?> rawClass = (Class<?>) ((ParameterizedType) type).getRawType();
            return IDualResponse.class.isAssignableFrom(rawClass);
        }
        return false;
    }

}
