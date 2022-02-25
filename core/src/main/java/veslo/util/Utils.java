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

import org.apache.commons.lang3.ArrayUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.FormUrlEncodedMapperException;
import veslo.RuntimeIOException;
import veslo.UtilityClassException;
import veslo.client.response.IDualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static veslo.constant.ParameterNameConstants.*;
import static veslo.constant.SonarRuleConstants.SONAR_GENERIC_WILDCARD_TYPES;

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
        Utils.parameterRequireNonNull(data, DATA_PARAMETER);
        return toObjectByteArray(data.getBytes());
    }

    /**
     * @param bytes - byte[]
     * @return {@link Byte[]}
     */
    @EverythingIsNonNull
    public static Byte[] toObjectByteArray(byte[] bytes) {
        Utils.parameterRequireNonNull(bytes, BYTES_PARAMETER);
        return ArrayUtils.toObject(bytes);
    }

    /**
     * @param bytes - {@link Byte[]} or {@link byte[]} object
     * @return byte[]
     */
    @EverythingIsNonNull
    public static byte[] toPrimitiveByteArray(Object bytes) {
        Utils.parameterRequireNonNull(bytes, BYTES_PARAMETER);
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
        Utils.parameterRequireNonNull(bytes, BYTES_PARAMETER);
        return ArrayUtils.toPrimitive(bytes);
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

    /**
     * Helper method to read UTF-8 encoded resource file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param path - resource file path
     * @return resource file content ({@link String})
     * @throws RuntimeIOException if resource file not readable
     */
    public static String readResourceFile(String path) {
        Utils.parameterRequireNonNull(path, PATH_PARAMETER);
        return readResourceFile(path, StandardCharsets.UTF_8);
    }

    /**
     * Helper method to read a resource file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param path    - resource file path
     * @param charset - resource file charset
     * @return resource file content ({@link String})
     * @throws RuntimeIOException if resource file not readable
     */
    public static String readResourceFile(String path, Charset charset) {
        Utils.parameterRequireNonNull(path, PATH_PARAMETER);
        Utils.parameterRequireNonNull(charset, CHARSET_PARAMETER);
        try (final InputStream stream = getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new RuntimeIOException("Resource file not readable: " + path);
            }
            return getBufferedReader(stream, charset).lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeIOException("Resource file not readable: " + path, e);
        }
    }

    /**
     * Helper method to read UTF-8 encoded file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param path - file path
     * @return file content ({@link String})
     * @throws RuntimeIOException if file not readable
     */
    public static String readFile(String path) {
        Utils.parameterRequireNonNull(path, PATH_PARAMETER);
        return readFile(path, StandardCharsets.UTF_8);
    }

    /**
     * Helper method to read a file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param path    - file path
     * @param charset - file charset
     * @return file content ({@link String})
     * @throws RuntimeIOException if file not readable
     */
    public static String readFile(String path, Charset charset) {
        Utils.parameterRequireNonNull(path, PATH_PARAMETER);
        Utils.parameterRequireNonNull(charset, CHARSET_PARAMETER);
        return readFile(new File(path), charset);
    }

    /**
     * Helper method to read UTF-8 encoded file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param file - {@link File}
     * @return file content ({@link String})
     * @throws RuntimeIOException if file not readable
     */
    public static String readFile(File file) {
        Utils.parameterRequireNonNull(file, FILE_PARAMETER);
        return readFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Helper method to read a file.
     * Important! In case of errors, a RuntimeException will be thrown!
     * Use only in tests.
     *
     * @param file    - {@link File}
     * @param charset - file charset
     * @return file content ({@link String})
     * @throws RuntimeIOException if file not readable
     */
    public static String readFile(File file, Charset charset) {
        Utils.parameterRequireNonNull(file, FILE_PARAMETER);
        Utils.parameterRequireNonNull(charset, CHARSET_PARAMETER);
        try {
            final Path path = file.toPath();
            return String.join("\n", Files.readAllLines(path, charset));
        } catch (IOException e) {
            throw new RuntimeIOException("File not readable: " + file, e);
        }
    }

    /**
     * @param inputStream - source {@link InputStream}
     * @param charset     - source input stream {@link Charset}
     * @return {@link BufferedReader} for input stream
     */
    public static BufferedReader getBufferedReader(InputStream inputStream, Charset charset) {
        return new BufferedReader(new InputStreamReader(inputStream, charset));
    }

    /**
     * @return {@link ClassLoader} for current thread
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * @param value array || collection
     * @return {@link Collection}
     * @throws FormUrlEncodedMapperException if value is not array or collection
     */
    @SuppressWarnings(SONAR_GENERIC_WILDCARD_TYPES)
    protected Collection<?> arrayToCollection(final Object value) {
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.getClass().isArray()) {
            return Arrays.asList((Object[]) value);
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value);
        }
        // TODO
        throw new RuntimeException("Received unsupported type to convert to collection: " + value.getClass());
    }

}
