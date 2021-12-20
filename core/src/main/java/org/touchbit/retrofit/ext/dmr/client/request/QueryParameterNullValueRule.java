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

package org.touchbit.retrofit.ext.dmr.client.request;

import javax.annotation.Nullable;

/**
 * see constant descriptions
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public enum QueryParameterNullValueRule {

    /**
     * constant only for default value of {@link QueryMapParameter#nullRule()}
     */
    NONE,
    /**
     * Ignore null value parameters
     */
    RULE_IGNORE,
    /**
     * replace null to {@code \x00} null marker -> {@code /api/call?foo=%00}
     */
    RULE_NULL_MARKER,
    /**
     * replace null to empty string -> {@code /api/call?foo=}
     */
    RULE_EMPTY_STRING,
    /**
     * replace null to null string -> {@code /api/call?foo=null}
     */
    RULE_NULL_STRING,
    ;

    /**
     * @param value query parameter value
     * @return {@link QueryParameterNullValueRule} or null if
     */
    @Nullable
    public static QueryParameterNullValueRule valueOf(@Nullable Object value) {
        final String strValue = String.valueOf(value);
        for (QueryParameterNullValueRule rule : values()) {
            if (rule.name().equals(strValue)) {
                return rule;
            }
        }
        return null;
    }

}
