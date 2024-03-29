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

package veslo.client.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;
import veslo.JacksonModelAdditionalProperties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@SuppressWarnings("unused")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class UserPassport extends JacksonModelAdditionalProperties<UserDTO> {

    private @Pattern(regexp = "^[0-9]{4}$") String series;

    private @Pattern(regexp = "^[0-9]{6}$") String number;

    public String series() {
        return series;
    }

    public UserPassport series(String series) {
        this.series = series;
        return this;
    }

    public String number() {
        return number;
    }

    public UserPassport number(String number) {
        this.number = number;
        return this;
    }

}
