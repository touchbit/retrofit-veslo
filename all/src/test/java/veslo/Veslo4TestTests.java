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

import internal.test.utils.BaseUnitTest;
import okhttp3.Interceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import retrofit2.http.GET;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.inteceptor.CompositeInterceptor;
import veslo.client.response.DualResponse;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings({"unused", "ConstantConditions"})
@DisplayName("Veslo4Test.class unit tests")
public class Veslo4TestTests extends BaseUnitTest {

    private static final String URL = "http://localhost";
    private static final Interceptor INTERCEPTOR = new CompositeInterceptor();
    private static final UniversalCallAdapterFactory CA_FACTORY = new UniversalCallAdapterFactory();
    private static final ExtensionConverterFactory C_FACTORY = new ExtensionConverterFactory();
    private static final Class<Client> CLI_CLASS = Client.class;

    @Test
    @DisplayName("Utility class")
    public void test1643952397800() {
        assertUtilityClassException(Veslo4Test.class);
    }

    @Nested
    @DisplayName("#build() method tests")
    public class BuildMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1643952436037() {
            assertNPE(() -> Veslo4Test.build(null, INTERCEPTOR, CA_FACTORY, C_FACTORY, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.build(URL, null, CA_FACTORY, C_FACTORY, CLI_CLASS), "interceptor");
            assertNPE(() -> Veslo4Test.build(URL, INTERCEPTOR, null, C_FACTORY, CLI_CLASS), "callAdapterFactory");
            assertNPE(() -> Veslo4Test.build(URL, INTERCEPTOR, CA_FACTORY, null, CLI_CLASS), "converterFactory");
            assertNPE(() -> Veslo4Test.build(URL, INTERCEPTOR, CA_FACTORY, C_FACTORY, null), "clientClass");
        }

        @Test
        @DisplayName("Successful test client creation")
        public void test1643953029234() {
            final Client client = Veslo4Test.build(URL, INTERCEPTOR, CA_FACTORY, C_FACTORY, CLI_CLASS);
            assertThat(client, not(nullValue()));
        }

    }

    @Nested
    @DisplayName("#buildJacksonClient() method tests")
    public class BuildJacksonClientMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1643953158413() {
            assertNPE(() -> Veslo4Test.buildJacksonClient(null, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildJacksonClient(URL, null), "clientClass");
            assertNPE(() -> Veslo4Test.buildJacksonClient(null, INTERCEPTOR, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildJacksonClient(URL, null, CLI_CLASS), "interceptor");
            assertNPE(() -> Veslo4Test.buildJacksonClient(URL, INTERCEPTOR, null), "clientClass");
        }

        @Test
        @DisplayName("Successful test client creation")
        public void test1643953250016() {
            final Client client1 = Veslo4Test.buildJacksonClient(URL, CLI_CLASS);
            assertThat(client1, not(nullValue()));
            final Client client2 = Veslo4Test.buildJacksonClient(URL, INTERCEPTOR, CLI_CLASS);
            assertThat(client2, not(nullValue()));
        }

    }

    @Nested
    @DisplayName("#buildAllureJacksonClient() method tests")
    public class BuildAllureJacksonClientMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1643953317050() {
            assertNPE(() -> Veslo4Test.buildAllureJacksonClient(null, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildAllureJacksonClient(URL, null), "clientClass");
            assertNPE(() -> Veslo4Test.buildAllureJacksonClient(null, INTERCEPTOR, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildAllureJacksonClient(URL, null, CLI_CLASS), "interceptor");
            assertNPE(() -> Veslo4Test.buildAllureJacksonClient(URL, INTERCEPTOR, null), "clientClass");
        }

        @Test
        @DisplayName("Successful test client creation")
        public void test1643953336704() {
            final Client client1 = Veslo4Test.buildAllureJacksonClient(URL, CLI_CLASS);
            assertThat(client1, not(nullValue()));
            final Client client2 = Veslo4Test.buildAllureJacksonClient(URL, INTERCEPTOR, CLI_CLASS);
            assertThat(client2, not(nullValue()));
        }

    }

    @Nested
    @DisplayName("#buildGsonClient() method tests")
    public class BuildGsonClientMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1643953412428() {
            assertNPE(() -> Veslo4Test.buildGsonClient(null, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildGsonClient(URL, null), "clientClass");
            assertNPE(() -> Veslo4Test.buildGsonClient(null, INTERCEPTOR, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildGsonClient(URL, null, CLI_CLASS), "interceptor");
            assertNPE(() -> Veslo4Test.buildGsonClient(URL, INTERCEPTOR, null), "clientClass");
        }

        @Test
        @DisplayName("Successful test client creation")
        public void test1643953428704() {
            final Client client1 = Veslo4Test.buildGsonClient(URL, CLI_CLASS);
            assertThat(client1, not(nullValue()));
            final Client client2 = Veslo4Test.buildGsonClient(URL, INTERCEPTOR, CLI_CLASS);
            assertThat(client2, not(nullValue()));
        }

    }

    @Nested
    @DisplayName("#buildAllureGsonClient() method tests")
    public class BuildAllureGsonClientMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1643953460508() {
            assertNPE(() -> Veslo4Test.buildAllureGsonClient(null, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildAllureGsonClient(URL, null), "clientClass");
            assertNPE(() -> Veslo4Test.buildAllureGsonClient(null, INTERCEPTOR, CLI_CLASS), "baseUrl");
            assertNPE(() -> Veslo4Test.buildAllureGsonClient(URL, null, CLI_CLASS), "interceptor");
            assertNPE(() -> Veslo4Test.buildAllureGsonClient(URL, INTERCEPTOR, null), "clientClass");
        }

        @Test
        @DisplayName("Successful test client creation")
        public void test1643953474518() {
            final Client client1 = Veslo4Test.buildAllureGsonClient(URL, CLI_CLASS);
            assertThat(client1, not(nullValue()));
            final Client client2 = Veslo4Test.buildAllureGsonClient(URL, INTERCEPTOR, CLI_CLASS);
            assertThat(client2, not(nullValue()));
        }

    }

}
