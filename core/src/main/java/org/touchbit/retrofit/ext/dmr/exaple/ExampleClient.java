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

package org.touchbit.retrofit.ext.dmr.exaple;

import org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import retrofit2.http.Body;

import static org.touchbit.retrofit.ext.dmr.asserter.AssertionMatcher.*;

public interface ExampleClient {

    DualResponse<SucDTO, ErrDTO> exampleApiCall(@Body Object body);

    class ErrDTO {

        public Integer code;

        public ErrDTO(Integer code) {
            this.code = code;
        }

        public void assertDTO(ErrDTO expected) {
            try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
                asserter.softly(() -> is("ErrDTO.code", this.code, expected.code));
            }
        }

        public void assertConsistency() {
            try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
                asserter.softly(() -> isNotNull("SucDTO.code", this.code));
                asserter.softly(() -> inRange("SucDTO.code range", this.code, 0, 1000));
            }
        }

    }

    class SucDTO {

        public String msg;

        public SucDTO(String msg) {
            this.msg = msg;
        }

        public void assertDTO(SucDTO expected) {
            try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
                asserter.softly(() -> is("SucDTO.message", this.msg, expected.msg));
            }
        }

        public void assertConsistency() {
            try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
                asserter.softly(() -> isNotNull("SucDTO.message", this.msg));
                asserter.softly(() -> inRange("SucDTO.message length", this.msg.length(), 1, 255));
            }
        }

    }

}
