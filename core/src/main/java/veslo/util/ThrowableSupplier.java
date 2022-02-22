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

package veslo.util;

import static veslo.constant.SonarRuleConstants.GENERIC_EXCEPTIONS_THROWN;

/**
 * Auxiliary functional interface for Asserting
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.11.2021
 */
@FunctionalInterface
public interface ThrowableSupplier<R> {

    /**
     * @return function call result
     * @throws Throwable to catch any errors and exceptions
     */
    @SuppressWarnings(GENERIC_EXCEPTIONS_THROWN)
    R execute() throws Throwable;

}
