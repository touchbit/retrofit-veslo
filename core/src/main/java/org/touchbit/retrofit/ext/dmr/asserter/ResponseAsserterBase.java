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

package org.touchbit.retrofit.ext.dmr.asserter;

import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class with built-in soft checks to check the response from the server.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.11.2021
 */
public abstract class ResponseAsserterBase<SUC_DTO, ERR_DTO, HA> implements Closeable, SoftlyAsserter {

    private final IDualResponse<SUC_DTO, ERR_DTO> response;
    private final List<Throwable> errors = new ArrayList<>();

    public ResponseAsserterBase(@Nonnull IDualResponse<SUC_DTO, ERR_DTO> response) {
        Utils.parameterRequireNonNull(response, "response");
        this.response = response;
    }

    @EverythingIsNonNull
    public abstract ResponseAsserter<SUC_DTO, ERR_DTO> assertHeaders(Consumer<HA> consumer);

    @Nonnull
    public IDualResponse<SUC_DTO, ERR_DTO> getResponse() {
        return response;
    }

    @Override
    @EverythingIsNonNull
    public List<Throwable> getErrors() {
        return errors;
    }

    @Override
    @EverythingIsNonNull
    public void addErrors(@Nonnull List<Throwable> throwableList) {
        errors.addAll(throwableList);
    }

}
