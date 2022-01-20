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

package veslo.client.converter.defaults;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.client.converter.typed.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@DisplayName("JavaReferenceTypeConverter.class tests")
public class JavaPrimitiveTypeConverterUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("Default java primitive types")
    public void test1639677310324() {
        final JavaPrimitiveTypeConverter converter = JavaPrimitiveTypeConverter.INSTANCE;
        final Type[] supportedTypes = converter.getSupportedTypes();
        final List<Type> types = Arrays.asList(supportedTypes);
        assertThat(types, containsInAnyOrder(Character.TYPE, Boolean.TYPE, Byte.TYPE, Integer.TYPE, Double.TYPE,
                Float.TYPE, Long.TYPE, Short.TYPE));
        assertThat(converter.getConverterForType(Character.TYPE), is(CharacterConverter.INSTANCE));
        assertThat(converter.getConverterForType(Boolean.TYPE), is(BooleanConverter.INSTANCE));
        assertThat(converter.getConverterForType(Byte.TYPE), is(ByteConverter.INSTANCE));
        assertThat(converter.getConverterForType(Integer.TYPE), is(IntegerConverter.INSTANCE));
        assertThat(converter.getConverterForType(Double.TYPE), is(DoubleConverter.INSTANCE));
        assertThat(converter.getConverterForType(Float.TYPE), is(FloatConverter.INSTANCE));
        assertThat(converter.getConverterForType(Long.TYPE), is(LongConverter.INSTANCE));
        assertThat(converter.getConverterForType(Short.TYPE), is(ShortConverter.INSTANCE));
    }

}
