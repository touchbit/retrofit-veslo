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

package org.touchbit.retrofit.veslo.client.converter.defaults;

import org.touchbit.retrofit.veslo.client.converter.typed.*;

/**
 * Converter for reference java types:
 * Boolean.class, Byte.class, Char.class, Double.class, Float.class, Int.class, Long.class, Short.class, String.class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.12.2021
 */
public class JavaReferenceTypeConverter extends BaseAggregatedConverter {

    public static final JavaReferenceTypeConverter INSTANCE = new JavaReferenceTypeConverter();

    public JavaReferenceTypeConverter() {
        addConverter(CharacterConverter.INSTANCE, Character.class);
        addConverter(BooleanConverter.INSTANCE, Boolean.class);
        addConverter(ByteConverter.INSTANCE, Byte.class);
        addConverter(IntegerConverter.INSTANCE, Integer.class);
        addConverter(DoubleConverter.INSTANCE, Double.class);
        addConverter(FloatConverter.INSTANCE, Float.class);
        addConverter(LongConverter.INSTANCE, Long.class);
        addConverter(ShortConverter.INSTANCE, Short.class);
        addConverter(StringConverter.INSTANCE, String.class);
    }

}
