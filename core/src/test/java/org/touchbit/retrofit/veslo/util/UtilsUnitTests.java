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

package org.touchbit.retrofit.veslo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static internal.test.utils.RetrofitTestUtils.getCallMethodAnnotations;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions", "ObviousNullCheck"})
@DisplayName("Utils class tests")
public class UtilsUnitTests extends BaseCoreUnitTest {

    @Test
    @DisplayName("Utility Class")
    public void test1639065947880() {
        assertUtilityClassException(Utils.class);
    }

    @Nested
    @DisplayName("#parameterRequireNonNull() method tests")
    public class ParameterRequireNonNullMethodTests {

        @Test
        @DisplayName("Positive case")
        public void test1639982550520() {
            Utils.parameterRequireNonNull("", "");
        }

        @Test
        @DisplayName("Positive case (parameterName == null)")
        public void test1639982736881() {
            Utils.parameterRequireNonNull("", null);
        }


        @Test
        @DisplayName("Negative case (parameter = null)")
        public void test1639982637469() {
            assertThrow(() -> Utils.parameterRequireNonNull(null, "test"))
                    .assertClass(NullPointerException.class)
                    .assertMessageIs("Parameter 'test' is required and cannot be null.");
        }

    }

    @Nested
    @DisplayName("#getAnnotation() method tests")
    public class GetAnnotationMethodTests {

        @Test
        @DisplayName("#getAnnotation() return annotation if present")
        public void test1639065947905() {
            final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
            final POST annotation = Utils.getAnnotation(callMethodAnnotations, POST.class);
            assertThat("", annotation, notNullValue());
        }

        @Test
        @DisplayName("#getAnnotation() return null if annotation not present")
        public void test1639065947913() {
            final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
            final GET annotation = Utils.getAnnotation(callMethodAnnotations, GET.class);
            assertThat("", annotation, nullValue());
        }

        @Test
        @DisplayName("#getAnnotation() return null if annotations == null")
        public void test1639982397454() {
            final GET annotation = Utils.getAnnotation(null, GET.class);
            assertThat("", annotation, nullValue());
        }

        @Test
        @DisplayName("#getAnnotation() return null if expected == null")
        public void test1639982459727() {
            final Annotation[] callMethodAnnotations = getCallMethodAnnotations();
            final GET annotation = Utils.getAnnotation(callMethodAnnotations, null);
            assertThat("", annotation, nullValue());
        }

    }

    @Nested
    @DisplayName("#toObjectByteArray() method tests")
    public class ToObjectByteArrayMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639983028549() {
            assertNPE(() -> Utils.toObjectByteArray((String) null), "data");
            assertNPE(() -> Utils.toObjectByteArray((byte[]) null), "bytes");
        }

        @Test
        @DisplayName("Convert primitive byte array to object byte array")
        public void test1640471613990() {
            byte[] expectedBytes = "test".getBytes();
            assertThat("", Utils.toObjectByteArray(expectedBytes), is(expectedBytes));
        }

        @Test
        @DisplayName("Convert String to object byte array")
        public void test1639983119675() {
            String expectedBytes = "test";
            assertThat("", Utils.toObjectByteArray(expectedBytes), is(expectedBytes.getBytes()));
        }


    }

    @Nested
    @DisplayName("#toPrimitiveByteArray() method tests")
    public class ToPrimitiveByteArrayMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639982911245() {
            assertNPE(() -> Utils.toPrimitiveByteArray(null), "bytes");
        }


        @Test
        @DisplayName("Convert object byte array to primitive byte array")
        public void test1639065947886() {
            String expected = "test";
            final Byte[] objBytes = Utils.toObjectByteArray(expected);
            assertThat("", Utils.toPrimitiveByteArray(objBytes), is(expected.getBytes()));
        }

        @Test
        @DisplayName("Convert object (byte[]) to primitive byte array")
        public void test1639983181453() {
            String expected = "test";
            final Object obj = expected.getBytes();
            assertThat("", Utils.toPrimitiveByteArray(obj), is(expected.getBytes()));
        }

        @Test
        @DisplayName("Convert object (Byte[]) to primitive byte array")
        public void test1639983245385() {
            String expected = "test";
            final Object obj = Utils.toObjectByteArray(expected);
            assertThat("", Utils.toPrimitiveByteArray(obj), is(expected.getBytes()));
        }

        @Test
        @DisplayName("IllegalArgumentException if object not byte array")
        public void test1639983273626() {
            assertThrow(() -> Utils.toPrimitiveByteArray(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type: class java.lang.Object\n" +
                            "Expected: java.lang.Byte[] or byte[]");
        }

    }

    @Nested
    @DisplayName("#getTypeName() method tests")
    public class GetTypeNameMethodTests {

        @Test
        @DisplayName("return 'null' type name if type == null")
        public void test1639983415115() {
            Type type = null;
            assertThat(Utils.getTypeName(type), is("null"));
        }

        @Test
        @DisplayName("return 'null' type name if object == null")
        public void test1639983501722() {
            Object object = null;
            assertThat(Utils.getTypeName(object), is("null"));
        }

        @Test
        @DisplayName("return type name if type == byte array")
        public void test1639983534683() {
            Type type = byte[].class;
            assertThat(Utils.getTypeName(type), is("byte[]"));
        }

        @Test
        @DisplayName("return type name if type == byte array")
        public void test1639983601725() {
            Object object = new Byte[]{};
            assertThat(Utils.getTypeName(object), is("java.lang.Byte[]"));
        }

    }

    @Nested
    @DisplayName("#arrayToPrettyString() method tests")
    public class ArrayToPrettyStringMethodTests {

        @Test
        @DisplayName("Array to multiline string list")
        public void test1639983717594() {
            final String result = Utils.arrayToPrettyString(new Object[]{"foo", null, "bar"});
            assertThat(result, is("\n" +
                    "  foo\n" +
                    "  bar"));
        }

        @Test
        @DisplayName("return empty string if objects == null")
        public void test1639983877383() {
            final String result = Utils.arrayToPrettyString(null);
            assertThat(result, emptyString());
        }

        @Test
        @DisplayName("return empty string if objects == []")
        public void test1639983905357() {
            final String result = Utils.arrayToPrettyString(new Object[]{});
            assertThat(result, emptyString());
        }

    }

}
