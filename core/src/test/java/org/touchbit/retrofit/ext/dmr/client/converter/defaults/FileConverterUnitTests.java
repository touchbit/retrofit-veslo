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

import java.io.File;
import java.nio.file.Files;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("FileConverter tests")
public class FileConverterUnitTests {

    @Test
    @DisplayName("Successful conversion File->RequestBody if body instanceof File.class")
    public void test1637467409145() {
        final String expected = "test1637466272864";
        final File body = new File("src/test/resources/test/data/test1637466272864.txt");
        final RequestBody requestBody = new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(body);
        assertThat("RequestBody", requestBody, notNullValue());
        final String actual = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Body", actual, is(expected));
    }

    @Test
    @DisplayName("Error converting File->RequestBody if body == null")
    public void test1637467411821() {
        final Runnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertClass(NullPointerException.class).assertMessageIs("Parameter 'body' required");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1637467415631() {
        final Runnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if content length == 0 (return null)")
    public void test1637467418220() throws Exception {
        final String expected = "test1637463929423";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        final File result = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        assertThat("Body", result, nullValue());
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if content length > 0 (return File)")
    public void test1637467421357() throws Exception {
        final String expected = "test1637466286230";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        when(responseBody.contentLength()).thenReturn(Long.valueOf(expected.length()));
        final File result = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        final byte[] resultData = Files.readAllBytes(result.toPath());
        assertThat("Body", resultData, is("test1637466286230".getBytes()));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if body == null (return null)")
    public void test1637467424106() {
        final File body = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", body, nullValue());
    }

}
