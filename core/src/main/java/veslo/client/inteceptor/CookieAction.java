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

import okhttp3.*;
import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static veslo.constant.ParameterNameConstants.*;

/**
 * {@link CompositeInterceptor} action for managing cookies
 * <p>
 * Interceptor initialisation:
 * * public class CustomCompositeInterceptor extends CompositeInterceptor {
 * *
 * *     public CustomCompositeInterceptor() {
 * *         super(LoggerFactory.getLogger(CustomCompositeInterceptor.class));
 * *         withRequestInterceptActionsChain(CookieAction.INSTANCE, LoggingAction.INSTANCE);
 * *         withResponseInterceptActionsChain(LoggingAction.INSTANCE, CookieAction.INSTANCE);
 * *     }
 * *
 * * }
 * <p>
 * * Client initialisation:
 * * new Retrofit.Builder()
 * *        .client(new OkHttpClient.Builder()
 * *                .addInterceptor(new CustomCompositeInterceptor()) // <------------------
 * *                .build())
 * *        .baseUrl(URL)
 * *        .addCallAdapterFactory(new UniversalCallAdapterFactory())
 * *        .addConverterFactory(new JacksonConverterFactory())
 * *        .build()
 * *        .create(SomeClient.class);
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 26.12.2021
 */
public class CookieAction implements InterceptAction {

    /**
     * Default instance
     */
    public static final CookieAction INSTANCE = new CookieAction();

    /**
     * ThreadLocal {@link Set} of {@link Cookie} cache
     */
    private static final ThreadLocal<Set<Cookie>> COOKIES = ThreadLocal.withInitial(HashSet::new);

    /**
     * Build {@link Request} with a cookie header if cached cookies
     * are present for the request domain, path, and have not expired.
     *
     * @param request - {@link Request}
     * @return {@link Request} with a cookie header (by condition)
     */
    @Override
    @EverythingIsNonNull
    public Request requestAction(final Request request) {
        Utils.parameterRequireNonNull(request, REQUEST_PARAMETER);
        final String cookie = getCookieHeaderValue(request.url());
        return cookie.isEmpty() ? request : request.newBuilder().header("Cookie", cookie).build();
    }

    /**
     * Cache {@link Cookie} from "Set-Cookie" {@link Request} headers
     *
     * @param response - {@link Response}
     * @return {@link Response}
     */
    @Override
    @EverythingIsNonNull
    public Response responseAction(final Response response) {
        Utils.parameterRequireNonNull(response, RESPONSE_PARAMETER);
        final HttpUrl url = response.request().url();
        final Headers headers = response.headers();
        Cookie.parseAll(url, headers).forEach(CookieAction::addCookie);
        return response;
    }

    /**
     * @return all cached {@link Cookie} collection
     */
    @EverythingIsNonNull
    public static Set<Cookie> getCookie() {
        if (COOKIES.get() == null) {
            COOKIES.set(new HashSet<>());
        }
        return COOKIES.get();
    }

    /**
     * @return set of {@link Cookie} by name for different domains and paths
     */
    @EverythingIsNonNull
    public static Set<Cookie> getCookie(final String cookieName) {
        Utils.parameterRequireNonNull(cookieName, COOKIE_NAME_PARAMETER);
        return getCookie().stream()
                .filter(c -> c.name().equals(cookieName))
                .collect(Collectors.toSet());
    }

    /**
     * @return set of {@link Cookie} by name and domain for different paths
     */
    @EverythingIsNonNull
    public static Set<Cookie> getCookie(final String cookieName, String domain) {
        Utils.parameterRequireNonNull(cookieName, COOKIE_NAME_PARAMETER);
        Utils.parameterRequireNonNull(domain, DOMAIN_PARAMETER);
        return getCookie().stream()
                .filter(c -> c.name().equals(cookieName))
                .filter(c -> c.domain().equals(domain))
                .collect(Collectors.toSet());
    }

    /**
     * @return set of {@link Cookie} by name, domain and path
     */
    @EverythingIsNonNull
    public static Set<Cookie> getCookie(final String cookieName, String domain, String path) {
        Utils.parameterRequireNonNull(cookieName, COOKIE_NAME_PARAMETER);
        Utils.parameterRequireNonNull(domain, DOMAIN_PARAMETER);
        Utils.parameterRequireNonNull(path, PATH_PARAMETER);
        return getCookie().stream()
                .filter(c -> c.name().equals(cookieName))
                .filter(c -> c.domain().equals(domain))
                .filter(c -> c.path().equals(path))
                .collect(Collectors.toSet());
    }

    /**
     * @param url - request url
     * @return cached {@link Cookie} collection for the url
     */
    @EverythingIsNonNull
    public static Set<Cookie> getRequestUnexpiredCookie(final HttpUrl url) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        return getCookie().stream()
                .filter(cookie -> cookie.matches(url))
                .filter(cookie -> cookie.expiresAt() > new Date().getTime())
                .collect(Collectors.toSet());
    }

    /**
     * @param url - request url
     * @return string value cached {@link Cookie} collection for the url
     */
    @EverythingIsNonNull
    public static String getCookieHeaderValue(final HttpUrl url) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        return getRequestUnexpiredCookie(url).stream()
                .map(cookie -> cookie.name() + "=" + cookie.value())
                .collect(Collectors.joining("; "));
    }

    /**
     * Add {@link Cookie} to {@link ThreadLocal} set with replacement by name, domain and path
     *
     * @param cookies - {@link Cookie} list to add
     */
    @EverythingIsNonNull
    public static void addCookie(final Cookie... cookies) {
        Utils.parameterRequireNonNull(cookies, COOKIES_PARAMETER);
        addCookie(true, cookies);
    }

    /**
     * Add {@link Cookie} to {@link ThreadLocal} set with/without replacement
     *
     * @param cookies - {@link Cookie} list to add
     * @param replace - flag for replacement by name, domain and path
     */
    @EverythingIsNonNull
    public static void addCookie(final boolean replace, final Cookie... cookies) {
        Utils.parameterRequireNonNull(cookies, COOKIES_PARAMETER);
        for (Cookie cookie : cookies) {
            Utils.parameterRequireNonNull(cookie, COOKIE_PARAMETER);
            if (replace) {
                getCookie().removeIf(c -> c.domain().equals(cookie.domain())
                        && c.path().equals(cookie.path())
                        && c.name().equals(cookie.name()));
            }
            getCookie().add(cookie);
        }
    }

    /**
     * Clear all Cookies for the {@link HttpUrl} host
     *
     * @param url - cookie {@link HttpUrl}
     */
    @EverythingIsNonNull
    public static void clearCookie(final HttpUrl url) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        clearCookie(url.url());
    }

    /**
     * Clear all Cookies for the {@link URL} host
     *
     * @param url - cookie {@link URL}
     */
    @EverythingIsNonNull
    public static void clearCookie(final URL url) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        getCookie().removeIf(cookie -> cookie.domain().equals(url.getHost()));
    }

    /**
     * Clear all Cookies for the host
     *
     * @param cookieName - cookie name
     */
    @EverythingIsNonNull
    public static void clearCookie(final String cookieName) {
        Utils.parameterRequireNonNull(cookieName, COOKIE_NAME_PARAMETER);
        getCookie().removeIf(cookie -> cookie.name().equals(cookieName));
    }

    /**
     * Clear all Cookies for the host
     *
     * @param cookieName - cookie name
     * @param url - cookie {@link URL}
     */
    @EverythingIsNonNull
    public static void clearCookie(final URL url, final String cookieName) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        clearCookie(url.getHost(), cookieName);
    }

    /**
     * Clear all Cookies for the host
     *
     * @param cookieName - cookie name
     * @param url - cookie {@link HttpUrl}
     */
    @EverythingIsNonNull
    public static void clearCookie(final HttpUrl url, final String cookieName) {
        Utils.parameterRequireNonNull(url, URL_PARAMETER);
        clearCookie(url.host(), cookieName);
    }

    /**
     * Clear all Cookies for the host
     *
     * @param cookieName - cookie name
     * @param domain - cookie domain
     */
    @EverythingIsNonNull
    public static void clearCookie(final String domain, final String cookieName) {
        Utils.parameterRequireNonNull(domain, DOMAIN_PARAMETER);
        Utils.parameterRequireNonNull(cookieName, COOKIE_NAME_PARAMETER);
        getCookie().removeIf(cookie -> cookie.name().equals(cookieName) && cookie.domain().equals(domain));
    }

    /**
     * Clear all Cookies
     */
    public static void clearCookie() {
        COOKIES.remove();
    }

    public static String toStringCookies() {
        return toStringCookies(Collectors.joining("\n"));
    }

    @EverythingIsNonNull
    public static String toStringCookies(Collector<CharSequence, ?, String> collector) {
        Utils.parameterRequireNonNull(collector, COLLECTOR_PARAMETER);
        return getCookie().stream().map(Cookie::toString).collect(collector);
    }

}
