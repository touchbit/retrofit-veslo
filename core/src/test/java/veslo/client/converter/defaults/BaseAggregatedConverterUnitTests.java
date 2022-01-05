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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.converter.api.ExtensionConverter.RequestBodyConverter;
import veslo.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import veslo.client.converter.typed.BooleanConverter;
import veslo.client.converter.typed.IntegerConverter;
import veslo.client.converter.typed.StringConverter;

import java.lang.reflect.Type;

import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ConfusingArgumentToVarargsMethod", "ConstantConditions"})
@DisplayName("BaseAggregatedConverter.class tests")
public class BaseAggregatedConverterUnitTests extends BaseCoreUnitTest {

    private static BaseAggregatedConverter getConverter() {
        return new BaseAggregatedConverter() {
        };
    }

    @Nested
    @DisplayName("#addConverter() method tests")
    public class AddConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639678209639() {
            final BaseAggregatedConverter converter = getConverter();
            assertNPE(() -> converter.addConverter(null, String.class), "converter");
            assertNPE(() -> converter.addConverter(BooleanConverter.INSTANCE, null), "types");
        }

        @Test
        @DisplayName("Add converter by type")
        public void test1639678405896() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(IntegerConverter.INSTANCE, Integer.class);
            assertThat(converter.getDefaultConverters(), aMapWithSize(1));
            assertThat(converter.getDefaultConverters().get(IntegerConverter.INSTANCE), hasSize(1));
            assertThat(converter.getDefaultConverters().get(IntegerConverter.INSTANCE), hasItem(Integer.class));
        }

    }

    @Nested
    @DisplayName("#getConverterForType() method tests")
    public class GetConverterForTypeMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639678758443() {
            final BaseAggregatedConverter converter = getConverter();
            assertNPE(() -> converter.getConverterForType(null), "type");
        }

        @Test
        @DisplayName("return null if converter not found")
        public void test1639678833995() {
            final BaseAggregatedConverter converter = getConverter();
            assertThat(converter.getConverterForType(String.class), nullValue());
            converter.addConverter(BooleanConverter.INSTANCE, Boolean.class);
            assertThat(converter.getConverterForType(Boolean.TYPE), nullValue());
            assertThat(converter.getConverterForType(Boolean.class), notNullValue());
        }

        @Test
        @DisplayName("ConvertCallException if found more than one converter")
        public void test1639679646147() {
            final BaseAggregatedConverter converter = getConverter();
            assertThat(converter.getConverterForType(String.class), nullValue());
            converter.addConverter(BooleanConverter.INSTANCE, Boolean.class);
            converter.addConverter(StringConverter.INSTANCE, Boolean.class);
            assertThrow(() -> converter.getConverterForType(Boolean.class))
                    .assertClass(ConvertCallException.class)
                    .assertMessageContains("Found more than one converters for type java.lang.Boolean:")
                    .assertMessageContains(BooleanConverter.INSTANCE.getClass().getTypeName())
                    .assertMessageContains(StringConverter.INSTANCE.getClass().getTypeName());
        }

    }

    @Nested
    @DisplayName("#getSupportedTypes() method tests")
    public class GetSupportedTypesMethodTests {

        @Test
        @DisplayName("By default return empty array")
        public void test1639679028917() {
            final BaseAggregatedConverter converter = getConverter();
            assertThat(converter.getSupportedTypes(), emptyArray());
        }

        @Test
        @DisplayName("Get added types for different converters")
        public void test1639679229254() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(BooleanConverter.INSTANCE, Boolean.class);
            converter.addConverter(StringConverter.INSTANCE, String.class);
            final Type[] supportedTypes = converter.getSupportedTypes();
            assertThat(supportedTypes, arrayContainingInAnyOrder(Boolean.class, String.class));
        }

    }

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639680219464() {
            final BaseAggregatedConverter converter = getConverter();
            assertNPE(() -> converter.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> converter.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> converter.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> converter.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Get converter by registered type")
        public void test1639680266781() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(StringConverter.INSTANCE, String.class);
            final RequestBodyConverter result = converter.requestBodyConverter(String.class, AA, AA, RTF);
            assertThat(result, notNullValue());
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if converter not found")
        public void test1639680932478() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(StringConverter.INSTANCE, String.class, Integer.class);
            assertThrow(() -> converter.requestBodyConverter(Boolean.class, AA, AA, RTF))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageContains("Unsupported type for converter " +
                            "veslo.client.converter.defaults.BaseAggregatedConverterUnitTests$1\n" +
                            "Received: java.lang.Boolean\n" +
                            "Expected: ")
                    .assertMessageContains("java.lang.String")
                    .assertMessageContains("java.lang.Integer");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639681096539() {
            final BaseAggregatedConverter converter = getConverter();
            assertNPE(() -> converter.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> converter.responseBodyConverter(BOOLEAN_C, null, RTF), "methodAnnotations");
            assertNPE(() -> converter.responseBodyConverter(BOOLEAN_C, AA, null), "retrofit");
        }

        @Test
        @DisplayName("Get converter by registered type")
        public void test1639681141882() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(StringConverter.INSTANCE, String.class);
            final ResponseBodyConverter<?> result = converter.responseBodyConverter(String.class, AA, RTF);
            assertThat(result, notNullValue());
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if converter not found")
        public void test1639681172886() {
            final BaseAggregatedConverter converter = getConverter();
            converter.addConverter(StringConverter.INSTANCE, String.class, Integer.class);
            assertThrow(() -> converter.responseBodyConverter(Boolean.class, AA, RTF))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageContains("Unsupported type for converter " +
                            "veslo.client.converter.defaults.BaseAggregatedConverterUnitTests$1\n" +
                            "Received: java.lang.Boolean\n" +
                            "Expected: ")
                    .assertMessageContains("java.lang.String")
                    .assertMessageContains("java.lang.Integer");
        }


    }

}
