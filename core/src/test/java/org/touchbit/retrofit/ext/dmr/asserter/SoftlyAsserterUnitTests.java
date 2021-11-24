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

package org.touchbit.retrofit.ext.dmr.asserter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
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

    @Test
    @DisplayName("#addError(Throwable) and #addErrors(List<Throwable>)")
    public void test1637387611654() {
        SoftlyAsserter softly = SoftlyAsserter.get();
        assertThat("ResponseAsserterBase.getErrors() size", softly.getErrors().size(), is(0));
        softly.addErrors(new RuntimeException("0"), new RuntimeException("1"));
        assertThat("ResponseAsserterBase.getErrors() size", softly.getErrors().size(), is(2));
        List<Throwable> list = new ArrayList<>();
        list.add(new RuntimeException("2"));
        list.add(new RuntimeException("3"));
        softly.addErrors(list);
        assertThat("ResponseAsserterBase.getErrors() size", softly.getErrors().size(), is(4));
    }

    @Test
    @DisplayName("#softlyAsserter() NPE if asserterConsumer = null")
    public void test1637484774676() {
        assertThrow(() -> SoftlyAsserter.softlyAsserter(null)).assertNPE("asserterConsumer");
    }

    @Test
    @DisplayName("#softlyAsserter() assertion call (positive)")
    public void test1637484858588() {
        assertThrow(() -> SoftlyAsserter.softlyAsserter(asserter -> asserter.softly(() -> assertThat("", 1, is(1)))));
    }

    @Test
    @DisplayName("#softlyAsserter() assertion call (positive)")
    public void test1637484897013() {
        assertThrow(() -> SoftlyAsserter.softlyAsserter(asserter -> asserter
                .softly(() -> assertThat("test1637484897013", 1, is(2)))))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "test1637484897013\n" +
                        "Expected: is <2>\n" +
                        "     but: was <1>");
    }

    @Test
    @DisplayName("#close() Cleaning up redundant SoftlyAsserter error header")
    public void test1637485265947() {
        String errMsg = "Collected the following errors:\n\n" +
                "test1637485265947\n" +
                "Expected: is <2>\n" +
                "     but: was <1>";
        assertThrow(() -> {
            try (final SoftlyAsserter softlyAsserter = SoftlyAsserter.get()) {
                softlyAsserter.addErrors(new AssertionError(errMsg));
                softlyAsserter.addErrors(new AssertionError(errMsg));
            }
        })
                .assertClass(AssertionError.class)
                .assertMessageIs("Collected the following errors:\n" +
                        "\n" +
                        "test1637485265947\n" +
                        "Expected: is <2>\n" +
                        "     but: was <1>\n" +
                        "\n" +
                        "test1637485265947\n" +
                        "Expected: is <2>\n" +
                        "     but: was <1>");

    }

}
