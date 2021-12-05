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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.lang.annotation.Annotation;

import static internal.test.utils.RetrofitUtils.getCallMethodAnnotations;
import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static internal.test.utils.asserter.ThrowableAsserter.assertUtilityClassException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
@DisplayName("Utils class tests")
public class UtilsUnitTests {

    @Test
    @DisplayName("Is util class")
    public void test1639065947880() {
        assertUtilityClassException(Utils.class);
    }

    @Test
    @DisplayName("#toObjectByteArray() & #toPrimitiveByteArray() positive")
    public void test1639065947886() {
        String expected = "test1637491140861";
        byte[] expectedBytes = "test1637491140861".getBytes();
        final Byte[] objBytes = Utils.toObjectByteArray(expected);
        assertThat("", objBytes, is(expectedBytes));
        assertThat("", Utils.toObjectByteArray(expectedBytes), is(expectedBytes));
        assertThat("", Utils.toPrimitiveByteArray(objBytes), is(expectedBytes));
    }

    @Test
    @DisplayName("#toObjectByteArray() & #toPrimitiveByteArray() negative")
    public void test1639065947897() {
        assertThrow(() -> Utils.toObjectByteArray((String) null)).assertNPE("data");
        assertThrow(() -> Utils.toObjectByteArray((byte[]) null)).assertNPE("bytes");
        assertThrow(() -> Utils.toPrimitiveByteArray(null)).assertNPE("bytes");
    }

    @Test
    @DisplayName("#getAnnotation() return annotation if present")
    public void test1639065947905() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final POST annotation = Utils.getAnnotation(callMethodAnnotations, POST.class);
        assertThat("", annotation, notNullValue());
    }

    @Test
    @DisplayName("#getAnnotation() return null if annotation not absent")
    public void test1639065947913() {
        final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
        final GET annotation = Utils.getAnnotation(callMethodAnnotations, GET.class);
        assertThat("", annotation, nullValue());
    }

}