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

package org.touchbit.retrofit.ext.dmr.client.response;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.OkHttpTestUtils;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.asserter.HeadersAsserter;
import org.touchbit.retrofit.ext.dmr.asserter.ResponseAsserter;

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
                        ("test1639065948701", "test1639065948701", response, "info", annotations) {
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
        assertThat("", responseBase.getSucDTO(), is("test1639065948701"));
        assertThat("", responseBase.getErrDTO(), is("test1639065948701"));
        assertThat("", responseBase.getEndpointInfo(), is("info"));
        assertThat("", responseBase.getCallAnnotations(), is(annotations));
        responseBase.assertResponse(asserter -> asserter
                .assertSucBody(BaseUnitTest::assertIs, "test1639065948701")
                .assertHeaders(headersAsserter -> headersAsserter.contentTypeIs("text/plain"))
        );
    }

}
