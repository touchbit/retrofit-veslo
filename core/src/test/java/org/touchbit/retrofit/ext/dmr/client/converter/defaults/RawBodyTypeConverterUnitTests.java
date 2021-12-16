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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ByteArrayConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.FileConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.RawBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ResourceFileConverter;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@DisplayName("RawBodyTypeConverter.class tests")
public class RawBodyTypeConverterUnitTests extends BaseCoreUnitTest {

    @Test
    @DisplayName("Default java primitive types")
    public void test1639677317550() {
        final RawBodyTypeConverter converter = RawBodyTypeConverter.INSTANCE;
        final Type[] supportedTypes = converter.getSupportedTypes();
        final List<Type> types = Arrays.asList(supportedTypes);
        assertThat(types, containsInAnyOrder(RawBody.class, Byte[].class, byte[].class, File.class, ResourceFile.class));
        assertThat(converter.getConverterForType(RawBody.class), is(RawBodyConverter.INSTANCE));
        assertThat(converter.getConverterForType(Byte[].class), is(ByteArrayConverter.INSTANCE));
        assertThat(converter.getConverterForType(byte[].class), is(ByteArrayConverter.INSTANCE));
        assertThat(converter.getConverterForType(File.class), is(FileConverter.INSTANCE));
        assertThat(converter.getConverterForType(ResourceFile.class), is(ResourceFileConverter.INSTANCE));
    }

}
