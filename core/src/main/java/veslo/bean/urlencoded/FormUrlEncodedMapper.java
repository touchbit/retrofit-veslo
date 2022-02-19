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

package veslo.bean.urlencoded;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import veslo.FormUrlEncodedMapperException;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 */
public class FormUrlEncodedMapper implements IFormUrlEncodedMapper {

    public static final FormUrlEncodedMapper INSTANCE = new FormUrlEncodedMapper();

    /**
     * Model to string conversion
     *
     * @param model - FormUrlEncoded model
     * @return model string representation
     */
    @Override
    public String marshal(Object model) {
        Utils.parameterRequireNonNull(model, "model");
        return null;
    }

    /**
     * String to model conversion
     *
     * @param data       - String data to conversation
     * @param modelClass - FormUrlEncoded model class
     * @param <M>        - FormUrlEncoded model type
     * @return completed model
     */
    @Override
    public <M> M unmarshal(String data, Class<M> modelClass) {
        Utils.parameterRequireNonNull(data, "model");
        Utils.parameterRequireNonNull(modelClass, "model");
        final M model;
        try {
            model = ConstructorUtils.invokeConstructor(modelClass);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to instantiate " + modelClass, e);
        }
        if (data.isEmpty()) {
            return model;
        }
        final List<Field> fields = Arrays.asList(modelClass.getDeclaredFields());
        final Field additionalProperties = getAdditionalProperties(modelClass);
        final List<String> keyValue = Arrays.asList(data.split("&"));
        final List<String> brokenPairs = keyValue.stream().filter(pair -> pair.split("=").length != 2)
                .collect(Collectors.toList());
        final Map<Field, String> result = new HashMap<>();

        return null;
    }

    /**
     * @param modelClass - FormUrlEncoded model class
     * @return field annotated with FormUrlEncodedAdditionalProperties or null
     * @throws NullPointerException          if modelClass parameter is null
     * @throws FormUrlEncodedMapperException if additionalProperties fields more than one
     * @throws FormUrlEncodedMapperException if additionalProperties type != {@code Map<String, String>}
     */
    @Nullable
    protected Field getAdditionalProperties(@Nonnull final Class<?> modelClass) {
        Utils.parameterRequireNonNull(modelClass, "modelClass");
        final List<Field> fields = Arrays.asList(modelClass.getDeclaredFields());
        final List<Field> additionalProperties = fields.stream()
                .filter(f -> f.isAnnotationPresent(FormUrlEncodedAdditionalProperties.class))
                .collect(Collectors.toList());
        if (additionalProperties.size() > 1) {
            final String fNames = additionalProperties.stream().map(Field::getName).collect(Collectors.joining(", "));
            throw new FormUrlEncodedMapperException("Model contains more than one field annotated with " +
                    FormUrlEncodedAdditionalProperties.class.getSimpleName() + ":\n" +
                    "    Model: " + modelClass + "\n" +
                    "    Fields: " + fNames + "\n");
        }
        if (additionalProperties.isEmpty()) {
            return null;
        }
        final Field additionalProperty = additionalProperties.get(0);
        final Type type = additionalProperty.getGenericType();
        final boolean isParameterizedType = type instanceof ParameterizedType;
        final boolean isMap = Map.class.isAssignableFrom(additionalProperty.getType());
        final boolean isValidTypeArguments;
        if (isParameterizedType && isMap) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            final Type keyType = actualTypeArguments[0];
            final Type valueType = actualTypeArguments[1];
            isValidTypeArguments = keyType == String.class && valueType == String.class;
        } else {
            isValidTypeArguments = false;
        }
        if (!isParameterizedType || !isMap || !isValidTypeArguments) {
            throw new FormUrlEncodedMapperException("Invalid field type with @" +
                    FormUrlEncodedAdditionalProperties.class.getSimpleName() + " annotation\n" +
                    "    Model         - " + modelClass + "\n" +
                    "    Field         - " + additionalProperty.getName() + "\n" +
                    "    Actual type   - " + type.getTypeName() + "\n" +
                    "    Expected type - java.util.Map<java.lang.String, java.lang.String>\n");
        }
        return additionalProperty;
    }

    /**
     * If a field marked with the {@link FormUrlEncodedAdditionalProperties} annotation is present and not initialized,
     * then a new HashMap instance will be written to the field value.
     *
     * @param model - FormUrlEncoded model
     * @return additionalProperty field value (Map) or null if field not present
     * @throws NullPointerException          if model parameter is null
     * @throws FormUrlEncodedMapperException in case of initialization errors of the additionalProperty field
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public Map<String, String> initAdditionalProperties(@Nonnull final Object model) {
        Utils.parameterRequireNonNull(model, "model");
        final Field additionalProperty = getAdditionalProperties(model.getClass());
        if (additionalProperty == null) {
            return null;
        }
        final String fieldName = additionalProperty.getName();
        try {
            if (Modifier.isFinal(additionalProperty.getModifiers())) {
                return (Map<String, String>) FieldUtils.readDeclaredField(model, additionalProperty.getName(), true);
            }
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to get field value: " + fieldName, e);
        }
        try {
            final HashMap<String, String> value = new HashMap<>();
            FieldUtils.writeDeclaredField(model, fieldName, value, true);
            return value;
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to initialize field " + fieldName, e);
        }
    }

}
