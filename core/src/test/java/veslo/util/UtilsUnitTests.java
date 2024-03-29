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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import retrofit2.http.GET;
import retrofit2.http.POST;
import veslo.BaseCoreUnitTest;
import veslo.RuntimeIOException;
import veslo.client.response.DualResponse;
import veslo.client.response.IDualResponse;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static internal.test.utils.RetrofitTestUtils.getCallMethodAnnotations;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"ConstantConditions", "ObviousNullCheck"})
@DisplayName("Utils class tests")
public class UtilsUnitTests extends BaseCoreUnitTest {

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

    @Nested
    @DisplayName("#readResourceFile() method tests")
    public class ReadResourceFileMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645210107069() {
            assertNPE(() -> Utils.readResourceFile(null), "path");
            assertNPE(() -> Utils.readResourceFile(null, StandardCharsets.UTF_8), "path");
            assertNPE(() -> Utils.readResourceFile("", null), "charset");
        }

        @Test
        @DisplayName("Successfully read resource file")
        public void test1645210277950() {
            final String result = Utils.readResourceFile("test/data/Notes_utf_8.txt");
            assertNotNull(result);
        }

        @Test
        @DisplayName("RuntimeIOException throws if file not exists")
        public void test1645210340598() {
            assertThrow(() -> Utils.readResourceFile("test1645210340598"))
                    .assertClass(RuntimeIOException.class)
                    .assertMessageIs("Resource file not readable: test1645210340598");
        }

        @Test
        @DisplayName("RuntimeIOException throws if invalid charset")
        public void test1645537062375() {
            final Charset mock = mock(Charset.class);
            assertThrow(() -> Utils.readResourceFile("RequestConvertersInfo.txt", mock))
                    .assertClass(RuntimeIOException.class)
                    .assertMessageIs("Resource file not readable: RequestConvertersInfo.txt");
        }

    }

    @Nested
    @DisplayName("#readFile() method tests")
    public class ReadFileMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645210767344() {
            assertNPE(() -> Utils.readFile((String) null), "path");
            assertNPE(() -> Utils.readFile((String) null, StandardCharsets.UTF_8), "path");
            assertNPE(() -> Utils.readFile("", null), "charset");
            assertNPE(() -> Utils.readFile((File) null), "file");
            assertNPE(() -> Utils.readFile((File) null, StandardCharsets.UTF_8), "file");
            assertNPE(() -> Utils.readFile(new File("."), null), "charset");
        }

        @Test
        @DisplayName("Successfully read file by path")
        public void test1645210835344() {
            final String result = Utils.readFile("src/test/resources/test/data/Notes_utf_8.txt");
            assertNotNull(result);
        }

        @Test
        @DisplayName("Successfully read file ")
        public void test1645210949413() {
            final File file = new File("src/test/resources/test/data/Notes_utf_8.txt");
            final String result = Utils.readFile(file);
            assertNotNull(result);
        }

        @Test
        @DisplayName("RuntimeIOException throws if file not exists")
        public void test1645210859030() {
            assertThrow(() -> Utils.readFile("test1645210859030"))
                    .assertClass(RuntimeIOException.class)
                    .assertMessageIs("File not readable: test1645210859030");
        }

    }

    @Nested
    @DisplayName("#isIDualResponse() method tests")
    public class IsIDualResponseMethodTests {

        @Test
        @DisplayName("Return true if type = IDualResponse<String, String>")
        public void test1645536401744() {
            assertTrue(Utils.isIDualResponse(ResponseClass.getInterfaceType()));
        }

        @Test
        @DisplayName("Return true if type = DualResponse<String, String>")
        public void test1645536811903() {
            assertTrue(Utils.isIDualResponse(ResponseClass.getClassType()));
        }

        @Test
        @DisplayName("Return false if type = null")
        public void test1645536360752() {
            assertFalse(Utils.isIDualResponse(null));
        }

        @Test
        @DisplayName("Return false if type = IDualResponse")
        public void test1645536849477() {
            assertFalse(Utils.isIDualResponse(ResponseClass.getRawInterfaceType()));
        }

        @Test
        @DisplayName("Return false if type = DualResponse")
        public void test1645536860616() {
            assertFalse(Utils.isIDualResponse(ResponseClass.getRawClassType()));
        }

    }

    @SuppressWarnings("rawtypes")
    private static class ResponseClass {

        IDualResponse<String, String> interfaceType;
        IDualResponse rawInterfaceType;
        DualResponse<String, String> classType;
        DualResponse rawClassType;

        static Type getInterfaceType() {
            return wrap(() -> ResponseClass.class.getDeclaredField("interfaceType").getGenericType());
        }

        static Type getRawInterfaceType() {
            return wrap(() -> ResponseClass.class.getDeclaredField("rawInterfaceType").getGenericType());
        }

        static Type getClassType() {
            return wrap(() -> ResponseClass.class.getDeclaredField("classType").getGenericType());
        }

        static Type getRawClassType() {
            return wrap(() -> ResponseClass.class.getDeclaredField("rawClassType").getGenericType());
        }

    }

}
