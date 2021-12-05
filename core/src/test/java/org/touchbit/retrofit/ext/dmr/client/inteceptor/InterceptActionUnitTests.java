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

package org.touchbit.retrofit.ext.dmr.client.inteceptor;

import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@DisplayName("InterceptAction interface tests")
public class InterceptActionUnitTests {

    @Test
    @DisplayName("#chainAction() return Request without modification")
    public void test1639065951290() {
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        final Interceptor.Chain result = new InterceptAction() {
        }.chainAction(chain);
        assertThat("", result, is(chain));
    }

    @Test
    @DisplayName("#requestAction() return Request without modification")
    public void test1639065951299() throws Exception {
        Request request = new Request.Builder().url("http://localhost").get().headers(Headers.of()).build();
        final Request result = new InterceptAction() {
        }.requestAction(request);
        assertThat("", result, is(request));
    }

    @Test
    @DisplayName("#responseAction() return Response without modification")
    public void test1639065951308() throws Exception {
        Request request = new Request.Builder().url("http://localhost").get().headers(Headers.of()).build();
        Response expected = new Response.Builder()
                .request(request)
                .headers(Headers.of())
                .body(ResponseBody.create(null, "test1637817584009"))
                .protocol(Protocol.HTTP_1_1)
                .message("TEST")
                .code(200)
                .build();
        final Response result = new InterceptAction() {
        }.responseAction(expected);
        assertThat("", result, is(expected));
    }

    @Test
    @DisplayName("#errorAction() does not throw an exception")
    public void test1639065951325() {
        new InterceptAction() {
        }.errorAction(new RuntimeException());
    }

}