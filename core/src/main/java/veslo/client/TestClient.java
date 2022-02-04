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

package veslo.client;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.UtilityClassException;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.inteceptor.CompositeInterceptor;
import veslo.util.Utils;

import static veslo.client.TrustSocketHelper.*;

/**
 * Utility class for building an HTTP client for testing
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.02.2022
 */
public class TestClient {

    /**
     * Creates an HTTP client for testing
     *
     * @param baseUrl            - HTTP resource URL
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor)
     * @param callAdapterFactory - {@link UniversalCallAdapterFactory} or heirs
     * @param converterFactory   - {@link ExtensionConverterFactory} or heirs
     * @param clientClass                - client interface class
     * @param <CLIENT>           - client interface
     * @return built client
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT build(final String baseUrl,
                                        final Interceptor interceptor,
                                        final CallAdapter.Factory callAdapterFactory,
                                        final Converter.Factory converterFactory,
                                        final Class<CLIENT> clientClass) {
        Utils.parameterRequireNonNull(baseUrl, "baseUrl");
        Utils.parameterRequireNonNull(interceptor, "interceptor");
        Utils.parameterRequireNonNull(callAdapterFactory, "callAdapterFactory");
        Utils.parameterRequireNonNull(converterFactory, "converterFactory");
        Utils.parameterRequireNonNull(clientClass, "clientClass");
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        // Configure this client to follow redirects
                        // (HTTP status 301, 302...).
                        .followRedirects(true)
                        // follow redirects (httpS -> http and http -> httpS)
                        // (for test environment)
                        .followSslRedirects(true)
                        // instead of adding self signed certificates to the keystore
                        // (for test environment)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        // Interceptor with your call handling rules
                        // (include `follow redirects`)
                        .addNetworkInterceptor(interceptor)
                        .build())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build()
                .create(clientClass);
    }

    /**
     * Utility class. Forbidden instantiation.
     */
    private TestClient() {
        throw new UtilityClassException();
    }

}
