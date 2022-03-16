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

package veslo.client.urlencoded;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class FormUrlEncodedModel {

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
