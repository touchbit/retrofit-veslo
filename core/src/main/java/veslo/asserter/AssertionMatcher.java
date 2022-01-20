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

import veslo.BriefAssertionError;
import veslo.UtilityClassException;

import java.util.Objects;

/**
 * Build-in assertion matcher
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public class AssertionMatcher {

    /**
     * @param reason   - assertion description
     * @param actual   - actual object
     * @param expected - expected object
     * @param <M>      object generic type
     * @throws BriefAssertionError if actual not equals expected
     */
    public static <M> void is(String reason, M actual, M expected) {
        if (!Objects.equals(actual, expected)) {
            throw new BriefAssertionError(reason + "\n" +
                    "Expected: is  " + expected + "\n" +
                    "  Actual: was " + actual);
        }
    }

    /**
     * @param reason      - assertion description
     * @param actual      - actual int value
     * @param expectedMin - expected min value
     * @param expectedMax - expected max value
     * @throws BriefAssertionError if actual not in range
     */
    public static void inRange(String reason, int actual, int expectedMin, int expectedMax) {
        if (actual < expectedMin || actual > expectedMax) {
            throw new BriefAssertionError(reason + "\n" +
                    "Expected: in range " + expectedMin + "..." + expectedMax + "\n" +
                    "  Actual: was " + actual);
        }
    }

    /**
     * @param reason - assertion description
     * @param actual - actual object
     * @throws BriefAssertionError if actual is null
     */
    public static void isNotNull(String reason, Object actual) {
        if (actual == null) {
            throw new BriefAssertionError(reason + "\n" +
                    "Expected: is not null\n" +
                    "  Actual: null");
        }
    }

    /**
     * @param reason - assertion description
     * @param actual - actual object
     * @throws BriefAssertionError if actual not null
     */
    public static void isNull(String reason, Object actual) {
        if (actual != null) {
            throw new BriefAssertionError(reason + "\n" +
                    "Expected: is null\n" +
                    "  Actual: " + actual);
        }
    }

    /**
     * Utility class
     */
    private AssertionMatcher() {
        throw new UtilityClassException();
    }

}
