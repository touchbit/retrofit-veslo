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

import okhttp3.Response;
import org.touchbit.retrofit.ext.dmr.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

public abstract class DualResponseBase<SUC_DTO, ERR_DTO> implements IDualResponse<SUC_DTO, ERR_DTO> {

    private final SUC_DTO sucDTO;
    private final ERR_DTO errDTO;
    private final Response response;
    private final String endpointInfo;
    private final Annotation[] callAnnotations;

    protected DualResponseBase(final @Nullable SUC_DTO sucDTO,
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

}
