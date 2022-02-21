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

import internal.test.utils.BaseUnitTest;
import internal.test.utils.CorruptedTestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.FormUrlEncodedMapperException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

import static internal.test.utils.TestUtils.listOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests.TypedModel.*;

@SuppressWarnings({"ConstantConditions", "unused"})
@DisplayName("FormUrlEncodedMapper.class unit tests")
public class FormUrlEncodedMapperUnitTests extends BaseUnitTest {

    private static final FormUrlEncodedMapper MAPPER = FormUrlEncodedMapper.INSTANCE;

    @Nested
    @DisplayName("#getAdditionalField() method tests")
    public class GetAdditionalFieldMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645279093093() {
            assertNPE(() -> MAPPER.getAdditionalProperties(null), "modelClass");
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field from a class (contains annotation)")
        public void test1645281367804() {
            final Field additionalProperties = MAPPER.getAdditionalProperties(AdditionalFields.class);
            assertNotNull(additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field (null) from a class (without annotation)")
        public void test1645281802803() {
            final Field additionalProperties = MAPPER.getAdditionalProperties(AdditionalFieldsWithoutAnnotation.class);
            assertIsNull(additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field (null) from a class (empty class)")
        public void test1645281893559() {
            final Field additionalProperties = MAPPER.getAdditionalProperties(EmptyModel.class);
            assertIsNull(additionalProperties);
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if additionalProperties type != Map<String, String>")
        public void test1645282054759() {
            assertThrow(() -> MAPPER.getAdditionalProperties(AdditionalFieldsInvalidType.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Invalid field type with @FormUrlEncodedAdditionalProperties annotation\n" +
                            "    Model: " + AdditionalFieldsInvalidType.class + "\n" +
                            "    Field: additionalProperties\n" +
                            "    Actual type: java.util.Map<?, ?>\n" +
                            "    Expected type: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if additionalProperties type == Map (raw type)")
        public void test1645282252989() {
            assertThrow(() -> MAPPER.getAdditionalProperties(AdditionalFieldsRawMap.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Invalid field type with @FormUrlEncodedAdditionalProperties annotation\n" +
                            "    Model: " + AdditionalFieldsRawMap.class + "\n" +
                            "    Field: additionalProperties\n" +
                            "    Actual type: java.util.Map\n" +
                            "    Expected type: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if additionalProperties type == List<String>")
        public void test1645282363009() {
            assertThrow(() -> MAPPER.getAdditionalProperties(AdditionalFieldsList.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Invalid field type with @FormUrlEncodedAdditionalProperties annotation\n" +
                            "    Model: " + AdditionalFieldsList.class + "\n" +
                            "    Field: additionalProperties\n" +
                            "    Actual type: java.util.List<java.lang.String>\n" +
                            "    Expected type: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if several additionalProperties fields")
        public void test1645282588975() {
            assertThrow(() -> MAPPER.getAdditionalProperties(SeveralAdditionalFields.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Model contains more than one field annotated with FormUrlEncodedAdditionalProperties:\n" +
                            "    Model: class veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$SeveralAdditionalFields\n" +
                            "    Fields: additionalProperties1, additionalProperties2\n");
        }

    }

    @Nested
    @DisplayName("#initAdditionalProperties() method tests")
    public class InitAdditionalPropertiesMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645292235482() {
            assertNPE(() -> MAPPER.initAdditionalProperties(null), "model");
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field initiated")
        public void test1645292268987() {
            final AdditionalFields additionalFields = new AdditionalFields();
            additionalFields.additionalProperties = new HashMap<>();
            additionalFields.additionalProperties.put("1", "2");
            final Map<String, Object> result = MAPPER.initAdditionalProperties(additionalFields);
            assertIs(result, additionalFields.additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field not initiated")
        public void test1645292357593() {
            final Map<String, Object> result = MAPPER.initAdditionalProperties(new AdditionalFields());
            assertIs(result, new HashMap<>());
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map (null) if field not present")
        public void test1645292405815() {
            final Map<String, Object> result = MAPPER.initAdditionalProperties(new EmptyModel());
            assertIsNull(result);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field initiated (final)")
        public void test1645292517696() {
            final FinalAdditionalFields additionalFields = new FinalAdditionalFields();
            additionalFields.additionalProperties.put("1", "2");
            final Map<String, Object> result = MAPPER.initAdditionalProperties(additionalFields);
            assertIs(result, additionalFields.additionalProperties);
        }

    }

    @Nested
    @DisplayName("#parseAndDecodeUrlEncodedString() method tests")
    public class ParseAndDecodeUrlEncodedStringMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645294525400() {
            assertNPE(() -> MAPPER.parseAndDecodeUrlEncodedString(null, UTF_8), "urlEncodedString");
            assertNPE(() -> MAPPER.parseAndDecodeUrlEncodedString("", null), "charset");
        }

        @Test
        @DisplayName("Successfully parsing a plain key value pair")
        public void test1645294673959() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("a=b", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("a"), contains("b"));

        }

        @Test
        @DisplayName("Successfully parsing encoded value")
        public void test1645294871965() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("text=%D1%82%D0%B5%D1%81%D1%82", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("тест"));
        }

        @Test
        @DisplayName("Successfully parsing encoded json")
        public void test1645303623520() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("text=%7B%22a%22%3D%22%26%22%7D", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("{\"a\"=\"&\"}"));
        }

        @Test
        @DisplayName("Successfully parsing empty value")
        public void test1645294974883() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("text=", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains(""));
        }

        @Test
        @DisplayName("Successfully parsing if data start with '?' character")
        public void test1645295172307() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("?text=123", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data ends with '&' character")
        public void test1645295257672() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("text=123&", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data start with '&' character")
        public void test1645295303635() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("&text=123", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data contains list")
        public void test1645303330255() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("id=1&text=foo&text=bar", UTF_8);
            assertThat(map, aMapWithSize(2));
            assertThat(map.get("text"), containsInAnyOrder("bar", "foo"));
            assertThat(map.get("id"), containsInAnyOrder("1"));
        }

        @Test
        @DisplayName("Successfully parsing if data contains empty string")
        public void test1645303459015() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("", UTF_8);
            assertThat(map, aMapWithSize(0));
        }

        @Test
        @DisplayName("Successfully parsing if data contains blank string")
        public void test1645303473746() {
            final Map<String, List<String>> map = MAPPER.parseAndDecodeUrlEncodedString("\n", UTF_8);
            assertThat(map, aMapWithSize(0));
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if URL string = 'a=b=c'")
        public void test1645303862266() {
            assertThrow(() -> MAPPER.parseAndDecodeUrlEncodedString("a=b=c", UTF_8))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("URL encoded string not in URL format:\na=b=c");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if charset == FooBar")
        public void test1645304020681() {
            final Charset mock = mock(Charset.class);
            when(mock.name()).thenReturn("");
            assertThrow(() -> MAPPER.parseAndDecodeUrlEncodedString("a=b", mock))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Error decoding URL encoded string:\na=b");
        }

    }

    @Nested
    @DisplayName("#convertValueToFieldType() method tests")
    public class ConvertValueToFieldTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645318639983() {
            final TypedModel model = new TypedModel();
            final Field declaredField = AdditionalFields.class.getDeclaredFields()[0];
            assertNPE(() -> MAPPER.convertValueToFieldType(null, declaredField, new ArrayList<>()), "model");
            assertNPE(() -> MAPPER.convertValueToFieldType(model, null, new ArrayList<>()), "field");
            assertNPE(() -> MAPPER.convertValueToFieldType(model, declaredField, null), "value");
        }

        @Test
        @DisplayName("Transform raw value to List<String>")
        public void test1645318712610() {
            final Field field = TypedModel.field(LIST_STRING_FIELD);
            final List<String> value = Collections.singletonList("test");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, instanceOf(List.class));
            assertThat(o, is(value));
        }

        @Test
        @DisplayName("Transform raw value to String")
        public void test1645319853249() {
            final Field field = TypedModel.field(STRING_FIELD);
            final List<String> value = Collections.singletonList("2");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, instanceOf(String.class));
            assertThat(o, is("2"));
        }

        @Test
        @DisplayName("Transform raw value to Integer")
        public void test1645389602012() {
            final Field field = TypedModel.field(INTEGER_FIELD);
            final List<String> value = Collections.singletonList("2");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, instanceOf(Integer.class));
            assertThat(o, is(2));
        }

        @Test
        @DisplayName("Transform raw value to String[]")
        public void test1645389712876() {
            final Field field = TypedModel.field(STRING_ARRAY_FIELD);
            final List<String> value = listOf("1", "2");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, is(new String[]{"1", "2"}));
        }

        @Test
        @DisplayName("Transform raw value to String[]")
        public void test1645389793160() {
            final Field field = TypedModel.field(INTEGER_ARRAY_FIELD);
            final List<String> value = listOf("1", "2");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, is(new Integer[]{1, 2}));
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if value list is empty")
        public void test1645389846421() {
            final Field field = TypedModel.field(STRING_FIELD);
            final List<String> value = listOf();
            assertThrow(() -> MAPPER.convertValueToFieldType(new TypedModel(), field, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("The 'value' field does not contain data to be converted.");
        }

    }

    @Nested
    @DisplayName("#convertUrlDecodedStringValueToType() method tests")
    public class ConvertUrlDecodedStringValueToTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645376480179() {
            assertNPE(() -> MAPPER.convertUrlDecodedStringValueToType(null, Object.class), "value");
            assertNPE(() -> MAPPER.convertUrlDecodedStringValueToType("", null), "targetType");
        }

        @Test
        @DisplayName("Successfully conversion string value to String type")
        public void test1645376586652() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("test", String.class);
            assertIs(result, "test");
        }

        @Test
        @DisplayName("Successfully conversion string value to Object type")
        public void test1645377510696() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("test", Object.class);
            assertIs(result, "test");
        }

        @Test
        @DisplayName("Successfully conversion integer string value to Integer type")
        public void test1645376667247() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("1", Integer.class);
            assertIs(result, 1);
        }

        @Test
        @DisplayName("Successfully conversion integer string value to BigInteger type")
        public void test1645376817230() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("1", BigInteger.class);
            assertIs(result, BigInteger.valueOf(1L));
        }

        @Test
        @DisplayName("Successfully conversion long string value to Long type")
        public void test1645376695492() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("1", Long.class);
            assertIs(result, 1L);
        }

        @Test
        @DisplayName("Successfully conversion short string value to Short type")
        public void test1645376718177() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("1", Short.class);
            assertIs(result, Short.valueOf("1"));
        }

        @Test
        @DisplayName("Successfully conversion float string value to Float type")
        public void test1645376750074() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("0.1", Float.class);
            assertIs(result, 0.1F);
        }

        @Test
        @DisplayName("Successfully conversion double string value to Double type")
        public void test1645376788850() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("0.1", Double.class);
            assertIs(result, 0.1);
        }

        @Test
        @DisplayName("Successfully conversion integer string value to BigDecimal type")
        public void test1645376867326() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("0.1", BigDecimal.class);
            assertIs(result, BigDecimal.valueOf(0.1));
        }

        @Test
        @DisplayName("Successfully conversion 'true' string value to Boolean type")
        public void test1645376909313() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("true", Boolean.class);
            assertIs(result, true);
        }

        @Test
        @DisplayName("Successfully conversion 'false' string value to Boolean type")
        public void test1645376924701() {
            final Object result = MAPPER.convertUrlDecodedStringValueToType("false", Boolean.class);
            assertIs(result, false);
        }

        @Test
        @DisplayName("IllegalArgumentException -> conversion 'FooBar' string value to Boolean type")
        public void test1645377271047() {
            assertThrow(() -> MAPPER.convertUrlDecodedStringValueToType("FooBar", Boolean.class))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Cannot convert string to boolean: 'FooBar'");
        }

        @Test
        @DisplayName("IllegalArgumentException -> field type is primitive")
        public void test1645377119906() {
            assertThrow(() -> MAPPER.convertUrlDecodedStringValueToType("false", Boolean.TYPE))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("It is forbidden to use primitive types in FormUrlEncoded models: boolean");
        }

        @Test
        @DisplayName("IllegalArgumentException -> unsupported field type")
        public void test1645377473744() {
            assertThrow(() -> MAPPER.convertUrlDecodedStringValueToType("false", Map.class))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: interface java.util.Map");
        }

    }

    @Nested
    @DisplayName("#getFormUrlEncodedFieldName() method tests")
    public class GetFormUrlEncodedFieldNameMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645374343042() {
            assertNPE(() -> MAPPER.getFormUrlEncodedFieldName(null), "field");
        }

        @Test
        @DisplayName("Successfully getting URL field name from FormUrlEncodedField annotation")
        public void test1645374994327() {
            final Field field = field(STRING_FIELD);
            final String result = MAPPER.getFormUrlEncodedFieldName(field);
            assertIs(result, STRING_FIELD);
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if FormUrlEncodedField.value = empty string")
        public void test1645375279348() {
            final Field field = field(EMPTY_FORM_URL_ENCODED_FIELD_VALUE);
            assertThrow(() -> MAPPER.getFormUrlEncodedFieldName(field))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "URL field name can not be empty or blank.\n" +
                            "    Field: emptyFormUrlEncodedFieldValue\n" +
                            "    Annotation: veslo.bean.urlencoded.FormUrlEncodedField\n" +
                            "    Method: value()\n" +
                            "    Actual: ''\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if FormUrlEncodedField.value = blank string")
        public void test1645375670651() {
            final Field field = field(BLANK_FORM_URL_ENCODED_FIELD_VALUE);
            assertThrow(() -> MAPPER.getFormUrlEncodedFieldName(field))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "URL field name can not be empty or blank.\n" +
                            "    Field: blankFormUrlEncodedFieldValue\n" +
                            "    Annotation: veslo.bean.urlencoded.FormUrlEncodedField\n" +
                            "    Method: value()\n" +
                            "    Actual: ' \n'\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if field does not contain @FormUrlEncodedField annotation")
        public void test1645376067254() {
            final Field field = TypedModel.fieldWithoutFormUrlEncodedFieldAnnotation();
            assertThrow(() -> MAPPER.getFormUrlEncodedFieldName(field))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Field does not contain a required annotation.\n" +
                            "    Field: withoutAnnotation\n" +
                            "    Expected annotation: veslo.bean.urlencoded.FormUrlEncodedField\n");
        }

    }

    @Nested
    @DisplayName("#convertParameterizedType() method tests")
    public class ConvertParameterizedTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645378798457() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_STRING_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = Collections.singletonList("test");
            assertNPE(() -> MAPPER.convertParameterizedType(null, field, genericType, value), "model");
            assertNPE(() -> MAPPER.convertParameterizedType(model, null, genericType, value), "field");
            assertNPE(() -> MAPPER.convertParameterizedType(model, field, null, value), "parameterizedType");
            assertNPE(() -> MAPPER.convertParameterizedType(model, field, genericType, null), "value");
        }

        @Test
        @DisplayName("Successfully conversion '[test]' string to List<String> type")
        public void test1645379107057() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_STRING_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = Collections.singletonList("test");
            final Collection<Object> collection = MAPPER.convertParameterizedType(model, field, genericType, value);
            assertThat(collection, instanceOf(List.class));
            assertThat(collection, contains("test"));
        }

        @Test
        @DisplayName("Successfully conversion '[test1, test2]' string to List<String> type")
        public void test1645379248235() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_STRING_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("test1", "test2");
            final Collection<Object> collection = MAPPER.convertParameterizedType(model, field, genericType, value);
            assertThat(collection, instanceOf(List.class));
            assertThat(collection, containsInAnyOrder("test1", "test2"));
        }

        @Test
        @DisplayName("Successfully conversion '[1, 2]' string to List<Integer> type")
        public void test1645379404348() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_INTEGER_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("1", "2");
            final Collection<Object> collection = MAPPER.convertParameterizedType(model, field, genericType, value);
            assertThat(collection, instanceOf(List.class));
            assertThat(collection, containsInAnyOrder(1, 2));
        }

        @Test
        @DisplayName("Successfully conversion '[test1, test2]' string to Set<String> type")
        public void test1645379523002() {
            final TypedModel model = new TypedModel();
            final Field field = field(SET_STRING_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("test1", "test2");
            final Collection<Object> collection = MAPPER.convertParameterizedType(model, field, genericType, value);
            assertThat(collection, instanceOf(Set.class));
            assertThat(collection, containsInAnyOrder("test1", "test2"));
        }

        @Test
        @DisplayName("Successfully conversion '[test, test]' string to Set<String> type")
        public void test1645379575376() {
            final TypedModel model = new TypedModel();
            final Field field = field(SET_STRING_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("test", "test");
            final Collection<Object> collection = MAPPER.convertParameterizedType(model, field, genericType, value);
            assertThat(collection, instanceOf(Set.class));
            assertThat(collection, contains("test"));
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException -> unsupported parameterized type (Map<>)")
        public void test1645379801127() {
            final TypedModel model = new TypedModel();
            final Field field = field(MAP_STRING_INTEGER_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("test", "test");
            assertThrow(() -> MAPPER.convertParameterizedType(model, field, genericType, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Received unsupported parameterized type for conversion.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: interface java.util.Map\n" +
                            "    Field name: mapStringIntegerField\n" +
                            "    URL form field name: MAP_STRING_INTEGER_FIELD\n" +
                            "    Supported parameterized types:\n" +
                            "    - java.util.List\n" +
                            "    - java.util.Set\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException -> unsupported parameterized type (generic type argument)")
        public void test1645379919097() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_ENUM_FIELD);
            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            final List<String> value = listOf("test", "test");
            assertThrow(() -> MAPPER.convertParameterizedType(model, field, genericType, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Received unsupported type for conversion.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: java.util.List<java.lang.Enum<?>>\n" +
                            "    Field name: listEnumField\n" +
                            "    URL form field name: LIST_ENUM_FIELD\n" +
                            "    Type to convert: java.lang.Enum<?>\n" +
                            "    Value for convert: test\n" +
                            "    Error cause: Received unsupported type for conversion: java.lang.Enum<?>\n");
        }

    }

    @Nested
    @DisplayName("#convertArrayType() method tests")
    public class ConvertArrayTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645383123445() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = Collections.singletonList("test");
            assertNPE(() -> MAPPER.convertArrayType(null, field, type, value), "model");
            assertNPE(() -> MAPPER.convertArrayType(model, null, type, value), "field");
            assertNPE(() -> MAPPER.convertArrayType(model, field, null, value), "fieldType");
            assertNPE(() -> MAPPER.convertArrayType(model, field, type, null), "value");
        }

        @Test
        @DisplayName("Successfully conversion '[test]' string to String[] type")
        public void test1645383368879() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = Collections.singletonList("test");
            final Object[] result = MAPPER.convertArrayType(model, field, type, value);
            assertThat(result, arrayWithSize(1));
            assertThat(result, arrayContaining("test"));
        }

        @Test
        @DisplayName("Successfully conversion '[test, test]' string to String[] type")
        public void test1645383383031() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("test", "test");
            final Object[] result = MAPPER.convertArrayType(model, field, type, value);
            assertThat(result, arrayWithSize(2));
            assertThat(result, arrayContaining("test", "test"));
        }

        @Test
        @DisplayName("Successfully conversion '[1, 2]' string to Integer[] type")
        public void test1645383431358() {
            final TypedModel model = new TypedModel();
            final Field field = field(INTEGER_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1", "2");
            final Object[] result = MAPPER.convertArrayType(model, field, type, value);
            assertThat(result, arrayWithSize(2));
            assertThat(result, arrayContaining(1, 2));
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if field type is not array")
        public void test1645386934351() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1", "2");
            assertThrow(() -> MAPPER.convertArrayType(model, field, type, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Mismatch types. Got a single type instead of an array.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: java.lang.String\n" +
                            "    Field name: stringField\n" +
                            "    URL form field name: STRING_FIELD\n" +
                            "    Expected type: array\n");
        }

        @Test
        @DisplayName("Throws IllegalArgumentException if field component type is primitive")
        public void test1645387069345() {
            final TypedModel model = new TypedModel();
            final Field field = field(PRIMITIVE_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1", "2");
            assertThrow(() -> MAPPER.convertArrayType(model, field, type, value))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("" +
                            "It is forbidden to use primitive types in FormUrlEncoded models.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: int[]\n" +
                            "    Field name: primitiveArrayField\n" +
                            "    URL form field name: PRIMITIVE_ARRAY_FIELD\n");
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if field component type not supported")
        public void test1645387316882() {
            final TypedModel model = new TypedModel();
            final Field field = field(CHARACTER_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1", "2");
            assertThrow(() -> MAPPER.convertArrayType(model, field, type, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Received unsupported type for conversion.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: Character[]\n" +
                            "    Field name: characterArrayField\n" +
                            "    URL form field name: CHARACTER_ARRAY_FIELD\n" +
                            "    Type to convert: class java.lang.Character\n" +
                            "    Value for convert: 1\n" +
                            "    Error cause: Received unsupported type for conversion: class java.lang.Character\n");
        }

    }

    @Nested
    @DisplayName("#convertSingleType() method tests")
    public class ConvertSingleTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645387634626() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = Collections.singletonList("test");
            assertNPE(() -> MAPPER.convertSingleType(null, field, type, value), "model");
            assertNPE(() -> MAPPER.convertSingleType(model, null, type, value), "field");
            assertNPE(() -> MAPPER.convertSingleType(model, field, null, value), "fieldType");
            assertNPE(() -> MAPPER.convertSingleType(model, field, type, null), "value");
        }

        @Test
        @DisplayName("Successfully conversion '[1]' string to Integer type")
        public void test1645388027397() {
            final TypedModel model = new TypedModel();
            final Field field = field(INTEGER_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1");
            final Object result = MAPPER.convertSingleType(model, field, type, value);
            assertThat(result, is(1));
        }

        @Test
        @DisplayName("Successfully conversion '[test]' string to String type")
        public void test1645388038498() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("test");
            final Object result = MAPPER.convertSingleType(model, field, type, value);
            assertThat(result, is("test"));
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if value list is empty")
        public void test1645388254928() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf();
            assertThrow(() -> MAPPER.convertSingleType(model, field, type, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("The 'value' field does not contain data to be converted.");
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if value list has more than one record")
        public void test1645388313701() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1", "2");
            assertThrow(() -> MAPPER.convertSingleType(model, field, type, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Mismatch types. Got an array instead of a single value.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: java.lang.String\n" +
                            "    Field name: stringField\n" +
                            "    URL form field name: STRING_FIELD\n" +
                            "    Received type: array\n" +
                            "    Received value: [1, 2]\n" +
                            "    Expected value: single value\n");
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if field type not supported")
        public void test1645388419144() {
            final TypedModel model = new TypedModel();
            final Field field = field(CHARACTER_FIELD);
            final Class<?> type = field.getType();
            final List<String> value = listOf("1");
            assertThrow(() -> MAPPER.convertSingleType(model, field, type, value))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Error converting string to field type.\n" +
                            "    Model type: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field type: java.lang.Character\n" +
                            "    Field name: characterField\n" +
                            "    URL form field name: CHARACTER_FIELD\n" +
                            "    Value for convert: 1\n" +
                            "    Error cause: Received unsupported type for conversion: class java.lang.Character\n");
        }

    }

    @Nested
    @DisplayName("#writeFieldValue() method tests")
    public class WriteFieldValueMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645390941993() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            assertNPE(() -> MAPPER.writeFieldValue(null, field, "fooBar"), "model");
            assertNPE(() -> MAPPER.writeFieldValue(model, null, "fooBar"), "field");
            assertNPE(() -> MAPPER.writeFieldValue(model, field, null), "value");
        }

        @Test
        @DisplayName("Successfully writing String to the model field")
        public void test1645391130913() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            MAPPER.writeFieldValue(model, field, "fooBar");
            assertIs(model.stringField, "fooBar");
        }

        @Test
        @DisplayName("Successfully writing String[] to the model field")
        public void test1645391207681() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            MAPPER.writeFieldValue(model, field, new String[]{"fooBar1", "fooBar2"});
            assertThat(model.stringArrayField, arrayContaining("fooBar1", "fooBar2"));
        }

        @Test
        @DisplayName("Successfully writing List<String> to the model field")
        public void test1645391288159() {
            final TypedModel model = new TypedModel();
            final Field field = field(LIST_STRING_FIELD);
            MAPPER.writeFieldValue(model, field, listOf("fooBar1", "fooBar2"));
            assertThat(model.listStringField, contains("fooBar1", "fooBar2"));
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if the value cannot be written to the model field")
        public void test1645391472862() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_FIELD);
            assertThrow(() -> MAPPER.writeFieldValue(model, field, listOf("fooBar1", "fooBar2")))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Unable to write value to model field.\n" +
                            "    Model: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field name: stringField\n" +
                            "    Field type: String\n" +
                            "    Value type: ArrayList\n" +
                            "    Value: [fooBar1, fooBar2]\n" +
                            "    Error cause:" +
                            " Can not set java.lang.String field" +
                            " veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel.stringField" +
                            " to java.util.ArrayList\n");
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException if the value cannot be written to the model field (array)")
        public void test1645400443881() {
            final TypedModel model = new TypedModel();
            final Field field = field(STRING_ARRAY_FIELD);
            assertThrow(() -> MAPPER.writeFieldValue(model, field, new Integer[] {1, 2}))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("" +
                            "Unable to write value to model field.\n" +
                            "    Model: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel\n" +
                            "    Field name: stringArrayField\n" +
                            "    Field type: String[]\n" +
                            "    Value type: Integer[]\n" +
                            "    Value: [1, 2]\n" +
                            "    Error cause:" +
                            " Can not set [Ljava.lang.String;" +
                            " field veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$TypedModel.stringArrayField" +
                            " to [Ljava.lang.Integer;\n");
        }

    }

    @Nested
    @DisplayName("#writeAdditionalProperties() method tests")
    public class WriteAdditionalPropertiesMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645392361215() {
            final AdditionalFields model = new AdditionalFields();
            final Map<String, List<String>> parsed = new HashMap<>();
            final List<Field> handled = new ArrayList<>();
            assertNPE(() -> MAPPER.writeAdditionalProperties(null, parsed, handled), "model");
            assertNPE(() -> MAPPER.writeAdditionalProperties(model, null, handled), "parsed");
            assertNPE(() -> MAPPER.writeAdditionalProperties(model, parsed, null), "handled");
        }

        @Test
        @DisplayName("Fill additional properties if field present")
        public void test1645392502716() {
            final AdditionalFields model = new AdditionalFields();
            final List<Field> handled = new ArrayList<>();
            final Map<String, List<String>> parsed = new HashMap<>();
            parsed.put("singleField", listOf("value"));
            parsed.put("emptyField", listOf());
            parsed.put("listField", listOf("value1", "value2"));
            MAPPER.writeAdditionalProperties(model, parsed, handled);
            Map<String, Object> expected = new HashMap<>();
            expected.put("singleField", "value");
            expected.put("emptyField", "");
            expected.put("listField", listOf("value1", "value2"));
            assertIs(model.additionalProperties, expected);
        }

        @Test
        @DisplayName("Method complete without errors if additionalProperties field not present")
        public void test1645393261530() {
            final AdditionalFieldsWithoutAnnotation model = new AdditionalFieldsWithoutAnnotation();
            final List<Field> handled = new ArrayList<>();
            final Map<String, List<String>> parsed = new HashMap<>();
            parsed.put("singleField", listOf("value"));
            parsed.put("emptyField", listOf());
            parsed.put("listField", listOf("value1", "value2"));
            MAPPER.writeAdditionalProperties(model, parsed, handled);
            assertIsNull(model.additionalProperties);
        }

    }

    @Nested
    @DisplayName("#unmarshal() method tests")
    public class UnmarshalMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645393437479() {
            final AdditionalFields model = new AdditionalFields();
            assertNPE(() -> MAPPER.unmarshal(null, ""), "modelClass");
            assertNPE(() -> MAPPER.unmarshal(AdditionalFields.class, null), "encodedString");
            assertNPE(() -> MAPPER.unmarshal(null, "", UTF_8), "modelClass");
            assertNPE(() -> MAPPER.unmarshal(AdditionalFields.class, null, UTF_8), "encodedString");
            assertNPE(() -> MAPPER.unmarshal(AdditionalFields.class, "", null), "encodeCharset");
        }

        @Test
        @DisplayName("Return empty model if empty string received")
        public void test1645393873436() {
            final AdditionalFields unmarshal = MAPPER.unmarshal(AdditionalFields.class, "");
            assertIsNull(unmarshal.additionalProperties);
        }

        @Test
        @DisplayName("Successful unmarshal over all model fields")
        public void test1645394207527() {
            final String value = "?\n" +
                    "stringField=%D1%82%D0%B5%D1%81%D1%82&\n" +
                    "integerField=111&\n" +
                    "stringArrayField=stringArrayValue1&\n" +
                    "stringArrayField=stringArrayValue2&\n" +
                    "integerArrayField=1&\n" +
                    "listStringField[]=listStringValue1&\n" +
                    "listStringField[]=listStringValue2&\n" +
                    "listIntegerField[100]=1&\n" +
                    "additional=properties&\n" +
                    "empty=\n";
            final GoodModel model = MAPPER.unmarshal(GoodModel.class, value);
            assertThat(model, notNullValue());
            assertThat(model.stringField, is("тест"));
            assertThat(model.integerField, is(111));
            assertThat(model.stringArrayField, arrayContaining("stringArrayValue1", "stringArrayValue2"));
            assertThat(model.integerArrayField, arrayContaining(1));
            assertThat(model.listStringField, contains("listStringValue1", "listStringValue2"));
            assertThat(model.listIntegerField, contains(1));
            assertThat(model.missed, nullValue());
            Map<String, Object> additionalProperties = new HashMap<>();
            additionalProperties.put("additional", "properties");
            additionalProperties.put("empty", "");
            assertThat(model.additionalProperties, is(additionalProperties));
        }

        @Test
        @DisplayName("Throws FormUrlEncodedMapperException on class instantiation errors (private constructor)")
        public void test1645393625291() {
            assertThrow(() -> MAPPER.unmarshal(PrivateModel.class, ""))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Unable to instantiate model class\n" +
                            "    Model class: veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$PrivateModel\n" +
                            "    Error cause: No such accessible constructor on object: " +
                            "veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests$PrivateModel\n");
        }

    }

    @FormUrlEncoded
    public static class GoodModel {

        @FormUrlEncodedField("missed")
        private String missed;

        @FormUrlEncodedField("stringField")
        private String stringField;

        @FormUrlEncodedField("integerField")
        private Integer integerField;

        @FormUrlEncodedField("stringArrayField")
        private String[] stringArrayField;

        @FormUrlEncodedField("integerArrayField")
        private Integer[] integerArrayField;

        @FormUrlEncodedField("listStringField")
        private List<String> listStringField;

        @FormUrlEncodedField("listIntegerField")
        private List<Integer> listIntegerField;

        @FormUrlEncodedAdditionalProperties()
        private Map<String, Object> additionalProperties;

    }

    @FormUrlEncoded
    @SuppressWarnings("rawtypes")
    static class TypedModel {

        static final String EMPTY_FORM_URL_ENCODED_FIELD_VALUE = "";
        static final String BLANK_FORM_URL_ENCODED_FIELD_VALUE = " \n";
        static final String STRING_FIELD = "STRING_FIELD";
        static final String CHARACTER_FIELD = "CHARACTER_FIELD";
        static final String STRING_ARRAY_FIELD = "STRING_ARRAY_FIELD";
        static final String INTEGER_FIELD = "INTEGER_FIELD";
        static final String INTEGER_ARRAY_FIELD = "INTEGER_ARRAY_FIELD";
        static final String CHARACTER_ARRAY_FIELD = "CHARACTER_ARRAY_FIELD";
        static final String LIST_STRING_FIELD = "LIST_STRING_FIELD";
        static final String LIST_INTEGER_FIELD = "LIST_INTEGER_FIELD";
        static final String SET_STRING_FIELD = "SET_STRING_FIELD";
        static final String SET_INTEGER_FIELD = "SET_INTEGER_FIELD";
        static final String PRIMITIVE_FIELD = "PRIMITIVE_FIELD";
        static final String PRIMITIVE_ARRAY_FIELD = "PRIMITIVE_ARRAY_FIELD";
        static final String MAP_STRING_INTEGER_FIELD = "MAP_STRING_INTEGER_FIELD";
        static final String MAP_RAW_FIELD = "MAP_RAW_FIELD";
        static final String LIST_RAW_FIELD = "LIST_RAW_FIELD";
        static final String LIST_ENUM_FIELD = "LIST_ENUM_FIELD";

        private String withoutAnnotation;

        @FormUrlEncodedField(EMPTY_FORM_URL_ENCODED_FIELD_VALUE)
        private String emptyFormUrlEncodedFieldValue;

        @FormUrlEncodedField(BLANK_FORM_URL_ENCODED_FIELD_VALUE)
        private String blankFormUrlEncodedFieldValue;

        @FormUrlEncodedField(PRIMITIVE_FIELD)
        private int primitiveField;

        @FormUrlEncodedField(PRIMITIVE_ARRAY_FIELD)
        private int[] primitiveArrayField;

        @FormUrlEncodedField(STRING_FIELD)
        private String stringField;

        @FormUrlEncodedField(CHARACTER_FIELD)
        private Character characterField;

        @FormUrlEncodedField(INTEGER_FIELD)
        private Integer integerField;

        @FormUrlEncodedField(STRING_ARRAY_FIELD)
        private String[] stringArrayField;

        @FormUrlEncodedField(INTEGER_ARRAY_FIELD)
        private Integer[] integerArrayField;

        @FormUrlEncodedField(CHARACTER_ARRAY_FIELD)
        private Character[] characterArrayField;

        @FormUrlEncodedField(LIST_STRING_FIELD)
        private List<String> listStringField;

        @FormUrlEncodedField(LIST_INTEGER_FIELD)
        private List<Integer> listIntegerField;

        @FormUrlEncodedField(LIST_ENUM_FIELD)
        private List<Enum<?>> listEnumField;

        @FormUrlEncodedField(SET_STRING_FIELD)
        private Set<String> setStringField;

        @FormUrlEncodedField(SET_INTEGER_FIELD)
        private Set<Integer> setIntegerField;

        @FormUrlEncodedField(MAP_STRING_INTEGER_FIELD)
        private Map<String, Integer> mapStringIntegerField;

        @FormUrlEncodedField(MAP_RAW_FIELD)
        private Map mapRawField;

        @FormUrlEncodedField(LIST_RAW_FIELD)
        private List listRawField;

        static Field fieldWithoutFormUrlEncodedFieldAnnotation() {
            return Arrays.stream(TypedModel.class.getDeclaredFields())
                    .filter(f -> f.getName().equals("withoutAnnotation"))
                    .findFirst()
                    .orElseThrow(CorruptedTestException::new);
        }

        static Field field(String name) {
            return Arrays.stream(TypedModel.class.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(FormUrlEncodedField.class))
                    .filter(f -> f.getAnnotation(FormUrlEncodedField.class).value().equals(name))
                    .findFirst()
                    .orElseThrow(CorruptedTestException::new);
        }

    }

    @FormUrlEncoded
    private static class EmptyModel {

    }

    @FormUrlEncoded
    public static class PrivateModel {

        private PrivateModel() {

        }

    }

    @FormUrlEncoded
    public static class AdditionalFields {

        @FormUrlEncodedAdditionalProperties()
        private Map<String, Object> additionalProperties;

    }

    @FormUrlEncoded
    private static class FinalAdditionalFields {

        @FormUrlEncodedAdditionalProperties()
        private final Map<String, Object> additionalProperties = new HashMap<>();

    }

    @FormUrlEncoded
    private static class AdditionalFieldsWithoutAnnotation {

        private Map<String, Object> additionalProperties;

    }

    @FormUrlEncoded
    private static class AdditionalFieldsInvalidType {

        @FormUrlEncodedAdditionalProperties()
        private Map<?, ?> additionalProperties;

    }

    @FormUrlEncoded
    @SuppressWarnings({"rawtypes"})
    private static class AdditionalFieldsRawMap {

        @FormUrlEncodedAdditionalProperties()
        private Map additionalProperties;

    }

    @FormUrlEncoded
    private static class AdditionalFieldsList {

        @FormUrlEncodedAdditionalProperties()
        private List<String> additionalProperties;

    }

    @FormUrlEncoded
    private static class SeveralAdditionalFields {

        @FormUrlEncodedAdditionalProperties()
        private List<String> additionalProperties1;

        @FormUrlEncodedAdditionalProperties()
        private List<String> additionalProperties2;

    }

}
