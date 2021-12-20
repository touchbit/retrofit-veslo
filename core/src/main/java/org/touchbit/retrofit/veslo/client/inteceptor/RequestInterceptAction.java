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

package org.touchbit.retrofit.veslo.client.inteceptor;

import okhttp3.Interceptor.Chain;
import okhttp3.Request;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * The interface is used in the {@link CompositeInterceptor}
 * to indirectly handle/modify requests and chains.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 25.11.2021
 */
public interface RequestInterceptAction {

    /**
     * Pre-modification/processing of the {@link Chain}
     *
     * @param chain - {@link Chain}
     * @return {@link Chain}
     */
    @Nonnull
    default Chain chainAction(@Nonnull Chain chain) {
        return chain;
    }

    /**
     * Pre-modification/processing of the {@link Request}
     *
     * @param request - {@link Request}
     * @return {@link Request}
     * @throws IOException - IO errors
     */
    @Nonnull
    default Request requestAction(@Nonnull Request request) throws IOException {
        return request;
    }

}
