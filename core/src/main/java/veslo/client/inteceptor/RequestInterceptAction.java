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

package veslo.client.inteceptor;

import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import retrofit2.internal.EverythingIsNonNull;

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
    @EverythingIsNonNull
    default Chain chainAction(Chain chain) {
        return chain;
    }

    /**
     * Pre-modification/processing of the {@link Request}
     *
     * @param request - {@link Request}
     * @return {@link Request}
     * @throws IOException - IO errors
     */
    @EverythingIsNonNull
    default Request requestAction(Request request) throws IOException {
        return request;
    }

}
