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
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("StringConverter tests")
public class StringConverterUnitTests {

    @Test
    @DisplayName("Successful conversion String->RequestBody if body instanceof String.class")
    public void test1637469447639() {
        final String expected = "test1637469447639";
        final RequestBody requestBody = new StringConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(expected);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(expected));
    }

    @Test
    @DisplayName("Error converting String->RequestBody if body == null")
    public void test1637469450638() {
        final Runnable runnable = () -> new StringConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertClass(NullPointerException.class).assertMessageIs("Parameter 'body' required");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1637469454092() {
        final Runnable runnable = () -> new StringConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->String if content length == 0 (return null)")
    public void test1637469456722() throws Exception {
        final String expected = "test1637469456722";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        final String result = new StringConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", result, nullValue());
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->String if content length > 0 (return String)")
    public void test1637469459518() throws Exception {
        final String expected = "test1637469459518";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        when(responseBody.contentLength()).thenReturn(Long.valueOf(expected.length()));
        final String result = new StringConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", result, is(expected));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->String if body == null (return null)")
    public void test1637469462375() {
        final String body = new StringConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", body, nullValue());
    }

}
