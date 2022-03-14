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

package veslo.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.UtilityClassException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static veslo.constant.ParameterNameConstants.FIELD_PARAMETER;
import static veslo.constant.ParameterNameConstants.TYPE_PARAMETER;

/**
 * TODO
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 25.02.2022
 */
public class ReflectUtils {

    private ReflectUtils() {
        throw new UtilityClassException();
    }

    public static boolean isConstantField(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isNotAssignableConstantField(final Object object, final Field field) {
        Utils.parameterRequireNonNull(object, "object");
        return isNotAssignableConstantField(object.getClass(), field);
    }

    public static boolean isNotAssignableConstantField(final Class<?> aClass, final Field field) {
        Utils.parameterRequireNonNull(aClass, "aClass");
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return !aClass.isAssignableFrom(field.getType()) && !isConstantField(field);
    }

    public static boolean isAssignableConstantField(final Object object, final Field field) {
        Utils.parameterRequireNonNull(object, "object");
        return isAssignableConstantField(object.getClass(), field);
    }

    public static boolean isAssignableConstantField(final Class<?> aClass, final Field field) {
        Utils.parameterRequireNonNull(aClass, "aClass");
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return aClass.isAssignableFrom(field.getType()) && isConstantField(field);
    }

    public static boolean isJacocoDataField(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return field.getName().contains("jacocoData");
    }

    public static Object readField(final Object object, final Field field) {
        Utils.parameterRequireNonNull(object, "object");
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return readField(object, field.getName());
    }

    public static Object readField(final Object object, final String fieldName) {
        try {
            return FieldUtils.readField(object, fieldName, true);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get value from field: " + fieldName, e);
        }
    }

    @EverythingIsNonNull
    public static boolean isGenericMap(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Map.class;
    }

    @EverythingIsNonNull
    public static boolean isGenericMap(final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null && parameterizedType.getRawType() == Map.class;
    }

    @EverythingIsNonNull
    public static boolean isGenericCollection(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Collection.class;
    }

    @EverythingIsNonNull
    public static boolean isGenericCollection(final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null && parameterizedType.getRawType() == Collection.class;
    }

    @EverythingIsNonNull
    public static boolean isArray(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return field.getType().isArray();
    }

    @EverythingIsNonNull
    public static boolean isArray(final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        return (type instanceof Class) && ((Class<?>) type).isArray();
    }

    @Nullable
    public static ParameterizedType getParameterizedType(@Nonnull final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final Type genericType = field.getGenericType();
        return getParameterizedType(genericType);
    }

    @Nullable
    public static ParameterizedType getParameterizedType(@Nonnull final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return null;
    }

}
