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

import internal.test.utils.BaseUnitTest;
import internal.test.utils.CorruptedTestException;
import internal.test.utils.OkHttpUtils;
import okhttp3.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.BODY_NULL_VALUE;

@SuppressWarnings({"rawtypes", "ConstantConditions"})
@DisplayName("JavaTypeConverterBase abstract class tests")
public class JavaTypeConverterBaseUnitTests extends BaseUnitTest {

    public static final JavaTypeConverterBase CONVERTER = new JavaTypeConverterBase() {
        @Override
        @EverythingIsNonNull
        public ResponseBodyConverter responseBodyConverter(Type ignore1, Annotation[] ignore2, Retrofit ignore3) {
            throw new CorruptedTestException("Unusable method call");
        }
    };
    private static final RequestBodyConverter REQUEST_CONVERTER = CONVERTER.requestBodyConverter(OBJ_T, AA, AA, RTF);

    @Nested
    @DisplayName(".requestBodyConverter(Type, Annotation[], Annotation[], Retrofit) method")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1638717993279() {
            assertThrow(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF)).assertNPE("type");
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_T, null, AA, RTF)).assertNPE("parameterAnnotations");
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_T, AA, null, RTF)).assertNPE("methodAnnotations");
            assertThrow(() -> CONVERTER.requestBodyConverter(OBJ_T, AA, AA, null)).assertNPE("retrofit");
        }

        @Nested
        @DisplayName(".convert(Object) method")
        public class ConvertMethodTests {
            @Test
            @DisplayName("return RequestBody if body present")
            public void test1638616072076() throws IOException {
                final RequestBody result = REQUEST_CONVERTER.convert("test1638616072076");
                final String body = OkHttpUtils.requestBodyToString(result);
                assertThat("", body, is("test1638616072076"));
            }

            @Test
            @DisplayName("return null if body = 'BODY_NULL_VALUE' (const)")
            public void test1638616333838() throws IOException {
                final RequestBody result = REQUEST_CONVERTER.convert(BODY_NULL_VALUE);
                assertThat("", result, nullValue());
            }

            @Test
            @DisplayName("#requestBodyConverter() NPE if body = null")
            public void test1638616412496() {
                assertThrow(() -> REQUEST_CONVERTER.convert(null)).assertNPE("body");
            }
        }

    }

}
