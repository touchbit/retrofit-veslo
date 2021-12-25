/*
 * Copyright 2021 Shaburov Oleg
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
import org.touchbit.retrofit.veslo.exception.BriefAssertionError;
import org.touchbit.retrofit.veslo.jackson.AdditionalProperties;
import org.touchbit.retrofit.veslo.jackson.JacksonConverter;
import org.touchbit.retrofit.veslo.jsr.BeanValidation;

@SuppressWarnings("unchecked")
public abstract class AssertableModel<DTO>
        extends AdditionalProperties<DTO>
        implements BeanValidation<DTO> {

    public DTO assertNoAdditionalProperties() {
        if (!additionalProperties().isEmpty()) {
            final String name = this.getClass().getSimpleName();
            throw new BriefAssertionError("The presence of extra fields in the model: " + name + "\n" +
                    "Expected: no extra fields\n" +
                    "  Actual: " + additionalProperties() + "\n");
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


}
