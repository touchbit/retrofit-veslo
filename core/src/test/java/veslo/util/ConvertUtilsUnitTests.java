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

package veslo.util;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.client.header.ContentType;
import veslo.client.response.DualResponse;
import veslo.client.response.IDualResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import static internal.test.utils.RetrofitTestUtils.getCallMethodAnnotations;
import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static internal.test.utils.asserter.ThrowableAsserter.assertUtilityClassException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@DisplayName("ConverterUtils tests")
public class ConvertUtilsUnitTests {

    @Test
    @DisplayName("Is util class")
    public void test1639065947965() {
        assertUtilityClassException(ConvertUtils.class);
    }

    @Test
    @DisplayName("#isIDualResponse() & #toPrimitiveByteArray() positive")
    public void test1639065947971() {
        final ParameterizedType parameterizedType = mock(ParameterizedType.class);
        when(parameterizedType.getRawType()).thenReturn(Objects.class);
        assertThat("", ConvertUtils.isIDualResponse(IDualResponse.class), is(false));
        assertThat("", ConvertUtils.isIDualResponse(parameterizedType), is(false));

        final DualResponse dualResponse = new DualResponse(null, null, null, null, null);
        when(parameterizedType.getRawType()).thenReturn(dualResponse.getClass());
        assertThat("", ConvertUtils.isIDualResponse(parameterizedType), is(true));
        assertThat("", ConvertUtils.isIDualResponse(dualResponse.getClass().getGenericSuperclass()), is(true));
    }

    @Test
    @DisplayName("#isIDualResponse() & #toPrimitiveByteArray() negative")
    public void test1639065947985() {
        assertThrow(() -> ConvertUtils.isIDualResponse(null)).assertNPE("type");
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if parameter 'methodAnnotations' = null -> return empty Headers")
    public void test1639065947991() {
        final Headers act = ConvertUtils.getAnnotationHeaders(null);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if parameter 'methodAnnotations' = [] -> return empty Headers")
    public void test1639065947999() {
        final Headers act = ConvertUtils.getAnnotationHeaders(new Annotation[]{});
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if retrofit2.http.Headers is empty -> return empty Headers")
    public void test1639065948007() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final Headers act = ConvertUtils.getAnnotationHeaders(callMethodAnnotations);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(0));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() if retrofit2.http.Headers has header -> return empty Headers")
    public void test1639065948016() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Test: test1637492653253");
        final Headers act = ConvertUtils.getAnnotationHeaders(callMethodAnnotations);
        assertThat("", act, notNullValue());
        assertThat("", act.size(), is(1));
        assertThat("", act.get("Test"), is("test1637492653253"));
    }

    @Test
    @DisplayName("#getAnnotationHeaders() error if invalid header value")
    public void test1639065948026() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Test-test1637492653253");
        assertThrow(() -> ConvertUtils.getAnnotationHeaders(callMethodAnnotations))
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
    public void test1639065948040() {
        final MediaType mediaType = ConvertUtils.getMediaType(null);
        assertThat("", mediaType, nullValue());
    }

    @Test
    @DisplayName("#getMediaType() if retrofit2.http.Headers is empty -> return null")
    public void test1639065948047() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final MediaType mediaType = ConvertUtils.getMediaType(callMethodAnnotations);
        assertThat("", mediaType, nullValue());
    }

    @Test
    @DisplayName("#getMediaType() if retrofit2.http.Headers has Content-Type -> return MediaType obj")
    public void test1639065948055() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations("Content-Type: text/xml");
        final MediaType mediaType = ConvertUtils.getMediaType(callMethodAnnotations);
        assertThat("", mediaType, is(MediaType.get("text/xml")));
    }

    @Test
    @DisplayName("#getContentType() if parameter 'methodAnnotations' = null -> return ContentType obj")
    public void test1639065948063() {
        final ContentType contentType = ConvertUtils.getContentType((Annotation[]) null);
        assertThat("", contentType, notNullValue());
    }

    @Test
    @DisplayName("#getContentType() if parameter 'responseBody' = null -> return ContentType obj")
    public void test1639065948070() {
        final ContentType contentType = ConvertUtils.getContentType((ResponseBody) null);
        assertThat("", contentType, notNullValue());
    }

    @Test
    @DisplayName("#getContentType() if parameter 'responseBody' != null -> return ContentType obj")
    public void test1639987020216() {
        final Response response = OkHttpTestUtils.getResponse("test", 200, MediaType.get("foo/bar"));
        final ContentType contentType = ConvertUtils.getContentType(response.body());
        assertThat("", contentType, is(new ContentType("foo", "bar", "utf-8")));
    }


}