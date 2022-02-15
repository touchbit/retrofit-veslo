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

package veslo.client.model;

import internal.test.utils.BaseUnitTest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.ReplaceableTextFileException;
import veslo.ResourceFileException;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static veslo.client.model.ReplaceableTextFile.TextFileType.OS_FILE;
import static veslo.client.model.ReplaceableTextFile.TextFileType.RESOURCE_FILE;

@DisplayName("ReplaceableTextFile.class unit tests")
public class ReplaceableTextFileTests extends BaseUnitTest {

    private static final String NOT_FILLED = "<note>\n" +
            "    <to>null</to>\n" +
            "    <from>null</from>\n" +
            "    <heading>Reminder</heading>\n" +
            "    <body>\n" +
            "        <content>null</content>\n" +
            "        <status>DRAFT</status>\n" +
            "    </body>\n" +
            "</note>";

    @Test
    @DisplayName("Constructor required parameters")
    public void test1644930350094() {
        assertNPE(() -> new TestFile(null, "path"), "textFileType");
        assertNPE(() -> new TestFile(RESOURCE_FILE, null), "textFilePath");
        assertNPE(() -> new TestFile(null, "path", UTF_8), "textFileType");
        assertNPE(() -> new TestFile(RESOURCE_FILE, null, UTF_8), "textFilePath");
        assertNPE(() -> new TestFile(RESOURCE_FILE, "path", null), "charset");
    }

    @Test
    @DisplayName("Successful reading RESOURCE_FILE")
    public void test1644934138709() {
        final String actual = new Notes(RESOURCE_FILE, "test/data/Notes.xml").toString();
        assertIs(actual, NOT_FILLED);
    }

    @Test
    @DisplayName("Successful reading OS_FILE")
    public void test1644934333792() {
        final String actual = new Notes(OS_FILE, "src/test/resources/test/data/Notes.xml").toString();
        assertIs(actual, NOT_FILLED);
    }

    @Test
    @DisplayName("ResourceFileException if resource file not readable (does not exist)")
    public void test1644934646881() {
        assertThrow(() -> new Notes(RESOURCE_FILE, "test/data/Notes.xml111111"))
                .assertClass(ResourceFileException.class)
                .assertMessageIs("Resource not exists: test/data/Notes.xml111111");
    }

    @Test
    @DisplayName("ReplaceableTextFileException if file not readable (does not exist)")
    public void test1644934809550() {
        assertThrow(() -> new Notes(OS_FILE, "test/data/Notes.xml111111"))
                .assertClass(ReplaceableTextFileException.class)
                .assertMessageIs("Unable to read file: test/data/Notes.xml111111");
    }

    @Nested
    @DisplayName("#getReplacement() method tests")
    public class GetReplacementMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1644934991225() {
            final Notes notes = new Notes();
            assertNPE(() -> notes.getReplacement(null), "field");
        }

        @Test
        @DisplayName("Get field values (filled)")
        public void test1644935432315() {
            final Notes notes = new Notes().to("1").from("2").bodyContent("3");
            final Field[] declaredFields = notes.getClass().getDeclaredFields();
            final Map<String, Field> fields = Arrays.stream(declaredFields)
                    .collect(Collectors.toMap(Field::getName, f -> f));
            assertIs(notes.getReplacement(fields.get("to")), "1");
            assertIs(notes.getReplacement(fields.get("from")), "2");
            assertIs(notes.getReplacement(fields.get("bodyContent")), "3");
        }

        @Test
        @DisplayName("Get field values (not filled)")
        public void test1644935653669() {
            final Notes notes = new Notes();
            final Field[] declaredFields = notes.getClass().getDeclaredFields();
            final Map<String, Field> fields = Arrays.stream(declaredFields)
                    .collect(Collectors.toMap(Field::getName, f -> f));
            assertIs(notes.getReplacement(fields.get("to")), "null");
            assertIs(notes.getReplacement(fields.get("from")), "null");
            assertIs(notes.getReplacement(fields.get("bodyContent")), "null");
        }

        @Test
        @DisplayName("ReplaceableTextFileException if field is unreadable")
        public void test1644935100321() {
            final Notes notes = new Notes();
            final Field declaredField = ReplaceableTextFileTests.class.getDeclaredFields()[0];
            assertThrow(() -> notes.getReplacement(declaredField))
                    .assertClass(ReplaceableTextFileException.class)
                    .assertMessageIs("Unable to get value from field: Notes.NOT_FILLED")
                    .assertCause(cause -> cause
                            .assertClass(IllegalArgumentException.class)
                            .assertMessageIs("Cannot locate field NOT_FILLED on class veslo.client.model.ReplaceableTextFileTests$Notes"));
        }

    }


    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    public static final class Notes extends ReplaceableTextFile {

        @ReplaceAll(regex = "\\[note.to]")
        private String to;
        @ReplaceAll(regex = "replace_note_from")
        private String from;
        @ReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

        public Notes(TextFileType textFileType, String textFilePath) {
            super(textFileType, textFilePath, UTF_8);
        }

        public Notes(TextFileType textFileType, String textFilePath, Charset charset) {
            super(textFileType, textFilePath, charset);
        }

        public Notes() {
            super(RESOURCE_FILE, "test/data/Notes.xml", UTF_8);
        }

        public String getReplacement(final Field field) {
            return super.getReplacement(field);
        }

    }

    public static final class TestFile extends ReplaceableTextFile {

        public TestFile(TextFileType textFileType, String textFilePath) {
            super(textFileType, textFilePath);
        }

        public TestFile(TextFileType textFileType, String textFilePath, Charset charset) {
            super(textFileType, textFilePath, charset);
        }

    }

}
