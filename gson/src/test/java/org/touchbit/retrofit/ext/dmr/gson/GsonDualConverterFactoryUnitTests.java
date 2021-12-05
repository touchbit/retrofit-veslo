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

package org.touchbit.retrofit.ext.dmr.gson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("GsonDualConverterFactory class tests")
public class GsonDualConverterFactoryUnitTests {

    @Test
    @DisplayName("JacksonDualConverterFactory constructor")
    public void test1639065946429() {
        final GsonDualConverterFactory jacksonDualConverterFactory = new GsonDualConverterFactory();
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeRequestConverters =
                jacksonDualConverterFactory.getMimeRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof GsonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME request converters", mimeRequestConverters.size(), is(4));
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeResponseConverters =
                jacksonDualConverterFactory.getMimeResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof GsonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME response converters", mimeResponseConverters.size(), is(4));
        final Map<? extends Type, ? extends ExtensionConverter<?>> rawRequestConverters =
                jacksonDualConverterFactory.getRawRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof GsonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw request converters", rawRequestConverters.size(), is(0));
        final Map<? extends Type, ? extends ExtensionConverter<?>> rawResponseConverters =
                jacksonDualConverterFactory.getRawResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof GsonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw response converters", rawResponseConverters.size(), is(0));
    }

}