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

package veslo;

import internal.test.utils.BaseUnitTest;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static veslo.client.TrustSocketHelper.*;

public abstract class BaseFuncTest extends BaseUnitTest {

    protected static <CLI> CLI buildClient(final Class<CLI> clientClass,
                                           final Interceptor interceptor,
                                           final CallAdapter.Factory callAdapterFactory,
                                           final Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        .addInterceptor(interceptor)
                        .build())
                .baseUrl("http://localhost")
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build()
                .create(clientClass);
    }

}
