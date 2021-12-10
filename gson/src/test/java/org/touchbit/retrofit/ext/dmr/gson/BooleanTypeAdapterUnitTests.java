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

import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("BooleanTypeAdapter class tests")
public class BooleanTypeAdapterUnitTests {

    private static final BooleanTypeAdapter ADAPTER = new BooleanTypeAdapter();
    @Test
    @DisplayName("#deserialize() boolean")
    public void test1639065946489() {
        final JsonPrimitive primitive = new JsonPrimitive(true);
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(true));
    }

    @Test
    @DisplayName("#deserialize() Boolean")
    public void test1639065946497() {
        final JsonPrimitive primitive = new JsonPrimitive(Boolean.TRUE);
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(true));
    }

    @Test
    @DisplayName("#deserialize() string true")
    public void test1639065946505() {
        final JsonPrimitive primitive = new JsonPrimitive("true");
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(true));
    }

    @Test
    @DisplayName("#deserialize() string false")
    public void test1639065946513() {
        final JsonPrimitive primitive = new JsonPrimitive("false");
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(false));
    }

    @Test
    @DisplayName("#deserialize() UUID string (exception)")
    public void test1639065946521() {
        final String uuid = UUID.randomUUID().toString();
        final JsonPrimitive primitive = new JsonPrimitive(uuid);
        assertThrow(() -> ADAPTER.deserialize(primitive, null, null))
                .assertClass(JsonParseException.class)
                .assertMessageIs("Expected a boolean but was: '" + uuid + "'");
    }

    @Test
    @DisplayName("#deserialize() int 1 to true")
    public void test1639065946531() {
        final JsonPrimitive primitive = new JsonPrimitive(1);
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(true));
    }

    @Test
    @DisplayName("#deserialize() int 0 to false")
    public void test1639065946539() {
        final JsonPrimitive primitive = new JsonPrimitive(0);
        final Boolean deserialize = ADAPTER.deserialize(primitive, null, null);
        assertThat("", deserialize, is(false));
    }

    @Test
    @DisplayName("#deserialize() int -1 (exception)")
    public void test1639065946547() {
        final JsonPrimitive primitive = new JsonPrimitive(-1);
        assertThrow(() -> ADAPTER.deserialize(primitive, null, null))
                .assertClass(JsonParseException.class)
                .assertMessageIs("Expected a boolean but was: -1");
    }

}