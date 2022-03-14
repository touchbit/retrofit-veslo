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

package veslo.client.converter.annotated;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.OkHttpTestUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static internal.test.utils.TestUtils.arrayOf;
import static internal.test.utils.TestUtils.listOf;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("ConstantConditions")
@DisplayName("FormUrlEncodedConverter.class unit tests")
public class FormUrlEncodedConverterTests extends BaseUnitTest {

    private static final FormUrlEncodedConverter CONVERTER = FormUrlEncodedConverter.INSTANCE;
    private static final String FORM_STRING = "ap2=foobar&" +
                                              "ap1=foo&" +
                                              "ap1=%D1%82%D0%B5%D1%81%D1%82&" +
                                              "listIntegerField=1&" +
                                              "listIntegerField=2&" +
                                              "integerField=1&" +
                                              "listStringField=1&" +
                                              "listStringField=2&" +
                                              "stringField=%D1%82%D0%B5%D1%81%D1%82";
    private static final GoodModel FORM_MODEL = new GoodModel()
            .listStringField(listOf("1", "2"))
            .listIntegerField(listOf(1, 2))
            .integerField(1)
            .stringField("тест")
            .additionalProperties(new HashMap<String, Object>() {{
                put("ap1", listOf("foo", "тест"));
                put("ap2", "foobar");
            }});
    private static final ParameterizedModel<?, ?> FORM_MAP_MODEL = new ParameterizedModel<>()
            .listStringField(listOf("1", "2"))
            .listIntegerField(listOf(1, 2))
            .integerField(1)
            .stringField("тест")
            .additionalProperties(new HashMap<String, Object>() {{
                put("ap1", listOf("foo", "тест"));
                put("ap2", "foobar");
            }});

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645461557221() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert ResourceFile to RequestBody")
        public void test1645461563327() throws IOException {
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, arrayOf(), arrayOf(), RTF).convert(FORM_MODEL);
            final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat("Body", actual, is(FORM_STRING));
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645461566566() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Return model if ResponseBody present")
        public void test1645461569468() throws IOException {
            final ResponseBody responseBody = ResponseBody.create(null, FORM_STRING);
            final GoodModel act = (GoodModel) CONVERTER
                    .responseBodyConverter(GoodModel.class, AA, RTF).convert(responseBody);
            assertThat("GoodModel.missed", act.missed, is(FORM_MODEL.missed));
            assertThat("GoodModel.stringField", act.stringField, is(FORM_MODEL.stringField));
            assertThat("GoodModel.integerField", act.integerField, is(FORM_MODEL.integerField));
            assertThat("GoodModel.stringArrayField", act.stringArrayField, is(FORM_MODEL.stringArrayField));
            assertThat("GoodModel.integerArrayField", act.integerArrayField, is(FORM_MODEL.integerArrayField));
            assertThat("GoodModel.listStringField", act.listStringField, is(FORM_MODEL.listStringField));
            assertThat("GoodModel.listIntegerField", act.listIntegerField, is(FORM_MODEL.listIntegerField));
            assertThat("GoodModel.additionalProperties", act.additionalProperties, is(FORM_MODEL.additionalProperties));
        }

        @Test
        @DisplayName("Return ParameterizedType model if ResponseBody present")
        public void test1645462309972() throws Exception {
            final ResponseBody responseBody = ResponseBody.create(null, FORM_STRING);
            final Type type = ParameterizedModel.class.getDeclaredField("CONSTANT").getGenericType();
            final ParameterizedModel<?, ?> act = (ParameterizedModel<?, ?>) CONVERTER
                    .responseBodyConverter(type, AA, RTF).convert(responseBody);
            assertThat("GoodModel.missed", act.missed, is(FORM_MAP_MODEL.missed));
            assertThat("GoodModel.stringField", act.stringField, is(FORM_MAP_MODEL.stringField));
            assertThat("GoodModel.integerField", act.integerField, is(FORM_MAP_MODEL.integerField));
            assertThat("GoodModel.stringArrayField", act.stringArrayField, is(FORM_MAP_MODEL.stringArrayField));
            assertThat("GoodModel.integerArrayField", act.integerArrayField, is(FORM_MAP_MODEL.integerArrayField));
            assertThat("GoodModel.listStringField", act.listStringField, is(FORM_MAP_MODEL.listStringField));
            assertThat("GoodModel.listIntegerField", act.listIntegerField, is(FORM_MAP_MODEL.listIntegerField));
            assertThat("GoodModel.additionalProperties", act.additionalProperties, is(FORM_MAP_MODEL.additionalProperties));
        }

        @Test
        @DisplayName("Return null if ResponseBody null")
        public void test1645462158752() throws IOException {
            final GoodModel act = (GoodModel) CONVERTER.responseBodyConverter(GoodModel.class, AA, RTF).convert(null);
            assertIsNull(act);
        }

    }

    @Setter
    @Accessors(chain = true, fluent = true)
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

    @Setter
    @Accessors(chain = true, fluent = true)
    @FormUrlEncoded
    public static class ParameterizedModel<A, B> extends HashMap<A, B> {

        public static final ParameterizedModel<String, String> CONSTANT = null;

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

}
