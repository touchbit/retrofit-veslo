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

package org.touchbit.retrofit.veslo.allure;

import io.qameta.allure.Allure;
import okhttp3.Request;
import okhttp3.Response;
import org.touchbit.retrofit.veslo.client.inteceptor.InterceptAction;
import org.touchbit.retrofit.veslo.util.OkhttpUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Adds request/response attachments to the Allure step
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class AllureAction implements InterceptAction {

    public static final AllureAction INSTANCE = new AllureAction();

    /**
     * Add allure attachment with request info
     *
     * @param request - {@link Request}
     * @return {@link Request}
     * @throws IOException - IO errors
     */
    @Override
    @Nonnull
    public Request requestAction(@Nonnull Request request) throws IOException {
        String requestToString = OkhttpUtils.requestToString(request);
        Allure.addAttachment("REQUEST", requestToString);
        return request;
    }

    /**
     * Add allure attachment with response info
     *
     * @param response - {@link Response}
     * @return {@link Response}
     * @throws IOException - IO errors
     */
    @Override
    @Nonnull
    public Response responseAction(@Nonnull Response response) throws IOException {
        String responseToString = OkhttpUtils.responseToString(response);
        Allure.addAttachment("RESPONSE", responseToString);
        return response;
    }

}
