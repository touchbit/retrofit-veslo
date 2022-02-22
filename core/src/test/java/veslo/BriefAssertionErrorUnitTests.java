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

package veslo;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static veslo.BriefAssertionError.TruncationPredicate;

@SuppressWarnings({"ThrowableNotThrown", "ConstantConditions"})
@DisplayName("BriefAssertionError.class unit tests")
public class BriefAssertionErrorUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("Required parameters (all parameters nullable)")
    public void test1645532416672() {
        new BriefAssertionError(null);
        new BriefAssertionError(null, null);
    }

    @Test
    @DisplayName("#truncateStackTrace() Required parameters")
    public void test1645533297417() {
        assertNPE(() -> BriefAssertionError.truncateStackTrace(null), "truncationPredicate");
    }

    @Test
    @DisplayName("Applying a Predicates")
    public void test1645533354268() {
        BriefAssertionError.enableStackTrace();
        assertTrue(new BriefAssertionError("1").getStackTrace().length > 1);
        BriefAssertionError.truncateStackTrace(e -> true);
        assertTrue(new BriefAssertionError("1").getStackTrace().length == 1);
        BriefAssertionError.enableStackTrace();
        assertTrue(new BriefAssertionError("1").getStackTrace().length > 1);
        BriefAssertionError.disableStackTrace();
        assertTrue(new BriefAssertionError("1").getStackTrace().length == 1);
    }

    @Nested
    @DisplayName("TruncationPredicate.class tests (predicate function)")
    public class TruncationPredicateClassTests {

        @Test
        @DisplayName("#defaultFunction() method")
        public void test1645532532641() {
            final TruncationPredicate predicate = TruncationPredicate.defaultFunction();
            // abort (Test class)
            assertTrue(predicate.test("veslo.BriefAssertionErrorUnitTests.test1645532532641()"));
            assertTrue(predicate.test("veslo.BriefAssertionErrorUnitTest.test1645532532641()"));
            assertTrue(predicate.test("veslo.TestsBriefAssertionErrorUnit.test1645532532641()"));
            assertTrue(predicate.test("veslo.TestBriefAssertionErrorUnit.test1645532532641()"));
            // continue (Base test class)
            assertFalse(predicate.test("veslo.BriefAssertionErrorUnitBaseTests.test1645532532641()"));
            assertFalse(predicate.test("veslo.BriefAssertionErrorUnitBaseTest.test1645532532641()"));
            assertFalse(predicate.test("veslo.TestsBriefAssertionErrorBaseUnit.test1645532532641()"));
            assertFalse(predicate.test("veslo.TestBriefAssertionErrorBaseUnit.test1645532532641()"));
        }

        @Test
        @DisplayName("#test() method")
        public void test1645532875698() {
            TruncationPredicate predicate = TruncationPredicate.defaultFunction();
            // abort (Test class)
            assertTrue(predicate.test((StackTraceElement) null));
            assertTrue(predicate.test(new StackTraceElement("veslo.BriefAssertionErrorUnitTests", "test1645532875698()", null, 1)));
            assertTrue(predicate.test(new StackTraceElement("veslo.BriefAssertionErrorUnitTest", "test1645532875698()", null, 1)));
            assertTrue(predicate.test(new StackTraceElement("veslo.TestsBriefAssertionErrorUnit", "test1645532875698()", null, 1)));
            assertTrue(predicate.test(new StackTraceElement("veslo.TestBriefAssertionErrorUnit", "test1645532875698()", null, 1)));
            // continue (Base test class)
            assertFalse(predicate.test(new StackTraceElement("veslo.BriefAssertionErrorUnitBaseTests", "test1645532532641()", null, 1)));
            assertFalse(predicate.test(new StackTraceElement("veslo.BriefAssertionErrorUnitBaseTest", "test1645532532641()", null, 1)));
            assertFalse(predicate.test(new StackTraceElement("veslo.TestsBriefAssertionErrorBaseUnit", "test1645532532641()", null, 1)));
            assertFalse(predicate.test(new StackTraceElement("veslo.TestBriefAssertionErrorBaseUnit", "test1645532532641()", null, 1)));
        }


    }

}
