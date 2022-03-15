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

package veslo.client.response;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.OkHttpTestUtils;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.asserter.HeadersAsserter;
import veslo.asserter.ResponseAsserter;

import java.lang.annotation.Annotation;

import static org.hamcrest.Matchers.is;

@DisplayName("DualResponseBase tests")
public class BaseDualResponseUnitTests extends BaseCoreUnitTest {

    @Test
    @DisplayName("Check default methods if all objects is present")
    public void test1639065948701() {
        final Response response = OkHttpTestUtils.getResponse();
        final Annotation[] annotations = new Annotation[]{};
        BaseDualResponse<String, String, ResponseAsserter<String, String, HeadersAsserter>> responseBase =
                new BaseDualResponse<String, String, ResponseAsserter<String, String, HeadersAsserter>>
                        ("success", "error", response, "info", annotations) {
                    @Override
                    public HeadersAsserter getHeadersAsserter() {
                        return new HeadersAsserter(getResponse().headers());
                    }

                    @Override
                    public ResponseAsserter<String, String, HeadersAsserter> getResponseAsserter() {
                        return new ResponseAsserter<>(this, getHeadersAsserter());
                    }
                };
        assertThat("", responseBase.getResponse(), is(response));
        assertThat("", responseBase.getSucDTO(), is("success"));
        assertThat("", responseBase.getErrDTO(), is("error"));
        assertThat("", responseBase.getEndpointInfo(), is("info"));
        assertThat("", responseBase.getCallAnnotations(), is(annotations));
        responseBase.assertResponse(asserter -> asserter
                .assertSucBody(BaseUnitTest::assertIs, "success")
                .assertErrBody(BaseUnitTest::assertIs, "error")
                .assertHeaders(headersAsserter -> headersAsserter.contentTypeIs("text/plain"))
        );
        responseBase.assertSucResponse(this::assertSuccessResponse, "success");
        responseBase.assertErrResponse(this::assertErrorResponse, "error");
    }

    public void assertSuccessResponse(ResponseAsserter<String, ?, HeadersAsserter> asserter, String expected) {
        asserter.assertSucBody(actual -> assertThat("", actual, is(expected)));
    }

    public void assertErrorResponse(ResponseAsserter<String, ?, HeadersAsserter> asserter, String expected) {
        asserter.assertErrBody(actual -> assertThat("", actual, is(expected)));
    }

    @Test
    @DisplayName("assertSucResponse")
    public void test1647375138577() {

    }

    @Test
    @DisplayName("assertErrResponse")
    public void test1647375150477() {

    }


}
