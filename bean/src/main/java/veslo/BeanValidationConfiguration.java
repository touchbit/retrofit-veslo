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

import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static veslo.constant.ParameterNameConstants.LOG_LEVEL_PARAMETER;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 22.02.2022
 */
public class BeanValidationConfiguration {

    /**
     * @param targetLocale - hibernate validator {@link Locale}
     * @see Locale
     */
    public static void setDefaultLocale(Locale targetLocale) {
        Locale.setDefault(targetLocale);
    }

    /**
     * - {@link Level#OFF} is a special level that can be used to turn off logging.
     * - {@link Level#SEVERE} is a message level indicating a serious failure.
     * - {@link Level#WARNING} is a message level indicating a potential problem.
     * - {@link Level#INFO} is a message level for informational messages.
     * - {@link Level#CONFIG} is a message level for static configuration messages.
     * - {@link Level#FINE} is a message level providing tracing information.
     * - {@link Level#FINER} indicates a fairly detailed tracing message.
     * - {@link Level#FINEST} indicates a highly detailed tracing message.
     * - {@link Level#ALL} indicates that all messages should be logged.
     *
     * @param logLevel - hibernate validator logging level
     */
    @EverythingIsNonNull
    @SuppressWarnings("java:S4792")
    public static void setLogLevel(final Level logLevel) {
        Utils.parameterRequireNonNull(logLevel, LOG_LEVEL_PARAMETER);
        Logger.getLogger("org.hibernate.validator.internal.util").setLevel(logLevel);
    }

    /**
     * Utility class. Forbidden instantiation.
     */
    private BeanValidationConfiguration() {
        throw new UtilityClassException();
    }

}
