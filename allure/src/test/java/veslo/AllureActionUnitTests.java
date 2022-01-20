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

package veslo;

import com.fasterxml.jackson.databind.ObjectMapper;
import internal.test.utils.OkHttpTestUtils;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.model.AllureResult;
import veslo.model.AttachmentsItem;
import veslo.model.StepsItem;
import veslo.testutil.UnitTestInternalAllurePlatform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("AllureAction class tests")
public class AllureActionUnitTests extends BaseUnitTests {

    @Test
    @DisplayName("#requestAction() Successful add request attachment")
    public void test1639065952922() throws IOException {
        UnitTestInternalAllurePlatform.execute("test1638347276789", () -> {
            AllureAction action = new AllureAction();
            final Request expRequest = OkHttpTestUtils.getRequest(MediaType.get("text/plain"), "generated");
            final Request actRequest = action.requestAction(expRequest);
            assertThat("Request", expRequest, is(actRequest));
        });
        final AllureResult allureResult = getAllureResult();
        final StepsItem stepsItem = allureResult.getSteps().stream()
                .filter(step -> step.getName().equals("test1638347276789"))
                .findFirst()
                .orElseThrow(AssertionError::new);
        final List<AttachmentsItem> attachments = stepsItem.getAttachments();
        assertThat("Allure step attachments", attachments.size(), is(1));
        final AttachmentsItem attachment = attachments.get(0);
        assertThat("Allure step attachment name", attachment.getName(), is("REQUEST"));
        final File attachmentFile = new File(RESULTS_PATH.toFile(), attachment.getSource());
        final String attachmentContent = new String(Files.readAllBytes(attachmentFile.toPath()));
        assertThat("attachment content", attachmentContent, is("" +
                "REQUEST:\n" +
                "POST http://localhost/\n" +
                "Headers:\n" +
                "  Content-Type: text/plain\n" +
                "  X-Request-ID: generated\n" +
                "  Content-Length: 9\n" +
                "Body: (9-byte body)\n" +
                "  generated\n"));
    }

    @Test
    @DisplayName("#responseAction() Successful add response attachment")
    public void test1639065952951() throws IOException {
        UnitTestInternalAllurePlatform.execute("test1638351724471", () -> {
            AllureAction action = new AllureAction();
            final Response expResponse = OkHttpTestUtils.getResponse();
            final Response actResponse = action.responseAction(expResponse);
            assertThat("Response", expResponse, is(actResponse));
            final List<Path> attachments = Files.list(RESULTS_PATH).collect(Collectors.toList());
            assertThat("attachments.size()", attachments.size(), is(1));
            final String attachment = new String(Files.readAllBytes(attachments.get(0)));
            assertThat("attachment", attachment, is("" +
                    "RESPONSE:\n" +
                    "200 TEST http://localhost/\n" +
                    "Headers:\n" +
                    "  Content-Type: text/plain\n" +
                    "  Content-Length: 9\n" +
                    "  X-Request-ID: generated\n" +
                    "Body: (9-byte body)\n" +
                    "  generated\n"));
        });
        boolean containsResultJson = false;
        for (Path path : Files.list(RESULTS_PATH).collect(Collectors.toList())) {
            if (path.toString().contains("result.json")) {
                containsResultJson = true;
                final byte[] results = Files.readAllBytes(path);
                final AllureResult allureResult = new ObjectMapper().readValue(results, AllureResult.class);
                assertThat("Allure steps", allureResult.getSteps().size(), is(1));
                final StepsItem step = allureResult.getSteps().get(0);
                assertThat("Allure step attachments", step.getAttachments().size(), is(1));
                final AttachmentsItem attachment = step.getAttachments().get(0);
                assertThat("Allure step attachment name", attachment.getName(), is("RESPONSE"));
            }
        }
        assertThat("result.json present", containsResultJson, is(true));
    }

}
