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

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.FormUrlEncodedMapperException;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Convert model (JavaBean) to URL encoded form and back to model.
 * Model example:
 * <pre><code>
 * &#64;FormUrlEncoded()
 * public class Model {
 *
 *     &#64;FormUrlEncodedField("foo")
 *     private String foo;
 *
 *     &#64;FormUrlEncodedField("bar")
 *     private List<Integer> bar;
 *
 * }
 * </code></pre>
 * <p>
 * Usage:
 * <pre><code>
 *     Model model = new Model().foo("text").bar(1,2,3);
 *     Strung formUrlEncodedString = FormUrlEncodedMapper.INSTANCE.marshal(model);
 *     Model formUrlEncodedModel = FormUrlEncodedMapper.INSTANCE.unmarshal(formUrlEncodedString);
 * </code></pre>
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncoded
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
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
    @EverythingIsNonNull
    public String marshal(final Object model) {
        Utils.parameterRequireNonNull(model, "model");
        return null;
    }

    /**
     * String to model conversion
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation (UTF-8 encode charset)
     * @param <M>           - FormUrlEncoded model type
     * @return completed model
     */
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString) {
        return unmarshal(modelClass, encodedString, UTF_8);
    }

    /**
     * String to model conversion
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation
     * @param encodeCharset - String data charset
     * @param <M>           - FormUrlEncoded model type
     * @return completed model
     */
    @Override
    @EverythingIsNonNull
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString, final Charset encodeCharset) {
        Utils.parameterRequireNonNull(modelClass, "modelClass");
        Utils.parameterRequireNonNull(encodedString, "encodedString");
        Utils.parameterRequireNonNull(encodeCharset, "encodeCharset");
        final M model;
        try {
            model = ConstructorUtils.invokeConstructor(modelClass);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to instantiate " + modelClass, e);
        }
        if (encodedString.isEmpty()) {
            return model;
        }
        final Map<String, Object> additionalProperties = initAdditionalProperties(model);
        final Map<String, List<String>> parsed = parseEncodedString(encodedString, encodeCharset);
        final List<Field> annotatedFields = Arrays.stream(modelClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FormUrlEncodedField.class))
                .collect(Collectors.toList());
        final List<Field> handledAnnotatedFields = new ArrayList<>();
        for (Field annotatedField : annotatedFields) {
            final String fieldName = getFormUrlEncodedFieldName(annotatedField);
            final List<String> value = parsed.get(fieldName);
            if (value == null) {
                handledAnnotatedFields.add(annotatedField);
                continue;
            }
            final Object forWrite = convertValueToFieldType(model, annotatedField, value);
            writeFieldValue(model, annotatedField, forWrite);
            handledAnnotatedFields.add(annotatedField);
        }
        if (additionalProperties != null) {
            handledAnnotatedFields.stream()
                    .map(this::getFormUrlEncodedFieldName)
                    .forEach(parsed::remove);
            additionalProperties.putAll(parsed);
        }
        return model;
    }

    @EverythingIsNonNull
    protected Object convertValueToFieldType(final Object model, final Field field, final List<String> value) {
        Utils.parameterRequireNonNull(model, "model");
        Utils.parameterRequireNonNull(field, "field");
        Utils.parameterRequireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new FormUrlEncodedMapperException("The 'value' field does not contain data to be converted.");
        }
        final Class<?> fieldType = field.getType();
        // convert to collection type
        if (field.getGenericType() instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return convertParameterizedType(model, field, parameterizedType, value);
        }
        // convert to array type
        if (fieldType.isArray()) {
            return convertArrayType(model, field, fieldType, value);
        }
        // convert to single type
        return convertSingleType(model, field, fieldType, value);
    }

    @EverythingIsNonNull
    protected Object convertSingleType(final Object model,
                                       final Field field,
                                       final Class<?> fieldType,
                                       final List<String> value) {
        if (value.isEmpty()) {
            throw new FormUrlEncodedMapperException("The 'value' field does not contain data to be converted.");
        }
        if (value.size() > 1) {
            throw new FormUrlEncodedMapperException("Mismatch types. Got an array instead of a single value.\n" +
                    "Model type: " + model.getClass().getName() +
                    "Field type: " + fieldType.getName() + "\n" +
                    "Field name: " + field.getName() + "\n" +
                    "URL form field name: " + getFormUrlEncodedFieldName(field) +
                    "Received type: array\n" +
                    "Received value: " + value + "\n" +
                    "Expected value: single value");
        }
        final String forConvert = value.get(0);
        try {
            return convertStringValueToType(forConvert, fieldType);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Error converting string to field type.\n" +
                    "    Model type: " + model.getClass().getName() + "\n" +
                    "    Field type: " + fieldType.getName() + "\n" +
                    "    Field name: " + field.getName() + "\n" +
                    "    URL form field name: " + getFormUrlEncodedFieldName(field) + "\n" +
                    "    Value for convert: " + forConvert + "\n" +
                    "    Error cause: " + e.getMessage().trim() + "\n");
        }
    }

    @EverythingIsNonNull
    protected Object[] convertArrayType(final Object model,
                                        final Field field,
                                        final Class<?> fieldType,
                                        final List<String> value) {
        if (!fieldType.isArray()) {
            throw new FormUrlEncodedMapperException("Mismatch types. Got a single type instead of an array.\n" +
                    "    Model type: " + model.getClass().getName() +
                    "    Field type: " + fieldType.getName() + "\n" +
                    "    Field name: " + field.getName() + "\n" +
                    "    URL form field name: " + getFormUrlEncodedFieldName(field) +
                    "    Expected type: array\n");
        }
        final List<Object> result = new ArrayList<>();
        for (String element : value) {
            final Class<?> arrayComponentType = fieldType.getComponentType();
            try {
                final Object convertedValue = convertStringValueToType(element, arrayComponentType);
                result.add(convertedValue);
            } catch (Exception e) {
                throw new FormUrlEncodedMapperException("Received unsupported type for conversion.\n" +
                        "    Model type: " + model.getClass().getName() + "\n" +
                        "    Field type: " + fieldType.getName() + "\n" +
                        "    Field name: " + field.getName() + "\n" +
                        "    URL form field name: " + getFormUrlEncodedFieldName(field) + "\n" +
                        "    Type to convert: " + arrayComponentType + "\n" +
                        "    Value for convert: " + element + "\n" +
                        "    Error cause: " + e.getMessage().trim() + "\n");
            }
        }
        return result.toArray();
    }

    @EverythingIsNonNull
    protected Collection<Object> convertParameterizedType(final Object model,
                                                          final Field field,
                                                          final ParameterizedType parameterizedType,
                                                          final List<String> value) {
        final Type rawType = parameterizedType.getRawType();
        final Type targetType = parameterizedType.getActualTypeArguments()[0];
        if (Collection.class.isAssignableFrom((Class<?>) rawType)) {
            final List<Object> list = new ArrayList<>();
            for (String element : value) {
                try {
                    list.add(convertStringValueToType(element, targetType));
                } catch (Exception e) {
                    throw new FormUrlEncodedMapperException("Received unsupported type for conversion.\n" +
                            "    Model type: " + model.getClass().getName() + "\n" +
                            "    Field type: " + rawType + "\n" +
                            "    Field name: " + field.getName() + "\n" +
                            "    URL form field name: " + getFormUrlEncodedFieldName(field) + "\n" +
                            "    Type to convert: " + targetType + "\n" +
                            "    Value for convert: " + element + "\n" +
                            "    Error cause: " + e.getMessage().trim() + "\n");
                }
            }
            if (List.class.equals(rawType)) {
                return list;
            }
            if (Set.class.equals(rawType)) {
                return new HashSet<>(list);
            }
        }
        throw new FormUrlEncodedMapperException("Received unsupported parameterized type for conversion.\n" +
                "    Model type: " + model.getClass().getName() + "\n" +
                "    Field type: " + rawType + "\n" +
                "    Field name: " + field.getName() + "\n" +
                "    URL form field name: " + getFormUrlEncodedFieldName(field) + "\n" +
                "    Supported parameterized types:\n" +
                "    - " + List.class.getName() + "\n" +
                "    - " + Set.class.getName() + "\n");
    }

    protected Object convertStringValueToType(final String value, final Type targetType) {
        Utils.parameterRequireNonNull(value, "value");
        Utils.parameterRequireNonNull(targetType, "targetType");
        if (targetType instanceof Class && ((Class<?>) targetType).isPrimitive()) {
            throw new IllegalArgumentException("It is forbidden to use primitive types in FormUrlEncoded models: " + targetType);
        }
        if (targetType.equals(String.class) || targetType.equals(Object.class)) {
            return value;
        } else if (targetType.equals(Boolean.class) || targetType.equals(Boolean.TYPE)) {
            if ("true".equals(value)) {
                return true;
            }
            if ("false".equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("Cannot convert string to boolean: '" + value + "'");
        } else if (targetType.equals(Short.class) || targetType.equals(Short.TYPE)) {
            return Short.valueOf(value);
        } else if (targetType.equals(Long.class) || targetType.equals(Long.TYPE)) {
            return Long.valueOf(value);
        } else if (targetType.equals(Float.class) || targetType.equals(Float.TYPE)) {
            return Float.valueOf(value);
        } else if (targetType.equals(Integer.class) || targetType.equals(Integer.TYPE)) {
            return Integer.valueOf(value);
        } else if (targetType.equals(Double.class) || targetType.equals(Double.TYPE)) {
            return Double.valueOf(value);
        } else if (targetType.equals(BigInteger.class)) {
            return NumberUtils.createBigInteger(value);
        } else if (targetType.equals(BigDecimal.class)) {
            return NumberUtils.createBigDecimal(value);
        } else {
            throw new IllegalArgumentException("Received unsupported field type for conversion: " + targetType);
        }
    }

    @EverythingIsNonNull
    protected <M> void writeFieldValue(M model, Field field, Object value) {
        Utils.parameterRequireNonNull(model, "model");
        Utils.parameterRequireNonNull(field, "field");
        Utils.parameterRequireNonNull(value, "value");
        try {
            FieldUtils.writeDeclaredField(model, field.getName(), value, true);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to write field value.\n" +
                    "    Model: " + model.getClass().getName() + "\n" +
                    "    Field name: " + field.getName() + "\n" +
                    "    Field type: " + field.getType() + "\n" +
                    "    Field value: " + value + "\n" +
                    "    Error cause: " + e.getMessage().trim() + "\n", e);
        }
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
            isValidTypeArguments = keyType == String.class;
        } else {
            isValidTypeArguments = false;
        }
        if (!isParameterizedType || !isMap || !isValidTypeArguments) {
            throw new FormUrlEncodedMapperException("Invalid field type with @" +
                    FormUrlEncodedAdditionalProperties.class.getSimpleName() + " annotation\n" +
                    "    Model: " + modelClass + "\n" +
                    "    Field: " + additionalProperty.getName() + "\n" +
                    "    Actual type: " + type.getTypeName() + "\n" +
                    "    Expected type: java.util.Map<java.lang.String, java.lang.Object>\n");
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
    protected Map<String, Object> initAdditionalProperties(@Nonnull final Object model) {
        Utils.parameterRequireNonNull(model, "model");
        final Field additionalProperty = getAdditionalProperties(model.getClass());
        if (additionalProperty == null) {
            return null;
        }
        final String fieldName = additionalProperty.getName();
        try {
            if (Modifier.isFinal(additionalProperty.getModifiers())) {
                return (Map<String, Object>) FieldUtils.readDeclaredField(model, additionalProperty.getName(), true);
            }
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to get field value: " + fieldName, e);
        }
        try {
            final HashMap<String, Object> value = new HashMap<>();
            FieldUtils.writeDeclaredField(model, fieldName, value, true);
            return value;
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to initialize field " + fieldName, e);
        }
    }

    @EverythingIsNonNull
    protected Map<String, List<String>> parseEncodedString(final String rawData, final Charset charset) {
        Utils.parameterRequireNonNull(rawData, "rawData");
        Utils.parameterRequireNonNull(charset, "charset");
        final Map<String, List<String>> result = new HashMap<>();
        if (rawData.trim().length() == 0) {
            return result;
        }
        final String prepared;
        if (rawData.startsWith("?")) {
            prepared = rawData.substring(1);
        } else {
            prepared = rawData;
        }
        final String[] pairs = prepared.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            final String[] split = pair.split("=");
            if (split.length > 2 || split.length == 0) {
                throw new FormUrlEncodedMapperException("URL encoded string not in URL format:\n" + rawData);
            }
            final String key = split[0];
            final String urlEncodedValue;
            if (split.length == 1) {
                urlEncodedValue = "";
            } else {
                urlEncodedValue = split[1];
            }
            try {
                final String urlDecodedValue = URLDecoder.decode(urlEncodedValue, charset.name());
                result.computeIfAbsent(key, i -> new ArrayList<>()).add(urlDecodedValue);
            } catch (Exception e) {
                throw new FormUrlEncodedMapperException("Error decoding URL encoded string:\n" + pair, e);
            }
        }
        return result;
    }

    /**
     * @param field - model field
     * @return URL form field name from the {@link FormUrlEncodedField} field annotation
     */
    @EverythingIsNonNull
    protected String getFormUrlEncodedFieldName(final Field field) {
        Utils.parameterRequireNonNull(field, "field");
        final FormUrlEncodedField annotation = field.getAnnotation(FormUrlEncodedField.class);
        if (annotation == null) {
            throw new FormUrlEncodedMapperException("Field does not contain a required annotation.\n" +
                    "    Field: " + field.getName() + "\n" +
                    "    Expected annotation: " + FormUrlEncodedField.class.getName() + "\n");
        }
        final String value = annotation.value();
        if (value.trim().isEmpty()) {
            throw new FormUrlEncodedMapperException("URL field name can not be empty or blank.\n" +
                    "    Field: " + field.getName() + "\n" +
                    "    Annotation: " + FormUrlEncodedField.class.getName() + "\n" +
                    "    Method: value()\n" +
                    "    Actual: '" + value + "'\n");
        }
        return value;
    }

}
