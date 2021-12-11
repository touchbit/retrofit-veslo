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

package org.touchbit.retrofit.ext.dmr.client.converter.typed;

import internal.test.utils.OkHttpTestUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import java.io.IOException;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("AnyBodyConverter tests")
public class RawBodyConverterUnitTests {

    @Test
    @DisplayName("Successful conversion AnyBody->RequestBody if body instanceof AnyBody.class")
    public void test1639065950948() throws IOException {
        final RawBody expected = new RawBody("test1637432781973");
        final RequestBody requestBody = new RawBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(expected.string()));
    }

    @Test
    @DisplayName("Successful conversion AnyBody->RequestBody if body == AnyBody(null)")
    public void test1639065950960() throws IOException {
        final RawBody expected = new RawBody((byte[]) null);
        final RequestBody requestBody = new RawBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(""));
    }

    @Test
    @DisplayName("Error converting AnyBody->RequestBody if body == null")
    public void test1639065950972() {
        final ThrowableRunnable runnable = () -> new RawBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertNPE("body");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1639065950981() {
        final ThrowableRunnable runnable = () -> new RawBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->AnyBody if body present (return AnyBody)")
    public void test1639065950990() throws Exception {
        final ResponseBody responseBody = mock(ResponseBody.class);
        RawBody expected = new RawBody("test1637433847494");
        when(responseBody.bytes()).thenReturn(expected.bytes());
        final RawBody rawBody = new RawBodyConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", rawBody, is(expected));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->AnyBody if body == null (return AnyBody)")
    public void test1639065951002() throws IOException {
        RawBody expected = new RawBody((byte[]) null);
        final RawBody rawBody = new RawBodyConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", rawBody, is(expected));
    }

}