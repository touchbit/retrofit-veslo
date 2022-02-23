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

package veslo;

import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static veslo.constant.ParameterNameConstants.TRUNCATION_PREDICATE_PARAMETER;
import static veslo.constant.SonarRuleConstants.SONAR_EXCEPTION_IMMUTABLE;

/**
 * {@link AssertionError} with the ability to truncate StackTrace by {@link TruncationPredicate} function.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 26.12.2021
 */
public class BriefAssertionError extends AssertionError {

    /**
     * Global truncation function applied to all {@link BriefAssertionError} instances
     *
     * @see TruncationPredicate#defaultFunction
     */
    @SuppressWarnings(SONAR_EXCEPTION_IMMUTABLE)
    private static TruncationPredicate truncate = TruncationPredicate.defaultFunction();

    /**
     * Constructs a new error with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message - the detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public BriefAssertionError(String message) {
        this(message, null);
    }

    /**
     * Constructs a new error with the specified detail message and cause.
     * Note that the detail message associated with cause is not automatically incorporated in this error's detail message.
     *
     * @param message   the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param throwable the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                  (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public BriefAssertionError(String message, Throwable throwable) {
        super(message, throwable);
        List<StackTraceElement> result = new ArrayList<>();
        for (StackTraceElement stackTraceElement : getStackTrace()) {
            result.add(stackTraceElement);
            if (truncate.test(stackTraceElement)) {
                break;
            }
        }
        setStackTrace(result.toArray(new StackTraceElement[]{}));
    }

    /**
     * The error will contain truncated StackTrace.
     *
     * @param truncationPredicate - StackTrace truncation predicate {@link TruncationPredicate}
     */
    @EverythingIsNonNull
    public static void truncateStackTrace(TruncationPredicate truncationPredicate) {
        Utils.parameterRequireNonNull(truncationPredicate, TRUNCATION_PREDICATE_PARAMETER);
        BriefAssertionError.truncate = truncationPredicate;
    }

    /**
     * The error will contain the full StackTrace.
     */
    public static void enableStackTrace() {
        BriefAssertionError.truncate = e -> false;
    }

    /**
     * The error will contain the full StackTrace.
     */
    public static void disableStackTrace() {
        BriefAssertionError.truncate = e -> true;
    }

    /**
     * Predicate to abort the formation of a StackTrace (reduction)
     */
    public interface TruncationPredicate extends Predicate<String> {

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param stackTraceElement - stacktrace element in string representation
         * @return true if the input argument matches the predicate, otherwise false
         */
        @EverythingIsNonNull
        boolean test(String stackTraceElement);

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param stackTraceElement - stacktrace element
         * @return true if the input argument matches the predicate, otherwise false
         */
        default boolean test(StackTraceElement stackTraceElement) {
            return stackTraceElement == null || test(stackTraceElement.toString());
        }

        /**
         * Will return true if {@link StackTraceElement} string representation:
         * - Does not contain "Base" and contains ".Test"
         * - Does not contain "Base" and contains "Test."
         * - Does not contain "Base" and contains ".Tests"
         * - Does not contain "Base" and contains "Tests."
         *
         * @return default function to abort the formation of a StackTrace (truncated)
         */
        @EverythingIsNonNull
        static TruncationPredicate defaultFunction() {
            return element -> (!element.contains("Base") &&
                    (element.contains(".Test") || element.contains("Test.") ||
                            element.contains(".Tests") || element.contains("Tests.")));
        }

    }

}
