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

import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@DisplayName("DualResponseBase tests")
public class DualResponseBaseUnitTests {

    @Test
    @DisplayName("Check default methods if all objects is present")
    public void test1639065948701() {
        final Response response = mock(Response.class);
        final Annotation[] annotations = new Annotation[]{};
        DualResponseBase<String, String> responseBase = new DualResponseBase<String, String>
                ("test1639065948701", "test1639065948701", response, "info", annotations) {};
        assertThat("", responseBase.getResponse(), is(response));
        assertThat("", responseBase.getSucDTO(), is("test1639065948701"));
        assertThat("", responseBase.getErrDTO(), is("test1639065948701"));
        assertThat("", responseBase.getEndpointInfo(), is("info"));
        assertThat("", responseBase.getCallAnnotations(), is(annotations));
    }

}