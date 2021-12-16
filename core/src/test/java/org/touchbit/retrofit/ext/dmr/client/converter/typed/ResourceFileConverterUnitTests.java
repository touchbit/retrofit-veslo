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

import internal.test.utils.OkHttpTestUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;

import java.io.IOException;

import static internal.test.utils.TestUtils.array;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("ConstantConditions")
@DisplayName("ResourceFileConverter tests")
public class ResourceFileConverterUnitTests extends BaseCoreUnitTest {

    private static final ResourceFileConverter CONVERTER = ResourceFileConverter.INSTANCE;

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639675612780() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "body");
        }

        @Test
        @DisplayName("Convert ResourceFile to RequestBody")
        public void test1637468946514() throws IOException {
            final String expected = "test1637468946514";
            final ResourceFile body = new ResourceFile("test/data/test1637468946514.txt");
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, array(), array(), RTF).convert(body);
            final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat("Body", actual, is(expected));
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body != File type")
        public void test1639675616271() {
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(1))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Integer\n" +
                            "Expected: org.touchbit.retrofit.ext.dmr.client.model.ResourceFile\n");
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639675722127() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("ConvertCallException if ResponseBody present")
        public void test1639065950762() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(ResourceFile.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("It is forbidden to use the ResourceFile type to convert the response body.");
        }

        @Test
        @DisplayName("ConverterUnsupportedTypeException if body == unsupported type")
        public void test1639676718389() {
            assertThrow(() -> CONVERTER.responseBodyConverter(OBJ_C, AA, RTF).convert(null))
                    .assertClass(ConverterUnsupportedTypeException.class)
                    .assertMessageIs("Unsupported type for converter " +
                            CONVERTER.getClass().getTypeName() + "\n" +
                            "Received: java.lang.Object\n" +
                            "Expected: org.touchbit.retrofit.ext.dmr.client.model.ResourceFile\n");
        }


    }

}