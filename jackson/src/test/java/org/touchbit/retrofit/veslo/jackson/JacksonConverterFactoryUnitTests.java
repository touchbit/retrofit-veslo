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

package org.touchbit.retrofit.veslo.jackson;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.veslo.client.header.ContentType;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.veslo.client.header.ContentTypeConstants.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@DisplayName("JacksonDualConverterFactory.class unit tests")
public class JacksonConverterFactoryUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("JacksonDualConverterFactory default constructors")
    public void test1639065954656() {
        final Logger logger = LoggerFactory.getLogger(JacksonConverterFactoryUnitTests.class);
        assertJacksonDualConverterFactory(new JacksonConverterFactory(logger), JacksonConverter.class);
        assertJacksonDualConverterFactory(new JacksonConverterFactory(), JacksonConverter.class);
        assertJacksonDualConverterFactory(new JacksonConverterFactory(new TestJacksonConverter()), TestJacksonConverter.class);
        assertThrow(() -> new JacksonConverterFactory((Logger) null)).assertNPE("logger");
        assertThrow(() -> new JacksonConverterFactory((ExtensionConverter<Object>) null)).assertNPE("jacksonConverter");
    }

    private void assertJacksonDualConverterFactory(JacksonConverterFactory factory, Class<?> expectedConverter) {
        final Set<ExtensionConverter<?>> mimeConverters = new HashSet<>(factory.getMimeRequestConverters().values());
        mimeConverters.addAll(factory.getMimeResponseConverters().values());
        assertThat(mimeConverters, hasSize(1));
        assertThat(mimeConverters, hasItem(instanceOf(expectedConverter)));
        final Set<ContentType> requestContentTypes = factory.getMimeRequestConverters().keySet();
        assertThat(requestContentTypes, hasSize(4));
        assertThat(requestContentTypes, containsInAnyOrder(APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8));
        final Set<ContentType> responseContentTypes = factory.getMimeResponseConverters().keySet();
        assertThat(responseContentTypes, hasSize(4));
        assertThat(responseContentTypes, containsInAnyOrder(APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8));
        final Set<Type> requestJavaTypes = factory.getJavaTypeRequestConverters().keySet();
        assertThat(requestJavaTypes, hasItems(Map.class, List.class));
        final Set<Type> responseJavaTypes = factory.getJavaTypeResponseConverters().keySet();
        assertThat(responseJavaTypes, hasItems(Map.class, List.class));
        assertThat(factory.getLogger(), notNullValue());
    }


    private static class TestJacksonConverter extends JacksonConverter {

    }

}
