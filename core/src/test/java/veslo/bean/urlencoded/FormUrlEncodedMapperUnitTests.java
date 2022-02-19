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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.FormUrlEncodedMapperException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            "    Expected type - java.util.Map<java.lang.String, java.lang.String>\n");
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
                            "    Expected type - java.util.Map<java.lang.String, java.lang.String>\n");
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
                            "    Expected type - java.util.Map<java.lang.String, java.lang.String>\n");
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
            final Map<String, String> result = MAPPER.initAdditionalProperties(additionalFields);
            assertIs(result, additionalFields.additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field not initiated")
        public void test1645292357593() {
            final Map<String, String> result = MAPPER.initAdditionalProperties(new AdditionalFields());
            assertIs(result, new HashMap<>());
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map (null) if field not present")
        public void test1645292405815() {
            final Map<String, String> result = MAPPER.initAdditionalProperties(new EmptyModel());
            assertIsNull(result);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field initiated (final)")
        public void test1645292517696() {
            final FinalAdditionalFields additionalFields = new FinalAdditionalFields();
            additionalFields.additionalProperties.put("1", "2");
            final Map<String, String> result = MAPPER.initAdditionalProperties(additionalFields);
            assertIs(result, additionalFields.additionalProperties);
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
