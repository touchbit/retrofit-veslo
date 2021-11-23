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

package org.touchbit.retrofit.ext.dmr.util;

import okhttp3.Headers;
import okhttp3.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import static internal.test.utils.RetrofitUtils.getCallMethodAnnotations;
import static internal.test.utils.ThrowableAsserter.assertThrow;
import static internal.test.utils.ThrowableAsserter.assertUtilityClassException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@DisplayName("ConverterUtils tests")
public class ConverterUtilsUnitTests {

    @Test
    @DisplayName("Is util class")
    public void test1637491041193() {
        assertUtilityClassException(ConverterUtils.class);
    }

    @Test
    @DisplayName("#isIDualResponse() & #toPrimitiveByteArray() positive")
    public void test1637491661197() {
        final ParameterizedType parameterizedType = mock(ParameterizedType.class);
        when(parameterizedType.getRawType()).thenReturn(Objects.class);
        assertThat("", ConverterUtils.isIDualResponse(IDualResponse.class), is(false));
        assertThat("", ConverterUtils.isIDualResponse(parameterizedType), is(false));

        final DualResponse dualResponse = new DualResponse(null, null, null, null, null);
        when(parameterizedType.getRawType()).thenReturn(dualResponse.getClass());
        assertThat("", ConverterUtils.isIDualResponse(parameterizedType), is(true));
        assertThat("", ConverterUtils.isIDualResponse(dualResponse.getClass().getGenericSuperclass()), is(true));
    }

    @Test
    @DisplayName("#isIDualResponse() & #toPrimitiveByteArray() negative")
    public void test1637491666552() {
        assertThrow(() -> ConverterUtils.isIDualResponse(null)).assertNPE("type");
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if parameter 'methodAnnotations' = null -> return empty Headers")
    public void test1637492306442() {
        final Headers act = ConverterUtils.getAnnotationHeaders(null);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if parameter 'methodAnnotations' = [] -> return empty Headers")
    public void test1637492444059() {
        final Headers act = ConverterUtils.getAnnotationHeaders(new Annotation[]{});
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if retrofit2.http.Headers is empty -> return empty Headers")
    public void test1637492513743() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final Headers act = ConverterUtils.getAnnotationHeaders(callMethodAnnotations);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if retrofit2.http.Headers has header -> return empty Headers")
    public void test1637492653253() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Test: test1637492653253");
        final Headers act = ConverterUtils.getAnnotationHeaders(callMethodAnnotations);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(1));
        assertThat("", act.get("Test"), is("test1637492653253"));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() error if invalid header value")
    public void test1637493139344() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Test-test1637492653253");
        assertThrow(() -> ConverterUtils.getAnnotationHeaders(callMethodAnnotations))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("" +
                        "Invalid header value.\n" +
                        "Annotation: interface retrofit2.http.Headers\n" +
                        "Header: Test-test1637492653253\n" +
                        "Expected format: Header-Name: value; parameter-name=value\n" +
                        "Example: Content-Type: text/xml; charset=utf-8");
    }

    @Test
    @DisplayName("#getMediaType() if parameter 'methodAnnotations' = null -> return null")
    public void test1637493393399() {
        final MediaType mediaType = ConverterUtils.getMediaType(null);
        assertThat("", mediaType, nullValue());
    }

    @Test
    @DisplayName("#getMediaType() if retrofit2.http.Headers is empty -> return null")
    public void test1637493411431() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final MediaType mediaType = ConverterUtils.getMediaType(callMethodAnnotations);
        assertThat("", mediaType, nullValue());
    }

    @Test
    @DisplayName("#getMediaType() if retrofit2.http.Headers has Content-Type -> return MediaType obj")
    public void test1637493421423() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Content-Type: text/xml");
        final MediaType mediaType = ConverterUtils.getMediaType(callMethodAnnotations);
        assertThat("", mediaType, is(MediaType.get("text/xml")));
    }

    @Test
    @DisplayName("#getContentType() if parameter 'methodAnnotations' = null -> return ContentType obj")
    public void test1637493703838() {
        final ContentType contentType = ConverterUtils.getContentType(null);
        assertThat("", contentType, notNullValue());
    }

}
