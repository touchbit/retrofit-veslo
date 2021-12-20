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

import internal.test.utils.BaseUnitTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.client.converter.typed.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@DisplayName("JavaReferenceTypeConverter.class tests")
public class JavaReferenceTypeConverterUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("Default java reference types")
    public void test1639677313953() {
        final JavaReferenceTypeConverter converter = JavaReferenceTypeConverter.INSTANCE;
        final Type[] supportedTypes = converter.getSupportedTypes();
        final List<Type> types = Arrays.asList(supportedTypes);
        assertThat(types, containsInAnyOrder(Character.class, Boolean.class, Byte.class, Integer.class, Double.class,
                Float.class, Long.class, Short.class, String.class));
        assertThat(converter.getConverterForType(Character.class), Matchers.is(CharacterConverter.INSTANCE));
        assertThat(converter.getConverterForType(Boolean.class), Matchers.is(BooleanConverter.INSTANCE));
        assertThat(converter.getConverterForType(Byte.class), Matchers.is(ByteConverter.INSTANCE));
        assertThat(converter.getConverterForType(Integer.class), Matchers.is(IntegerConverter.INSTANCE));
        assertThat(converter.getConverterForType(Double.class), Matchers.is(DoubleConverter.INSTANCE));
        assertThat(converter.getConverterForType(Float.class), is(FloatConverter.INSTANCE));
        assertThat(converter.getConverterForType(Long.class), is(LongConverter.INSTANCE));
        assertThat(converter.getConverterForType(Short.class), is(ShortConverter.INSTANCE));
        assertThat(converter.getConverterForType(String.class), is(StringConverter.INSTANCE));
    }

}