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

package veslo.client.inteceptor;

import internal.test.utils.BaseUnitTest;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static internal.test.utils.OkHttpTestUtils.getRequest;
import static internal.test.utils.OkHttpTestUtils.getResponse;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ConstantConditions", "ConfusingArgumentToVarargsMethod"})
@DisplayName("CookieAction.class tests")
public class CookieActionUnitTests extends BaseUnitTest {

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
            assertThat(CookieAction.getRequestUnexpiredCookie(HttpUrl.get("http://localhost")), hasSize(1));
            assertThat(CookieAction.getRequestUnexpiredCookie(HttpUrl.get("http://foo.localhost")), hasSize(0));
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
            assertThat(CookieAction.getRequestUnexpiredCookie(HttpUrl.get("http://localhost")), hasSize(1));
            assertThat(CookieAction.getRequestUnexpiredCookie(HttpUrl.get("http://foo.localhost")), hasSize(1));
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
    @DisplayName("#getCookie() method tests")
    public class GetCookieMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645115306946() {
            assertNPE(() -> CookieAction.getCookie(null), "cookieName");
            assertNPE(() -> CookieAction.getCookie(null, "localhost"), "cookieName");
            assertNPE(() -> CookieAction.getCookie("name", null), "domain");
            assertNPE(() -> CookieAction.getCookie(null, "localhost", "/"), "cookieName");
            assertNPE(() -> CookieAction.getCookie("name", null, "/"), "domain");
            assertNPE(() -> CookieAction.getCookie("name", "localhost", null), "path");
        }

        @Test
        @DisplayName("Get Cookies by name for different domains and paths")
        public void test1645115432773() {
            final Cookie cookie1 = generateCookie("a", "b", "domain.1", "/");
            final Cookie cookie2 = generateCookie("a", "b", "domain.1", "/api/");
            final Cookie cookie3 = generateCookie("a", "b", "domain.2", "/");
            final Cookie cookie4 = generateCookie("a", "b", "domain.3", "/api/");
            CookieAction.addCookie(cookie1, cookie2, cookie3, cookie4);
            final Set<Cookie> actual = CookieAction.getCookie("a");
            assertThat(actual, containsInAnyOrder(cookie1, cookie2, cookie3, cookie4));
        }

        @Test
        @DisplayName("Get Cookies by name and domain for different paths")
        public void test1645115687537() {
            final Cookie cookie1 = generateCookie("a", "b", "domain.1", "/");
            final Cookie cookie2 = generateCookie("a", "b", "domain.1", "/api/");
            final Cookie cookie3 = generateCookie("a", "b", "domain.2", "/");
            final Cookie cookie4 = generateCookie("a", "b", "domain.3", "/api/");
            CookieAction.addCookie(cookie1, cookie2, cookie3, cookie4);
            final Set<Cookie> actual = CookieAction.getCookie("a", "domain.1");
            assertThat(actual, containsInAnyOrder(cookie1, cookie2));
        }

        @Test
        @DisplayName("Get Cookies by name, domain and path")
        public void test1645115727522() {
            final Cookie cookie1 = generateCookie("a", "b", "domain.1", "/");
            final Cookie cookie2 = generateCookie("a", "b", "domain.1", "/api/");
            final Cookie cookie3 = generateCookie("a", "b", "domain.2", "/");
            final Cookie cookie4 = generateCookie("a", "b", "domain.3", "/api/");
            CookieAction.addCookie(cookie1, cookie2, cookie3, cookie4);
            final Set<Cookie> actual = CookieAction.getCookie("a", "domain.1", "/");
            assertThat(actual, containsInAnyOrder(cookie1));
        }


    }

    @Nested
    @DisplayName("#getCookieHeaderValue() method tests")
    public class GetCookieHeaderValueMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645115111880() {
            assertNPE(() -> CookieAction.getCookieHeaderValue(null), "url");
        }

        @Test
        @DisplayName("Get cookie header value by domain")
        public void test1645115130397() {
            final Cookie cookie1 = generateCookie("a", "b", "localhost");
            final Cookie cookie2 = generateCookie("c", "d", "localhost");
            final Cookie cookie3 = generateCookie("e", "f", "localHostel");
            CookieAction.addCookie(cookie1, cookie2, cookie3);
            final String actual = CookieAction.getCookieHeaderValue(HttpUrl.get("http://localhost"));
            assertThat(actual, is("a=b; c=d"));
        }

    }

    @Nested
    @DisplayName("#addCookie() method tests")
    public class AddCookieMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645114440286() {
            assertNPE(() -> CookieAction.addCookie(null), "cookies");
            assertNPE(() -> CookieAction.addCookie(new Cookie[]{null}), "cookie");
            assertNPE(() -> CookieAction.addCookie(true, null), "cookies");
            assertNPE(() -> CookieAction.addCookie(true, new Cookie[]{null}), "cookie");
        }

        @Test
        @DisplayName("Add cookies with replace by default")
        public void test1645114786536() {
            final Cookie cookie1 = generateCookie("foo", "foo", "localhost");
            final Cookie cookie2 = generateCookie("foo", "bar", "localhost");
            CookieAction.addCookie(cookie1);
            assertThat(CookieAction.getCookie(cookie1.name()), contains(cookie1));
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.addCookie(cookie2);
            assertThat(CookieAction.getCookie(cookie2.name()), contains(cookie2));
            assertThat(CookieAction.getCookie(), hasSize(1));
        }

        @Test
        @DisplayName("Add cookies with replace by flag")
        public void test1645114948816() {
            final Cookie cookie1 = generateCookie("foo", "foo", "localhost");
            final Cookie cookie2 = generateCookie("foo", "bar", "localhost");
            CookieAction.addCookie(true, cookie1);
            assertThat(CookieAction.getCookie(cookie1.name()), contains(cookie1));
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.addCookie(true, cookie2);
            assertThat(CookieAction.getCookie(cookie2.name()), contains(cookie2));
            assertThat(CookieAction.getCookie(), hasSize(1));
        }

        @Test
        @DisplayName("Add cookies without replace by flag")
        public void test1645114975645() {
            final Cookie cookie1 = generateCookie("foo", "foo", "localhost");
            final Cookie cookie2 = generateCookie("foo", "bar", "localhost");
            CookieAction.addCookie(false, cookie1);
            assertThat(CookieAction.getCookie(cookie1.name()), containsInAnyOrder(cookie1));
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.addCookie(false, cookie2);
            assertThat(CookieAction.getCookie(cookie2.name()), containsInAnyOrder(cookie1, cookie2));
            assertThat(CookieAction.getCookie(), hasSize(2));
        }

    }

    @Nested
    @DisplayName("#clearCookie() method tests")
    public class ClearCookieMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645114436002() {
            assertNPE(() -> CookieAction.clearCookie((HttpUrl) null), "url");
            assertNPE(() -> CookieAction.clearCookie((URL) null), "url");
            assertNPE(() -> CookieAction.clearCookie((String) null), "cookieName");
            assertNPE(() -> CookieAction.clearCookie((HttpUrl) null, "foobar"), "url");
            assertNPE(() -> CookieAction.clearCookie((URL) null, "foobar"), "url");
            assertNPE(() -> CookieAction.clearCookie((String) null, "foobar"), "domain");
            assertNPE(() -> CookieAction.clearCookie(HttpUrl.get("https://localhost"), null), "cookieName");
            assertNPE(() -> CookieAction.clearCookie(new URL("https://localhost"), null), "cookieName");
            assertNPE(() -> CookieAction.clearCookie("localhost", null), "cookieName");
        }

        @Test
        @DisplayName("Clear cookie by HttpUrl")
        public void test1640549193017() {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(HttpUrl.get("https://" + cookie.domain()));
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by URL")
        public void test1640549251668() throws MalformedURLException {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(new URL("https://" + cookie.domain()));
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by cookie name")
        public void test1645112854968() {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by URL and cookie name")
        public void test1645113646322() throws MalformedURLException {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(new URL("https://localhost"), cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(new URL("https://" + cookie.domain()), "foobar");
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(new URL("https://" + cookie.domain()), cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by HttpUrl and cookie name")
        public void test1645113760348() {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(HttpUrl.get("https://localhost"), cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(HttpUrl.get("https://" + cookie.domain()), "foobar");
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(HttpUrl.get("https://" + cookie.domain()), cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear cookie by domain and cookie name")
        public void test1645113794788() {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie("localhost", cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(cookie.domain(), "foobar");
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie(cookie.domain(), cookie.name());
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

        @Test
        @DisplayName("Clear all cookie")
        public void test1640549334237() {
            final Cookie cookie = generateCookie();
            CookieAction.addCookie(cookie);
            assertThat(CookieAction.getCookie(), hasSize(1));
            CookieAction.clearCookie();
            assertThat(CookieAction.getCookie(), hasSize(0));
        }

    }

    @Nested
    @DisplayName("#toStringCookies() method tests")
    public class ToStringCookiesMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645114280524() {
            assertNPE(() -> CookieAction.toStringCookies(null), "collector");
        }

        @Test
        @DisplayName("toStringCookies with default Collector")
        public void test1645113899276() {
            final Cookie cookie1 = generateCookie("foo", "foo", "localhost");
            final Cookie cookie2 = generateCookie("bar", "bar", "localhost");
            CookieAction.addCookie(cookie1, cookie2);
            assertThat(CookieAction.toStringCookies(), is(cookie1 + "\n" + cookie2));
        }

        @Test
        @DisplayName("toStringCookies with custom Collector")
        public void test1645114363417() {
            final Cookie cookie1 = generateCookie("foo", "foo", "localhost");
            final Cookie cookie2 = generateCookie("bar", "bar", "localhost");
            CookieAction.addCookie(cookie1, cookie2);
            assertThat(CookieAction.toStringCookies(Collectors.joining(";")), is(cookie1 + ";" + cookie2));
        }

    }

    private static Cookie generateCookie() {
        return generateCookie(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private static Cookie generateCookie(String name, String value, String domain) {
        return generateCookie(name, value, domain, "/");
    }

    private static Cookie generateCookie(String name, String value, String domain, String path) {
        return new Cookie.Builder()
                .name(name)
                .value(value)
                .path(path)
                .domain(domain).build();
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
