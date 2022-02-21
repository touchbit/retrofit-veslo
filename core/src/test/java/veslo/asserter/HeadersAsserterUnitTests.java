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

package veslo.asserter;

import internal.test.utils.OkHttpTestUtils;
import okhttp3.Headers;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.BriefAssertionError;
import veslo.client.response.DualResponse;

import java.util.UUID;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.is;

@DisplayName("HeadersAsserter class tests")
public class HeadersAsserterUnitTests extends BaseCoreUnitTest {

    private static final String ANY_CONTENT = UUID.randomUUID().toString();
    private static final String EXP_CONTENT = UUID.randomUUID().toString();
    private static final String CONTAINS_CONTENT = EXP_CONTENT + ANY_CONTENT;
    private static final String ERR_MSG_PREFIX = "Collected the following errors:\n\nResponse header ";

    @Test
    @DisplayName("Constructor")
    public void test1639587530749() {
        final Headers headers1 = new HeadersAsserter(Headers.of(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT)).getHeaders();
        assertThat(headers1.get(HeadersAsserter.H_CONTENT_TYPE), is(EXP_CONTENT));
        final Response response = OkHttpTestUtils.getResponse();
        DualResponse<?, ?> dualResponse = new DualResponse<>("", "", response, "", arrayOf());
        final Headers headers2 = new HeadersAsserter(dualResponse).getHeaders();
        assertThat(headers2.get(HeadersAsserter.H_CONTENT_TYPE), is("text/plain"));
    }

    @Test
    @DisplayName("contentTypeIsPresent() positive")
    public void test1639065946709() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT);
        new HeadersAsserter(headers).contentTypeIsPresent().blame();
    }

    @Test
    @DisplayName("contentTypeIsPresent() negative")
    public void test1639065946716() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).contentTypeIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_CONTENT_TYPE));
    }

    @Test
    @DisplayName("contentTypeNotPresent() positive")
    public void test1639065946725() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).contentTypeNotPresent().blame();
    }

    @Test
    @DisplayName("contentTypeNotPresent() negative")
    public void test1639065946732() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT));
    }

    @Test
    @DisplayName("contentTypeIs() positive")
    public void test1639065946741() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT);
        new HeadersAsserter(headers).contentTypeIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentTypeIs() negative")
    public void test1639065946748() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("contentTypeContains() positive")
    public void test1639065946757() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).contentTypeContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentTypeContains() negative")
    public void test1639065946764() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_TYPE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentTypeContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_CONTENT_TYPE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginIsPresent() positive")
    public void test1639065946773() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginIsPresent().blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginIsPresent() negative")
    public void test1639065946780() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    @DisplayName("accessControlAllowOriginNotPresent() positive")
    public void test1639065946789() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).accessControlAllowOriginNotPresent().blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginNotPresent() negative")
    public void test1639065946796() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginIs() positive")
    public void test1639065946805() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginIs() negative")
    public void test1639065946812() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, CONTAINS_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT, CONTAINS_CONTENT));
    }

    @Test
    @DisplayName("accessControlAllowOriginContains() positive")
    public void test1639065946821() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, CONTAINS_CONTENT);
        new HeadersAsserter(headers).accessControlAllowOriginContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("accessControlAllowOriginContains() negative")
    public void test1639065946828() {
        Headers headers = Headers.of(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).accessControlAllowOriginContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_ACCESS_CONTROL_ALLOW_ORIGIN, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("varyIsPresent() positive")
    public void test1639065946837() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, EXP_CONTENT);
        new HeadersAsserter(headers).varyIsPresent().blame();
    }

    @Test
    @DisplayName("varyIsPresent() negative")
    public void test1639065946844() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).varyIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_VARY));
    }

    @Test
    @DisplayName("varyNotPresent() positive")
    public void test1639065946853() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).varyNotPresent().blame();
    }

    @Test
    @DisplayName("varyNotPresent() negative")
    public void test1639065946860() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_VARY, EXP_CONTENT));
    }

    @Test
    @DisplayName("varyIs() positive")
    public void test1639065946869() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, EXP_CONTENT);
        new HeadersAsserter(headers).varyIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("varyIs() negative")
    public void test1639065946876() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_VARY, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("varyContains() positive")
    public void test1639065946885() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, CONTAINS_CONTENT);
        new HeadersAsserter(headers).varyContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("varyContains() negative")
    public void test1639065946892() {
        Headers headers = Headers.of(HeadersAsserter.H_VARY, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).varyContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_VARY, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("setCookieIsPresent() positive")
    public void test1639065946901() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT);
        new HeadersAsserter(headers).setCookieIsPresent().blame();
    }

    @Test
    @DisplayName("setCookieIsPresent() negative")
    public void test1639065946908() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).setCookieIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_SET_COOKIE));
    }

    @Test
    @DisplayName("setCookieNotPresent() positive")
    public void test1639065946917() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).setCookieNotPresent().blame();
    }

    @Test
    @DisplayName("setCookieNotPresent() negative")
    public void test1639065946924() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT));
    }

    @Test
    @DisplayName("setCookieIs() positive")
    public void test1639065946933() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT);
        new HeadersAsserter(headers).setCookieIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("setCookieIs() negative")
    public void test1639065946940() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("setCookieContains() positive")
    public void test1639065946949() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).setCookieContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("setCookieContains() negative")
    public void test1639065946956() {
        Headers headers = Headers.of(HeadersAsserter.H_SET_COOKIE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).setCookieContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_SET_COOKIE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("serverIsPresent() positive")
    public void test1639065946965() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, EXP_CONTENT);
        new HeadersAsserter(headers).serverIsPresent().blame();
    }

    @Test
    @DisplayName("serverIsPresent() negative")
    public void test1639065946972() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).serverIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_SERVER));
    }

    @Test
    @DisplayName("serverNotPresent() positive")
    public void test1639065946981() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).serverNotPresent().blame();
    }

    @Test
    @DisplayName("serverNotPresent() negative")
    public void test1639065946988() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_SERVER, EXP_CONTENT));
    }

    @Test
    @DisplayName("serverIs() positive")
    public void test1639065946997() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, EXP_CONTENT);
        new HeadersAsserter(headers).serverIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("serverIs() negative")
    public void test1639065947004() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_SERVER, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("serverContains() positive")
    public void test1639065947013() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, CONTAINS_CONTENT);
        new HeadersAsserter(headers).serverContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("serverContains() negative")
    public void test1639065947020() {
        Headers headers = Headers.of(HeadersAsserter.H_SERVER, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).serverContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_SERVER, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("keepAliveIsPresent() positive")
    public void test1639065947029() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT);
        new HeadersAsserter(headers).keepAliveIsPresent().blame();
    }

    @Test
    @DisplayName("keepAliveIsPresent() negative")
    public void test1639065947036() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).keepAliveIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_KEEP_ALIVE));
    }

    @Test
    @DisplayName("keepAliveNotPresent() positive")
    public void test1639065947045() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).keepAliveNotPresent().blame();
    }

    @Test
    @DisplayName("keepAliveNotPresent() negative")
    public void test1639065947052() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT));
    }

    @Test
    @DisplayName("keepAliveIs() positive")
    public void test1639065947061() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT);
        new HeadersAsserter(headers).keepAliveIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("keepAliveIs() negative")
    public void test1639065947068() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("keepAliveContains() positive")
    public void test1639065947077() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, CONTAINS_CONTENT);
        new HeadersAsserter(headers).keepAliveContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("keepAliveContains() negative")
    public void test1639065947084() {
        Headers headers = Headers.of(HeadersAsserter.H_KEEP_ALIVE, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).keepAliveContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_KEEP_ALIVE, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("connectionIsPresent() positive")
    public void test1639065947093() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, EXP_CONTENT);
        new HeadersAsserter(headers).connectionIsPresent().blame();
    }

    @Test
    @DisplayName("connectionIsPresent() negative")
    public void test1639065947100() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).connectionIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_CONNECTION));
    }

    @Test
    @DisplayName("connectionNotPresent() positive")
    public void test1639065947109() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).connectionNotPresent().blame();
    }

    @Test
    @DisplayName("connectionNotPresent() negative")
    public void test1639065947116() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_CONNECTION, EXP_CONTENT));
    }

    @Test
    @DisplayName("connectionIs() positive")
    public void test1639065947125() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, EXP_CONTENT);
        new HeadersAsserter(headers).connectionIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("connectionIs() negative")
    public void test1639065947132() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_CONNECTION, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("connectionContains() positive")
    public void test1639065947141() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, CONTAINS_CONTENT);
        new HeadersAsserter(headers).connectionContains(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("connectionContains() negative")
    public void test1639065947148() {
        Headers headers = Headers.of(HeadersAsserter.H_CONNECTION, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).connectionContains(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage(HeadersAsserter.H_CONNECTION, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("contentEncodingIsPresent() positive")
    public void test1639065947157() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).contentEncodingIsPresent().blame();
    }

    @Test
    @DisplayName("contentEncodingIsPresent() negative")
    public void test1639065947164() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_CONTENT_ENCODING));
    }

    @Test
    @DisplayName("contentEncodingNotPresent() positive")
    public void test1639065947173() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).contentEncodingNotPresent().blame();
    }

    @Test
    @DisplayName("contentEncodingNotPresent() negative")
    public void test1639065947180() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_ENCODING, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_CONTENT_ENCODING, EXP_CONTENT));
    }

    @Test
    @DisplayName("contentEncodingIs() positive")
    public void test1639065947189() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).contentEncodingIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("contentEncodingIs() negative")
    public void test1639065947196() {
        Headers headers = Headers.of(HeadersAsserter.H_CONTENT_ENCODING, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).contentEncodingIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_CONTENT_ENCODING, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("transferEncodingIsPresent() positive")
    public void test1639065947205() {
        Headers headers = Headers.of(HeadersAsserter.H_TRANSFER_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).transferEncodingIsPresent().blame();
    }

    @Test
    @DisplayName("transferEncodingIsPresent() negative")
    public void test1639065947212() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_TRANSFER_ENCODING));
    }

    @Test
    @DisplayName("transferEncodingNotPresent() positive")
    public void test1639065947221() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).transferEncodingNotPresent().blame();
    }

    @Test
    @DisplayName("transferEncodingNotPresent() negative")
    public void test1639065947228() {
        Headers headers = Headers.of(HeadersAsserter.H_TRANSFER_ENCODING, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_TRANSFER_ENCODING, EXP_CONTENT));
    }

    @Test
    @DisplayName("transferEncodingIs() positive")
    public void test1639065947237() {
        Headers headers = Headers.of(HeadersAsserter.H_TRANSFER_ENCODING, EXP_CONTENT);
        new HeadersAsserter(headers).transferEncodingIs(EXP_CONTENT).blame();
    }

    @Test
    @DisplayName("transferEncodingIs() negative")
    public void test1639065947244() {
        Headers headers = Headers.of(HeadersAsserter.H_TRANSFER_ENCODING, ANY_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).transferEncodingIs(EXP_CONTENT).blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage(HeadersAsserter.H_TRANSFER_ENCODING, EXP_CONTENT, ANY_CONTENT));
    }

    @Test
    @DisplayName("eTagIsPresent() positive")
    public void test1639065947253() {
        Headers headers = Headers.of(HeadersAsserter.H_ETAG, EXP_CONTENT);
        new HeadersAsserter(headers).eTagIsPresent().blame();
    }

    @Test
    @DisplayName("eTagIsPresent() negative")
    public void test1639065947260() {
        Headers headers = Headers.of();
        assertThrow(() -> new HeadersAsserter(headers).eTagIsPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage(HeadersAsserter.H_ETAG));
    }

    @Test
    @DisplayName("eTagNotPresent() positive")
    public void test1639065947269() {
        Headers headers = Headers.of();
        new HeadersAsserter(headers).eTagNotPresent().blame();
    }

    @Test
    @DisplayName("eTagNotPresent() negative")
    public void test1639065947276() {
        Headers headers = Headers.of(HeadersAsserter.H_ETAG, EXP_CONTENT);
        assertThrow(() -> new HeadersAsserter(headers).eTagNotPresent().blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage(HeadersAsserter.H_ETAG, EXP_CONTENT));
    }

    @Test
    @DisplayName("assertHeaderNotPresent() positive")
    public void test1639065947285() {
        new HeadersAsserter(Headers.of()).assertHeaderNotPresent("any").blame();
    }

    @Test
    @DisplayName("assertHeaderNotPresent() negative")
    public void test1639065947291() {
        Headers headers = Headers.of("any", "test1637301041892");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderNotPresent("any").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getNotPresentAssertMessage("any", "test1637301041892"));
    }

    @Test
    @DisplayName("assertHeaderIsPresent() positive")
    public void test1639065947300() {
        Headers headers = Headers.of("any", "test1637301140882");
        new HeadersAsserter(headers).assertHeaderIsPresent("any").blame();
    }

    @Test
    @DisplayName("assertHeaderIsPresent() negative")
    public void test1639065947307() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderIsPresent("any").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsPresentAssertMessage("any"));
    }

    @Test
    @DisplayName("assertHeaderIs() positive")
    public void test1639065947315() {
        Headers headers = Headers.of("any", "test1637301314368");
        new HeadersAsserter(headers).assertHeaderIs("any", "test1637301314368").blame();
    }

    @Test
    @DisplayName("assertHeaderIs() negative (null)")
    public void test1639065947322() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderIs("any", "test1637301325502").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage("any", "test1637301325502", null));
    }

    @Test
    @DisplayName("assertHeaderIs() negative (not match)")
    public void test1639065947330() {
        Headers headers = Headers.of("any", "test1637301328290");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderIs("any", "1637301328290").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getIsAssertMessage("any", "1637301328290", "test1637301328290"));
    }

    @Test
    @DisplayName("assertHeaderContains() positive")
    public void test1639065947339() {
        Headers headers = Headers.of("any", "TEST1637301565659");
        new HeadersAsserter(headers).assertHeaderContains("any", "test16").blame();
    }

    @Test
    @DisplayName("assertHeaderContains() negative (null)")
    public void test1639065947346() {
        assertThrow(() -> new HeadersAsserter(Headers.of()).assertHeaderContains("any", "test1637301577263").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage("any", "test1637301577263", null));
    }

    @Test
    @DisplayName("assertHeaderContains() negative (not match)")
    public void test1639065947354() {
        Headers headers = Headers.of("any", "test_1637301589744");
        assertThrow(() -> new HeadersAsserter(headers).assertHeaderContains("any", "test1637301589744").blame())
                .assertClass(BriefAssertionError.class)
                .assertMessageIs(getContainsAssertMessage("any", "test1637301589744", "test_1637301589744"));
    }

    @Test
    @DisplayName("#addErrors() and #getErrors()")
    public void test1639065947363() {
        Headers headers = Headers.of("any", "test1637494229934");
        final HeadersAsserter headersAsserter = new HeadersAsserter(headers);
        assertThat("", headersAsserter.getErrors().size(), is(0));
        headersAsserter.addErrors(new BriefAssertionError());
        assertThat("", headersAsserter.getErrors().size(), is(1));
    }

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