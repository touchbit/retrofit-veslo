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
import internal.test.utils.ThrowableRunnable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("ByteArrayConverter tests")
public class ByteArrayConverterUnitTests {

    @Test
    @DisplayName("Successful conversion Byte[]->RequestBody if body instanceof Byte.class")
    public void test1637463917948() {
        final String expected = "test1637463917948";
        final Byte[] body = Utils.toObjectByteArray(expected);
        final RequestBody requestBody = new ByteArrayConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(body);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(expected));
    }

    @Test
    @DisplayName("Error converting Byte[]->RequestBody if body == null")
    public void test1637463921852() {
        final ThrowableRunnable runnable = () -> new ByteArrayConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertNPE("body");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1637463925672() {
        final ThrowableRunnable runnable = () -> new ByteArrayConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->Byte[] if content length == 0 then return empty byte array")
    public void test1637463929423() throws Exception {
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn("".getBytes());
        when(responseBody.contentLength()).thenReturn(0L);
        final Byte[] result = new ByteArrayConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", result, is(new Byte[]{}));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->Byte[] if content length > 0 then return byte array")
    public void test1637465032249() throws Exception {
        final String expected = "test1637465032249";
        final Byte[] body = Utils.toObjectByteArray(expected);
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        when(responseBody.contentLength()).thenReturn(Long.valueOf(expected.length()));
        final Byte[] result = new ByteArrayConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", result, is(body));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->Byte[] if body == null then return null")
    public void test1637463932624() {
        final Byte[] body = new ByteArrayConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", body, nullValue());
    }

}
