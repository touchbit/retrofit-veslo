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
import retrofit2.Response;

import java.lang.annotation.Annotation;

public abstract class DualResponseBase<SUCCESSFUL_DTO, ERROR_DTO> implements IDualResponse<SUCCESSFUL_DTO, ERROR_DTO> {

    private final Request rawRequest;
    private final String endpointInfo;
    private final Response<SUCCESSFUL_DTO> response;
    private final ERROR_DTO errorDTO;
    private final Annotation[] callAnnotations;

    protected DualResponseBase(final Request rawRequest,
                               final Response<SUCCESSFUL_DTO> response,
                               final ERROR_DTO errorDto,
                               final String endpointInfo,
                               final Annotation[] callAnnotations) {
        this.rawRequest = rawRequest;
        this.response = response;
        this.endpointInfo = endpointInfo;
        this.errorDTO = errorDto;
        this.callAnnotations = callAnnotations;
    }

    @Override
    public Request getRawRequest() {
        return rawRequest;
    }

    @Override
    public String getEndpointInfo() {
        return endpointInfo;
    }

    @Override
    public Response<SUCCESSFUL_DTO> getResponse() {
        return response;
    }

    @Override
    public ERROR_DTO getErrorDTO() {
        return errorDTO;
    }

    @Override
    public Annotation[] getCallAnnotations() {
        return callAnnotations;
    }

}