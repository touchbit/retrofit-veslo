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

package org.touchbit.retrofit.veslo.example.tests;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.touchbit.retrofit.veslo.example.client.PetApi;
import org.touchbit.retrofit.veslo.example.client.StoreApi;
import org.touchbit.retrofit.veslo.example.client.UserApi;
import org.touchbit.retrofit.veslo.example.client.transport.PetStoreInterceptor;
import retrofit2.Retrofit;
import veslo.AllureCallAdapterFactory;
import veslo.JacksonConverterFactory;

import java.util.Locale;

import static veslo.client.TrustSocketHelper.*;

@SuppressWarnings({"unused"})
public abstract class BaseTest {

    private static final String URL = System.getProperty("service_url", "https://petstore.swagger.io/");
    protected static final PetApi PET_API = createJacksonClient(PetApi.class);
    protected static final UserApi USER_API = createJacksonClient(UserApi.class);
    protected static final StoreApi STORE_API = createJacksonClient(StoreApi.class);

    static {
        Locale.setDefault(Locale.ENGLISH); // localisation (jakarta assertions)
    }

    private static <CLIENT> CLIENT createJacksonClient(final Class<CLIENT> clientClass) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        .addNetworkInterceptor(new PetStoreInterceptor())
                        .build())
                .baseUrl(URL)
                .addCallAdapterFactory(new AllureCallAdapterFactory())
//                .addCallAdapterFactory(new AllureCallAdapterFactory(ExampleCustomResponse::new))
                .addConverterFactory(new JacksonConverterFactory())
//                .addConverterFactory(new CustomJacksonConverterFactory())
                .build()
                .create(clientClass);
    }

    @BeforeEach
    public void logout() {
        USER_API.logout();
    }

}
