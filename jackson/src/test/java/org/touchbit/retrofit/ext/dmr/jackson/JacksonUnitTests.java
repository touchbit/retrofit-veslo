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

package org.touchbit.retrofit.ext.dmr.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import internal.test.utils.OkHttpUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.jackson.cli.model.ErrorDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static okhttp3.ResponseBody.create;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.BODY_NULL_VALUE;
import static org.touchbit.retrofit.ext.dmr.jackson.JacksonConverter.JSON_NULL_VALUE;

@SuppressWarnings("ALL")
@DisplayName("Jackson extension unit tests")
public class JacksonUnitTests {

    private static final RequestBodyConverter REQUEST_CONVERTER =
            new JacksonConverter<>().requestBodyConverter(null, null, null, null);

    @Test
    @DisplayName("JacksonDualConverterFactory constructor")
    public void test1637547165245() {
        final JacksonDualConverterFactory jacksonDualConverterFactory = new JacksonDualConverterFactory();
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeRequestConverters =
                jacksonDualConverterFactory.getMimeRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME request converters", mimeRequestConverters.size(), is(4));
        final Map<ContentType, ? extends ExtensionConverter<?>> mimeResponseConverters =
                jacksonDualConverterFactory.getMimeResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("MIME response converters", mimeResponseConverters.size(), is(4));
        final Map<? extends Class<?>, ? extends ExtensionConverter<?>> rawRequestConverters =
                jacksonDualConverterFactory.getRawRequestConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw request converters", rawRequestConverters.size(), is(0));
        final Map<? extends Class<?>, ? extends ExtensionConverter<?>> rawResponseConverters =
                jacksonDualConverterFactory.getRawResponseConverters().entrySet().stream()
                        .filter(e -> e.getValue() instanceof JacksonConverter)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat("Raw response converters", rawResponseConverters.size(), is(0));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert DTO")
    public void test1637548419338() {
        ErrorDTO errorDTO = new ErrorDTO().setCode(100).setMessage("test1637548419338");
        final RequestBody requestBody = REQUEST_CONVERTER.convert(errorDTO);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("ErrorDTO json", result, is("{\n  \"code\" : 100,\n  \"message\" : \"test1637548419338\"\n}"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert Map")
    public void test1637548760804() {
        Map<String, Object> body = new HashMap<>();
        body.put("method", "test1637548760804");
        final RequestBody requestBody = REQUEST_CONVERTER.convert(body);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Map json", result, is("{\n  \"method\" : \"test1637548760804\"\n}"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert List")
    public void test1637548951112() {
        Map<String, Object> inner = new HashMap<>();
        inner.put("method", "test1637548951112");
        List<Object> body = new ArrayList<>();
        body.add(inner);
        final RequestBody requestBody = REQUEST_CONVERTER.convert(body);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("List json", result, is("[ {\n  \"method\" : \"test1637548951112\"\n} ]"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert String")
    public void test1637549076590() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert("test1637549076590");
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("String json", result, is("\"test1637549076590\""));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert Integer")
    public void test1637549114760() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert(1);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Integer json", result, is("1"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert Double")
    public void test1637549157310() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert(16.3);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Double json", result, is("16.3"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert Boolean")
    public void test1637549208269() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert(true);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("Boolean json", result, is("true"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert null (JSON_NULL_VALUE)")
    public void test1637549282466() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert(JSON_NULL_VALUE);
        final String result = OkHttpUtils.requestBodyToString(requestBody);
        assertThat("null json", result, is("null"));
    }

    @Test
    @DisplayName("Jackson RequestBodyConverter.convert() successful convert null (BODY_NULL_VALUE)")
    public void test1637551063508() {
        final RequestBody requestBody = REQUEST_CONVERTER.convert(BODY_NULL_VALUE);
        assertThat("RequestBody", requestBody, nullValue());
    }

    @Test
    @DisplayName("Throw an exception if object not convertible")
    public void test1637549903950() {
        assertThrow(() -> REQUEST_CONVERTER.convert(new Object()))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Body not convertible to JSON. Body class java.lang.Object")
                .assertCause(cause -> cause
                        .assertClass(InvalidDefinitionException.class)
                        .assertMessageIs("No serializer found for class java.lang.Object " +
                                "and no properties discovered to create BeanSerializer " +
                                "(to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)"));
    }

    @Test
    @DisplayName("Throw an exception if parameter 'body'=null")
    public void test1637550512653() {
        assertThrow(() -> REQUEST_CONVERTER.convert(null)).assertNPE("body");
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to DTO")
    public void test1637551944533() {
        final ResponseBody body = create(null, "{\n  \"code\" : 100,\n  \"message\" : \"test1637551944533\"\n}");
        final ErrorDTO actual = responseBodyConverter(ErrorDTO.class).convert(body);
        assertThat("ErrorDTO", actual, notNullValue());
        assertThat("ErrorDTO.code", actual.getCode(), is(100));
        assertThat("ErrorDTO.message", actual.getMessage(), is("test1637551944533"));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to Map")
    public void test1637552426833() {
        final ResponseBody body = create(null, "{\n  \"code\" : 100,\n  \"message\" : \"test1637552426833\"\n}");
        final Map<String, Object> actual = responseBodyConverter(Map.class).convert(body);
        assertThat("Map", actual, notNullValue());
        assertThat("Map.code", actual.get("code"), is(100));
        assertThat("Map.message", actual.get("message"), is("test1637552426833"));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to List")
    public void test1637552486160() {
        final ResponseBody body = create(null, "[{\n  \"code\" : 100,\n  \"message\" : \"test1637552486160\"\n}]");
        final List<Map<String, Object>> actual = responseBodyConverter(List.class).convert(body);
        assertThat("List", actual, notNullValue());
        assertThat("List", actual.size(), is(1));
        assertThat("List.Map", actual.get(0), notNullValue());
        assertThat("List.Map.code", actual.get(0).get("code"), is(100));
        assertThat("List.Map.message", actual.get(0).get("message"), is("test1637552486160"));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to String")
    public void test1637552604106() {
        final ResponseBody body = create(null, "\"test1637552604106\"");
        final String actual = responseBodyConverter(String.class).convert(body);
        assertThat("String", actual, is("test1637552604106"));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to Integer")
    public void test1637552610914() {
        final ResponseBody body = create(null, "111");
        final Integer actual = responseBodyConverter(Integer.class).convert(body);
        assertThat("Integer", actual, is(111));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to Double")
    public void test1637552620768() {
        final ResponseBody body = create(null, "111.11");
        final Double actual = responseBodyConverter(Double.class).convert(body);
        assertThat("Double", actual, is(111.11));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to boolean (false)")
    public void test1637552783259() {
        final ResponseBody body = create(null, "false");
        final boolean actual = responseBodyConverter(boolean.class).convert(body);
        assertThat("boolean", actual, is(false));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to boolean (false (0))")
    public void test1637552834115() {
        final ResponseBody body = create(null, "0");
        final boolean actual = responseBodyConverter(boolean.class).convert(body);
        assertThat("boolean", actual, is(false));
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to null (ResponseBody = null)")
    public void test1637552975505() {
        final ErrorDTO actual = responseBodyConverter(ErrorDTO.class).convert(null);
        assertThat("ErrorDTO", actual, nullValue());
    }

    @Test
    @DisplayName("Jackson ResponseBodyConverter.convert() successful convert to null (content length = 0)")
    public void test1637553021315() {
        final ResponseBody body = create(null, "");
        final ErrorDTO actual = responseBodyConverter(ErrorDTO.class).convert(body);
        assertThat("ErrorDTO", actual, nullValue());
    }

    @Test
    @DisplayName("Throw an exception if ResponseBody.body.bytes not readable")
    public void test1637553128954() throws Exception {
        final ResponseBody body = mock(ResponseBody.class);
        when(body.contentLength()).thenReturn(Long.MAX_VALUE);
        when(body.bytes()).thenCallRealMethod();
        assertThrow(() -> responseBodyConverter(ErrorDTO.class).convert(body))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Unable to read response body. See cause below.")
                .assertCause(cause -> cause
                        .assertClass(IOException.class)
                        .assertMessageIs("Cannot buffer entire body for content length: 9223372036854775807"));
    }

    @Test
    @DisplayName("Throw an exception if DTO type not supported")
    public void test1637553447153() {
        final ResponseBody body = create(null, "body");
        assertThrow(() -> responseBodyConverter(Object.class).convert(body))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("Json body not convertible to class java.lang.Object")
                .assertCause(cause -> cause
                        .assertClass(JsonParseException.class)
                        .assertMessageContains("Unrecognized token 'body': ",
                                "expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')"));
    }

    private <C> ResponseBodyConverter<C> responseBodyConverter(Class<C> type) {
        return new JacksonConverter<C>().responseBodyConverter(type, null, null);
    }

}
