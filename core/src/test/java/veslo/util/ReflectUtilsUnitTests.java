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

package veslo.util;

import assist.Private;
import assist.Public;
import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.ReflectionException;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;

@SuppressWarnings("unused")
@DisplayName("ReflectUtils.class unit tests")
public class ReflectUtilsUnitTests extends BaseUnitTest {

    @Nested
    @DisplayName("#getAllSerializableFields() method tests")
    public class GetAllSerializableFieldsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1647356412550() {
            assertNPE(() -> ReflectUtils.getAllSerializableFields(null), "object");
            assertNPE(() -> ReflectUtils.getAllSerializableFields(new Object(), (Class<?>[]) null), "excludeClasses");
        }

        @Test
        @DisplayName("return empty list if object does not have fields")
        public void test1647356520889() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new NoFieldsClass());
            assertThat(list, empty());
        }

        @Test
        @DisplayName("return not-empty list if object has fields")
        public void test1647356782050() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new WithFieldClass());
            assertThat(list, hasSize(1));
            assertThat(list.get(0).getName(), is("foo"));
        }

        @Test
        @DisplayName("return empty list if object has constant field")
        public void test1647356857760() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new WithConstantFieldClass());
            assertThat(list, empty());
        }

        @Test
        @DisplayName("return empty list if object has transient field")
        public void test1647357099675() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new WithTransientFieldClass());
            assertThat(list, empty());
        }


        @Test
        @DisplayName("return not-empty list if object extends from Map")
        public void test1647356886521() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new ClassExtendsFromMap());
            assertThat(list, not(empty()));
        }

        @Test
        @DisplayName("return empty list if object extends from Map and exclude Map classes")
        public void test1647356963547() {
            final List<Field> list = ReflectUtils
                    .getAllSerializableFields(new ClassExtendsFromMap(), HashMap.class, AbstractMap.class);
            assertThat(list, empty());
        }

    }

    @Nested
    @DisplayName("#readFieldValue() method tests")
    public class ReadFieldValueMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1647357181713() {
            final List<Field> list = ReflectUtils.getAllSerializableFields(new WithFieldClass());
            assertNPE(() -> ReflectUtils.readFieldValue(null, list.get(0)), "object");
            assertNPE(() -> ReflectUtils.readFieldValue(new Object(), null), "field");
        }

        @Test
        @DisplayName("object")
        public void test1647357285540() {
            final WithFieldClass object = new WithFieldClass();
            object.foo = "test";
            final List<Field> list = ReflectUtils.getAllSerializableFields(object);
            final Object value = ReflectUtils.readFieldValue(object, list.get(0));
            assertIs(value, "test");
        }

        @Test
        @DisplayName("ReflectionException: field not readable")
        public void test1647357344603() {
            final WithFieldClass object = new WithFieldClass();
            object.foo = "test";
            final List<Field> list = ReflectUtils.getAllSerializableFields(object);
            assertThrow(() -> ReflectUtils.readFieldValue(new Object(), list.get(0)))
                    .assertClass(ReflectionException.class)
                    .assertMessageIs("\n  Unable to raed value from object field.\n" +
                                     "    Object: java.lang.Object\n" +
                                     "    Field: private String foo;\n" +
                                     "    Error cause:\n" +
                                     "     - IllegalArgumentException: Cannot locate field foo on class java.lang.Object\n");
        }

    }

    @Nested
    @DisplayName("#invokeConstructor() method tests")
    public class InvokeConstructorMethodTests {

        @Test
        @DisplayName("aClass")
        public void test1647357528357() {
            final Object o = ReflectUtils.invokeConstructor(Object.class);
            assertNotNull(o);
        }

        @Test
        @DisplayName("ReflectionException: private constructor")
        public void test1647357555837() {
            assertThrow(() -> ReflectUtils.invokeConstructor(Private.class))
                    .assertClass(ReflectionException.class)
                    .assertMessageIs("\n  Unable to instantiate class.\n" +
                                     "    Constructed type: assist.Private\n" +
                                     "    Constructor arguments: <absent>\n" +
                                     "    Error cause:\n" +
                                     "     - NoSuchMethodException: " +
                                     "No such accessible constructor on object: assist.Private\n");
        }

        @Test
        @DisplayName("ReflectionException: invalid constructor args")
        public void test1647357937771() {
            assertThrow(() -> ReflectUtils.invokeConstructor(Public.class, "foo", "bar"))
                    .assertClass(ReflectionException.class)
                    .assertMessageIs("\n  Unable to instantiate class.\n" +
                                     "    Constructed type: assist.Public\n" +
                                     "    Constructor arguments:\n" +
                                     "     - foo\n" +
                                     "     - bar\n" +
                                     "    Error cause:\n" +
                                     "     - NoSuchMethodException: " +
                                     "No such accessible constructor on object: assist.Public\n");
        }

    }

    public static class NoFieldsClass {

    }

    public static class WithFieldClass {

        private String foo;

    }

    public static class WithTransientFieldClass {

        private transient String bar;

    }

    public static class WithConstantFieldClass {

        private static final String car = "";

    }

    public static class ClassExtendsFromMap extends HashMap<String, String> {

    }

}
