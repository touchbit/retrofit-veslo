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

import internal.test.utils.OkHttpUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("AnyBodyConverter tests")
public class AnyBodyConverterUnitTests {

    @Test
    @DisplayName("Successful conversion AnyBody->RequestBody if body instanceof AnyBody.class")
    public void test1637432781973() {
        final AnyBody expected = new AnyBody("test1637432781973");
        final RequestBody requestBody = new AnyBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(expected.string()));
    }

    @Test
    @DisplayName("Successful conversion AnyBody->RequestBody if body == AnyBody(null)")
    public void test1637433657612() {
        final AnyBody expected = new AnyBody((byte[]) null);
        final RequestBody requestBody = new AnyBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(""));
    }

    @Test
    @DisplayName("Error converting AnyBody->RequestBody if body == null")
    public void test1637463921852() {
        final ThrowableRunnable runnable = () -> new AnyBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertNPE("body");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1637433815174() {
        final ThrowableRunnable runnable = () -> new AnyBodyConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->AnyBody if body present (return AnyBody)")
    public void test1637433847494() throws Exception {
        final ResponseBody responseBody = mock(ResponseBody.class);
        AnyBody expected = new AnyBody("test1637433847494");
        when(responseBody.bytes()).thenReturn(expected.bytes());
        final AnyBody anyBody = new AnyBodyConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", anyBody, is(expected));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->AnyBody if body == null (return AnyBody)")
    public void test1637434016563() {
        AnyBody expected = new AnyBody((byte[]) null);
        final AnyBody anyBody = new AnyBodyConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", anyBody, is(expected));
    }

}
