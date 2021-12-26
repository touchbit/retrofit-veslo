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

package org.touchbit.retrofit.veslo.client.inteceptor;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.BaseCoreUnitTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static internal.test.utils.OkHttpTestUtils.getRequest;
import static internal.test.utils.OkHttpTestUtils.getResponse;
import static org.hamcrest.Matchers.*;

@DisplayName("CookieAction.class tests")
public class CookieActionUnitTests extends BaseCoreUnitTest {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    @BeforeEach
    public void clearCookie() {
        CookieAction.clearCookie();
    }

    @Nested
    @DisplayName("#requestAction() method tests")
    public class RequestActionMethodTests {

        @Test
        @DisplayName("Successfully getting a cookie from the cache if Cookie.Domain not set")
        public void test1640544378348() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            Headers headers = CookieAction.INSTANCE.requestAction(request).headers();
            assertThat(headers.get(COOKIE), is("a=b"));
            headers = CookieAction.INSTANCE.requestAction(getRequest("http://foo.localhost")).headers();
            assertThat(headers.get(COOKIE), nullValue());
        }

        @Test
        @DisplayName("Successfully getting a cookie from the cache if Cookie.Domain == Request.host")
        public void test1640547773315() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Domain=localhost"));
            CookieAction.INSTANCE.responseAction(response);
            Headers headers = CookieAction.INSTANCE.requestAction(request).headers();
            assertThat(headers.get(COOKIE), is("a=b"));
            headers = CookieAction.INSTANCE.requestAction(getRequest("http://foo.localhost")).headers();
            assertThat(headers.get(COOKIE), is("a=b"));
        }

        @Test
        @DisplayName("Successfully getting a cookie from the cache if Cookie.Expires > now")
        public void test1640548402322() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Expires=" + expires(new Date(), 1000)));
            CookieAction.INSTANCE.responseAction(response);
            Headers headers = CookieAction.INSTANCE.requestAction(request).headers();
            assertThat(headers.get(COOKIE), is("a=b"));
        }


        @Test
        @DisplayName("A cookie is not getting from the cache if Cookie.Expires <= now")
        public void test1640547958157() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Expires=" + expires(new Date())));
            CookieAction.INSTANCE.responseAction(response);
            Headers headers = CookieAction.INSTANCE.requestAction(request).headers();
            assertThat(headers.get(COOKIE), nullValue());
        }

    }

    @Nested
    @DisplayName("#responseAction() method tests")
    public class ResponseActionMethodTests {

        @Test
        @DisplayName("Successfully adding a cookie to the cache if Cookie.Domain not set")
        public void test1640545110701() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            final List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            assertThat(cookie.get(0).domain(), is("localhost"));
            assertThat(cookie.get(0).hostOnly(), is(true));
            assertThat(CookieAction.getCookie(HttpUrl.get("http://localhost")), hasSize(1));
            assertThat(CookieAction.getCookie(HttpUrl.get("http://foo.localhost")), hasSize(0));
        }

        @Test
        @DisplayName("Successfully adding a cookie to the cache if Cookie.Domain == Request.host")
        public void test1640545404769() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Domain=localhost"));
            CookieAction.INSTANCE.responseAction(response);
            final List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            assertThat(cookie.get(0).domain(), is("localhost"));
            assertThat(cookie.get(0).hostOnly(), is(false));
            assertThat(CookieAction.getCookie(HttpUrl.get("http://localhost")), hasSize(1));
            assertThat(CookieAction.getCookie(HttpUrl.get("http://foo.localhost")), hasSize(1));
        }

        @Test
        @DisplayName("A cookie is not added to the cache if Cookie.Domain == foo.localhost && Request.host == localhost")
        public void test1640545544884() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Domain=foo.localhost"));
            CookieAction.INSTANCE.responseAction(response);
            final List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

        @Test
        @DisplayName("A cookie is not added to the cache if Cookie.Domain == localhost && Request.host == foo.localhost")
        public void test1640545606899() {
            final Request request = getRequest("http://foo.localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b; Domain=localhost"));
            CookieAction.INSTANCE.responseAction(response);
            final List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

    }

    @Nested
    @DisplayName("#clearCookie() method tests")
    public class MethodTests {

        @Test
        @DisplayName("Clear cookie by HttpUrl")
        public void test1640549193017() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            CookieAction.clearCookie(HttpUrl.get("http://localhost"));
            cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by URL")
        public void test1640549251668() throws MalformedURLException {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            CookieAction.clearCookie(new URL("http://localhost"));
            cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by domain name")
        public void test1640549300738() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            CookieAction.clearCookie("localhost");
            cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

        @Test
        @DisplayName("Clear all cookie")
        public void test1640549334237() {
            final Request request = getRequest("http://localhost");
            final Response response = getResponse(request, Headers.of(SET_COOKIE, "a=b"));
            CookieAction.INSTANCE.responseAction(response);
            List<Cookie> cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(1));
            CookieAction.clearCookie();
            cookie = new ArrayList<>(CookieAction.getCookie());
            assertThat(cookie, hasSize(0));
        }

    }

    private static String expires(Date date) {
        return expires(date, 0);
    }

    private static String expires(Date date, long modify) {
        date.setTime(date.getTime() + modify);
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

}
