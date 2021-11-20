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

package org.touchbit.retrofit.ext.dmr.client.response;

import okhttp3.Request;
import org.touchbit.retrofit.ext.dmr.asserter.ResponseAsserter;
import retrofit2.Response;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

public class DualResponse<SUC_DTO, ERR_DTO> extends DualResponseBase<SUC_DTO, ERR_DTO> {

    public DualResponse(final Request rawRequest,
                        final Response<SUC_DTO> response,
                        final ERR_DTO errorDto,
                        final String endpointInfo,
                        final Annotation[] callAnnotations) {
        super(rawRequest, response, errorDto, endpointInfo, callAnnotations);
    }

    public DualResponse<SUC_DTO, ERR_DTO> assertResponse(Consumer<ResponseAsserter<SUC_DTO, ERR_DTO>> consumer) {
        try (final ResponseAsserter<SUC_DTO, ERR_DTO> responseAsserter = new ResponseAsserter<>(this)) {
            consumer.accept(responseAsserter);
        }
        return this;
    }

}
