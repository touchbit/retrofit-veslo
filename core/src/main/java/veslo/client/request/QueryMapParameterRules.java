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
package veslo.client.request;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for {@link ReflectQueryMap} nullable parameters values
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 * @see QueryParameterNullValueRule
 * @see QueryParameterCaseRule
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface QueryMapParameterRules {

    /**
     * @return Rule for processing a null value of a request parameter
     */
    QueryParameterNullValueRule nullRule() default QueryParameterNullValueRule.RULE_IGNORE;

    /**
     * @return Rule for processing parameter name case format {@link QueryParameterCaseRule}
     */
    QueryParameterCaseRule caseRule() default QueryParameterCaseRule.CAMEL_CASE;

}
