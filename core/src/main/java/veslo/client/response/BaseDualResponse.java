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

package veslo.client.response;

import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veslo.asserter.IHeadersAsserter;
import veslo.asserter.IResponseAsserter;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static veslo.constant.SonarRuleConstants.SONAR_TYPE_PARAMETER_NAMING;

@SuppressWarnings({SONAR_TYPE_PARAMETER_NAMING})
public abstract class BaseDualResponse<SUC_DTO, ERR_DTO, ASSERTER extends IResponseAsserter>
        implements IDualResponse<SUC_DTO, ERR_DTO> {

    private final SUC_DTO sucDTO;
    private final ERR_DTO errDTO;
    private final Response response;
    private final String endpointInfo;
    private final Annotation[] callAnnotations;
    private Logger logger = LoggerFactory.getLogger(BaseDualResponse.class);
    private static final String WITHOUT_ERRORS_MSG = "Response check completed without errors.";

    /**
     * @param sucDTO          - nullable success response DTO
     * @param errDTO          - nullable error response DTO
     * @param response        - okhttp raw response
     * @param endpointInfo    - called method info
     * @param callAnnotations - called method annotations
     */
    protected BaseDualResponse(final @Nullable SUC_DTO sucDTO,
                               final @Nullable ERR_DTO errDTO,
                               final @Nonnull Response response,
                               final @Nonnull String endpointInfo,
                               final @Nonnull Annotation[] callAnnotations) {
        this.response = response;
        this.sucDTO = sucDTO;
        this.errDTO = errDTO;
        this.endpointInfo = endpointInfo;
        this.callAnnotations = callAnnotations;
    }

    public abstract IHeadersAsserter getHeadersAsserter();

    public abstract ASSERTER getResponseAsserter();

    public BaseDualResponse<SUC_DTO, ERR_DTO, ASSERTER> assertResponse(Consumer<ASSERTER> respAsserter) {
        try (final ASSERTER responseAsserter = getResponseAsserter()) {
            respAsserter.accept(responseAsserter);
        }
        logger.info(WITHOUT_ERRORS_MSG);
        return this;
    }

    public BaseDualResponse<SUC_DTO, ERR_DTO, ASSERTER> assertSucResponse(BiConsumer<ASSERTER, SUC_DTO> respAsserter,
                                                                          SUC_DTO expected) {
        try (final ASSERTER responseAsserter = getResponseAsserter()) {
            respAsserter.accept(responseAsserter, expected);
        }
        logger.info(WITHOUT_ERRORS_MSG);
        return this;
    }

    public BaseDualResponse<SUC_DTO, ERR_DTO, ASSERTER> assertErrResponse(BiConsumer<ASSERTER, ERR_DTO> respAsserter,
                                                                          ERR_DTO expected) {
        try (final ASSERTER responseAsserter = getResponseAsserter()) {
            respAsserter.accept(responseAsserter, expected);
        }
        logger.info(WITHOUT_ERRORS_MSG);
        return this;
    }

    @Override
    @Nullable
    public ERR_DTO getErrDTO() {
        return errDTO;
    }

    @Override
    @Nullable
    public SUC_DTO getSucDTO() {
        return sucDTO;
    }

    @Override
    @Nonnull
    public String getEndpointInfo() {
        return endpointInfo;
    }


    @Override
    @Nonnull
    public Response getResponse() {
        return response;
    }

    @Override
    @Nonnull
    public Annotation[] getCallAnnotations() {
        return callAnnotations;
    }

    @Override
    public String toString() {
        return ("Success DTO: " + getSucDTO() + "\n" +
                "Error DTO: " + getErrDTO() + "\n" +
                "Raw response: " + getResponse() + "\n" +
                "Call info: '" + getEndpointInfo() + "'\n" +
                "API method annotations:" + Utils.arrayToPrettyString(getCallAnnotations()));
    }

    public Logger getLogger() {
        return logger;
    }

    public BaseDualResponse<SUC_DTO, ERR_DTO, ASSERTER> setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

}
