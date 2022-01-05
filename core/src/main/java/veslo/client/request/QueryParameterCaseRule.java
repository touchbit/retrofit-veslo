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

import static java.lang.Character.*;

/**
 * see constant descriptions
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
@SuppressWarnings("DuplicatedCode")
public enum QueryParameterCaseRule {

    CAMEL_CASE,
    KEBAB_CASE,
    SNAKE_CASE,
    DOT_CASE,
    PASCAL_CASE,
    ;

    private static final Character DT = '.';
    private static final Character KB = '-';
    private static final Character SN = '_';

    public String format(String raw) {
        if (this == QueryParameterCaseRule.DOT_CASE) {
            return toDotCase(raw);
        } else if (this == QueryParameterCaseRule.CAMEL_CASE) {
            return toCamelCase(raw);
        } else if (this == QueryParameterCaseRule.KEBAB_CASE) {
            return toKebabCase(raw);
        } else if (this == QueryParameterCaseRule.SNAKE_CASE) {
            return toSnakeCase(raw);
        } else {
            return toPascalCase(raw);
        }
    }

    public static String toDotCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
                continue;
            }
            if (isUpperCase(next)) {
                sb.append(DT).append(toLowerCase(next));
                continue;
            }
            if (next.equals(SN) || next.equals(KB)) {
                sb.append(DT);
                continue;
            }
            sb.append(toLowerCase(next));
        }
        return sb.toString();
    }

    public static String toSnakeCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
                continue;
            }
            if (isUpperCase(next)) {
                sb.append(SN).append(toLowerCase(next));
                continue;
            }
            if (next.equals(DT) || next.equals(KB)) {
                sb.append(SN);
                continue;
            }
            sb.append(toLowerCase(next));
        }
        return sb.toString();
    }

    public static String toKebabCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
                continue;
            }
            if (isUpperCase(next)) {
                sb.append(KB).append(toLowerCase(next));
                continue;
            }
            if (next.equals(DT) || next.equals(SN)) {
                sb.append(KB);
                continue;
            }
            sb.append(toLowerCase(next));
        }
        return sb.toString();
    }

    public static String toCamelCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        boolean nextUpperCase = false;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
                continue;
            }
            if (next.equals(KB) || next.equals(DT) || next.equals(SN)) {
                nextUpperCase = true;
                continue;
            }
            if (isUpperCase(next)) {
                sb.append(next);
                continue;
            }
            if (nextUpperCase) {
                sb.append(toUpperCase(next));
                nextUpperCase = false;
            } else {
                sb.append(toLowerCase(next));
            }
        }
        return sb.toString();
    }

    public static String toPascalCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        boolean nextUpperCase = false;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toUpperCase(next));
                first = false;
                continue;
            }
            if (isUpperCase(next)) {
                sb.append(next);
                continue;
            }
            if (next.equals(KB) || next.equals(DT) || next.equals(SN)) {
                nextUpperCase = true;
                continue;
            }
            if (nextUpperCase) {
                sb.append(toUpperCase(next));
                nextUpperCase = false;
            } else {
                sb.append(toLowerCase(next));
            }
        }
        return sb.toString();
    }

}
