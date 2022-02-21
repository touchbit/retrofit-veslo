/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo;

import okhttp3.Interceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.TestClient;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.inteceptor.CompositeInterceptor;
import veslo.client.inteceptor.CookieAction;
import veslo.client.inteceptor.LoggingAction;
import veslo.client.response.DualResponse;

/**
 * Utility class for building an HTTP client for testing
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.02.2022
 */
@SuppressWarnings("unused")
public class Veslo4Test {

    /**
     * Default network interceptor with allure integration.
     * It is recommended to use your own interceptor.
     */
    public static final CompositeInterceptor A_INTERCEPTOR = new CompositeInterceptor()
            .withRequestInterceptActionsChain(CookieAction.INSTANCE, LoggingAction.INSTANCE, AllureAction.INSTANCE)
            .withResponseInterceptActionsChain(LoggingAction.INSTANCE, AllureAction.INSTANCE, CookieAction.INSTANCE);

    /**
     * Default network interceptor without allure integration.
     * It is recommended to use your own interceptor.
     */
    public static final CompositeInterceptor U_INTERCEPTOR = new CompositeInterceptor()
            .withRequestInterceptActionsChain(CookieAction.INSTANCE, LoggingAction.INSTANCE)
            .withResponseInterceptActionsChain(LoggingAction.INSTANCE, CookieAction.INSTANCE);

    /**
     * Creates an HTTP client for testing with a built-in Jackson2 converter.
     * {@link #U_INTERCEPTOR} is used as an interceptor (recommended using your own interceptor)
     * Client methods return type - {@link DualResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildJacksonClient(final String baseUrl,
                                                     final Class<CLIENT> cli) {
        return JacksonTestClient.build(baseUrl, U_INTERCEPTOR, new UniversalCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Jackson2 converter.
     * Client methods return type - {@link DualResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor).
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildJacksonClient(final String baseUrl,
                                                     final Interceptor interceptor,
                                                     final Class<CLIENT> cli) {
        return JacksonTestClient.build(baseUrl, interceptor, new UniversalCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Jackson2 converter and allure integration.
     * {@link #A_INTERCEPTOR} is used as an interceptor (recommended using your own interceptor).
     * Client methods return type - {@link AResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildAllureJacksonClient(final String baseUrl,
                                                           final Class<CLIENT> cli) {
        return JacksonTestClient.build(baseUrl, A_INTERCEPTOR, new AllureCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Jackson2 converter and allure integration.
     * Client methods return type - {@link AResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor).
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildAllureJacksonClient(final String baseUrl,
                                                           final Interceptor interceptor,
                                                           final Class<CLIENT> cli) {
        return JacksonTestClient.build(baseUrl, interceptor, new AllureCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Gson converter.
     * {@link #U_INTERCEPTOR} is used as an interceptor (recommended using your own interceptor)
     * Client methods return type - {@link DualResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildGsonClient(final String baseUrl,
                                                  final Class<CLIENT> cli) {
        return GsonTestClient.build(baseUrl, U_INTERCEPTOR, new UniversalCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Gson converter.
     * Client methods return type - {@link DualResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor).
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildGsonClient(final String baseUrl,
                                                  final Interceptor interceptor,
                                                  final Class<CLIENT> cli) {
        return GsonTestClient.build(baseUrl, interceptor, new UniversalCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Gson converter and allure integration.
     * {@link #A_INTERCEPTOR} is used as an interceptor (recommended using your own interceptor).
     * Client methods return type - {@link AResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildAllureGsonClient(final String baseUrl,
                                                        final Class<CLIENT> cli) {
        return GsonTestClient.build(baseUrl, A_INTERCEPTOR, new AllureCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing with a built-in Gson converter and allure integration.
     * Client methods return type - {@link AResponse}.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor).
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT buildAllureGsonClient(final String baseUrl,
                                                        final Interceptor interceptor,
                                                        final Class<CLIENT> cli) {
        return GsonTestClient.build(baseUrl, interceptor, new AllureCallAdapterFactory(), cli);
    }

    /**
     * Creates an HTTP client for testing.
     *
     * @param baseUrl            - HTTP resource URL.
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor).
     * @param callAdapterFactory - {@link UniversalCallAdapterFactory} or heirs.
     * @param converterFactory   - {@link ExtensionConverterFactory} or heirs.
     * @param cli                - client interface class.
     * @param <CLIENT>           - client interface.
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT build(final String baseUrl,
                                        final Interceptor interceptor,
                                        final CallAdapter.Factory callAdapterFactory,
                                        final Converter.Factory converterFactory,
                                        final Class<CLIENT> cli) {
        return TestClient.build(baseUrl, interceptor, callAdapterFactory, converterFactory, cli);
    }

    /**
     * Utility class. Forbidden instantiation.
     */
    private Veslo4Test() {
        throw new UtilityClassException();
    }

}
