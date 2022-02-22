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

import retrofit2.internal.EverythingIsNonNull;
import veslo.BriefAssertionError;
import veslo.client.response.IDualResponse;
import veslo.example.ExampleApiClientAssertions;
import veslo.util.TripleConsumer;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static veslo.constant.SonarRuleConstants.TYPE_PARAMETER_NAMING;

/**
 * {@link IDualResponse} asserter with built-in {@link IHeadersAsserter}
 * All examples for this class accumulated here {@link ExampleApiClientAssertions}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 12.12.2021
 * <p>
 * Examples
 * @see ExampleApiClientAssertions.AssertResponseMethodExamples#example1639328754881()
 * @see ExampleApiClientAssertions.AssertResponseMethodExamples#example1639329013867()
 * @see ExampleApiClientAssertions.AssertResponseMethodExamples#example1639437048937()
 */
@SuppressWarnings({"UnusedReturnValue", TYPE_PARAMETER_NAMING})
public class ResponseAsserter<SUC_DTO, ERR_DTO, HA extends IHeadersAsserter> implements IResponseAsserter {

    private final List<Throwable> errors = new ArrayList<>();
    private final IDualResponse<SUC_DTO, ERR_DTO> response;
    private final HA headersAsserter;
    private boolean isIgnoreNPE = false;

    /**
     * @param response        - {@link IDualResponse}
     * @param headersAsserter - {@link IHeadersAsserter} implementation
     */
    @EverythingIsNonNull
    public ResponseAsserter(final IDualResponse<SUC_DTO, ERR_DTO> response, final HA headersAsserter) {
        Utils.parameterRequireNonNull(response, "response");
        Utils.parameterRequireNonNull(headersAsserter, "headersAsserter");
        this.response = response;
        this.headersAsserter = headersAsserter;
    }

    /**
     * @param headerAsserterConsumer - {@link IHeadersAsserter} implementation consumer
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertHeadersMethodExamples#example1639329975511()
     * @see ExampleApiClientAssertions.AssertHeadersMethodExamples#example1639330184783()
     */
    @EverythingIsNonNull
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertHeaders(Consumer<HA> headerAsserterConsumer) {
        Utils.parameterRequireNonNull(headerAsserterConsumer, "headerAsserterConsumer");
        headerAsserterConsumer.accept(headersAsserter);
        addErrors(headersAsserter.getErrors());
        return this;
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual successful DTO model assertion consumer
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639323053942()
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639323439880()
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639324202096()
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639324398972()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucResponse(final int expCode,
                                                                    final Consumer<SUC_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        return assertHttpStatusCodeIs(expCode).assertSucBody(assertionConsumer);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual/expected successful DTO model assertion bi consumer (actual, expected)
     * @param expectedSucDto    - expected successful DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639323829528()
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639325042327()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucResponse(final int expCode,
                                                                    final BiConsumer<SUC_DTO, SUC_DTO> assertionConsumer,
                                                                    final SUC_DTO expectedSucDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedSucDto, "expectedSucDto");
        return assertHttpStatusCodeIs(expCode).assertSucBody(assertionConsumer, expectedSucDto);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual successful DTO model assertion bi consumer (SoftlyAsserter, actual)
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639328323975()
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639328330398()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucResponse(final int expCode,
                                                                    final BiConsumer<SoftlyAsserter, SUC_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        return assertHttpStatusCodeIs(expCode).assertSucBody(assertionConsumer);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual/expected successful DTO model assertion triple consumer (SoftlyAsserter, actual, expected)
     * @param expectedSucDto    - expected successful DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucResponseMethodExamples#example1639327810398()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucResponse(final int expCode,
                                                                    final TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO> assertionConsumer,
                                                                    final SUC_DTO expectedSucDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedSucDto, "expectedSucDto");
        return assertHttpStatusCodeIs(expCode).assertSucBody(assertionConsumer, expectedSucDto);
    }

    /**
     * @param assertionConsumer - actual successful DTO model assertion consumer
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325312188()
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325440427()
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325443017()
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325444984()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBody(final Consumer<SUC_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(actual));
            if (SoftlyAsserter.class.isAssignableFrom(actual.getClass())) {
                this.addErrors(((SoftlyAsserter) actual).getErrors());
            }
        }
        return assertSucBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual/expected successful DTO model assertion bi consumer (actual, expected)
     * @param expectedSucDto    - expected successful DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325561761()
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325563806()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBody(final BiConsumer<SUC_DTO, SUC_DTO> assertionConsumer,
                                                                final SUC_DTO expectedSucDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedSucDto, "expectedSucDto");
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(actual, expectedSucDto));
        }
        return assertSucBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual successful DTO model assertion bi consumer (SoftlyAsserter, actual)
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639325807124()
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639326363134()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBody(final BiConsumer<SoftlyAsserter, SUC_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(this, actual));
        }
        return assertSucBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual/expected successful DTO model assertion triple consumer (SoftlyAsserter, actual, expected)
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertSucBodyMethodExamples#example1639326893023()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBody(final TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO> assertionConsumer,
                                                                final SUC_DTO expectedSucDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedSucDto, "expectedSucDto");
        final SUC_DTO actual = getResponse().getSucDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(this, actual, expectedSucDto));
        }
        return assertSucBodyNotNull().blame();
    }

    /**
     * @return this
     * @throws BriefAssertionError if HTTP status code not in range 200...299
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertIsSucHttpStatusCode() {
        final int code = getResponse().getHttpStatusCode();
        softly(() -> AssertionMatcher.inRange("Successful HTTP status code", code, 200, 299));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if success body is null
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBodyNotNull() {
        softly(() -> AssertionMatcher.isNotNull("Successful body", getResponse().getSucDTO()));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if success body is not null
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertSucBodyIsNull() {
        softly(() -> AssertionMatcher.isNull("Successful body", getResponse().getSucDTO()));
        return this;
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual error DTO model assertion consumer
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435639889()
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435652717()
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435661467()
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435669326()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrResponse(final int expCode,
                                                                    final Consumer<ERR_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        return assertHttpStatusCodeIs(expCode).assertErrBody(assertionConsumer);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual/expected error DTO model assertion bi consumer (actual, expected)
     * @param expectedErrDto    - expected error DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435677413()
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435684781()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrResponse(final int expCode,
                                                                    final BiConsumer<ERR_DTO, ERR_DTO> assertionConsumer,
                                                                    final ERR_DTO expectedErrDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedErrDto, "expectedErrDto");
        return assertHttpStatusCodeIs(expCode).assertErrBody(assertionConsumer, expectedErrDto);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual error DTO model assertion bi consumer (SoftlyAsserter, actual)
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435692258()
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435699849()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrResponse(final int expCode,
                                                                    final BiConsumer<SoftlyAsserter, ERR_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        return assertHttpStatusCodeIs(expCode).assertErrBody(assertionConsumer);
    }

    /**
     * @param expCode           - expected HTTP status code
     * @param assertionConsumer - actual/expected error DTO model assertion triple consumer (SoftlyAsserter, actual, expected)
     * @param expectedErrDto    - expected error DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrResponseMethodExamples#example1639435708260()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrResponse(final int expCode,
                                                                    final TripleConsumer<SoftlyAsserter, ERR_DTO, ERR_DTO> assertionConsumer,
                                                                    final ERR_DTO expectedErrDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedErrDto, "expectedErrDto");
        return assertHttpStatusCodeIs(expCode).assertErrBody(assertionConsumer, expectedErrDto);
    }

    /**
     * @param assertionConsumer - actual error DTO model assertion consumer
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437244747()
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437254775()
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437276238()
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437293672()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBody(final Consumer<ERR_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(actual));
        }
        return assertErrBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual/expected error DTO model assertion bi consumer (actual, expected)
     * @param expectedErrDto    - expected error DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437371546()
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437376687()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBody(final BiConsumer<ERR_DTO, ERR_DTO> assertionConsumer,
                                                                final ERR_DTO expectedErrDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedErrDto, "expectedErrDto");
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(actual, expectedErrDto));
        }
        return assertErrBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual error DTO model assertion bi consumer (SoftlyAsserter, actual)
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437383840()
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437391538()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBody(final BiConsumer<SoftlyAsserter, ERR_DTO> assertionConsumer) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(this, actual));
        }
        return assertErrBodyNotNull().blame();
    }

    /**
     * @param assertionConsumer - actual/expected error DTO model assertion triple consumer (SoftlyAsserter, actual, expected)
     * @param expectedErrDto    - expected error DTO model
     * @return this
     * <p>
     * Examples:
     * @see ExampleApiClientAssertions.AssertErrBodyMethodExamples#example1639437397547()
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBody(final TripleConsumer<SoftlyAsserter, ERR_DTO, ERR_DTO> assertionConsumer,
                                                                final ERR_DTO expectedErrDto) {
        Utils.parameterRequireNonNull(assertionConsumer, "assertionConsumer");
        Utils.parameterRequireNonNull(expectedErrDto, "expectedErrDto");
        final ERR_DTO actual = getResponse().getErrDTO();
        if (actual != null) {
            softly(() -> assertionConsumer.accept(this, actual, expectedErrDto));
        }
        return assertErrBodyNotNull().blame();
    }

    /**
     * @return this
     * @throws BriefAssertionError if HTTP status code not in range 300...599
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertIsErrHttpStatusCode() {
        final int code = getResponse().getHttpStatusCode();
        softly(() -> AssertionMatcher.inRange("Error HTTP status code", code, 300, 599));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if error body is null
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBodyNotNull() {
        softly(() -> AssertionMatcher.isNotNull("Error body", getResponse().getErrDTO()));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if error body is not null
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertErrBodyIsNull() {
        softly(() -> AssertionMatcher.isNull("Error body", getResponse().getErrDTO()));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if unexpected HTTP status code received
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertHttpStatusCodeIs(final int expected) {
        int actual = getResponse().getHttpStatusCode();
        softly(() -> AssertionMatcher.is("HTTP status code", actual, expected));
        return this;
    }

    /**
     * @return this
     * @throws BriefAssertionError if unexpected HTTP status message received
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> assertHttpStatusMessageIs(final String expected) {
        final String actual = getResponse().getHttpStatusMessage();
        softly(() -> AssertionMatcher.is("HTTP status message", actual, expected));
        return this;
    }

    /**
     * Force throwing of assertion errors
     *
     * @return this
     */
    public ResponseAsserter<SUC_DTO, ERR_DTO, HA> blame() {
        close();
        return this;
    }

    /**
     * @return collected softly errors
     */
    @Override
    @EverythingIsNonNull
    public List<Throwable> getErrors() {
        return errors;
    }

    /**
     * @param throwableList add list of assertion errors
     */
    @Override
    @EverythingIsNonNull
    public void addErrors(@Nonnull List<Throwable> throwableList) {
        errors.addAll(throwableList);
    }

    @Override
    public void ignoreNPE(boolean value) {
        isIgnoreNPE = value;
    }

    @Override
    public boolean isIgnoreNPE() {
        return isIgnoreNPE;
    }

    /**
     * @return {@link IHeadersAsserter} implementation object
     */
    public HA getHeadersAsserter() {
        return headersAsserter;
    }

    /**
     * @return {@link IDualResponse} implementation object
     */
    @Nonnull
    public IDualResponse<SUC_DTO, ERR_DTO> getResponse() {
        return response;
    }

}
