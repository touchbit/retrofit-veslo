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

package veslo.client.converter.annotated;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.OkHttpTestUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.ConvertCallException;
import veslo.bean.template.TemplateReplaceAll;
import veslo.bean.template.TemplateSource;
import veslo.client.model.ResourceFile;

import java.io.IOException;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.is;
import static veslo.bean.template.TemplateSourceType.RESOURCE;

@SuppressWarnings("ConstantConditions")
@DisplayName("TemplateSourceConverter.class unit tests")
public class TemplateSourceConverterTests extends BaseUnitTest {

    private static final TemplateSourceConverter CONVERTER = TemplateSourceConverter.INSTANCE;
    private static final String NOT_FILLED = "<note>\n" +
            "    <to>null</to>\n" +
            "    <from>null</from>\n" +
            "    <heading>Reminder</heading>\n" +
            "    <body>\n" +
            "        <content>null</content>\n" +
            "        <status>DRAFT</status>\n" +
            "    </body>\n" +
            "</note>";

    @Nested
    @DisplayName("#requestBodyConverter() method tests")
    public class RequestBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645209300345() {
            assertNPE(() -> CONVERTER.requestBodyConverter(null, AA, AA, RTF), "type");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, null, AA, RTF), "parameterAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, null), "retrofit");
            assertNPE(() -> CONVERTER.requestBodyConverter(OBJ_C, AA, AA, RTF).convert(null), "template");
        }

        @Test
        @DisplayName("Convert ResourceFile to RequestBody")
        public void test1645209303378() throws IOException {
            final ResourceNotesUTF8 template = new ResourceNotesUTF8();
            final RequestBody requestBody = CONVERTER.requestBodyConverter(OBJ_C, arrayOf(), arrayOf(), RTF).convert(template);
            final String actual = OkHttpTestUtils.requestBodyToString(requestBody);
            assertThat("Body", actual, is(NOT_FILLED));
        }

    }

    @Nested
    @DisplayName("#responseBodyConverter() method tests")
    public class ResponseBodyConverterMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1645209310019() {
            assertNPE(() -> CONVERTER.responseBodyConverter(null, AA, RTF), "type");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, null, RTF), "methodAnnotations");
            assertNPE(() -> CONVERTER.responseBodyConverter(Long.class, AA, null), "retrofit");
        }

        @Test
        @DisplayName("ConvertCallException if ResponseBody present")
        public void test1645209314300() {
            final ResponseBody responseBody = ResponseBody.create(null, "test");
            assertThrow(() -> CONVERTER.responseBodyConverter(ResourceFile.class, AA, RTF).convert(responseBody))
                    .assertClass(ConvertCallException.class)
                    .assertMessageIs("Template classes with the @TemplateSource annotation are not allowed to " +
                            "be used to convert the response body.");
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

}
