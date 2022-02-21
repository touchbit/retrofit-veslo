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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import veslo.BaseCoreUnitTest;
import veslo.BriefAssertionError;
import veslo.client.response.DualResponse;
import veslo.client.response.IDualResponse;
import veslo.example.dto.ErrDTO;
import veslo.example.dto.SucDTO;
import veslo.util.TripleConsumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static internal.test.utils.TestUtils.arrayOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
@DisplayName("ResponseAsserter.class unit tests")
public class ResponseAsserterUnitTests extends BaseCoreUnitTest {

    private static final HeadersAsserter EMPTY_HEADER_ASSERTER = new HeadersAsserter(Headers.of());
    private static final IDualResponse<Object, Object> RESPONSE = mock(IDualResponse.class);
    private static final Consumer<SucDTO> NULL_CONSUMER_1 = null;
    private static final BiConsumer<SucDTO, SucDTO> NULL_CONSUMER_2 = null;
    private static final BiConsumer<SoftlyAsserter, SucDTO> NULL_CONSUMER_3 = null;
    private static final TripleConsumer<SoftlyAsserter, SucDTO, SucDTO> NULL_CONSUMER_4 = null;
    private static final BiConsumer<SucDTO, SucDTO> CONSUMER_2 = (SucDTO o1, SucDTO o2) -> {
    };
    private static final TripleConsumer<SoftlyAsserter, SucDTO, SucDTO> CONSUMER_4 = (o1, o2, o3) -> {
    };

    @Nested
    @DisplayName("Constructor tests")
    public class ResponseAsserterConstructorTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639065947424() {
            assertNPE(() -> new ResponseAsserter<>(null, EMPTY_HEADER_ASSERTER), "response");
            assertNPE(() -> new ResponseAsserter<>(RESPONSE, null), "headersAsserter");
        }

        @Test
        @DisplayName("Get constructor parameters")
        public void test1639253809035() {
            final ResponseAsserter asserter = new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER);
            assertThat(asserter.getHeadersAsserter(), is(EMPTY_HEADER_ASSERTER));
            assertThat(asserter.getResponse(), is(RESPONSE));
        }

    }

    @Nested
    @DisplayName("#blame() method tests")
    public class BlameMethodTests {

        @Test
        @DisplayName("#blame() without BriefAssertionError if no errors")
        public void test1639254058024() {
            assertThat(new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER).blame(), notNullValue());
        }

        @Test
        @DisplayName("#blame() with BriefAssertionError if has errors")
        public void test1639254060794() {
            final ResponseAsserter responseAsserter = new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER);
            responseAsserter.addErrors(new RuntimeException("test1639254060794"));
            assertThrow(responseAsserter::blame)
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\ntest1639254060794");
        }

    }

    @Nested
    @DisplayName("#assertHeaders(Consumer) method tests")
    public class AssertHeadersMethodTests {

        @Test
        @DisplayName("All parameters required")
        public void test1639065947466() {
            final ResponseAsserter responseAsserter = new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER);
            assertNPE(() -> responseAsserter.assertHeaders(null), "headerAsserterConsumer");
        }

        @Test
        @DisplayName("#assertHeaders(Consumer) BriefAssertionError is not thrown if headers no errors (Closeable)")
        public void test1639065947474() {
            new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER)
                    .assertHeaders(HeadersAsserter::contentTypeNotPresent)
                    .blame();
        }

        @Test
        @DisplayName("#assertHeaders(Consumer) BriefAssertionError is not thrown if headers has errors (Closeable)")
        public void test1639065947484() {
            final ResponseAsserter asserter = new ResponseAsserter<>(RESPONSE, EMPTY_HEADER_ASSERTER)
                    .assertHeaders(HeadersAsserter::connectionIsPresent); // not thrown
            assertThrow(asserter::blame) // explicit throw
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "Response header 'Connection'\n" +
                            "Expected: is present\n" +
                            "  Actual: null");
        }

    }

    @Nested
    @DisplayName("#assertSucResponse() method tests")
    public class AssertSucResponseMethod1Tests {

        @Test
        @DisplayName("Required parameters")
        public void test1639496565737() {
            final SucDTO expected = new SucDTO("test1639496565737");
            final ResponseAsserter asserter = getSucResponseAsserter(200, null);
            assertNPE(() -> asserter.assertSucResponse(200, NULL_CONSUMER_1), "assertionConsumer");
            assertNPE(() -> asserter.assertSucResponse(200, NULL_CONSUMER_2, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucResponse(200, NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucResponse(200, NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucResponse(200, NULL_CONSUMER_4, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucResponse(200, CONSUMER_2, null), "expectedSucDto");
            assertNPE(() -> asserter.assertSucResponse(200, CONSUMER_4, null), "expectedSucDto");
        }

        @Test
        @DisplayName("(int, Consumer<SUC_DTO>) positive")
        public void test1639065947494() {
            final ResponseAsserter asserter = getSucResponseAsserter(200, new SucDTO(""));
            asserter.assertSucResponse(200, body -> assertThat("", body, notNullValue())).blame();
        }

        @Test
        @DisplayName("(int, Consumer<SUC_DTO>) negative")
        public void test1639065947506() {
            final ResponseAsserter asserter = getSucResponseAsserter(500, null);
            assertThrow(() -> asserter.assertSucResponse(200, body -> assertThat("", body, notNullValue())).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  200\n" +
                            "  Actual: was 500\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("(int, BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639497009347() {
            final SucDTO expected = new SucDTO("test1639497009347");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucResponse(200, ResponseAsserterUnitTests::assertSucDTO, expected).blame();
        }

        @Test
        @DisplayName("(int, BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative")
        public void test1639497019782() {
            final SucDTO expected = new SucDTO("test1639497019782");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(500, null);
            assertThrow(() -> asserter.assertSucResponse(200, ResponseAsserterUnitTests::assertSucDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  200\n" +
                            "  Actual: was 500\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucResponse(int, BiConsumer<SoftlyAsserter, SUC_DTO>) positive")
        public void test1639497656767() {
            final SucDTO expected = new SucDTO("test1639497656767");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucResponse(200, (softly, act) -> assertSoftlySucDTO(softly, act, expected)).blame();
        }

        @Test
        @DisplayName("#assertSucResponse(int, BiConsumer<SoftlyAsserter, SUC_DTO>) negative")
        public void test1639497681421() {
            final SucDTO expected = new SucDTO("test1639497681421");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(500, null);
            assertThrow(() -> asserter.assertSucResponse(200, (softly, act) -> assertSoftlySucDTO(softly, act, expected)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  200\n" +
                            "  Actual: was 500\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucResponse(int, TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639571729257() {
            final SucDTO expected = new SucDTO("test1639571729257");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucResponse(200, ResponseAsserterUnitTests::assertSoftlySucDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertSucResponse(int, TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative")
        public void test1639571731704() {
            final SucDTO expected = new SucDTO("test1639571731704");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(500, null);
            assertThrow(() -> asserter.assertSucResponse(200, ResponseAsserterUnitTests::assertSoftlySucDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  200\n" +
                            "  Actual: was 500\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

    }

    @Nested
    @DisplayName("#assertSucBody() method tests")
    public class AssertSucBodyMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639572189573() {
            final SucDTO expected = new SucDTO("test1639572189573");
            final ResponseAsserter asserter = getSucResponseAsserter(200, null);
            assertNPE(() -> asserter.assertSucBody(NULL_CONSUMER_1), "assertionConsumer");
            assertNPE(() -> asserter.assertSucBody(NULL_CONSUMER_2, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucBody(NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucBody(NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucBody(NULL_CONSUMER_4, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertSucBody(CONSUMER_2, null), "expectedSucDto");
            assertNPE(() -> asserter.assertSucBody(CONSUMER_4, null), "expectedSucDto");
        }

        @Test
        @DisplayName("#assertSucBody(Consumer<SUC_DTO>) positive")
        public void test1639572196947() {
            final SucDTO expected = new SucDTO("test1639572196947");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucBody(act -> assertSucDTO(act, expected)).blame();
        }

        @Test
        @DisplayName("#assertSucBody(Consumer<SUC_DTO>) negative (null body)")
        public void test1639572207597() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            assertThrow(() -> asserter.assertSucBody(act -> assertSucDTO(act, null)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucBody(Consumer<SUC_DTO>) negative (exist body)")
        public void test1639574632570() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO("foo"));
            assertThrow(() -> asserter.assertSucBody(act -> assertSucDTO(act, new SucDTO("bar"))).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertSucBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639572218719() {
            final SucDTO expected = new SucDTO("test1639572218719");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucBody(ResponseAsserterUnitTests::assertSucDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertSucBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative (null body)")
        public void test1639572227705() {
            final SucDTO expected = new SucDTO("test1639572227705");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            assertThrow(() -> asserter.assertSucBody(ResponseAsserterUnitTests::assertSucDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative (exist body)")
        public void test1639576070070() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO("foo"));
            assertThrow(() -> asserter.assertSucBody(ResponseAsserterUnitTests::assertSucDTO, new SucDTO("bar")).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertSucBody(int, BiConsumer<SoftlyAsserter, SUC_DTO>) positive")
        public void test1639572238235() {
            final SucDTO expected = new SucDTO("test1639572238235");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucBody((softly, act) -> assertSoftlySucDTO(softly, act, expected)).blame();
        }

        @Test
        @DisplayName("#assertSucBody(BiConsumer<SoftlyAsserter, SUC_DTO>) negative (null body)")
        public void test1639572268635() {
            final SucDTO expected = new SucDTO("test1639572268635");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            assertThrow(() -> asserter.assertSucBody((softly, act) -> assertSoftlySucDTO(softly, act, expected)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucBody(BiConsumer<SoftlyAsserter, SUC_DTO>) negative (exist body)")
        public void test1639577387233() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO("foo"));
            assertThrow(() -> asserter.assertSucBody((softly, act) -> assertSoftlySucDTO(softly, act, new SucDTO("bar"))).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertSucBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639572276347() {
            final SucDTO expected = new SucDTO("test1639572238235");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, expected);
            asserter.assertSucBody(ResponseAsserterUnitTests::assertSoftlySucDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertSucBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative (exist body)")
        public void test1639572284306() {
            final SucDTO expected = new SucDTO("test1639572268635");
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            assertThrow(() -> asserter.assertSucBody(ResponseAsserterUnitTests::assertSoftlySucDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertSucBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative (null body)")
        public void test1639577513399() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO("foo"));
            assertThrow(() -> asserter.assertSucBody(ResponseAsserterUnitTests::assertSoftlySucDTO, new SucDTO("bar")).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

    }

    @Nested
    @DisplayName("#assertSucBodyNotNull() method tests")
    public class AssertSucBodyNotNullMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639577948691() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO(""));
            asserter.assertSucBodyNotNull().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639577968226() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            assertThrow(() -> asserter.assertSucBodyNotNull().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

    }

    @Nested
    @DisplayName("#assertSucBodyIsNull() method tests")
    public class AssertSucBodyIsNullMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639577681621() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            asserter.assertSucBodyIsNull().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639577692294() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, new SucDTO(""));
            assertThrow(() -> asserter.assertSucBodyIsNull().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful body\n" +
                            "Expected: is null\n" +
                            "  Actual: SucDTO{msg=''}");
        }

    }

    @Nested
    @DisplayName("#assertIsSucHttpStatusCode() method tests")
    public class AssertIsSucHttpStatusCodeMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639578067020() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(200, null);
            asserter.assertIsSucHttpStatusCode().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639578072241() {
            final ResponseAsserter<SucDTO, ?, ?> asserter = getSucResponseAsserter(500, null);
            assertThrow(() -> asserter.assertIsSucHttpStatusCode().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Successful HTTP status code\n" +
                            "Expected: in range 200...299\n" +
                            "  Actual: was 500");
        }

    }

    @Nested
    @DisplayName("#assertErrResponse() method tests")
    public class AssertErrResponseMethod1Tests {

        @Test
        @DisplayName("Required parameters")
        public void test1639581994387() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter asserter = getErrResponseAsserter(500, null);
            assertNPE(() -> asserter.assertErrResponse(200, NULL_CONSUMER_1), "assertionConsumer");
            assertNPE(() -> asserter.assertErrResponse(200, NULL_CONSUMER_2, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrResponse(200, NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrResponse(200, NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrResponse(200, NULL_CONSUMER_4, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrResponse(200, CONSUMER_2, null), "expectedErrDto");
            assertNPE(() -> asserter.assertErrResponse(200, CONSUMER_4, null), "expectedErrDto");
        }

        @Test
        @DisplayName("(int, Consumer<SUC_DTO>) positive")
        public void test1639581997276() {
            final ResponseAsserter asserter = getErrResponseAsserter(500, new ErrDTO(""));
            asserter.assertErrResponse(500, body -> assertThat("", body, notNullValue())).blame();
        }

        @Test
        @DisplayName("(int, Consumer<SUC_DTO>) negative")
        public void test1639582000279() {
            final ResponseAsserter asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertErrResponse(500, body -> assertThat("", body, notNullValue())).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  500\n" +
                            "  Actual: was 200\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("(int, BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639582003077() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrResponse(500, ResponseAsserterUnitTests::assertErrDTO, expected).blame();
        }

        @Test
        @DisplayName("(int, BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative")
        public void test1639582006729() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertErrResponse(500, ResponseAsserterUnitTests::assertErrDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  500\n" +
                            "  Actual: was 200\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrResponse(int, BiConsumer<SoftlyAsserter, SUC_DTO>) positive")
        public void test1639582009952() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrResponse(500, (softly, act) -> assertSoftlyErrDTO(softly, act, expected)).blame();
        }

        @Test
        @DisplayName("#assertErrResponse(int, BiConsumer<SoftlyAsserter, SUC_DTO>) negative")
        public void test1639582012623() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertErrResponse(500, (softly, act) -> assertSoftlyErrDTO(softly, act, expected)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  500\n" +
                            "  Actual: was 200\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrResponse(int, TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639582015591() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrResponse(500, ResponseAsserterUnitTests::assertSoftlyErrDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertErrResponse(int, TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative")
        public void test1639582019014() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertErrResponse(500, ResponseAsserterUnitTests::assertSoftlyErrDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n\n" +
                            "HTTP status code\n" +
                            "Expected: is  500\n" +
                            "  Actual: was 200\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

    }

    @Nested
    @DisplayName("#assertErrBody() method tests")
    public class AssertErrBodyMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1639582023470() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter asserter = getErrResponseAsserter(500, null);
            assertNPE(() -> asserter.assertErrBody(NULL_CONSUMER_1), "assertionConsumer");
            assertNPE(() -> asserter.assertErrBody(NULL_CONSUMER_2, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrBody(NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrBody(NULL_CONSUMER_3, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrBody(NULL_CONSUMER_4, expected), "assertionConsumer");
            assertNPE(() -> asserter.assertErrBody(CONSUMER_2, null), "expectedErrDto");
            assertNPE(() -> asserter.assertErrBody(CONSUMER_4, null), "expectedErrDto");
        }

        @Test
        @DisplayName("#assertErrBody(Consumer<SUC_DTO>) positive")
        public void test1639582027537() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrBody(act -> assertErrDTO(act, expected)).blame();
        }

        @Test
        @DisplayName("#assertErrBody(Consumer<SUC_DTO>) negative (null body)")
        public void test1639582029743() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            assertThrow(() -> asserter.assertErrBody(act -> assertErrDTO(act, null)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrBody(Consumer<SUC_DTO>) negative (exist body)")
        public void test1639582034789() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, new ErrDTO("foo"));
            assertThrow(() -> asserter.assertErrBody(act -> assertErrDTO(act, new ErrDTO("bar"))).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertErrBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639582040746() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrBody(ResponseAsserterUnitTests::assertErrDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertErrBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative (null body)")
        public void test1639582043621() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            assertThrow(() -> asserter.assertErrBody(ResponseAsserterUnitTests::assertErrDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO) negative (exist body)")
        public void test1639582047416() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, new ErrDTO("foo"));
            assertThrow(() -> asserter.assertErrBody(ResponseAsserterUnitTests::assertErrDTO, new ErrDTO("bar")).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertErrBody(int, BiConsumer<SoftlyAsserter, SUC_DTO>) positive")
        public void test1639582050586() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrBody((softly, act) -> assertSoftlyErrDTO(softly, act, expected)).blame();
        }

        @Test
        @DisplayName("#assertErrBody(BiConsumer<SoftlyAsserter, SUC_DTO>) negative (null body)")
        public void test1639582053595() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            assertThrow(() -> asserter.assertErrBody((softly, act) -> assertSoftlyErrDTO(softly, act, expected)).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrBody(BiConsumer<SoftlyAsserter, SUC_DTO>) negative (exist body)")
        public void test1639582056530() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, new ErrDTO("foo"));
            assertThrow(() -> asserter.assertErrBody((softly, act) -> assertSoftlyErrDTO(softly, act, new ErrDTO("bar"))).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

        @Test
        @DisplayName("#assertErrBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) positive")
        public void test1639582059395() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, expected);
            asserter.assertErrBody(ResponseAsserterUnitTests::assertSoftlyErrDTO, expected).blame();
        }

        @Test
        @DisplayName("#assertErrBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative (exist body)")
        public void test1639582062204() {
            final ErrDTO expected = new ErrDTO("test");
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            assertThrow(() -> asserter.assertErrBody(ResponseAsserterUnitTests::assertSoftlyErrDTO, expected).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

        @Test
        @DisplayName("#assertErrBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO) negative (null body)")
        public void test1639582064921() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, new ErrDTO("foo"));
            assertThrow(() -> asserter.assertErrBody(ResponseAsserterUnitTests::assertSoftlyErrDTO, new ErrDTO("bar")).blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "DTO.message\n" +
                            "Expected: is  bar\n" +
                            "  Actual: was foo");
        }

    }

    @Nested
    @DisplayName("#assertErrBodyNotNull() method tests")
    public class AssertErrBodyNotNullMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639582069076() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, new ErrDTO(""));
            asserter.assertErrBodyNotNull().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639582074095() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertErrBodyNotNull().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is not null\n" +
                            "  Actual: null");
        }

    }

    @Nested
    @DisplayName("#assertErrBodyIsNull() method tests")
    public class AssertErrBodyIsNullMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639582078953() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            asserter.assertErrBodyIsNull().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639582081800() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, new ErrDTO(""));
            assertThrow(() -> asserter.assertErrBodyIsNull().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error body\n" +
                            "Expected: is null\n" +
                            "  Actual: ErrDTO{msg=''}");
        }

    }

    @Nested
    @DisplayName("#assertIsErrHttpStatusCode() method tests")
    public class AssertIsErrHttpStatusCodeMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639582085048() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            asserter.assertIsErrHttpStatusCode().blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639582088065() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(200, null);
            assertThrow(() -> asserter.assertIsErrHttpStatusCode().blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "Error HTTP status code\n" +
                            "Expected: in range 300...599\n" +
                            "  Actual: was 200");
        }

    }

    @Nested
    @DisplayName("#assertHttpStatusMessageIs() method tests")
    public class AssertHttpStatusMessageIsMethodTests {

        @Test
        @DisplayName("Positive test")
        public void test1639581975295() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            asserter.assertHttpStatusMessageIs("TEST").blame();
        }

        @Test
        @DisplayName("Negative test")
        public void test1639582154391() {
            final ResponseAsserter<?, ErrDTO, ?> asserter = getErrResponseAsserter(500, null);
            assertThrow(() -> asserter.assertHttpStatusMessageIs("test").blame())
                    .assertClass(BriefAssertionError.class)
                    .assertMessageIs("Collected the following errors:\n" +
                            "\n" +
                            "HTTP status message\n" +
                            "Expected: is  test\n" +
                            "  Actual: was TEST");
        }

    }

    public static ResponseAsserter<SucDTO, ?, ?> getSucResponseAsserter(int status, SucDTO dto) {
        return getSucResponseAsserter(status, dto, EMPTY_HEADER_ASSERTER);
    }

    public static ResponseAsserter<SucDTO, ?, ?> getSucResponseAsserter(int status, SucDTO dto, IHeadersAsserter ha) {
        final Response response = OkHttpTestUtils.getResponse(status);
        DualResponse<SucDTO, ErrDTO> dualResponse = new DualResponse<>(dto, null, response, "", arrayOf());
        return new ResponseAsserter<>(dualResponse, ha);
    }

    public static ResponseAsserter<?, ErrDTO, ?> getErrResponseAsserter(int status, ErrDTO dto) {
        return getErrResponseAsserter(status, dto, EMPTY_HEADER_ASSERTER);
    }

    public static ResponseAsserter<?, ErrDTO, ?> getErrResponseAsserter(int status, ErrDTO dto, IHeadersAsserter ha) {
        final Response response = OkHttpTestUtils.getResponse(status);
        DualResponse<SucDTO, ErrDTO> dualResponse = new DualResponse<>(null, dto, response, "", arrayOf());
        return new ResponseAsserter<>(dualResponse, ha);
    }

    public static void assertSucDTO(SucDTO actual, SucDTO expected) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(actual::assertConsistency)
                    .softly(() -> AssertionMatcher.is("DTO.message", actual.msg, expected.msg));
        }
    }

    public static void assertSoftlySucDTO(SoftlyAsserter asserter, SucDTO actual, SucDTO expected) {
        asserter.softly(actual::assertConsistency)
                .softly(() -> AssertionMatcher.is("DTO.message", actual.msg, expected.msg));
    }

    public static void assertErrDTO(ErrDTO actual, ErrDTO expected) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(actual::assertConsistency)
                    .softly(() -> AssertionMatcher.is("DTO.message", actual.msg, expected.msg));
        }
    }

    public static void assertSoftlyErrDTO(SoftlyAsserter asserter, ErrDTO actual, ErrDTO expected) {
        asserter.softly(actual::assertConsistency)
                .softly(() -> AssertionMatcher.is("DTO.message", actual.msg, expected.msg));
    }

}
