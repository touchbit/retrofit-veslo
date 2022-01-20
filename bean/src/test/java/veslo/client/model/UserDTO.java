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

package veslo.client.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import veslo.BeanValidationModel;
import veslo.JacksonModelAdditionalProperties;

import java.util.function.Function;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class UserDTO extends JacksonModelAdditionalProperties<UserDTO> implements BeanValidationModel<UserDTO> {

    private @NotNull @Size(min = 1, max = 10) String firstName;

    private @NotNull @Size(min = 1, max = 10) String lastName;

    private @NotNull @Valid UserPassport passport;

    public String firstName() {
        return firstName;
    }

    public UserDTO firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String lastName() {
        return lastName;
    }

    public UserDTO lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserPassport passport() {
        return passport;
    }

    public UserDTO passport(UserPassport passport) {
        this.passport = passport;
        return this;
    }

    public UserDTO passport(Function<UserPassport, UserPassport> passportFunction) {
        if (this.passport == null) {
            this.passport = new UserPassport();
        }
        this.passport = passportFunction.apply(this.passport);
        return this;
    }

}
