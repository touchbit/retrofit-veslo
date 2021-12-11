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

import com.google.gson.JsonSyntaxException;
import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.gson.model.GsonDTO;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static internal.test.utils.TestUtils.array;
import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.BODY_NULL_VALUE;
import static org.touchbit.retrofit.ext.dmr.gson.GsonConverter.JSON_NULL_VALUE;

@SuppressWarnings({"rawtypes", "ConstantConditions", "unchecked"})
@DisplayName("GsonConverter class tests")
public class GsonConverterUnitTests {

    private static final Retrofit RTF = mock(Retrofit.class);
    private static final Annotation[] AA = array();

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert DTO")
    public void test1639065946162() throws IOException {
        GsonDTO dto = new GsonDTO().setCode(123).setMessage("test1638367311822");
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("DTO json", result, is("{\n  \"code\": 123,\n  \"message\": \"test1638367311822\"\n}"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert Map")
    public void test1639065946171() throws IOException {
        Map<String, Object> dto = new HashMap<>();
        dto.put("method", "test1638367516788");
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Map json", result, is("{\n  \"method\": \"test1638367516788\"\n}"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert List")
    public void test1639065946181() throws IOException {
        Map<String, Object> inner = new HashMap<>();
        inner.put("method", "test1638367557865");
        List<Object> dto = new ArrayList<>();
        dto.add(inner);
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("List json", result, is("[\n  {\n    \"method\": \"test1638367557865\"\n  }\n]"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert String")
    public void test1639065946193() throws IOException {
        String dto = "test1638367611927";
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("String json", result, is("\"test1638367611927\""));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert Integer")
    public void test1639065946202() throws IOException {
        int dto = 1;
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Integer json", result, is("1"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert Double")
    public void test1639065946211() throws IOException {
        double dto = 16.3;
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Double json", result, is("16.3"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert Boolean")
    public void test1639065946220() throws IOException {
        boolean dto = true;
        final RequestBody requestBody = getRequestConverter(dto).convert(dto);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Boolean json", result, is("true"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert null (JSON_NULL_VALUE)")
    public void test1639065946229() throws IOException {
        final RequestBody requestBody = getRequestConverter(Object.class).convert(JSON_NULL_VALUE);
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("null json", result, is("null"));
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert null (BODY_NULL_VALUE)")
    public void test1639065946237() throws IOException {
        final RequestBody requestBody = getRequestConverter(Object.class).convert(BODY_NULL_VALUE);
        assertThat("RequestBody", requestBody, nullValue());
    }

    @Test
    @DisplayName("Gson RequestBodyConverter.convert() successful convert Object.class")
    public void test1639065946244() throws IOException {
        final RequestBody requestBody = getRequestConverter(Object.class).convert(new Object());
        final String result = OkHttpTestUtils.requestBodyToString(requestBody);
        assertThat("Any json", result, is("{}"));
    }

    @Test
    @DisplayName("Throw an exception if parameter 'body' != json")
    public void test1639065946252() {
        assertThrow(() -> getRequestConverter(Integer.class).convert(Object.class))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Body not convertible to JSON. Body type: java.lang.Class")
                .assertCause(cause -> cause
                        .assertClass(UnsupportedOperationException.class)
                        .assertMessageContains("Attempted to serialize java.lang.Class: java.lang.Object."));
    }

    @Test
    @DisplayName("Throw an exception if parameter 'body'=null")
    public void test1639065946263() {
        assertThrow(() -> getRequestConverter(Object.class).convert(null)).assertNPE("body");
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to DTO")
    public void test1639065946269() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "{\"code\":100,\"message\":\"test1638368396682\"}");
        final GsonDTO actual = getResponseConverter(GsonDTO.class).convert(body);
        assertThat("GsonDTO", actual, notNullValue());
        assertThat("GsonDTO.code", actual.getCode(), is(100));
        assertThat("GsonDTO.message", actual.getMessage(), is("test1638368396682"));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to Map")
    public void test1639065946279() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "{\"code\":100,\"message\":\"test1638368400272\"}");
        final Map<String, Object> actual = getResponseConverter(Map.class).convert(body);
        assertThat("Map", actual, notNullValue());
        assertThat("Map.code", actual.get("code"), is(100L));
        assertThat("Map.message", actual.get("message"), is("test1638368400272"));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to List")
    public void test1639065946289() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "[{\"code\":100,\"message\":\"test1638368990181\"}]");
        final List<Map<String, Object>> actual = getResponseConverter(List.class).convert(body);
        assertThat("List", actual, notNullValue());
        assertThat("List", actual.size(), is(1));
        assertThat("List.Map", actual.get(0), notNullValue());
        assertThat("List.Map.code", actual.get(0).get("code"), is(100L));
        assertThat("List.Map.message", actual.get(0).get("message"), is("test1638368990181"));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to String")
    public void test1639065946301() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "\"test1638368986867\"");
        final String actual = getResponseConverter(String.class).convert(body);
        assertThat("String", actual, is("test1638368986867"));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to Integer")
    public void test1639065946309() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "111");
        final Integer actual = getResponseConverter(Integer.class).convert(body);
        assertThat("Integer", actual, is(111));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to Double")
    public void test1639065946317() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "111.11");
        final Double actual = getResponseConverter(Double.class).convert(body);
        assertThat("Double", actual, is(111.11));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to boolean (false)")
    public void test1639065946325() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "false");
        final boolean actual = getResponseConverter(boolean.class).convert(body);
        assertThat("boolean", actual, is(false));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to boolean (false (0))")
    public void test1639065946333() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "0");
        final boolean actual = getResponseConverter(boolean.class).convert(body);
        assertThat("boolean", actual, is(false));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to Boolean (false (0))")
    public void test1639065946341() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "0");
        final boolean actual = getResponseConverter(Boolean.class).convert(body);
        assertThat("boolean", actual, is(false));
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to null (ResponseBody = null)")
    public void test1639065946349() throws IOException {
        final GsonDTO actual = getResponseConverter(GsonDTO.class).convert(null);
        assertThat("ErrorDTO", actual, nullValue());
    }

    @Test
    @DisplayName("Gson ResponseBodyConverter.convert() successful convert to null (content length = 0)")
    public void test1639065946356() throws IOException {
        final ResponseBody body = ResponseBody.create(null, "");
        final GsonDTO actual = getResponseConverter(GsonDTO.class).convert(body);
        assertThat("ErrorDTO", actual, nullValue());
    }

    @Test
    @DisplayName("Throw an exception if ResponseBody.body.bytes not readable")
    public void test1639065946364() throws Exception {
        final ResponseBody body = mock(ResponseBody.class);
        when(body.contentLength()).thenReturn(-2L);
        when(body.bytes()).thenCallRealMethod();
        when(body.string()).thenCallRealMethod();
        assertThrow(() -> getResponseConverter(GsonDTO.class).convert(body))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Unable to read response body. See cause below.")
                .assertCause(cause -> cause
                        .assertClass(NullPointerException.class));
    }

    @Test
    @DisplayName("Throw an exception if body not in json format")
    public void test1639065946378() {
        final ResponseBody body = ResponseBody.create(null, ".");
        assertThrow(() -> getResponseConverter(Map.class).convert(body))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("\nResponse body not convertible to type interface java.util.Map\n" +
                        "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")
                .assertCause(cause -> cause.assertClass(JsonSyntaxException.class));
    }

    private static RequestBodyConverter getRequestConverter(Object o) {
        return new GsonConverter().requestBodyConverter(o.getClass(), AA, AA, RTF);
    }

    private static <DTO> ResponseBodyConverter<DTO> getResponseConverter(Class<DTO> dtoClass) {
        return new GsonConverter<DTO>().responseBodyConverter(dtoClass, AA, RTF);
    }

}