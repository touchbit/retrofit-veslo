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

import veslo.util.ExceptionBuilder;

import javax.annotation.Nullable;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.03.2022
 */
public class ReflectionException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param t       the cause
     */
    public ReflectionException(@Nullable String message, @Nullable Throwable t) {
        super(message, t);
    }

    /**
     * @return instance of {@link ExceptionBuilder}
     */
    @SuppressWarnings("java:S1452") // Generic wildcard does not affect anything
    public static ExceptionBuilder<?> builder() {
        return new ExceptionBuilder<ReflectionException>() {
            @Override
            public ReflectionException build() {
                return new ReflectionException(getMessage(), getCause());
            }
        };
    }

}
