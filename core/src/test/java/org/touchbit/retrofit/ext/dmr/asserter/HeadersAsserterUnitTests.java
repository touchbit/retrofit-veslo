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

package org.touchbit.retrofit.ext.dmr.asserter;

import okhttp3.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.touchbit.retrofit.ext.dmr.asserter.HeadersAsserter.*;

@DisplayName("HeadersAsserter class tests")
public class HeadersAsserterUnitTests {

    private static final String ANY_CONTENT = UUID.randomUUID().toString();
    private static final String EXP_CONTENT = UUID.randomUUID().toString();
    private static final String CONTAINS_CONTENT = EXP_CONTENT + ANY_CONTENT;

    @Test
    @DisplayName("contentTypeIsPresent() positive")
    public void test1637280314015() {
        Headers headers = Headers.of(H_CONTENT_TYPE, EXP_CONTENT);
        new HeadersAsserter(headers).contentTypeIsPresent().blame();
    }

    @Test
    @DisplayName("contentTypeIsPresent() negative")
    public void test1637280332969() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).contentTypeIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_CONTENT_TYPE));
    }

    @Test
    @DisplayName("contentTypeNotPresent() positive")
    public void test1637280361381() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).contentTypeNotPresent().blame();
    }

    @Test
    @DisplayName("contentTypeNotPresent() negative")
    public void test1637280370483() {
        Headers headers = Headers.of(H_CONTENT_TYPE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_CONTENT_TYPE, EXP_CONTENT));
    }

    @Test
    @DisplayName("contentTypeIs() positive")
    public void test1637280493428() {
        Headers headers = Headers.of(H_CONTENT_TYPE, EXP_CONTENT);
        new HeadersAsserter(headers).contentTypeIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentTypeIs() negative")
    public void test1637280495711() {
        Headers headers = Headers.of(H_CONTENT_TYPE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_CONTENT_TYPE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("contentTypeContains() positive")
    public void test1637280497867() {
        Headers headers = Headers.of(H_CONTENT_TYPE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).contentTypeContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentTypeContains() negative")
    public void test1637280500485() {
        Headers headers = Headers.of(H_CONTENT_TYPE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_CONTENT_TYPE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginIsPresent() positive")
    public void test1637293284176() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginIsPresent().blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginIsPresent() negative")
    public void test1637293287754() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    @DisplayName("accessControlAllowOriginNotPresent() positive")
    public void test1637293290827() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).accessControlAllowOriginNotPresent().blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginNotPresent() negative")
    public void test1637293294716() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginIs() positive")
    public void test1637293298384() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginIs() negative")
    public void test1637293301265() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, CONTAINS_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT, CONTAINS_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginContains() positive")
    public void test1637293304796() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, CONTAINS_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginContains() negative")
    public void test1637293307309() {
        Headers headers = Headers.of(H_ACCESS_CONTROL_ALLOW_ORIGIN, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("varyIsPresent() positive")
    public void test1637294408531() {
        Headers headers = Headers.of(H_VARY, EXP_CONTENT);
        new HeadersAsserter(headers).varyIsPresent().blame();
    }

    @Test
    @DisplayName("varyIsPresent() negative")
    public void test1637294414396() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).varyIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_VARY));
    }

    @Test
    @DisplayName("varyNotPresent() positive")
    public void test1637294418143() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).varyNotPresent().blame();
    }

    @Test
    @DisplayName("varyNotPresent() negative")
    public void test1637294420909() {
        Headers headers = Headers.of(H_VARY, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_VARY, EXP_CONTENT));
    }

    @Test
    @DisplayName("varyIs() positive")
    public void test1637294423255() {
        Headers headers = Headers.of(H_VARY, EXP_CONTENT);
        new HeadersAsserter(headers).varyIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("varyIs() negative")
    public void test1637294426504() {
        Headers headers = Headers.of(H_VARY, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_VARY, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("varyContains() positive")
    public void test1637294429463() {
        Headers headers = Headers.of(H_VARY, CONTAINS_CONTENT);
        new HeadersAsserter(headers).varyContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("varyContains() negative")
    public void test1637294433526() {
        Headers headers = Headers.of(H_VARY, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_VARY, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("setCookieIsPresent() positive")
    public void test1637294593539() {
        Headers headers = Headers.of(H_SET_COOKIE, EXP_CONTENT);
        new HeadersAsserter(headers).setCookieIsPresent().blame();
    }

    @Test
    @DisplayName("setCookieIsPresent() negative")
    public void test1637294596253() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).setCookieIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_SET_COOKIE));
    }

    @Test
    @DisplayName("setCookieNotPresent() positive")
    public void test1637294598949() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).setCookieNotPresent().blame();
    }

    @Test
    @DisplayName("setCookieNotPresent() negative")
    public void test1637294601193() {
        Headers headers = Headers.of(H_SET_COOKIE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_SET_COOKIE, EXP_CONTENT));
    }

    @Test
    @DisplayName("setCookieIs() positive")
    public void test1637294604027() {
        Headers headers = Headers.of(H_SET_COOKIE, EXP_CONTENT);
        new HeadersAsserter(headers).setCookieIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("setCookieIs() negative")
    public void test1637294606667() {
        Headers headers = Headers.of(H_SET_COOKIE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_SET_COOKIE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("setCookieContains() positive")
    public void test1637294609177() {
        Headers headers = Headers.of(H_SET_COOKIE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).setCookieContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("setCookieContains() negative")
    public void test1637294611823() {
        Headers headers = Headers.of(H_SET_COOKIE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_SET_COOKIE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("serverIsPresent() positive")
    public void test1637294640381() {
        Headers headers = Headers.of(H_SERVER, EXP_CONTENT);
        new HeadersAsserter(headers).serverIsPresent().blame();
    }

    @Test
    @DisplayName("serverIsPresent() negative")
    public void test1637294642598() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).serverIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_SERVER));
    }

    @Test
    @DisplayName("serverNotPresent() positive")
    public void test1637294645750() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).serverNotPresent().blame();
    }

    @Test
    @DisplayName("serverNotPresent() negative")
    public void test1637294651757() {
        Headers headers = Headers.of(H_SERVER, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_SERVER, EXP_CONTENT));
    }

    @Test
    @DisplayName("serverIs() positive")
    public void test1637294654896() {
        Headers headers = Headers.of(H_SERVER, EXP_CONTENT);
        new HeadersAsserter(headers).serverIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("serverIs() negative")
    public void test1637294657617() {
        Headers headers = Headers.of(H_SERVER, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_SERVER, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("serverContains() positive")
    public void test1637294660416() {
        Headers headers = Headers.of(H_SERVER, CONTAINS_CONTENT);
        new HeadersAsserter(headers).serverContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("serverContains() negative")
    public void test1637294662497() {
        Headers headers = Headers.of(H_SERVER, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_SERVER, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("keepAliveIsPresent() positive")
    public void test1637294737304() {
        Headers headers = Headers.of(H_KEEP_ALIVE, EXP_CONTENT);
        new HeadersAsserter(headers).keepAliveIsPresent().blame();
    }

    @Test
    @DisplayName("keepAliveIsPresent() negative")
    public void test1637294739252() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).keepAliveIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_KEEP_ALIVE));
    }

    @Test
    @DisplayName("keepAliveNotPresent() positive")
    public void test1637294741989() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).keepAliveNotPresent().blame();
    }

    @Test
    @DisplayName("keepAliveNotPresent() negative")
    public void test1637294744289() {
        Headers headers = Headers.of(H_KEEP_ALIVE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_KEEP_ALIVE, EXP_CONTENT));
    }

    @Test
    @DisplayName("keepAliveIs() positive")
    public void test1637294748304() {
        Headers headers = Headers.of(H_KEEP_ALIVE, EXP_CONTENT);
        new HeadersAsserter(headers).keepAliveIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("keepAliveIs() negative")
    public void test1637294751078() {
        Headers headers = Headers.of(H_KEEP_ALIVE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_KEEP_ALIVE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("keepAliveContains() positive")
    public void test1637294753992() {
        Headers headers = Headers.of(H_KEEP_ALIVE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).keepAliveContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("keepAliveContains() negative")
    public void test1637294756916() {
        Headers headers = Headers.of(H_KEEP_ALIVE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_KEEP_ALIVE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("connectionIsPresent() positive")
    public void test1637294810480() {
        Headers headers = Headers.of(H_CONNECTION, EXP_CONTENT);
        new HeadersAsserter(headers).connectionIsPresent().blame();
    }

    @Test
    @DisplayName("connectionIsPresent() negative")
    public void test1637294812711() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).connectionIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_CONNECTION));
    }

    @Test
    @DisplayName("connectionNotPresent() positive")
    public void test1637294815596() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).connectionNotPresent().blame();
    }

    @Test
    @DisplayName("connectionNotPresent() negative")
    public void test1637294817842() {
        Headers headers = Headers.of(H_CONNECTION, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_CONNECTION, EXP_CONTENT));
    }

    @Test
    @DisplayName("connectionIs() positive")
    public void test1637294820511() {
        Headers headers = Headers.of(H_CONNECTION, EXP_CONTENT);
        new HeadersAsserter(headers).connectionIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("connectionIs() negative")
    public void test1637294823123() {
        Headers headers = Headers.of(H_CONNECTION, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_CONNECTION, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("connectionContains() positive")
    public void test1637294825667() {
        Headers headers = Headers.of(H_CONNECTION, CONTAINS_CONTENT);
        new HeadersAsserter(headers).connectionContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("connectionContains() negative")
    public void test1637294828459() {
        Headers headers = Headers.of(H_CONNECTION, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionContains(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage(H_CONNECTION, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("contentEncodingIsPresent() positive")
    public void test1637294863925() {
        Headers headers = Headers.of(H_CONTENT_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).contentEncodingIsPresent().blame();
    }

    @Test
    @DisplayName("contentEncodingIsPresent() negative")
    public void test1637294866002() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_CONTENT_ENCODING));
    }

    @Test
    @DisplayName("contentEncodingNotPresent() positive")
    public void test1637294868679() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).contentEncodingNotPresent().blame();
    }

    @Test
    @DisplayName("contentEncodingNotPresent() negative")
    public void test1637294870901() {
        Headers headers = Headers.of(H_CONTENT_ENCODING, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_CONTENT_ENCODING, EXP_CONTENT));
    }

    @Test
    @DisplayName("contentEncodingIs() positive")
    public void test1637294874091() {
        Headers headers = Headers.of(H_CONTENT_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).contentEncodingIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentEncodingIs() negative")
    public void test1637294876577() {
        Headers headers = Headers.of(H_CONTENT_ENCODING, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_CONTENT_ENCODING, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("transferEncodingIsPresent() positive")
    public void test1637294917410() {
        Headers headers = Headers.of(H_TRANSFER_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).transferEncodingIsPresent().blame();
    }

    @Test
    @DisplayName("transferEncodingIsPresent() negative")
    public void test1637294920410() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_TRANSFER_ENCODING));
    }

    @Test
    @DisplayName("transferEncodingNotPresent() positive")
    public void test1637294923395() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).transferEncodingNotPresent().blame();
    }

    @Test
    @DisplayName("transferEncodingNotPresent() negative")
    public void test1637294926127() {
        Headers headers = Headers.of(H_TRANSFER_ENCODING, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_TRANSFER_ENCODING, EXP_CONTENT));
    }

    @Test
    @DisplayName("transferEncodingIs() positive")
    public void test1637294928914() {
        Headers headers = Headers.of(H_TRANSFER_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).transferEncodingIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("transferEncodingIs() negative")
    public void test1637294930987() {
        Headers headers = Headers.of(H_TRANSFER_ENCODING, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingIs(EXP_CONTENT).blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage(H_TRANSFER_ENCODING, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("eTagIsPresent() positive")
    public void test1637295004586() {
        Headers headers = Headers.of(H_ETAG, EXP_CONTENT);
        new HeadersAsserter(headers).eTagIsPresent().blame();
    }

    @Test
    @DisplayName("eTagIsPresent() negative")
    public void test1637295007925() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).eTagIsPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage(H_ETAG));
    }

    @Test
    @DisplayName("eTagNotPresent() positive")
    public void test1637295010706() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).eTagNotPresent().blame();
    }

    @Test
    @DisplayName("eTagNotPresent() negative")
    public void test1637295016346() {
        Headers headers = Headers.of(H_ETAG, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).eTagNotPresent().blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage(H_ETAG, EXP_CONTENT));
    }

    @Test
    @DisplayName("assertHeaderNotPresent() positive")
    public void test1637300974143() {
        new HeadersAsserter(Headers.of()).assertHeaderNotPresent("any").blame();
    }

    @Test
    @DisplayName("assertHeaderNotPresent() negative")
    public void test1637301041892() {
        Headers headers = Headers.of("any", "test1637301041892");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderNotPresent("any").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getNotPresentAssertMessage("any", "test1637301041892"));
    }

    @Test
    @DisplayName("assertHeaderIsPresent() positive")
    public void test1637301140882() {
        Headers headers = Headers.of("any", "test1637301140882");
        new HeadersAsserter(headers).assertHeaderIsPresent("any").blame();
    }

    @Test
    @DisplayName("assertHeaderIsPresent() negative")
    public void test1637301171263() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderIsPresent("any").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsPresentAssertMessage("any"));
    }

    @Test
    @DisplayName("assertHeaderIs() positive")
    public void test1637301314368() {
        Headers headers = Headers.of("any", "test1637301314368");
        new HeadersAsserter(headers).assertHeaderIs("any", "test1637301314368").blame();
    }

    @Test
    @DisplayName("assertHeaderIs() negative (null)")
    public void test1637301325502() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderIs("any", "test1637301325502").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage("any", "test1637301325502", null));
    }

    @Test
    @DisplayName("assertHeaderIs() negative (not match)")
    public void test1637301328290() {
        Headers headers = Headers.of("any", "test1637301328290");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderIs("any", "1637301328290").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getIsAssertMessage("any", "1637301328290", "test1637301328290"));
    }

    @Test
    @DisplayName("assertHeaderContains() positive")
    public void test1637301565659() {
        Headers headers = Headers.of("any", "TEST1637301565659");
        new HeadersAsserter(headers).assertHeaderContains("any", "test16").blame();
    }

    @Test
    @DisplayName("assertHeaderContains() negative (null)")
    public void test1637301577263() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderContains("any", "test1637301577263").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage("any", "test1637301577263", null));
    }

    @Test
    @DisplayName("assertHeaderContains() negative (not match)")
    public void test1637301589744() {
        Headers headers = Headers.of("any", "test_1637301589744");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderContains("any", "test1637301589744").blame())
                .assertThrowClassIs(AssertionError.class)
                .assertThrowMessageIs(getContainsAssertMessage("any", "test1637301589744", "test_1637301589744"));
    }

    private static final String ERR_MSG_PREFIX = "The response contains the following errors:\nResponse header ";

    private String getIsAssertMessage(String header, String exp, String act) {
        return ERR_MSG_PREFIX + "'" + header + "'\nExpected: is '" + exp + "'\n  Actual: " + act;
    }

    private String getContainsAssertMessage(String header, String exp, String act) {
        return ERR_MSG_PREFIX + "'" + header + "'\nExpected: contains '" + exp + "'\n  Actual: " + act;
    }

    private String getNotPresentAssertMessage(String header, String act) {
        return ERR_MSG_PREFIX + "'" + header + "'\nExpected: not present\n  Actual: " + act;
    }

    private String getIsPresentAssertMessage(String header) {
        return ERR_MSG_PREFIX + "'" + header + "'\nExpected: is present\n  Actual: null";
    }

}
