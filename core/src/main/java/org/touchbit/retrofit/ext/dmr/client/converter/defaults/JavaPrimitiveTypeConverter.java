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

package org.touchbit.retrofit.ext.dmr.client.converter.defaults;

import org.touchbit.retrofit.ext.dmr.client.converter.typed.*;

/**
 * Converter for primitive java types: boolean, byte, char, double, float, int, long, short
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.12.2021
 */
public class JavaPrimitiveTypeConverter extends BaseDefaultConverter {

    public static final JavaPrimitiveTypeConverter INSTANCE = new JavaPrimitiveTypeConverter();

    public JavaPrimitiveTypeConverter() {
        addConverter(CharacterConverter.INSTANCE, Character.TYPE);
        addConverter(BooleanConverter.INSTANCE, Boolean.TYPE);
        addConverter(ByteConverter.INSTANCE, Byte.TYPE);
        addConverter(IntegerConverter.INSTANCE, Integer.TYPE);
        addConverter(DoubleConverter.INSTANCE, Double.TYPE);
        addConverter(FloatConverter.INSTANCE, Float.TYPE);
        addConverter(LongConverter.INSTANCE, Long.TYPE);
        addConverter(ShortConverter.INSTANCE, Short.TYPE);
    }

}
