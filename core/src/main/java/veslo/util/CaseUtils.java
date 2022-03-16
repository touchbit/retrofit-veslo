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

package veslo.util;

import veslo.UtilityClassException;

import static java.lang.Character.*;
import static java.lang.Character.toLowerCase;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 14.03.2022
 */
@SuppressWarnings("DuplicatedCode")
public class CaseUtils {

    public static final Character DT = '.';
    public static final Character KB = '-';
    public static final Character SN = '_';

    /**
     * Utility class
     */
    private CaseUtils() {
        throw new UtilityClassException();
    }

    public static String toKebabCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
            } else if (isUpperCase(next)) {
                sb.append(KB).append(toLowerCase(next));
            } else if (next.equals(DT) || next.equals(SN)) {
                sb.append(KB);
            } else {
                sb.append(toLowerCase(next));
            }
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
            } else if (next.equals(KB) || next.equals(DT) || next.equals(SN)) {
                nextUpperCase = true;
            } else if (isUpperCase(next)) {
                sb.append(next);
            } else if (nextUpperCase) {
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
            } else if (isUpperCase(next)) {
                sb.append(next);
            } else if (next.equals(KB) || next.equals(DT) || next.equals(SN)) {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                sb.append(toUpperCase(next));
                nextUpperCase = false;
            } else {
                sb.append(toLowerCase(next));
            }
        }
        return sb.toString();
    }

    public static String toDotCase(String raw) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Character next : raw.toCharArray()) {
            if (first) {
                sb.append(toLowerCase(next));
                first = false;
            } else if (isUpperCase(next)) {
                sb.append(DT).append(toLowerCase(next));
            } else if (next.equals(SN) || next.equals(KB)) {
                sb.append(DT);
            } else {
                sb.append(toLowerCase(next));
            }
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
            } else if (isUpperCase(next)) {
                sb.append(SN).append(toLowerCase(next));
            } else if (next.equals(DT) || next.equals(KB)) {
                sb.append(SN);
            } else {
                sb.append(toLowerCase(next));
            }
        }
        return sb.toString();
    }

}
