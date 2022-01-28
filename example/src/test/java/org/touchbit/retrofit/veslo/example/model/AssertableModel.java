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

package org.touchbit.retrofit.veslo.example.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import veslo.BeanValidationModel;
import veslo.JacksonConverter;
import veslo.JacksonModelAdditionalProperties;
import veslo.asserter.SoftlyAsserter;

@SuppressWarnings("unchecked")
public abstract class AssertableModel<DTO>
        extends JacksonModelAdditionalProperties<DTO>
        implements BeanValidationModel<DTO> {

    public abstract DTO match(SoftlyAsserter asserter, String parentName, DTO expected);

    public DTO match(SoftlyAsserter asserter, DTO expected) {
        return match(asserter, "", expected);
    }

    public DTO match(DTO expected) {
        return match("", expected);
    }

    public DTO match(String parentName, DTO expected) {
        try (SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.ignoreNPE(true);
            match(asserter, parentName, expected);
        }
        return (DTO) this;
    }

    public String toJsonString() {
        try {
            return JacksonConverter.INSTANCE.getRequestObjectMapper()
                    .writerFor(this.getClass())
                    .writeValueAsString(this);
        } catch (JsonProcessingException ignore) {
            return toString();
        }
    }

    protected String getName(String parentName, String name) {
        if (parentName == null || parentName.isEmpty()) {
            return name;
        }
        return parentName + "." + name;
    }

}
