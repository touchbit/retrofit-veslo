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
import veslo.UtilityClassException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static veslo.constant.ParameterNameConstants.FIELD_PARAMETER;

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

}
