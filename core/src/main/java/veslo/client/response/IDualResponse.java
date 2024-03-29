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

import okhttp3.Headers;
import okhttp3.Response;
import veslo.util.OkhttpUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

import static veslo.constant.SonarRuleConstants.SONAR_TYPE_PARAMETER_NAMING;

/**
 * Common generic interface for response API wrapper classes.
 * Contains the deserialized response body from the server, which can be
 * represented by two DTO models (error/success) depending on {@link IDualResponse#getHttpStatusCode()}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 03.11.2021
 */
@SuppressWarnings(SONAR_TYPE_PARAMETER_NAMING)
public interface IDualResponse<SUC_DTO, ERR_DTO> {

    /**
     * @return okhttp {@link Response}
     */
    @Nonnull
    Response getResponse();

    /**
     * @return error DTO model
     */
    @Nullable
    ERR_DTO getErrDTO();

    /**
     * @return successful DTO model {@link SUC_DTO}
     */
    @Nullable
    SUC_DTO getSucDTO();

    /**
     * @return description of the called resource in detail
     */
    @Nonnull
    String getEndpointInfo();

    /**
     * @return list of annotations for the called API method
     */
    @Nonnull
    Annotation[] getCallAnnotations();

    /**
     * @return okhttp3 response headers
     */
    default Headers getHeaders() {
        return OkhttpUtils.getResponseHeaders(getResponse());
    }

    /**
     * @return HTTP response status code
     */
    default int getHttpStatusCode() {
        return getResponse().code();
    }

    /**
     * @return HTTP response status message
     */
    default String getHttpStatusMessage() {
        return getResponse().message();
    }

    /**
     * @return true if {@link IDualResponse#getHttpStatusCode()} is in the range [200..299].
     */
    default boolean isSuccessful() {
        return getResponse().isSuccessful();
    }

}
