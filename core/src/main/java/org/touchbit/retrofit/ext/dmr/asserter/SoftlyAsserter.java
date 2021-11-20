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

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public interface SoftlyAsserter extends Closeable {

    @Nonnull
    List<Throwable> getErrors();

    void addError(@Nonnull Throwable throwable);

    void addErrors(@Nonnull List<Throwable> throwableList);

    default void softly(@Nonnull ThrowableRunnable throwableRunnable) {
        try {
            Objects.requireNonNull(throwableRunnable, "Parameter 'throwableRunnable' is required");
            throwableRunnable.execute();
        } catch (Throwable e) {
            addError(e);
        }
    }

    @Override
    default void close() {
        final List<Throwable> errors = getErrors();
        if (!errors.isEmpty()) {
            StringJoiner result = new StringJoiner("\n\n", "The response contains the following errors:\n", "");
            for (Throwable error : errors) {
                result.add(error.getMessage());
            }
            errors.clear();
            throw new AssertionError(result);
        }
    }

    static SoftlyAsserter get() {
        return new SoftlyAsserter() {

            private final List<Throwable> list = new ArrayList<>();

            @Nonnull
            @Override
            public List<Throwable> getErrors() {
                return list;
            }

            @Override
            public void addError(@Nonnull Throwable throwable) {
                list.add(throwable);
            }

            @Override
            public void addErrors(@Nonnull List<Throwable> throwableList) {
                list.addAll(throwableList);
            }
        };
    }

}
