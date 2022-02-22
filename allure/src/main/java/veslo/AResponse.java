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

package veslo;

import io.qameta.allure.Allure;
import veslo.asserter.HeadersAsserter;
import veslo.asserter.ResponseAsserter;
import veslo.client.response.BaseDualResponse;
import veslo.client.response.DualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static veslo.constant.SonarRuleConstants.TYPE_PARAMETER_NAMING;

/**
 * Allure dual model response
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 26.12.2021
 */
@SuppressWarnings(TYPE_PARAMETER_NAMING)
public class AResponse<SUC_DTO, ERR_DTO> extends DualResponse<SUC_DTO, ERR_DTO> {

    /**
     * @param sucDTO          - nullable success response DTO
     * @param errDTO          - nullable error response DTO
     * @param response        - okhttp raw response
     * @param endpointInfo    - called method info
     * @param callAnnotations - called method annotations
     */
    public AResponse(@Nullable SUC_DTO sucDTO,
                     @Nullable ERR_DTO errDTO,
                     @Nonnull okhttp3.Response response,
                     @Nonnull String endpointInfo,
                     @Nonnull Annotation[] callAnnotations) {
        super(sucDTO, errDTO, response, endpointInfo, callAnnotations);
    }

    /**
     * @see DualResponse#assertResponse(Consumer)
     */
    @Override
    public BaseDualResponse<SUC_DTO, ERR_DTO, ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>>
    assertResponse(Consumer<ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>> respAsserter) {
        return step("Check response: " + getEndpointInfo(), () -> super.assertResponse(respAsserter));
    }

    /**
     * @see DualResponse#assertErrResponse(BiConsumer, Object)
     */
    @Override
    public BaseDualResponse<SUC_DTO, ERR_DTO, ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>>
    assertErrResponse(BiConsumer<ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>, ERR_DTO> respAsserter,
                      ERR_DTO expected) {
        return step("Check error response: " + getEndpointInfo(),
                () -> super.assertErrResponse(respAsserter, expected));
    }

    /**
     * @see DualResponse#assertSucResponse(BiConsumer, Object)
     */
    @Override
    public BaseDualResponse<SUC_DTO, ERR_DTO, ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>>
    assertSucResponse(BiConsumer<ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>, SUC_DTO> respAsserter,
                      SUC_DTO expected) {
        return step("Check success response: " + getEndpointInfo(),
                () -> super.assertSucResponse(respAsserter, expected));
    }

    /**
     * Inner wrapper for Allure.step adding an exception message attachment to the current step.
     *
     * @param name     – the name of step.
     * @param runnable – the step's body.
     * @return function call result object
     */
    private static <T> T step(final String name, final Allure.ThrowableRunnable<T> runnable) {
        return Allure.step(name, () -> {
            try {
                return runnable.run();
            } catch (Throwable throwable) {
                Allure.addAttachment("ERROR", throwable.getMessage());
                throw throwable;
            }
        });
    }

}
