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
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static veslo.constant.ParameterNameConstants.*;
import static veslo.constant.SonarRuleConstants.SONAR_COGNITIVE_COMPLEXITY;
import static veslo.constant.SonarRuleConstants.SONAR_GENERIC_WILDCARD_TYPES;

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
 *     &#64;FormUrlEncodedAdditionalProperties()
 *     private Map<String, Object> additionalProperties;
 *
 * }
 * </code></pre>
 * <p>
 * Usage:
 * <pre><code>
 *     Model model = new Model().foo("text").bar(1,2,3);
 *     Strung formUrlEncodedString = FormUrlEncodedMapper.INSTANCE.marshal(model);
 *     Model formUrlDecodedModel = FormUrlEncodedMapper.INSTANCE.unmarshal(formUrlEncodedString);
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

    private static final String CONVERSION_UNSUPPORTED_TYPE_ERR_MSG = "Received unsupported type for conversion:\n";
    private static final String UNABLE_READ_FIELD_VALUE_ERR_MSG = "Unable to read value from model field.\n";
    private static final String UNABLE_ENCODE_ERR_MSG = "Unable to encode string to FormUrlEncoded format.\n";
    private static final String MODEL_TYPE_ERR_MSG = "    Model type: ";
    private static final String FIELD_NAME_ERR_MSG = "    Field name: ";
    private static final String FORF_FIELD_NAME_ERR_MSG = "    URL form field name: ";
    private static final String VALUE_TO_ENCODE_ERR_MSG = "    Value to encode: ";
    private static final String ENCODE_CHARSET_ERR_MSG = "    Encode charset: ";
    private static final String ERROR_CAUSE_ERR_MSG = "    Error cause: ";
    private static final String FIELD_TYPE_ERR_MSG = "    Field type: ";
    private static final String VALUE_FOR_CONVERT_ERR_MSG = "    Value for convert: ";
    private static final String ANNOTATION_ERR_MSG = "    Annotation: ";
    private static final String MODEL_ERR_MSG = "    Model: ";
    private static final String FIELD_ERR_MSG = "    Field: ";
    private static final String ACT_TYPE_ERR_MSG = "    Actual type: ";
    private static final String EXP_TYPE_ERR_MSG = "    Expected type: ";

    public static final FormUrlEncodedMapper INSTANCE = new FormUrlEncodedMapper();

    /**
     * Model to string conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param codingCharset - URL form data coding charset
     * @param indexedArray  - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return model string representation
     */
    @Override
    @EverythingIsNonNull
    public String marshal(final Object model, final Charset codingCharset, final boolean indexedArray) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        final List<Field> annotated = Arrays.stream(model.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FormUrlEncodedField.class))
                .collect(Collectors.toList());
        StringJoiner result = new StringJoiner("&");
        for (Field field : annotated) {
            final String formFieldName = getFormUrlEncodedFieldName(field);
            final String pair;
            if (Collection.class.isAssignableFrom(field.getType())) {
                pair = marshalCollectionToUrlEncodedString(model, field, formFieldName, codingCharset, indexedArray);
            } else if (field.getType().isArray()) {
                pair = marshalArrayToUrlEncodedString(model, field, formFieldName, codingCharset, indexedArray);
            } else {
                pair = marshalSingleTypeToUrlEncodedString(model, field, formFieldName, codingCharset);
            }
            if (pair != null && !pair.trim().isEmpty()) {
                result.add(pair);
            }
        }
        final Field apField = getAdditionalPropertiesField(model.getClass());
        if (apField != null) {
            final String ap = marshalAdditionalProperties(model, apField, codingCharset, indexedArray);
            if (ap != null && !ap.isEmpty()) {
                result.add(ap);
            }
        }
        return result.toString();
    }

    /**
     * String to model conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation
     * @param codingCharset - URL form data coding charset
     * @param <M>           - model generic type
     * @return completed model
     * @throws FormUrlEncodedMapperException on class instantiation errors
     */
    @Override
    @EverythingIsNonNull
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString, final Charset codingCharset) {
        Utils.parameterRequireNonNull(modelClass, MODEL_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        final M model;
        try {
            model = ConstructorUtils.invokeConstructor(modelClass);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to instantiate model class\n" +
                    MODEL_TYPE_ERR_MSG + modelClass.getName() + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        if (encodedString.isEmpty()) {
            return model;
        }
        final Map<String, List<String>> parsed = parseAndDecodeUrlEncodedString(encodedString, codingCharset);
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
            final Object forWrite = unmarshalDecodedValueToFieldType(model, annotatedField, value);
            writeFieldValue(model, annotatedField, forWrite);
            handledAnnotatedFields.add(annotatedField);
        }
        final Field additionalProperty = getAdditionalPropertiesField(model.getClass());
        if (additionalProperty != null) {
            unmarshalAndWriteAdditionalProperties(model, additionalProperty, parsed, handledAnnotatedFields);
        }
        return model;
    }

    /**
     * Converts additional properties {@link Map} to FormUrlEncoded string.
     * Additional properties is a field annotated with the {@link FormUrlEncodedAdditionalProperties} annotation.
     * Additional properties type - {@code Map<String, Object>}, where:
     * - key -> field name
     * - value -> simple type (String, Integer, etc.) or simple array or collection (List, Set)
     * If objects of other types are used as values, then {@code toString()} of these objects will be called.
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param codingCharset - URL form data coding charset
     * @param indexedArray  - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return FormUrlEncoded additionalProperties representation
     * @throws FormUrlEncodedMapperException if model field not readable
     * @throws FormUrlEncodedMapperException if unsupported URL form coding {@link Charset}
     */
    @EverythingIsNonNull
    @SuppressWarnings("java:S3776")
    protected String marshalAdditionalProperties(final Object model,
                                                 final Field field,
                                                 final Charset codingCharset,
                                                 final boolean indexedArray) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        StringJoiner result = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : readAdditionalProperties(model, field).entrySet()) {
            final String rawName = entry.getKey();
            final Object rawValue = entry.getValue();
            try {
                if (rawValue instanceof Collection || rawValue.getClass().isArray()) {
                    final AtomicLong index = new AtomicLong(0);
                    final Collection<?> values = arrayToCollection(rawValue);
                    if (values.isEmpty()) {
                        final long i = index.getAndIncrement();
                        final String fieldName = indexedArray ? rawName + "[" + i + "]" : rawName;
                        result.add(fieldName + "=");
                    } else {
                        for (Object value : values) {
                            final long i = index.getAndIncrement();
                            final String fieldName = indexedArray ? rawName + "[" + i + "]" : rawName;
                            final String stringValue = value == null ? "" : String.valueOf(value);
                            final String fieldValue = URLEncoder.encode(stringValue, codingCharset.name());
                            result.add(fieldName + "=" + fieldValue);
                        }
                    }
                } else {
                    final String stringValue = String.valueOf(rawValue);
                    final String fieldValue = URLEncoder.encode(stringValue, codingCharset.name());
                    result.add(rawName + "=" + fieldValue);
                }
            } catch (Exception e) {
                throw new FormUrlEncodedMapperException(UNABLE_ENCODE_ERR_MSG +
                        MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                        FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                        FORF_FIELD_NAME_ERR_MSG + rawName + "\n" +
                        VALUE_TO_ENCODE_ERR_MSG + rawValue + "\n" +
                        ENCODE_CHARSET_ERR_MSG + codingCharset + "\n" +
                        ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
            }
        }
        return result.toString();
    }

    /**
     * @param value array || collection
     * @return {@link Collection}
     * @throws FormUrlEncodedMapperException if value is not array or collection
     */
    @SuppressWarnings(SONAR_GENERIC_WILDCARD_TYPES)
    protected Collection<?> arrayToCollection(Object value) {
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.getClass().isArray()) {
            return Arrays.asList((Object[]) value);
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value);
        }
        throw new FormUrlEncodedMapperException("Received unsupported type to convert to collection: " + value.getClass());
    }

    /**
     * Reading a value from a model field annotated with the {@link FormUrlEncodedAdditionalProperties} annotation
     *
     * @param model - FormUrlEncoded model
     * @param field - additionalProperties Field
     * @return {@link Map} where key - form parameter name, value - form parameter value (not null)
     * @throws FormUrlEncodedMapperException if model field not readable
     */
    @EverythingIsNonNull
    @SuppressWarnings("unchecked")
    protected Map<String, Object> readAdditionalProperties(final Object model, final Field field) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final Map<String, Object> additionalProperties;
        try {
            additionalProperties = (Map<String, Object>) FieldUtils.readField(model, field.getName(), true);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException(UNABLE_READ_FIELD_VALUE_ERR_MSG +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        if (additionalProperties == null) {
            return new HashMap<>();
        }
        return additionalProperties.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() == null ? "" : e.getValue()));
    }

    /**
     * Converts single type (String, Integer, etc.) filed to FormUrlEncoded string
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param field         - model field
     * @param formFieldName - model field {@link FormUrlEncodedField#value()}
     * @param codingCharset - URL form data coding charset
     * @return FormUrlEncoded collection representation
     * @throws FormUrlEncodedMapperException if model field not readable
     * @throws FormUrlEncodedMapperException if unsupported URL form coding {@link Charset}
     */
    @EverythingIsNonNull
    protected String marshalSingleTypeToUrlEncodedString(final Object model,
                                                         final Field field,
                                                         final String formFieldName,
                                                         final Charset codingCharset) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(formFieldName, FORM_FIELD_NAME_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        final Object rawValue;
        try {
            rawValue = FieldUtils.readField(model, field.getName(), true);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException(UNABLE_READ_FIELD_VALUE_ERR_MSG +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        if (rawValue == null) {
            return "";
        }
        final String value = String.valueOf(rawValue);
        try {
            return formFieldName + "=" + URLEncoder.encode(value, codingCharset.name());
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException(UNABLE_ENCODE_ERR_MSG +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                    VALUE_TO_ENCODE_ERR_MSG + value + "\n" +
                    ENCODE_CHARSET_ERR_MSG + codingCharset + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
    }

    /**
     * Converts array to FormUrlEncoded array string
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param field         - model field
     * @param formFieldName - model field {@link FormUrlEncodedField#value()}
     * @param codingCharset - URL form data coding charset
     * @param indexedArray  - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return FormUrlEncoded collection representation
     * @throws FormUrlEncodedMapperException if model field not readable
     * @throws FormUrlEncodedMapperException if unsupported URL form coding {@link Charset}
     */
    @EverythingIsNonNull
    protected String marshalArrayToUrlEncodedString(final Object model,
                                                    final Field field,
                                                    final String formFieldName,
                                                    final Charset codingCharset,
                                                    final boolean indexedArray) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(formFieldName, FORM_FIELD_NAME_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        final StringJoiner result = new StringJoiner("&");
        final Object[] array;
        try {
            array = (Object[]) FieldUtils.readField(model, field.getName(), true);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException(UNABLE_READ_FIELD_VALUE_ERR_MSG +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        if (array == null || array.length == 0) {
            return "";
        }
        final AtomicLong index = new AtomicLong(0);
        for (Object rawValue : array) {
            final String fieldName = indexedArray ? formFieldName + "[" + index.getAndIncrement() + "]" : formFieldName;
            if (rawValue == null) {
                result.add(fieldName + "=");
            } else {
                final String value = String.valueOf(rawValue);
                try {
                    final String encodedValue = URLEncoder.encode(value, codingCharset.name());
                    result.add(fieldName + "=" + encodedValue);
                } catch (Exception e) {
                    throw new FormUrlEncodedMapperException(UNABLE_ENCODE_ERR_MSG +
                            MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                            FIELD_TYPE_ERR_MSG + field.getType().getSimpleName() + "\n" +
                            FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                            FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                            VALUE_TO_ENCODE_ERR_MSG + value + "\n" +
                            ENCODE_CHARSET_ERR_MSG + codingCharset + "\n" +
                            ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
                }
            }
        }
        return result.toString();
    }

    /**
     * Converts the collection to FormUrlEncoded array string
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param field         - model field
     * @param formFieldName - model field {@link FormUrlEncodedField#value()}
     * @param codingCharset - URL form data coding charset
     * @param indexedArray  - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return FormUrlEncoded collection representation
     * @throws FormUrlEncodedMapperException if model field not readable
     * @throws FormUrlEncodedMapperException if unsupported URL form coding {@link Charset}
     */
    @EverythingIsNonNull
    protected String marshalCollectionToUrlEncodedString(final Object model,
                                                         final Field field,
                                                         final String formFieldName,
                                                         final Charset codingCharset,
                                                         final boolean indexedArray) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(formFieldName, FORM_FIELD_NAME_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        final StringJoiner result = new StringJoiner("&");
        final Collection<?> collection;
        try {
            collection = (Collection<?>) FieldUtils.readField(model, field.getName(), true);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException(UNABLE_READ_FIELD_VALUE_ERR_MSG +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        final AtomicLong index = new AtomicLong(0);
        for (Object rawValue : collection) {
            final String fieldName = indexedArray ? formFieldName + "[" + index.getAndIncrement() + "]" : formFieldName;
            if (rawValue == null) {
                result.add(fieldName + "=");
            } else {
                final String value = String.valueOf(rawValue);
                try {
                    final String encodedValue = URLEncoder.encode(value, codingCharset.name());
                    result.add(fieldName + "=" + encodedValue);
                } catch (Exception e) {
                    throw new FormUrlEncodedMapperException(UNABLE_ENCODE_ERR_MSG +
                            MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                            FIELD_TYPE_ERR_MSG + field.getType().getName() + "\n" +
                            FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                            FORF_FIELD_NAME_ERR_MSG + formFieldName + "\n" +
                            VALUE_TO_ENCODE_ERR_MSG + value + "\n" +
                            ENCODE_CHARSET_ERR_MSG + codingCharset + "\n" +
                            ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
                }
            }
        }
        return result.toString();
    }

    /**
     * Populates the field marked with the {@link FormUrlEncodedField} annotation
     * with the received data that is not in the model.
     *
     * @param model   - FormUrlEncoded model
     * @param parsed  - all parsed URL decoded entries
     * @param handled - processed model fields ({@link FormUrlEncodedField})
     * @param <M>     - model generic type
     * @throws FormUrlEncodedMapperException if unable to initialize additionalProperties field
     */
    @EverythingIsNonNull
    protected <M> void unmarshalAndWriteAdditionalProperties(final M model,
                                                             final Field field,
                                                             final Map<String, List<String>> parsed,
                                                             final List<Field> handled) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(parsed, PARSED_PARAMETER);
        Utils.parameterRequireNonNull(handled, HANDLED_PARAMETER);
        final Map<String, List<String>> unhandled = new HashMap<>(parsed);
        final Map<String, Object> additionalProperties = initAdditionalProperties(model, field);
        handled.stream()
                .map(this::getFormUrlEncodedFieldName)
                .forEach(unhandled::remove);
        for (Map.Entry<String, List<String>> entry : unhandled.entrySet()) {
            final String key = entry.getKey();
            final List<String> values = entry.getValue();
            if (values.isEmpty()) {
                additionalProperties.put(key, "");
            } else if (values.size() > 1) {
                additionalProperties.put(key, values);
            } else {
                additionalProperties.put(key, values.get(0));
            }
        }
    }

    /**
     * Converts a [string] to supported model field type
     *
     * @param model - FormUrlEncoded model
     * @param field - model field
     * @param value - String value to convert
     * @return converted reference type object
     * @throws FormUrlEncodedMapperException if value list is empty
     * @see FormUrlEncodedMapper#unmarshalDecodedValueToParameterizedType
     * @see FormUrlEncodedMapper#unmarshalDecodedValueToArrayType
     * @see FormUrlEncodedMapper#unmarshalDecodedValueToSingleType
     * @see FormUrlEncodedMapper#convertUrlDecodedStringValueToType
     */
    @EverythingIsNonNull
    protected Object unmarshalDecodedValueToFieldType(final Object model, final Field field, final List<String> value) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.isEmpty()) {
            throw new FormUrlEncodedMapperException("The 'value' field does not contain data to be converted.");
        }
        final Class<?> fieldType = field.getType();
        // convert to parameterized type (collection)
        if (field.getGenericType() instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return unmarshalDecodedValueToParameterizedType(model, field, parameterizedType, value);
        }
        // convert to array type
        if (fieldType.isArray()) {
            return unmarshalDecodedValueToArrayType(model, field, fieldType, value);
        }
        // convert to single type
        return unmarshalDecodedValueToSingleType(model, field, fieldType, value);
    }

    /**
     * Converts a [string] to single reference type (type != list && type != array)
     *
     * @param model     - FormUrlEncoded model
     * @param field     - model field
     * @param fieldType - field type
     * @param value     - String value to convert
     * @return converted reference type array
     * @throws FormUrlEncodedMapperException if value list is empty
     * @throws FormUrlEncodedMapperException if value list has more than one record
     * @throws FormUrlEncodedMapperException if field type not supported
     * @see FormUrlEncodedMapper#convertUrlDecodedStringValueToType
     */
    @EverythingIsNonNull
    protected Object unmarshalDecodedValueToSingleType(final Object model,
                                                       final Field field,
                                                       final Class<?> fieldType,
                                                       final List<String> value) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(fieldType, FIELD_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.isEmpty()) {
            throw new FormUrlEncodedMapperException("The 'value' field does not contain data to be converted.");
        }
        if (value.size() > 1) {
            throw new FormUrlEncodedMapperException("Mismatch types. Got an array instead of a single value.\n" +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + fieldType.getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                    "    Received type: array\n" +
                    "    Received value: " + value + "\n" +
                    "    Expected value: single value\n");
        }
        final String forConvert = value.get(0);
        try {
            return convertUrlDecodedStringValueToType(forConvert, fieldType);
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Error converting string to field type.\n" +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + fieldType.getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                    VALUE_FOR_CONVERT_ERR_MSG + forConvert + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n");
        }
    }

    /**
     * Converts a [string] to reference type array
     *
     * @param model     - FormUrlEncoded model
     * @param field     - model field
     * @param fieldType - field type
     * @param value     - String value to convert
     * @return converted reference type array
     * @throws FormUrlEncodedMapperException if field type is not array
     * @throws FormUrlEncodedMapperException if field component type not supported
     * @throws IllegalArgumentException      if field component type is primitive
     * @see FormUrlEncodedMapper#convertUrlDecodedStringValueToType
     */
    @EverythingIsNonNull
    protected Object[] unmarshalDecodedValueToArrayType(final Object model,
                                                        final Field field,
                                                        final Class<?> fieldType,
                                                        final List<String> value) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(fieldType, FIELD_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (!fieldType.isArray()) {
            throw new FormUrlEncodedMapperException("Mismatch types. Got a single type instead of an array.\n" +
                    MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + fieldType.getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                    EXP_TYPE_ERR_MSG + "array\n");
        }
        final List<Object> result = new ArrayList<>();
        for (String element : value) {
            final Class<?> arrayComponentType = fieldType.getComponentType();
            if (arrayComponentType.isPrimitive()) {
                throw new IllegalArgumentException("It is forbidden to use primitive types in FormUrlEncoded models.\n" +
                        MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                        FIELD_TYPE_ERR_MSG + fieldType.getSimpleName() + "\n" +
                        FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                        FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n");
            }
            try {
                final Object convertedValue = convertUrlDecodedStringValueToType(element, arrayComponentType);
                result.add(convertedValue);
            } catch (Exception e) {
                throw new FormUrlEncodedMapperException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG +
                        MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                        FIELD_TYPE_ERR_MSG + fieldType.getSimpleName() + "\n" +
                        FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                        FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                        "    Type to convert: " + arrayComponentType + "\n" +
                        VALUE_FOR_CONVERT_ERR_MSG + element + "\n" +
                        ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n");
            }
        }
        return result.toArray((Object[]) Array.newInstance(fieldType.getComponentType(), 0));
    }

    /**
     * Converts a [string] to the target {@link ParameterizedType}.
     * Supports the following types for conversion:
     * - {@link List}
     * - {@link Set}
     *
     * @param model             - FormUrlEncoded model
     * @param field             - model field
     * @param parameterizedType - field type
     * @param value             - String value to convert
     * @return converted {@link Collection}
     * @throws FormUrlEncodedMapperException if conversion type is different from {@link List}, {@link Set}
     * @throws FormUrlEncodedMapperException if {@link Collection} generic type ({@code <?>}) not supported
     * @see FormUrlEncodedMapper#convertUrlDecodedStringValueToType
     */
    @EverythingIsNonNull
    protected Collection<Object> unmarshalDecodedValueToParameterizedType(final Object model,
                                                                          final Field field,
                                                                          final ParameterizedType parameterizedType,
                                                                          final List<String> value) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        Utils.parameterRequireNonNull(parameterizedType, PARAMETERIZED_TYPE_PARAMETER);
        final Type rawType = parameterizedType.getRawType();
        final Type targetType = parameterizedType.getActualTypeArguments()[0];
        if (Collection.class.isAssignableFrom((Class<?>) rawType)) {
            final List<Object> list = new ArrayList<>();
            for (String element : value) {
                try {
                    list.add(convertUrlDecodedStringValueToType(element, targetType));
                } catch (Exception e) {
                    throw new FormUrlEncodedMapperException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG +
                            MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                            FIELD_TYPE_ERR_MSG + parameterizedType + "\n" +
                            FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                            FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                            "    Type to convert: " + targetType + "\n" +
                            VALUE_FOR_CONVERT_ERR_MSG + element + "\n" +
                            ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n");
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
                MODEL_TYPE_ERR_MSG + model.getClass().getName() + "\n" +
                FIELD_TYPE_ERR_MSG + rawType + "\n" +
                FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                FORF_FIELD_NAME_ERR_MSG + getFormUrlEncodedFieldName(field) + "\n" +
                "    Supported parameterized types:\n" +
                "    - " + List.class.getName() + "\n" +
                "    - " + Set.class.getName() + "\n");
    }

    /**
     * Converts a string to the target type.
     * Supports the following types for conversion:
     * - {@link String}
     * - {@link Object} (string by default)
     * - {@link Boolean}
     * - {@link Short}
     * - {@link Long}
     * - {@link Float}
     * - {@link Integer}
     * - {@link Double}
     * - {@link BigInteger}
     * - {@link BigDecimal}
     *
     * @param value      - String value to convert
     * @param targetType - Java type to which the string is converted
     * @return converted value
     * @throws IllegalArgumentException if targetType is primitive
     * @throws IllegalArgumentException if targetType not supported
     * @throws IllegalArgumentException if the value cannot be converted to {@link Boolean} type
     * @throws NumberFormatException    if the value cannot be converted to number types
     */
    @SuppressWarnings(SONAR_COGNITIVE_COMPLEXITY)
    protected Object convertUrlDecodedStringValueToType(final String value, final Type targetType) {
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        Utils.parameterRequireNonNull(targetType, TARGET_TYPE_PARAMETER);
        if (targetType instanceof Class && ((Class<?>) targetType).isPrimitive()) {
            throw new IllegalArgumentException("It is forbidden to use primitive types " +
                    "in FormUrlEncoded models: " + targetType);
        }
        if (targetType.equals(String.class) || targetType.equals(Object.class)) {
            return value;
        }
        if (targetType.equals(Boolean.class)) {
            if ("true".equals(value)) {
                return true;
            }
            if ("false".equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("Cannot convert string to boolean: '" + value + "'");
        }
        if (targetType.equals(Short.class)) {
            return Short.valueOf(value);
        }
        if (targetType.equals(Long.class)) {
            return Long.valueOf(value);
        }
        if (targetType.equals(Float.class)) {
            return Float.valueOf(value);
        }
        if (targetType.equals(Integer.class)) {
            return Integer.valueOf(value);
        }
        if (targetType.equals(Double.class)) {
            return Double.valueOf(value);
        }
        if (targetType.equals(BigInteger.class)) {
            return NumberUtils.createBigInteger(value);
        }
        if (targetType.equals(BigDecimal.class)) {
            return NumberUtils.createBigDecimal(value);
        }
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + targetType);
    }

    /**
     * @param model - FormUrlEncoded model
     * @param field - model field
     * @param value - String value to convert
     * @param <M>   model generic type
     * @throws FormUrlEncodedMapperException if the value cannot be written to the model field
     */
    @EverythingIsNonNull
    protected <M> void writeFieldValue(final M model, final Field field, final Object value) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        Utils.parameterRequireNonNull(value, VALUE_PARAMETER);
        try {
            FieldUtils.writeDeclaredField(model, field.getName(), value, true);
        } catch (Exception e) {
            final String fieldTypeName = field.getType().getSimpleName();
            final String fieldValue;
            if (value.getClass().isArray()) {
                fieldValue = Arrays.toString((Object[]) value);
            } else {
                fieldValue = String.valueOf(value);
            }
            throw new FormUrlEncodedMapperException("Unable to write value to model field.\n" +
                    MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + fieldTypeName + "\n" +
                    "    Value type: " + value.getClass().getSimpleName() + "\n" +
                    "    Value: " + fieldValue + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
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
    protected Field getAdditionalPropertiesField(@Nonnull final Class<?> modelClass) {
        Utils.parameterRequireNonNull(modelClass, MODEL_CLASS_PARAMETER);
        final List<Field> fields = Arrays.asList(modelClass.getDeclaredFields());
        final List<Field> additionalProperties = fields.stream()
                .filter(f -> f.isAnnotationPresent(FormUrlEncodedAdditionalProperties.class))
                .collect(Collectors.toList());
        if (additionalProperties.size() > 1) {
            final String fNames = additionalProperties.stream().map(Field::getName).collect(Collectors.joining(", "));
            throw new FormUrlEncodedMapperException("Model contains more than one field annotated with " +
                    FormUrlEncodedAdditionalProperties.class.getSimpleName() + ":\n" +
                    MODEL_ERR_MSG + modelClass + "\n" +
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
            isValidTypeArguments = keyType == String.class && valueType == Object.class;
        } else {
            isValidTypeArguments = false;
        }
        if (!isParameterizedType || !isMap || !isValidTypeArguments) {
            throw new FormUrlEncodedMapperException("Invalid field type with @" +
                    FormUrlEncodedAdditionalProperties.class.getSimpleName() + " annotation\n" +
                    MODEL_ERR_MSG + modelClass + "\n" +
                    FIELD_ERR_MSG + additionalProperty.getName() + "\n" +
                    ACT_TYPE_ERR_MSG + type.getTypeName() + "\n" +
                    EXP_TYPE_ERR_MSG + "java.util.Map<java.lang.String, java.lang.Object>\n");
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
     * @throws FormUrlEncodedMapperException if unable to initialize additionalProperties field
     * @throws FormUrlEncodedMapperException if additionalProperty field not readable
     */
    @EverythingIsNonNull
    @SuppressWarnings("unchecked")
    protected Map<String, Object> initAdditionalProperties(final Object model, final Field field) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final String fieldName = field.getName();
        try {
            if (Modifier.isFinal(field.getModifiers())) {
                return (Map<String, Object>) FieldUtils.readDeclaredField(model, field.getName(), true);
            }
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to read additional properties field.\n" +
                    MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType() + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        try {
            final HashMap<String, Object> value = new HashMap<>();
            FieldUtils.writeDeclaredField(model, fieldName, value, true);
            return value;
        } catch (Exception e) {
            throw new FormUrlEncodedMapperException("Unable to initialize additional properties field.\n" +
                    MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                    FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                    FIELD_TYPE_ERR_MSG + field.getType() + "\n" +
                    ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
    }

    /**
     * Parse `x-www-form-urlencoded` String
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     * - {@code name=value -> {"name":[VALUE_PARAMETER]}}
     * - {@code &name=value -> {"name":[VALUE_PARAMETER]}}
     * - {@code ?name=value -> {"name":[VALUE_PARAMETER]}}
     * - {@code name=value1&name=value2 -> {"name":["value1", "value2"]}}
     * - {@code name1=value1&name2=value2 -> {"name1":["value1"], "name2":["value2"]}}
     * - {@code name= -> {name:[""]}}
     * - {@code name=%7B%22a%22%3D%22%26%22%7D -> {name:["{\"a\"=\"&\"}"]}}
     *
     * @param urlEncodedString - `x-www-form-urlencoded` string
     * @param codingCharset    - URL form data coding {@link Charset}
     * @return {@link Map} where key - field name, value - list of field values (1...n)
     * @throws FormUrlEncodedMapperException - broken urlencoded string (for example a=b=c)
     * @throws FormUrlEncodedMapperException - unsupported URL form coding {@link Charset}
     */
    @EverythingIsNonNull
    protected Map<String, List<String>> parseAndDecodeUrlEncodedString(final String urlEncodedString,
                                                                       final Charset codingCharset) {
        Utils.parameterRequireNonNull(urlEncodedString, ENCODED_STRING_PARAMETER);
        Utils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        final Map<String, List<String>> result = new HashMap<>();
        if (urlEncodedString.trim().length() == 0) {
            return result;
        }
        final String prepared;
        if (urlEncodedString.startsWith("?")) {
            prepared = urlEncodedString.substring(1).trim();
        } else {
            prepared = urlEncodedString.trim();
        }
        final String[] pairs = prepared.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            final String[] split = pair.split("=");
            if (split.length > 2 || split.length == 0) {
                throw new FormUrlEncodedMapperException("URL encoded string not in URL format:\n" + urlEncodedString);
            }
            final String key = split[0].replaceAll("\\[.*]", "").trim();
            final String urlEncodedValue;
            if (split.length == 1) {
                urlEncodedValue = "";
            } else {
                urlEncodedValue = split[1].trim();
            }
            try {
                final String urlDecodedValue = URLDecoder.decode(urlEncodedValue, codingCharset.name());
                result.computeIfAbsent(key, i -> new ArrayList<>()).add(urlDecodedValue);
            } catch (Exception e) {
                throw new FormUrlEncodedMapperException("Error decoding URL encoded string:\n" + pair, e);
            }
        }
        return result;
    }

    /**
     * @param field - model field
     * @return URL form field name from the {@link FormUrlEncodedField#value()} field annotation
     * @throws FormUrlEncodedMapperException if field does not contain {@link FormUrlEncodedField} annotation
     * @throws FormUrlEncodedMapperException if {@link FormUrlEncodedField#value()} is empty or blank
     */
    @EverythingIsNonNull
    protected String getFormUrlEncodedFieldName(final Field field) {
        Utils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final FormUrlEncodedField annotation = field.getAnnotation(FormUrlEncodedField.class);
        if (annotation == null) {
            throw new FormUrlEncodedMapperException("Field does not contain a required annotation.\n" +
                    FIELD_ERR_MSG + field.getName() + "\n" +
                    EXP_TYPE_ERR_MSG + FormUrlEncodedField.class.getName() + "\n");
        }
        final String value = annotation.value();
        if (value.trim().isEmpty()) {
            throw new FormUrlEncodedMapperException("URL field name can not be empty or blank.\n" +
                    FIELD_ERR_MSG + field.getName() + "\n" +
                    ANNOTATION_ERR_MSG + FormUrlEncodedField.class.getName() + "\n" +
                    "    Method: value()\n" +
                    "    Actual: '" + value + "'\n");
        }
        return value;
    }

}
