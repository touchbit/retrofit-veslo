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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import veslo.asserter.SoftlyAsserter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ModelApiResponse
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class Status extends AssertableModel<Status> {

//    public static final Status a = new Status().code(200).type("unknown").message(message == null ? null : String.valueOf(message))

    @JsonProperty("code")
    private Integer code = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("message")
    private String message = null;

    public void assertStatus(SoftlyAsserter asserter, Status expected) {
        asserter.softly(() -> assertThat(this.code()).as("Status.code").isEqualTo(expected.code()));
        asserter.softly(() -> assertThat(this.type()).as("Status.type").isEqualTo(expected.type()));
        asserter.softly(() -> assertThat(this.message()).as("Status.message").isEqualTo(expected.message()));
    }

}
