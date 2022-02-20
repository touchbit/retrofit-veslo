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

package internal.test.utils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestUtils {

    @SafeVarargs
    public static <C> C[] array(C... items) {
        return items;
    }

    @SafeVarargs
    public static <C> List<C> listOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <C> Set<C> setOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toSet());
    }

    public static Type getGenericReturnTypeForMethod(Class<?> aClass, String methodName) {
        try {
            return aClass.getDeclaredMethod(methodName).getGenericReturnType();
        } catch (Exception e) {
            throw new CorruptedTestException(e);
        }
    }

}
