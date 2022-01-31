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

package veslo.example.dto;

import veslo.asserter.AssertionMatcher;
import veslo.asserter.SoftlyAsserter;
import veslo.util.ExcludeFromJacocoGeneratedReport;

import static veslo.asserter.AssertionMatcher.is;

@ExcludeFromJacocoGeneratedReport()
public class SucDTO implements DTO {

    public String msg;

    public SucDTO(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void assertConsistency() {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> AssertionMatcher.isNotNull("SucDTO.message", this.msg));
            asserter.softly(() -> AssertionMatcher.inRange("SucDTO.message length", this.msg.length(), 1, 255));
        }
    }

    public void assertDTO(SucDTO expected) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> AssertionMatcher.is("SucDTO.message", this.msg, expected.msg));
        }
    }

    @Override
    public String toString() {
        return "SucDTO{" +
                "msg='" + msg + '\'' +
                '}';
    }

}
