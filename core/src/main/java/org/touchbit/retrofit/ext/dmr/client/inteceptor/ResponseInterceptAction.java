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

package org.touchbit.retrofit.ext.dmr.client.inteceptor;

import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * The interface is used in the {@link CompositeInterceptor}
 * to indirectly handle/modify responses, and exceptions.
 * <p>
 * Created by Oleg Shaburov on 25.11.2021
 * shaburov.o.a@gmail.com
 */
public interface ResponseInterceptAction {

    /**
     * The method for handling the received error.
     * For example, logging.
     *
     * @param throwable - Error received when making a request
     */
    default void errorAction(@Nonnull Throwable throwable) {
        // do nothing
    }

    /**
     * Post-modification/processing of the received response
     *
     * @param response {@link Response}
     * @return {@link Response}
     * @throws IOException - IO errors
     */
    @Nonnull
    default Response responseAction(@Nonnull Response response) throws IOException {
        return response;
    }

}
