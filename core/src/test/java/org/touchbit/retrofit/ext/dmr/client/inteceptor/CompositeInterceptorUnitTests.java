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

import internal.test.utils.OkHttpUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CompositeInterceptor class tests")
public class CompositeInterceptorUnitTests {

    private static final TestInterceptAction ACTION = new TestInterceptAction();

    @Test
    @DisplayName("CompositeInterceptor with actions without exceptions")
    public void test1637818616701() throws Exception {
        final Response response = OkHttpUtils.getResponse();
        final Request request = response.request();
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenReturn(response);
        final CompositeInterceptor interceptor = new CompositeInterceptor()
                .withResponseInterceptActionsChain(ACTION)
                .withRequestInterceptActionsChain(ACTION);
        final Response result = interceptor.intercept(chain);
        assertThat("", result, is(response));
    }

    @Test
    @DisplayName("CompositeInterceptor with actions with IOException")
    public void test1637857177331() throws IOException {
        final Response response = OkHttpUtils.getResponse();
        final Request request = response.request();
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenThrow(new IOException("test1637857177331"));
        final CompositeInterceptor interceptor = new CompositeInterceptor()
                .withResponseInterceptActionsChain(ACTION)
                .withRequestInterceptActionsChain(ACTION);
        assertThrow(() -> interceptor.intercept(chain))
                .assertClass(IOException.class)
                .assertMessageIs("test1637857177331");
    }

    @Test
    @DisplayName("CompositeInterceptor without actions with RuntimeException")
    public void test1637857418542() throws IOException {
        final Response response = OkHttpUtils.getResponse();
        final Request request = response.request();
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenThrow(new RuntimeException("test1637857177331"));
        final CompositeInterceptor interceptor = new CompositeInterceptor()
                .withResponseInterceptActionsChain()
                .withRequestInterceptActionsChain();
        assertThrow(() -> interceptor.intercept(chain))
                .assertClass(RuntimeException.class)
                .assertMessageIs("test1637857177331");
    }

    public static class TestInterceptAction implements InterceptAction {

    }

}
