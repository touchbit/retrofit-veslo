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
import org.slf4j.LoggerFactory;
import veslo.asserter.HeadersAsserter;
import veslo.asserter.ResponseAsserter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

public class DualResponse<SUC_DTO, ERR_DTO>
        extends BaseDualResponse<SUC_DTO, ERR_DTO, ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter>> {

    public DualResponse(final @Nullable SUC_DTO sucDTO,
                        final @Nullable ERR_DTO errDTO,
                        final @Nonnull Response response,
                        final @Nonnull String endpointInfo,
                        final @Nonnull Annotation[] callAnnotations) {
        super(sucDTO, errDTO, response, endpointInfo, callAnnotations);
        setLogger(LoggerFactory.getLogger(DualResponse.class));
    }

    @Override
    public HeadersAsserter getHeadersAsserter() {
        return new HeadersAsserter(this);
    }

    @Override
    public ResponseAsserter<SUC_DTO, ERR_DTO, HeadersAsserter> getResponseAsserter() {
        return new ResponseAsserter<>(this, getHeadersAsserter());
    }

}
