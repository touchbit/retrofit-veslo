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

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import veslo.ReflectionException;
import veslo.UtilityClassException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static veslo.constant.ParameterNameConstants.*;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 25.02.2022
 */
public class ReflectUtils {

    /**
     * Utility class
     */
    private ReflectUtils() {
        throw new UtilityClassException();
    }

    /**
     * @param object non-nullable object
     * @param excludeClasses list of classes whose fields are not returned
     * @return an array of Fields (possibly empty)
     */
    public static List<Field> getAllSerializableFields(final Object object, final Class<?>... excludeClasses) {
        Utils.parameterRequireNonNull(object, OBJECT_PARAMETER);
        Utils.parameterRequireNonNull(excludeClasses, EXCLUDE_CLASSES);
        return FieldUtils.getAllFieldsList(object.getClass()).stream()
                .filter(ReflectUtils::isNotTransient)
                .filter(ReflectUtils::isNotConstantField)
                .filter(f -> Arrays.stream(excludeClasses).noneMatch(f.getDeclaringClass()::equals))
                .collect(Collectors.toList());
    }

    /**
     * @param field {@link Field}
     * @return true if the field does not have 'transient' modifier
     */
    public static boolean isNotTransient(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return !Modifier.isTransient(field.getModifiers());
    }

    /**
     * @param field {@link Field}
     * @return true if the field does not have 'static' and 'final' modifiers
     */
    public static boolean isNotConstantField(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final int modifiers = field.getModifiers();
        return !(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * @param object target for read
     * @param field  object field
     * @return field value
     * @throws ReflectionException if the value cannot be read form the object field
     */
    public static Object readFieldValue(final Object object, final Field field) {
        Utils.parameterRequireNonNull(object, OBJECT_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        try {
            return FieldUtils.readField(object, field.getName(), true);
        } catch (Exception e) {
            throw ReflectionException.builder()
                    .errorMessage("Unable to raed value from object field.")
                    .object(object)
                    .field(field)
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * @param aClass {@link Class} for instantiation
     * @param <M>    generic type
     * @return new instance of the class
     * @throws ReflectionException on instantiation errors
     */
    public static <M> M invokeConstructor(final Class<M> aClass, final Object... args) {
        try {
            return ConstructorUtils.invokeConstructor(aClass, args);
        } catch (Exception e) {
            throw ReflectionException.builder()
                    .errorMessage("Unable to instantiate class.")
                    .constructedType(aClass)
                    .constructorArguments(args)
                    .errorCause(e)
                    .build();
        }
    }

}
