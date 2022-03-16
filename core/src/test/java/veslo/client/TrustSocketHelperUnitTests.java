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

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.hamcrest.Matchers.emptyArray;
import static veslo.client.TrustSocketHelper.*;

@DisplayName("TrustSocketHelper.class unit tests")
public class TrustSocketHelperUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("getTrustAllSSLContext() return SSLContext if manager = TrustAllCertsManager")
    public void test1647377221164() {
        final SSLContext trustAllSSLContext = getTrustAllSSLContext(CONTEXT, TRUST_ALL_CERTS_MANAGER);
        assertNotNull(trustAllSSLContext);
    }

    @Test
    @DisplayName("getTrustAllSSLContext() return SSLContext if manager = null")
    public void test1647377432675() {
        final SSLContext trustAllSSLContext = getTrustAllSSLContext(CONTEXT, null);
        assertNotNull(trustAllSSLContext);
    }

    @Test
    @DisplayName("getTrustAllSSLContext() IllegalStateException if context = null")
    public void test1647377347579() {
        assertThrow(() -> getTrustAllSSLContext(null, TRUST_ALL_CERTS_MANAGER))
                .assertClass(IllegalStateException.class)
                .assertMessageIs("Unable to create null SSL context");
    }

    @Test
    @DisplayName("TRUST_ALL_CERTS_MANAGER")
    public void test1647377470408() {
        TRUST_ALL_CERTS_MANAGER.checkClientTrusted(null, null);
        TRUST_ALL_CERTS_MANAGER.checkServerTrusted(null, null);
        assertThat(TRUST_ALL_CERTS_MANAGER.getAcceptedIssuers(), emptyArray());
    }

    @Test
    @DisplayName("TRUST_ALL_HOSTNAME")
    public void test1647377537647() {
        assertTrue(TRUST_ALL_HOSTNAME.verify(null, null));
        assertTrue(TRUST_ALL_HOSTNAME.verify("foo", null));
    }

}
