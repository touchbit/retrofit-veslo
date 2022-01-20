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

package org.touchbit.retrofit.veslo.example.client.transport.querymap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import veslo.client.request.QueryMapParameterRules;
import veslo.client.request.ReflectQueryMap;

import static veslo.client.request.QueryParameterCaseRule.CAMEL_CASE;
import static veslo.client.request.QueryParameterNullValueRule.RULE_IGNORE;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
// for example
@QueryMapParameterRules(nullRule = RULE_IGNORE, caseRule = CAMEL_CASE)
public class LoginUserQueryMap extends ReflectQueryMap {

    public static final LoginUserQueryMap ADMIN = new LoginUserQueryMap().username("test").password("abc123");

    private Object username;
    private Object password;

}
