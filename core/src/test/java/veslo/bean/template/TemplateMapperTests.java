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

package veslo.bean.template;

import internal.test.utils.BaseUnitTest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.RuntimeIOException;
import veslo.TemplateException;

import java.lang.reflect.Field;
import java.util.Arrays;

import static veslo.bean.template.TemplateSourceType.FILE;
import static veslo.bean.template.TemplateSourceType.RESOURCE;

@SuppressWarnings("ConstantConditions")
@DisplayName("ReplaceableTextFile.class unit tests")
public class TemplateMapperTests extends BaseUnitTest {

    private static final String TEMPLATE = "<note>\n" +
            "    <to>[note.to]</to>\n" +
            "    <from>replace_note_from</from>\n" +
            "    <heading>Reminder</heading>\n" +
            "    <body>\n" +
            "        <content>{note_body_content}</content>\n" +
            "        <status>DRAFT</status>\n" +
            "    </body>\n" +
            "</note>";

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
    @DisplayName("Utility class")
    public void test1645208642059() {
        assertUtilityClassException(TemplateMapper.class);
    }

    @Nested
    @DisplayName("#readTemplate() method tests")
    public class ReadTemplateMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645199293960() {
            assertNPE(() -> TemplateMapper.readTemplate(null), "template");
        }

        @Test
        @DisplayName("Successful reading resource template with UTF-8 charset")
        public void test1645199952518() {
            final ResourceNotesUTF8 notes = new ResourceNotesUTF8();
            final String template = TemplateMapper.readTemplate(notes);
            assertIs(template, TEMPLATE);
        }

        @Test
        @DisplayName("Successful reading file template with UTF-8 charset")
        public void test1645207494275() {
            final FileNotesUTF8 fileNotesUTF8 = new FileNotesUTF8();
            final String template = TemplateMapper.readTemplate(fileNotesUTF8);
            assertIs(template, TEMPLATE);
        }

        @Test
        @DisplayName("Successful reading resource template with CP-1251 charset")
        public void test1645200331699() {
            final ResourceNotes1251 notes = new ResourceNotes1251();
            final String template = TemplateMapper.readTemplate(notes);
            assertIs(template, TEMPLATE);
        }

        @Test
        @DisplayName("TemplateException throws if TemplateSource.charset = FooBar")
        public void test1645200411827() {
            final ResourceNotesBadCharset notes = new ResourceNotesBadCharset();
            assertThrow(() -> TemplateMapper.readTemplate(notes))
                    .assertClass(TemplateException.class)
                    .assertMessageIs("TemplateSource annotation contains unsupported Charset.\n" +
                            "Template: " + ResourceNotesBadCharset.class + "\n" +
                            "Source path: test/data/Notes_utf_8.txt\n" +
                            "Source type: RESOURCE\n" +
                            "Source charset: FooBar\n");
        }

        @Test
        @DisplayName("TemplateException throws if TemplateSource annotation not present")
        public void test1645207820418() {
            final TemplateWithoutAnnotation template = new TemplateWithoutAnnotation();
            assertThrow(() -> TemplateMapper.readTemplate(template))
                    .assertClass(TemplateException.class)
                    .assertMessageIs("The template class must contain an annotation.\n" +
                            "Template: veslo.bean.template.TemplateMapperTests$TemplateWithoutAnnotation\n" +
                            "Annotation: veslo.bean.template.TemplateSource\n");
        }

        @Test
        @DisplayName("RuntimeIOException throws if resource not exists")
        public void test1645200961153() {
            final ResourceNotExists resourceNotExists = new ResourceNotExists();
            assertThrow(() -> TemplateMapper.readTemplate(resourceNotExists))
                    .assertClass(RuntimeIOException.class)
                    .assertMessageIs("Resource file not readable: test_6453.txt");
        }

        @Test
        @DisplayName("RuntimeIOException throws if file not exists")
        public void test1645207513748() {
            final FileNotExists fileNotExists = new FileNotExists();
            assertThrow(() -> TemplateMapper.readTemplate(fileNotExists))
                    .assertClass(RuntimeIOException.class)
                    .assertMessageIs("File not readable: test_6453.txt");
        }

    }

    @Nested
    @DisplayName("#getReplacement() method tests")
    public class GetReplacementMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645208022855() {
            final Field field = ResourceNotesUTF8.class.getDeclaredFields()[0];
            assertNPE(() -> TemplateMapper.getReplacement(null, field), "template");
            assertNPE(() -> TemplateMapper.getReplacement(ResourceNotesUTF8.class, null), "field");
        }

        @Test
        @DisplayName("Get value from nullable field")
        public void test1645208145857() {
            final ResourceNotesUTF8 resource = new ResourceNotesUTF8().to(null);
            final Field field = Arrays.stream(resource.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals("to"))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            final String replacement = TemplateMapper.getReplacement(resource, field);
            assertIs(replacement, "null");
        }

        @Test
        @DisplayName("Get value from filled field")
        public void test1645208336371() {
            final ResourceNotesUTF8 resource = new ResourceNotesUTF8().to("123");
            final Field field = Arrays.stream(resource.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals("to"))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            final String replacement = TemplateMapper.getReplacement(resource, field);
            assertIs(replacement, "123");
        }

        @Test
        @DisplayName("RuntimeIOException throws field not readable")
        public void test1645208390755() {
            final ResourceNotesUTF8 resource = new ResourceNotesUTF8().to("123");
            final Field field = TemplateMapperTests.class.getDeclaredFields()[0];
            assertThrow(() -> TemplateMapper.getReplacement(resource, field))
                    .assertClass(TemplateException.class)
                    .assertMessageIs("Unable to get value from field.\n" +
                            "Class: veslo.bean.template.TemplateMapperTests$ResourceNotesUTF8\n" +
                            "Field: TEMPLATE\n");
        }

    }

    @Nested
    @DisplayName("#marshal() method tests")
    public class MarshalMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645208610160() {
            assertNPE(() -> TemplateMapper.marshal(null), "template");
        }

        @Test
        @DisplayName("Marshal filled template")
        public void test1645208913067() {
            final ResourceNotesUTF8 template = new ResourceNotesUTF8().to("1").from("2").bodyContent("3");
            final String result = TemplateMapper.marshal(template);
            assertIs(result, "" +
                    "<note>\n" +
                    "    <to>1</to>\n" +
                    "    <from>2</from>\n" +
                    "    <heading>Reminder</heading>\n" +
                    "    <body>\n" +
                    "        <content>3</content>\n" +
                    "        <status>DRAFT</status>\n" +
                    "    </body>\n" +
                    "</note>");
        }

        @Test
        @DisplayName("Marshal unfilled template (all fields is null)")
        public void test1645208667259() {
            final ResourceNotesUTF8 template = new ResourceNotesUTF8();
            final String result = TemplateMapper.marshal(template);
            assertIs(result, NOT_FILLED);
        }

        @Test
        @DisplayName("Marshal template without fields")
        public void test1645208784823() {
            final WithoutFields template = new WithoutFields();
            final String result = TemplateMapper.marshal(template);
            assertIs(result, TEMPLATE);
        }


    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = RESOURCE, path = "test/data/Notes_utf_8.txt")
    public static final class ResourceNotesUTF8 {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = FILE, path = "src/test/resources/test/data/Notes_utf_8.txt")
    public static final class FileNotesUTF8 {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = RESOURCE, path = "test/data/Notes_utf_8.txt", charset = "CP1251")
    public static final class ResourceNotes1251 {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = RESOURCE, path = "test/data/Notes_utf_8.txt", charset = "FooBar")
    public static final class ResourceNotesBadCharset {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = RESOURCE, path = "test_6453.txt")
    public static final class ResourceNotExists {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @TemplateSource(type = FILE, path = "test_6453.txt")
    public static final class FileNotExists {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    public static final class TemplateWithoutAnnotation {

        @TemplateReplaceAll(regex = "\\[note.to]")
        private String to;
        @TemplateReplaceAll(regex = "replace_note_from")
        private String from;
        @TemplateReplaceAll(regex = "\\{note_body_content}")
        private String bodyContent;

    }

    @TemplateSource(type = RESOURCE, path = "test/data/Notes_utf_8.txt")
    public static final class WithoutFields {

    }

}
