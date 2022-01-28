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

package org.touchbit.retrofit.veslo.example.model.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.retrofit.veslo.example.model.AssertableModel;
import veslo.asserter.SoftlyAsserter;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * User
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class User extends AssertableModel<User> {

    private Long id = null;
    private String username = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String password = null;
    private String phone = null;
    private Integer userStatus = null;

    @Override
    public User match(SoftlyAsserter asserter, String parentName, User expected) {
        asserter.softly(() -> assertThat(this.id()).as(getName(parentName, "user.id")).isPositive());
        asserter.softly(() -> assertThat(this.username()).as(getName(parentName, "user.username")).isEqualTo(expected.username()));
        asserter.softly(() -> assertThat(this.firstName()).as(getName(parentName, "user.firstName")).isEqualTo(expected.firstName()));
        asserter.softly(() -> assertThat(this.lastName()).as(getName(parentName, "user.lastName")).isEqualTo(expected.lastName()));
        asserter.softly(() -> assertThat(this.email()).as(getName(parentName, "user.email")).isEqualTo(expected.email()));
        asserter.softly(() -> assertThat(this.password()).as(getName(parentName, "user.password")).isEqualTo(expected.password()));
        asserter.softly(() -> assertThat(this.phone()).as(getName(parentName, "user.phone")).isEqualTo(expected.phone()));
        asserter.softly(() -> assertThat(this.userStatus()).as(getName(parentName, "user.userStatus")).isEqualTo(expected.userStatus()));
        return this;
    }

}
