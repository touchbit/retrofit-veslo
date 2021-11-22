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

import okhttp3.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.asserter.ResponseAsserter;
import retrofit2.Response;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
@DisplayName("DualResponse tests")
public class DualResponseUnitTests {

    @Test
    @DisplayName("DualResponse")
    public void test1637490683876() {
        final Request request = mock(Request.class);
        final Response<String> response = mock(Response.class);
        final Annotation[] aa = new Annotation[]{};
        DualResponse<String, String> dr = new DualResponse<>(request, response, "test1637490683876", "info", aa);
        assertThat("", dr.getRawRequest(), is(request));
        assertThat("", dr.getResponse(), is(response));
        assertThat("", dr.getErrorDTO(), is("test1637490683876"));
        assertThat("", dr.getEndpointInfo(), is("info"));
        assertThat("", dr.getCallAnnotations(), is(aa));
        dr.assertResponse(asserter -> asserter
                .softly(() -> assertThat("", asserter, instanceOf(ResponseAsserter.class))));
    }

}