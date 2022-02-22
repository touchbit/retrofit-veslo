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

package veslo;

import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static veslo.constant.ParameterNameConstants.*;

public class ConverterUnsupportedTypeException extends RuntimeException {

    @EverythingIsNonNull
    public ConverterUnsupportedTypeException(Type converter, Type actual, Type... expected) {
        super(getExceptionMessage(converter, actual, expected));
    }

    protected static String getExceptionMessage(Type converter, Type actual, Type... expected) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(actual, ACTUAL_PARAMETER);
        Utils.parameterRequireNonNull(expected, EXPECTED_PARAMETER);
        final String expectedTypes = Arrays.stream(expected)
                .filter(Objects::nonNull)
                .map(Type::getTypeName)
                .collect(Collectors.joining(" or "));
        return "Unsupported type for converter " + converter.getTypeName() + "\n" +
                "Received: " + actual.getTypeName() + "\n" +
                "Expected: " + expectedTypes + "\n";
    }

}
