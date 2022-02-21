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

package veslo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.client.FormUrlEncodedClient;
import veslo.client.LoggedMockInterceptor;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.response.DualResponse;
import veslo.client.urlencoded.FormUrlEncodedModel;

import java.util.HashMap;

import static internal.test.utils.TestUtils.listOf;
import static org.hamcrest.Matchers.is;

public class FormUrlEncodedFuncTests extends BaseFuncTest {

    private static final FormUrlEncodedModel FORM_MODEL = new FormUrlEncodedModel()
            .listStringField(listOf("1", "2"))
            .listIntegerField(listOf(1, 2))
            .integerField(1)
            .stringField("тест")
            .additionalProperties(new HashMap<String, Object>() {{
                put("ap1", listOf("foo", "bar"));
                put("ap2", "foobar");
            }});

    private static final FormUrlEncodedClient CLIENT = buildClient(
            FormUrlEncodedClient.class,
            new LoggedMockInterceptor(),
            new UniversalCallAdapterFactory(),
            new ExtensionConverterFactory());

    @Test
    @DisplayName("Successful marshaling/unmarshalling FormUrlEncoded model")
    public void test1645462732510() {
        CLIENT.urlEncoded(200, FORM_MODEL)
                .assertResponse(ra -> ra.assertHttpStatusCodeIs(200)
                        .assertSucBody((asserter, act) -> asserter
                                .softly(() -> assertThat("Model.missed", act.missed(), is(FORM_MODEL.missed())))
                                .softly(() -> assertThat("Model.stringField", act.stringField(), is(FORM_MODEL.stringField())))
                                .softly(() -> assertThat("Model.integerField", act.integerField(), is(FORM_MODEL.integerField())))
                                .softly(() -> assertThat("Model.stringArrayField", act.stringArrayField(), is(FORM_MODEL.stringArrayField())))
                                .softly(() -> assertThat("Model.integerArrayField", act.integerArrayField(), is(FORM_MODEL.integerArrayField())))
                                .softly(() -> assertThat("Model.listStringField", act.listStringField(), is(FORM_MODEL.listStringField())))
                                .softly(() -> assertThat("Model.listIntegerField", act.listIntegerField(), is(FORM_MODEL.listIntegerField())))
                                .softly(() -> assertThat("Model.additionalProperties", act.additionalProperties(), is(FORM_MODEL.additionalProperties())))));
    }

}
