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

package veslo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static veslo.constant.SonarRuleConstants.SONAR_TYPE_PARAMETER_NAMING;

/**
 * Jackson model additional properties base class
 * Allows you to handle the case when the response from the server contains extra fields without throwing an error when
 * converting (unlike Gson).
 * Contains a method for checking the absence of extra fields {@link #assertNoAdditionalProperties()}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 17.01.2022
 */
@SuppressWarnings({"unused", "unchecked", SONAR_TYPE_PARAMETER_NAMING})
public abstract class JacksonModelAdditionalProperties<DTO> {

    /**
     * additional properties map
     */
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return additional properties map
     */
    @JsonAnyGetter
    public Map<String, Object> additionalProperties() {
        return additionalProperties;
    }

    /**
     * @param name  - additional property name
     * @param value - additional property value
     * @return this
     */
    @JsonAnySetter
    public DTO additionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
        return (DTO) this;
    }

    /**
     * Checks for extra fields in the model
     *
     * @return this
     */
    @JsonIgnore
    public DTO assertNoAdditionalProperties() {
        if (!additionalProperties().isEmpty()) {
            DTO dto = (DTO) this;
            final String name = this.getClass().getSimpleName();
            throw new ContractViolationException("The presence of extra fields in the model: " + name + "\n" +
                    "Expected: no extra fields\n" +
                    "  Actual: " + additionalProperties() + "\n");
        }
        return (DTO) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JacksonModelAdditionalProperties<?> that = (JacksonModelAdditionalProperties<?>) o;
        return Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return additionalProperties.hashCode();
    }

}
