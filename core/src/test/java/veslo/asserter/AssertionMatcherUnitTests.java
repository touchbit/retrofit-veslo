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

package veslo.asserter;

import internal.test.utils.asserter.ThrowableAsserter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.BriefAssertionError;

@SuppressWarnings({"ConstantConditions"})
@DisplayName("AssertionMatcher.class unit tests")
public class AssertionMatcherUnitTests extends BaseCoreUnitTest {

    @Test
    @DisplayName("Constructor")
    public void test1639582491708() {
        ThrowableAsserter.assertUtilityClassException(AssertionMatcher.class);
    }

    @Test
    @DisplayName("#is() positive")
    public void test1639582398323() {
        AssertionMatcher.is("reason", "test", "test");
    }

    @Test
    @DisplayName("#is() negative")
    public void test1639582425697() {
        assertThrow(() -> AssertionMatcher.is("reason", "test", "TEST"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("reason\nExpected: is  TEST\n  Actual: was test");
    }

    @Test
    @DisplayName("#inRange() positive (min)")
    public void test1639582433960() {
        AssertionMatcher.inRange("reason", 0, 0, 2);
    }

    @Test
    @DisplayName("#inRange() positive (max)")
    public void test1639582780400() {
        AssertionMatcher.inRange("reason", 2, 0, 2);
    }


    @Test
    @DisplayName("#inRange() negative (min)")
    public void test1639582436802() {
        assertThrow(() -> AssertionMatcher.inRange("reason", 0, 1, 2))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("reason\nExpected: in range 1...2\n  Actual: was 0");
    }

    @Test
    @DisplayName("#inRange() negative (max)")
    public void test1639582764327() {
        assertThrow(() -> AssertionMatcher.inRange("reason", 3, 1, 2))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("reason\nExpected: in range 1...2\n  Actual: was 3");
    }

    @Test
    @DisplayName("#isNotNull() positive")
    public void test1639582442660() {
        AssertionMatcher.isNotNull("reason", "test");
    }

    @Test
    @DisplayName("#isNotNull() negative")
    public void test1639582444955() {
        assertThrow(() -> AssertionMatcher.isNotNull("reason", null))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("reason\nExpected: is not null\n  Actual: null");
    }

    @Test
    @DisplayName("#isNull() positive")
    public void test1639582450380() {
        AssertionMatcher.isNull("reason", null);
    }

    @Test
    @DisplayName("#isNull() negative")
    public void test1639582452324() {
        assertThrow(() -> AssertionMatcher.isNull("reason", "test"))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("reason\nExpected: is null\n  Actual: test");
    }

}
