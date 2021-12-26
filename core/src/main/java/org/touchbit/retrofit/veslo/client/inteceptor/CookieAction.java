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
import retrofit2.internal.EverythingIsNonNull;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        final HttpUrl url = response.request().url();
        final Headers headers = response.headers();
        Cookie.parseAll(url, headers).forEach(CookieAction::addCookie);
        return response;
    }

    /**
     * @return all cached {@link Cookie} collection
     */
    public static Set<Cookie> getCookie() {
        return COOKIES.get();
    }

    /**
     * @param url - request url
     * @return cached {@link Cookie} collection for the url
     */
    public static Set<Cookie> getCookie(final HttpUrl url) {
        return getCookie().stream()
                .filter(cookie -> cookie.matches(url))
                .filter(cookie -> cookie.expiresAt() > new Date().getTime())
                .collect(Collectors.toSet());
    }

    /**
     * @param url - request url
     * @return string value cached {@link Cookie} collection for the url
     */
    public static String getCookieHeaderValue(final HttpUrl url) {
        return getCookie(url).stream()
                .map(cookie -> cookie.name() + "=" + cookie.value())
                .collect(Collectors.joining("; "));
    }

    /**
     * @param cookie - {@link Cookie}
     */
    public static void addCookie(final Cookie cookie) {
        getCookie().add(cookie);
    }

    /**
     * Clear all Cookies for the {@link HttpUrl} host
     *
     * @param url - {@link HttpUrl}
     */
    public static void clearCookie(final HttpUrl url) {
        clearCookie(url.host());
    }

    /**
     * Clear all Cookies for the {@link URL} host
     *
     * @param url - {@link URL}
     */
    public static void clearCookie(final URL url) {
        clearCookie(url.getHost());
    }

    /**
     * Clear all Cookies for the host
     *
     * @param host - host/domain
     */
    public static void clearCookie(final String host) {
        getCookie().removeIf(cookie -> cookie.domain().equals(host));
    }

    /**
     * Clear all Cookies
     */
    public static void clearCookie() {
        getCookie().clear();
    }

}
