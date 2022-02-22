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
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.TestClient;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.inteceptor.CompositeInterceptor;

/**
 * Utility class for building an HTTP client with a built-in Gson converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.02.2022
 */
public class GsonTestClient {

    /**
     * Builds an HTTP client with a built-in Gson converter
     *
     * @param baseUrl            - HTTP resource URL
     * @param interceptor        - {@link CompositeInterceptor} (okhttp network interceptor)
     * @param callAdapterFactory - {@link UniversalCallAdapterFactory} or heirs
     * @param cli                - client interface class
     * @param <C>                - client interface
     * @return built client
     */
    @EverythingIsNonNull
    public static <C> C build(final String baseUrl,
                              final Interceptor interceptor,
                              final CallAdapter.Factory callAdapterFactory,
                              final Class<C> cli) {
        return TestClient.build(baseUrl, interceptor, callAdapterFactory, new GsonConverterFactory(), cli);
    }

    /**
     * Utility class. Forbidden instantiation.
     */
    private GsonTestClient() {
        throw new UtilityClassException();
    }

}
