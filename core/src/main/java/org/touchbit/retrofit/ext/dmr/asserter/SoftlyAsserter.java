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

import org.touchbit.retrofit.ext.dmr.util.ThrowableRunnable;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;

public interface SoftlyAsserter extends Closeable {

    @EverythingIsNonNull
    List<Throwable> getErrors();

    @EverythingIsNonNull
    void addErrors(@Nonnull List<Throwable> throwableList);

    @EverythingIsNonNull
    default void addErrors(@Nonnull Throwable... throwable) {
        addErrors(Arrays.asList(throwable));
    }

    @EverythingIsNonNull
    default SoftlyAsserter softly(@Nonnull ThrowableRunnable throwableRunnable) {
        Objects.requireNonNull(throwableRunnable, "Parameter 'throwableRunnable' is required");
        try {
            throwableRunnable.execute();
        } catch (Throwable e) {
            addErrors(e);
        }
        return this;
    }

    @EverythingIsNonNull
    static void softlyAsserter(Consumer<SoftlyAsserter> asserterConsumer) {
        Objects.requireNonNull(asserterConsumer, "Parameter 'asserterConsumer' required");
        try (final SoftlyAsserter softlyAsserter = get()) {
            asserterConsumer.accept(softlyAsserter);
        }
    }

    @Override
    default void close() {
        final List<Throwable> errors = getErrors();
        if (!errors.isEmpty()) {
            StringJoiner stringJoiner = new StringJoiner("\n\n", "", "");
            for (Throwable error : errors) {
                stringJoiner.add(error.getMessage());
            }
            errors.clear();
            final String header = "Collected the following errors:\n\n";
            // Cleaning up redundant header (nested asserts)
            final String result = stringJoiner.toString().replaceAll(header, "");
            throw new AssertionError(header + result);
        }
    }

    static SoftlyAsserter get() {
        return new SoftlyAsserter() {

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
        };
    }

}
