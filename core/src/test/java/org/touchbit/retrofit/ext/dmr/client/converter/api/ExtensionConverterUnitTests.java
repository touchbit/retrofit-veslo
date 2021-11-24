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

package org.touchbit.retrofit.ext.dmr.client.converter.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ThrowableSupplier;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"rawtypes"})
@DisplayName("ExtensionConverter tests")
public class ExtensionConverterUnitTests {

    @Test
    @DisplayName("#wrap(ThrowableRunnable) positive")
    @SuppressWarnings("RedundantOperationOnEmptyContainer")
    public void test1637470086630() {
        getExtensionConverter().wrap(() -> Arrays.sort(new String[]{}));
    }

    @Test
    @DisplayName("#wrap(ThrowableRunnable) negative")
    @SuppressWarnings("ConstantConditions")
    public void test1637470359360() {
        assertThrow(() -> getExtensionConverter().wrap(() -> Arrays.sort((byte[]) null)))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("An error occurred while converting. See the reasons below.")
                .assertCause(cause -> cause.assertClass(NullPointerException.class));
    }

    @Test
    @DisplayName("#wrap(ThrowableSupplier) positive")
    @SuppressWarnings("unchecked")
    public void test1637470477629() {
        String data = "test1637470477629";
        final Object result = getExtensionConverter().wrap((ThrowableSupplier) data::toLowerCase);
        assertThat("", result, is(data));
    }

    @Test
    @DisplayName("#wrap(ThrowableSupplier) negative")
    @SuppressWarnings({"unchecked"})
    public void test1637470484396() {
        assertThrow(() -> getExtensionConverter().wrap(() -> Integer.parseInt("test1637470484396")))
                .assertClass(ConvertCallException.class)
                .assertMessageIs("An error occurred while converting. See the reasons below.")
                .assertCause(cause -> cause
                        .assertClass(NumberFormatException.class)
                        .assertMessageIs("For input string: \"test1637470484396\""));
    }

    private ExtensionConverter getExtensionConverter() {
        return new ExtensionConverter() {
            @Override
            @EverythingIsNonNull
            public RequestBodyConverter requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
                return null;
            }

            @Override
            @EverythingIsNonNull
            public ResponseBodyConverter responseBodyConverter(Type type, Annotation[] methodAnnotations, Retrofit retrofit) {
                return null;
            }
        };
    }
}
