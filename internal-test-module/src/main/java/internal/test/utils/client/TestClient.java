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

package internal.test.utils.client;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class TestClient {

    public static <CLI> CLI build(Class<CLI> client, CallAdapter.Factory caf, Converter.Factory cf) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new MockInterceptor())
                        .build())
                .baseUrl("http://localhost:15425")
                .addCallAdapterFactory(caf)
                .addConverterFactory(cf)
                .build()
                .create(client);
    }

}
