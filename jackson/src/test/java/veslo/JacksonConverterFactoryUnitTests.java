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

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.header.ContentType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;

@DisplayName("JacksonDualConverterFactory.class unit tests")
public class JacksonConverterFactoryUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("Required parameters")
    public void test1645006625893() {
        assertThrow(() -> new JacksonConverterFactory((Logger) null)).assertNPE("logger");
        assertThrow(() -> new JacksonConverterFactory((ExtensionConverter<Object>) null)).assertNPE("converter");
    }

    @Test
    @DisplayName("JacksonDualConverterFactory default constructors")
    public void test1639065954656() {
        final JacksonConverterFactory converterFactory = new JacksonConverterFactory();
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeRequestConverters =
                converterFactory.getMimeRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME request converters", mimeRequestConverters.size(), is(4));
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeResponseConverters =
                converterFactory.getMimeResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME response converters", mimeResponseConverters.size(), is(4));
        final Map<? extends Type, ? extends ExtensionConverter<?>> rawRequestConverters =
                converterFactory.getRawRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw request converters", rawRequestConverters.size(), is(0));
        final Map<? extends Type, ? extends ExtensionConverter<?>> rawResponseConverters =
                converterFactory.getRawResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw response converters", rawResponseConverters.size(), is(0));
        final Map<? extends Type, ? extends ExtensionConverter<?>> aModelRequestConverters =
                converterFactory.getModelAnnotationRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Annotated model converters", aModelRequestConverters.size(), is(1));
        final Map<? extends Type, ? extends ExtensionConverter<?>> aModelResponseConverters =
                converterFactory.getModelAnnotationResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Annotated model converters", aModelResponseConverters.size(), is(1));
    }

}
