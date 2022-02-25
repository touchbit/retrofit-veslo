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

package veslo.client.header;

import org.apache.commons.lang3.reflect.FieldUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static veslo.constant.ParameterNameConstants.FIELD_PARAMETER;
import static veslo.util.ReflectUtils.*;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 25.02.2022
 */
public abstract class ReflectHeaders extends HashMap<String, String> {

    private static final Character DT = '.';
    private static final Character KB = '-';
    private static final Character SN = '_';


    @SuppressWarnings("ConstantConditions")
    public Set<Map.Entry<String, String>> entrySet() {
        this.putAll(readReflectHeadersFields());
        return super.entrySet();
    }

    protected Map<String, String> readReflectHeadersFields() {
        Map<String, String> result = new HashMap<>();
        final List<Field> fields = FieldUtils.getAllFieldsList(this.getClass()).stream()
                .filter(f -> f.getDeclaringClass() != HashMap.class)
                .filter(f -> f.getDeclaringClass() != AbstractMap.class)
                .filter(f -> isNotAssignableConstantField(this, f))
                .collect(Collectors.toList());
        for (Field field : fields) {
            // ignore jacocoData fields and types implements ReflectHeaders (protection from StackOverflowError)
            if (isAssignableConstantField(ReflectHeaders.class, field) || isJacocoDataField(field)) {
                continue;
            }
            final String headerName = getHeaderName(field);
            final Object value = readField(this, field);
            final String headerValue = getHeaderValue(value);
            if (headerValue != null) {
                result.put(headerName, headerValue);
            }
        }
        return result;
    }

    @EverythingIsNonNull
    protected String getHeaderName(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final String fieldName = field.getName();
        final HeaderKey annotation = field.getAnnotation(HeaderKey.class);
        if (isNullOrEmpty(HeaderKey::value, annotation)) {
            return toKebabCase(fieldName);
        }
        // trim to avoid throwing IllegalArgumentException
        return annotation.value().trim();
    }

    @Nullable
    protected String getHeaderValue(final @Nullable Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray() || value instanceof ParameterizedType) {
            throw new RuntimeException("Unsupported header value type.\n" +
                                       "Expected: simple type (String, Integer, etc.)\n" +
                                       "Actual: " + value.getClass().getName());
        }
        return value.toString();
    }

    // TODO utils
    protected List<String> collectionToStringList(final Collection<?> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    // TODO utils
    public static String toKebabCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.trim().toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
            } else if (isUpperCase(next)) {
                sb.append(KB).append(toLowerCase(next));
            } else if (next.equals(DT) || next.equals(SN)) {
                sb.append(KB);
            } else {
                sb.append(toLowerCase(next));
            }
        }
        return sb.toString();
    }

    // TODO utils
    public static <O> boolean isNullOrEmpty(@Nonnull final Function<O, String> function, @Nullable final O object) {
        return object == null || function.apply(object).trim().isEmpty();
    }

    @Override
    public String toString() {
        return entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

}
