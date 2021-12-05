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

import internal.test.utils.OkHttpUtils;
import internal.test.utils.asserter.ThrowableRunnable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@DisplayName("FileConverter tests")
public class FileConverterUnitTests {

    @Test
    @DisplayName("Successful conversion File->RequestBody if body instanceof File.class (exists)")
    public void test1639065950818() throws IOException {
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
    public void test1639065950831() {
        final ThrowableRunnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(null);
        assertThrow(runnable).assertNPE("body");
    }

    @Test
    @DisplayName("Error converting File->RequestBody if file not exists")
    public void test1639065950840() {
        final ThrowableRunnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new File("src/test1637544911671"));
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Request body file not exists: src/test1637544911671");
    }

    @Test
    @DisplayName("Error converting File->RequestBody if file is a directory")
    public void test1639065950851() {
        final ThrowableRunnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new File("src"));
        assertThrow(runnable)
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Request body file is not a readable file: src");
    }

    @Test
    @DisplayName("Error converting Object->RequestBody")
    public void test1639065950862() {
        final ThrowableRunnable runnable = () -> new FileConverter()
                .requestBodyConverter(null, null, null, null)
                .convert(new Object());
        assertThrow(runnable).assertClass(ConverterUnsupportedTypeException.class);
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if content length == 0 (return File)")
    public void test1639065950871() throws Exception {
        final String expected = "test1637467418220";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        final File result = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        final byte[] resultData = Files.readAllBytes(result.toPath());
        assertThat("Body", resultData, is("test1637467418220".getBytes()));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if content length > 0 (return File)")
    public void test1639065950884() throws Exception {
        final String expected = "test1637467421357";
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.bytes()).thenReturn(expected.getBytes());
        when(responseBody.contentLength()).thenReturn(Long.valueOf(expected.length()));
        final File result = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(responseBody);
        final byte[] resultData = Files.readAllBytes(result.toPath());
        assertThat("Body", resultData, is("test1637467421357".getBytes()));
    }

    @Test
    @DisplayName("Successful conversion ResponseBody->File if body == null (return null)")
    public void test1639065950898() throws IOException {
        final File body = new FileConverter()
                .responseBodyConverter(null, null, null)
                .convert(null);
        assertThat("Body", body, nullValue());
    }

}