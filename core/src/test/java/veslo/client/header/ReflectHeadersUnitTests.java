/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo.client.header;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.anEmptyMap;

@DisplayName("ReflectHeaders.class unit tests")
public class ReflectHeadersUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("entrySet")
    public void test1647367358579() {
        final RequestHeadersWithoutAnnotation headers = new RequestHeadersWithoutAnnotation();
        headers.xRequestId = "foo";
        final Map<String, String> result = headers.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertIs(result.get("x-request-id"), "foo");
    }

    @Test
    @DisplayName("toString")
    public void test1647367627565() {
        final RequestHeadersWithoutAnnotation headers = new RequestHeadersWithoutAnnotation();
        headers.xRequestId = "foo";
        headers.put("bar", "car");
        assertIs(headers.toString(), "x-request-id: foo\nbar: car");
    }

    @Nested
    @DisplayName("#readHeadersFields() method tests")
    public class ReadHeadersFieldsMethodTests {

        @Test
        @DisplayName("return empty map if field is null")
        public void test1647367125068() {
            final RequestHeadersWithoutAnnotation headers = new RequestHeadersWithoutAnnotation();
            headers.xRequestId = null;
            final Map<String, String> stringStringMap = headers.readHeadersFields();
            assertThat(stringStringMap.toString(), stringStringMap, anEmptyMap());
        }

        @Test
        @DisplayName("return value from non-annotated field")
        public void test1647366971057() {
            final RequestHeadersWithoutAnnotation headers = new RequestHeadersWithoutAnnotation();
            headers.xRequestId = "foo";
            final Map<String, String> stringStringMap = headers.readHeadersFields();
            assertIs(stringStringMap.get("x-request-id"), "foo");
        }

        @Test
        @DisplayName("return value from annotated field")
        public void test1647367061663() {
            final RequestHeadersWithAnnotation headers = new RequestHeadersWithAnnotation();
            headers.xRequestId = "foo";
            final Map<String, String> stringStringMap = headers.readHeadersFields();
            assertIs(stringStringMap.get(RequestHeadersWithAnnotation.HEADER_NAME), "foo");
        }

        @Test
        @DisplayName("return value from annotated field (blank header name)")
        public void test1647367088340() {
            final RequestHeadersWithBlankAnnotation headers = new RequestHeadersWithBlankAnnotation();
            headers.xRequestId = "foo";
            final Map<String, String> stringStringMap = headers.readHeadersFields();
            assertIs(stringStringMap.get("x-request-id"), "foo");
        }

    }

    public static class RequestHeadersWithoutAnnotation extends ReflectHeaders {

        private String xRequestId = UUID.randomUUID().toString();

    }

    public static class RequestHeadersWithAnnotation extends ReflectHeaders {

        private static final String HEADER_NAME = "request-id";
        @HeaderKey(HEADER_NAME)
        private String xRequestId = UUID.randomUUID().toString();

    }

    public static class RequestHeadersWithBlankAnnotation extends ReflectHeaders {

        @HeaderKey("                     ")
        private String xRequestId = UUID.randomUUID().toString();

    }

}
