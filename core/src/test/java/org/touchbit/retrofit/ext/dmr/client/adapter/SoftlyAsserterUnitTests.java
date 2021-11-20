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

package org.touchbit.retrofit.ext.dmr.client.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
@DisplayName("SoftlyAsserted tests")
public class SoftlyAsserterUnitTests {

    @Test
    @DisplayName("#softlyAssertCall() positive")
    public void test1637299961596() {
        SoftlyAsserter softly = SoftlyAsserter.get();
        softly.softly(""::trim);
        assertThat("ResponseAsserterBase.getErrors() size", softly.getErrors().size(), is(0));
    }

    @Test
    @DisplayName("#softlyAssertCall() negative")
    public void test1637299965003() {
        SoftlyAsserter softly = SoftlyAsserter.get();
        softly.softly(() -> "".getBytes((Charset) null));
        assertThat("ResponseAsserterBase.getErrors() size", softly.getErrors().size(), is(1));
    }

}
