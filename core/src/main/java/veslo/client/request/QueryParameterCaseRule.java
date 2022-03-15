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

import veslo.util.CaseUtils;

/**
 * see constant descriptions
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public enum QueryParameterCaseRule {

    CAMEL_CASE,
    KEBAB_CASE,
    SNAKE_CASE,
    DOT_CASE,
    PASCAL_CASE,
    ;

    public String format(String raw) {
        if (this == QueryParameterCaseRule.DOT_CASE) {
            return CaseUtils.toDotCase(raw);
        } else if (this == QueryParameterCaseRule.CAMEL_CASE) {
            return CaseUtils.toCamelCase(raw);
        } else if (this == QueryParameterCaseRule.KEBAB_CASE) {
            return CaseUtils.toKebabCase(raw);
        } else if (this == QueryParameterCaseRule.SNAKE_CASE) {
            return CaseUtils.toSnakeCase(raw);
        } else {
            return CaseUtils.toPascalCase(raw);
        }
    }

}
