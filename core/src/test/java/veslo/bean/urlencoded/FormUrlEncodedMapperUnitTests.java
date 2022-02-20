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
import java.nio.charset.Charset;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests.TypedModel.LIST_INTEGER_FIELD;
import static veslo.bean.urlencoded.FormUrlEncodedMapperUnitTests.TypedModel.LIST_STRING_FIELD;

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
                            "    Model         - " + AdditionalFieldsInvalidType.class + "\n" +
                            "    Field         - additionalProperties\n" +
                            "    Actual type   - java.util.Map<?, ?>\n" +
                            "    Expected type - java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if additionalProperties type == Map (raw type)")
        public void test1645282252989() {
            assertThrow(() -> MAPPER.getAdditionalProperties(AdditionalFieldsRawMap.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Invalid field type with @FormUrlEncodedAdditionalProperties annotation\n" +
                            "    Model         - " + AdditionalFieldsRawMap.class + "\n" +
                            "    Field         - additionalProperties\n" +
                            "    Actual type   - java.util.Map\n" +
                            "    Expected type - java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if additionalProperties type == List<String>")
        public void test1645282363009() {
            assertThrow(() -> MAPPER.getAdditionalProperties(AdditionalFieldsList.class))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("Invalid field type with @FormUrlEncodedAdditionalProperties annotation\n" +
                            "    Model         - " + AdditionalFieldsList.class + "\n" +
                            "    Field         - additionalProperties\n" +
                            "    Actual type   - java.util.List<java.lang.String>\n" +
                            "    Expected type - java.util.Map<java.lang.String, java.lang.Object>\n");
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
    @DisplayName("#parseEncodedString() method tests")
    public class ParseEncodedStringMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645294525400() {
            assertNPE(() -> MAPPER.parseEncodedString(null, UTF_8), "rawData");
            assertNPE(() -> MAPPER.parseEncodedString("", null), "charset");
        }

        @Test
        @DisplayName("Successfully parsing a plain key value pair")
        public void test1645294673959() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("a=b", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("a"), contains("b"));

        }

        @Test
        @DisplayName("Successfully parsing encoded value")
        public void test1645294871965() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("text=%D1%82%D0%B5%D1%81%D1%82", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("тест"));
        }

        @Test
        @DisplayName("Successfully parsing encoded json")
        public void test1645303623520() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("text=%7B%22a%22%3D%22%26%22%7D", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("{\"a\"=\"&\"}"));
        }

        @Test
        @DisplayName("Successfully parsing empty value")
        public void test1645294974883() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("text=", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains(""));
        }

        @Test
        @DisplayName("Successfully parsing if data start with '?' character")
        public void test1645295172307() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("?text=123", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data ends with '&' character")
        public void test1645295257672() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("text=123&", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data start with '&' character")
        public void test1645295303635() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("&text=123", UTF_8);
            assertThat(map, aMapWithSize(1));
            assertThat(map.get("text"), contains("123"));
        }

        @Test
        @DisplayName("Successfully parsing if data contains list")
        public void test1645303330255() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("id=1&text=foo&text=bar", UTF_8);
            assertThat(map, aMapWithSize(2));
            assertThat(map.get("text"), containsInAnyOrder("bar", "foo"));
            assertThat(map.get("id"), containsInAnyOrder("1"));
        }

        @Test
        @DisplayName("Successfully parsing if data contains empty string")
        public void test1645303459015() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("", UTF_8);
            assertThat(map, aMapWithSize(0));
        }

        @Test
        @DisplayName("Successfully parsing if data contains blank string")
        public void test1645303473746() {
            final Map<String, List<String>> map = MAPPER.parseEncodedString("\n", UTF_8);
            assertThat(map, aMapWithSize(0));
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if URL string = 'a=b=c'")
        public void test1645303862266() {
            assertThrow(() -> MAPPER.parseEncodedString("a=b=c", UTF_8))
                    .assertClass(FormUrlEncodedMapperException.class)
                    .assertMessageIs("URL encoded string not in URL format:\na=b=c");
        }

        @Test
        @DisplayName("FormUrlEncodedMapperException throws if charset == FooBar")
        public void test1645304020681() {
            final Charset mock = mock(Charset.class);
            when(mock.name()).thenReturn("");
            assertThrow(() -> MAPPER.parseEncodedString("a=b", mock))
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
        @DisplayName("Transform raw value to List<Integer>")
        public void test1645319853249() {
            final Field field = TypedModel.field(LIST_INTEGER_FIELD);
            final List<String> value = Arrays.asList("2", "3", "7");
            final Object o = MAPPER.convertValueToFieldType(new TypedModel(), field, value);
            assertThat(o, instanceOf(List.class));
            assertThat(o, is(Arrays.asList(2, 3, 7)));
        }

    }

    @Nested
    @DisplayName("convertStringValueToType#() method tests")
    public class ConvertStringValueToTypeMethodTests {



    }

    @FormUrlEncoded
    static class TypedModel {

        static final String LIST_STRING_FIELD = "listStringField";
        static final String LIST_INTEGER_FIELD = "listIntegerField";

        @FormUrlEncodedField("stringField")
        private String stringField;

        @FormUrlEncodedField(LIST_STRING_FIELD)
        private List<String> listStringField;

        @FormUrlEncodedField(LIST_INTEGER_FIELD)
        private List<Integer> listIntegerField;

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
    private static class AdditionalFields {

        @FormUrlEncodedAdditionalProperties()
        private Map<String, String> additionalProperties;

    }

    @FormUrlEncoded
    private static class FinalAdditionalFields {

        @FormUrlEncodedAdditionalProperties()
        private final Map<String, String> additionalProperties = new HashMap<>();

    }

    @FormUrlEncoded
    private static class AdditionalFieldsWithoutAnnotation {

        private Map<String, String> additionalProperties;

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
