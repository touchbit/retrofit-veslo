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

package org.touchbit.retrofit.veslo.client.converter.typed;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import org.touchbit.retrofit.veslo.client.model.RawBody;
import org.touchbit.retrofit.veslo.exception.ConverterUnsupportedTypeException;

import java.io.IOException;

import static internal.test.utils.TestUtils.array;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("ConstantConditions")
@DisplayName("RawBodyConverter tests")
public class RawBodyConverterUnitTests extends BaseCoreUnitTest {

    private static final RawBodyConverter CONVERTER = RawBodyConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639674366886() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Return RequestBody if RawBody not empty")
        public void test1639674369468() throws IOException {
            final RawBody body = new RawBody("test");
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(body);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(body.string()));
        }

        @Test
        @DisplayName("Return RequestBody if RawBody.isEmptyBody() == true")
        public void test1639674371868() throws IOException {
            final RawBody body = RawBody.empty();
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(body);
            final String result = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat(result, is(body.string()));
        }

        @Test
        @DisplayName("Return null if RawBody.isNullBody() == true")
        public void test1639674374437() throws IOException {
            final RawBody body = RawBody.nullable();
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(body);
            assertThat(requestBody, nullValue());
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != RawBody")
        public void test1639674377206() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: org.touchbit.retrofit.veslo.client.model.RawBody\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639674380757() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("return RawBody if response body present")
        public void test1639674352863() throws IOException {
            final RawBody expected = new RawBody("test");
            final ResponseBody responseBody = ResponseBody.create(null, expected.string());
            final RawBody result = CONVERTER.responseBodyConverter(RawBody.class, array(), RTF).convert(responseBody);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("return RawBody if response body is empty")
        public void test1639674355703() throws IOException {
            final RawBody expected = new RawBody("");
            final ResponseBody responseBody = ResponseBody.create(null, expected.string());
            final RawBody result = CONVERTER.responseBodyConverter(RawBody.class, array(), RTF).convert(responseBody);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("return RawBody if response body == null")
        public void test1639674359219() throws IOException {
            final RawBody expected = RawBody.nullable();
            final RawBody result = CONVERTER.responseBodyConverter(RawBody.class, array(), RTF).convert(null);
            assertThat(result, is(expected));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639674362074() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(responseBody))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: org.touchbit.retrofit.veslo.client.model.RawBody\n");
        }
    }

}
