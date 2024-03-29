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

package veslo.constant;

import veslo.UtilityClassException;

/**
 * Sonar rules constants
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 22.02.2022
 */
public class SonarRuleConstants {

    /**
     * Type parameter names should comply with a naming convention
     */
    public static final String SONAR_TYPE_PARAMETER_NAMING = "java:S119";

    /**
     * Generic wildcard types should not be used in return types
     */
    public static final String SONAR_GENERIC_WILDCARD_TYPES = "java:S1452";

    /**
     * Generic exceptions should never be thrown
     */
    public static final String SONAR_GENERIC_EXCEPTIONS_THROWN = "java:S112";

    /**
     * Exception classes should be immutable
     */
    public static final String SONAR_EXCEPTION_IMMUTABLE = "java:S1165";

    /**
     * Cognitive Complexity of methods should not be too high
     */
    public static final String SONAR_COGNITIVE_COMPLEXITY = "java:S3776";

    /**
     * Utility class. Forbidden instantiation.
     */
    private SonarRuleConstants() {
        throw new UtilityClassException();
    }

}
