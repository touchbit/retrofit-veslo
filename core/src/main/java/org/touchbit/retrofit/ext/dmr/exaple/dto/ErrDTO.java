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

package org.touchbit.retrofit.ext.dmr.exaple.dto;

import org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter;
import org.touchbit.retrofit.ext.dmr.util.ExcludeFromJacocoGeneratedReport;

import static org.touchbit.retrofit.ext.dmr.asserter.AssertionMatcher.*;

@ExcludeFromJacocoGeneratedReport()
public class ErrDTO implements DTO {

    public String msg;

    public ErrDTO(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void assertConsistency() {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> isNotNull("SucDTO.message", this.msg));
            asserter.softly(() -> inRange("SucDTO.message length", this.msg.length(), 1, 255));
        }
    }

    public void assertDTO(ErrDTO expected) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> is("ErrDTO.code", this.msg, expected.msg));
        }
    }

    @Override
    public String toString() {
        return "ErrDTO{" +
                "msg='" + msg + '\'' +
                '}';
    }

}
