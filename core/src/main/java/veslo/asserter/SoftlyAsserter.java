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

import retrofit2.internal.EverythingIsNonNull;
import veslo.BriefAssertionError;
import veslo.util.ThrowableRunnable;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static veslo.constant.ParameterNameConstants.*;

public interface SoftlyAsserter extends Closeable {

    @EverythingIsNonNull
    static void softlyAsserter(Consumer<SoftlyAsserter> asserterConsumer) {
        Utils.parameterRequireNonNull(asserterConsumer, ASSERTER_CONSUMER_PARAMETER);
        try (final SoftlyAsserter softlyAsserter = get()) {
            asserterConsumer.accept(softlyAsserter);
        }
    }

    static SoftlyAsserter get() {
        return new SoftlyAsserter() {

            private boolean isIgnoreNPE = false;
            private final List<Throwable> list = new ArrayList<>();

            @Override
            @EverythingIsNonNull
            public List<Throwable> getErrors() {
                return list;
            }

            @Override
            @EverythingIsNonNull
            public void addErrors(@Nonnull List<Throwable> throwableList) {
                list.addAll(throwableList);
            }

            @Override
            public void ignoreNPE(boolean value) {
                isIgnoreNPE = value;
            }

            @Override
            public boolean isIgnoreNPE() {
                return isIgnoreNPE;
            }

        };
    }

    @EverythingIsNonNull
    List<Throwable> getErrors();

    @EverythingIsNonNull
    void addErrors(@Nonnull List<Throwable> throwableList);

    @EverythingIsNonNull
    default void addErrors(@Nonnull Throwable... throwableArgs) {
        Utils.parameterRequireNonNull(throwableArgs, "throwableArgs");
        List<Throwable> result = new ArrayList<>();
        for (Throwable throwable : throwableArgs) {
            Utils.parameterRequireNonNull(throwable, THROWABLE_PARAMETER);
            result.add(throwable);
        }
        addErrors(result);
    }

    @EverythingIsNonNull
    default SoftlyAsserter softly(@Nonnull ThrowableRunnable throwableRunnable) {
        Utils.parameterRequireNonNull(throwableRunnable, THROWABLE_RUNNABLE_PARAMETER);
        try {
            throwableRunnable.execute();
        } catch (Throwable e) {
            if (e instanceof NullPointerException && isIgnoreNPE()) {
                return this;
            }
            addErrors(e);
        }
        return this;
    }

    @Override
    default void close() {
        final List<Throwable> errors = getErrors();
        if (!errors.isEmpty()) {
            StringJoiner stringJoiner = new StringJoiner("\n\n", "", "");
            for (Throwable error : errors) {
                stringJoiner.add(error.getMessage());
            }
            final String header = "Collected the following errors:\n\n";
            String errorMessages = stringJoiner.toString();
            // Cleaning up redundant header (nested asserts)
            while (errorMessages.contains(header)) {
                errorMessages = errorMessages.replace(header, "");
            }
            errors.clear();
            throw new BriefAssertionError(header + errorMessages);
        }
    }

    void ignoreNPE(boolean value);

    boolean isIgnoreNPE();

}
