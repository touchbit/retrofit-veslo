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

package org.touchbit.retrofit.ext.dmr.util;

import org.touchbit.retrofit.ext.dmr.exception.UtilityClassException;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

public class Utils {

    private Utils() {
        throw new UtilityClassException();
    }

    public static void parameterRequireNonNull(Object parameter, String parameterName) {
        Objects.requireNonNull(parameter, "Parameter '" + parameterName + "' is required and cannot be null.");
    }

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

}
