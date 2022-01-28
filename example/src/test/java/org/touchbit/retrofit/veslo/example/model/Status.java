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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import veslo.asserter.HeadersAsserter;
import veslo.asserter.ResponseAsserter;
import veslo.asserter.SoftlyAsserter;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ModelApiResponse
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class Status extends AssertableModel<Status> {

    public static final Status CODE_1 = new Status().code(1).type("error");
    public static final Status CODE_404 = new Status().code(404).type("unknown");
    public static final Status CODE_200 = new Status().code(200).type("unknown");

    private Object code = null;
    private Object type = null;
    private Object message = null;

    public static void assert200(ResponseAsserter<Status, ?, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(200).assertSucBody(actual -> actual.match(expected));
    }

    public static void assert400(ResponseAsserter<?, Status, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(400).assertErrBody(actual -> actual.match(expected));
    }

    public static void assert401(ResponseAsserter<?, Status, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(401).assertErrBody(actual -> actual.match(expected));
    }

    public static void assert404(ResponseAsserter<?, Status, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(404).assertErrBody(actual -> actual.match(expected));
    }

    @Override
    public Status match(SoftlyAsserter asserter, String ignore, Status expected) {
        asserter.softly(() -> assertThat(this.code()).as("Status.code").isEqualTo(expected.code()));
        asserter.softly(() -> assertThat(this.type()).as("Status.type").isEqualTo(expected.type()));
        asserter.softly(() -> assertThat(this.message()).as("Status.message").isEqualTo(expected.message()));
        return this;
    }

}
