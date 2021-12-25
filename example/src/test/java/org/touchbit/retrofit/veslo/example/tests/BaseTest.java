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

package org.touchbit.retrofit.veslo.example.tests;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.touchbit.retrofit.veslo.allure.AllureCallAdapterFactory;
import org.touchbit.retrofit.veslo.asserter.HeadersAsserter;
import org.touchbit.retrofit.veslo.asserter.ResponseAsserter;
import org.touchbit.retrofit.veslo.asserter.SoftlyAsserter;
import org.touchbit.retrofit.veslo.example.client.PetApi;
import org.touchbit.retrofit.veslo.example.client.StoreApi;
import org.touchbit.retrofit.veslo.example.client.UserApi;
import org.touchbit.retrofit.veslo.example.client.transport.AuthAction;
import org.touchbit.retrofit.veslo.example.client.transport.CustomCompositeInterceptor;
import org.touchbit.retrofit.veslo.example.client.transport.CustomJacksonConverterFactory;
import org.touchbit.retrofit.veslo.example.client.transport.ExampleCustomResponse;
import org.touchbit.retrofit.veslo.example.client.transport.querymap.LoginUserQueryMap;
import org.touchbit.retrofit.veslo.example.model.Status;
import retrofit2.Retrofit;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.touchbit.retrofit.veslo.client.TrustSocketHelper.*;

@SuppressWarnings({"SameParameterValue", "unused"})
public class BaseTest {

    private static final String URL = System.getProperty("service_url", "https://petstore.swagger.io/");
    protected static final PetApi PET_API = createJacksonClient(PetApi.class);
    protected static final UserApi USER_API = createJacksonClient(UserApi.class);
    protected static final StoreApi STORE_API = createJacksonClient(StoreApi.class);

    static {
        // localisation (jakarta assertions)
        Locale.setDefault(Locale.ENGLISH);
    }

    private static <CLIENT> CLIENT createJacksonClient(final Class<CLIENT> clientClass) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .followRedirects(true)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        .addInterceptor(new CustomCompositeInterceptor())
                        .build())
                .baseUrl(URL)
                .addCallAdapterFactory(new AllureCallAdapterFactory(ExampleCustomResponse::new))
                .addConverterFactory(new CustomJacksonConverterFactory())
                .build()
                .create(clientClass);
    }

    @AfterEach
    public void logout() {
        AuthAction.removeToken();
    }

    protected static void loginTestUser() {
        loginUser(LoginUserQueryMap.TEST_USER);
    }

    protected static void loginUser(LoginUserQueryMap namePass) {
        final Status status = USER_API.loginUser(namePass)
                .assertResponse(a -> a.assertHttpStatusCodeIs(200).assertSucBodyNotNull()).getSucDTO();
        // String token = status.getToken();
        // For this sample, uses the api key 'special-key'
        String token = "special-key";
        AuthAction.setToken(token);
    }

    protected void assertStatus200(ResponseAsserter<Status, ?, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(200)
                .assertSucBody(this::assertStatusModel, expected);
    }

    protected void assertStatus404(ResponseAsserter<?, Status, HeadersAsserter> asserter, Status expected) {
        asserter.assertHttpStatusCodeIs(404)
                .assertErrBody(this::assertStatusModel, expected);
    }

    protected void assertStatus(ResponseAsserter<?, Status, HeadersAsserter> asserter, int code, Status expected) {
        asserter.assertHttpStatusCodeIs(code)
                .assertErrBody(this::assertStatusModel, expected);
    }

    protected void assertStatusModel(SoftlyAsserter asserter, Status actual, Status expected) {
        asserter.softly(() -> assertThat(actual).isNotNull());
        asserter.softly(() -> assertThat(actual.code()).as("Status.code").isEqualTo(expected.code()));
        asserter.softly(() -> assertThat(actual.type()).as("Status.type").isEqualTo(expected.type()));
        asserter.softly(() -> assertThat(actual.message()).as("Status.message").isEqualTo(expected.message()));
    }

}
