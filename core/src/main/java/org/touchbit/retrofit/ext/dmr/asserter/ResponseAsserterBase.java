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
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base class with built-in soft checks to check the response from the server.
 * <p>
 * Created by Oleg Shaburov on 19.11.2021
 * shaburov.o.a@gmail.com
 */
public abstract class ResponseAsserterBase<SUCCESSFUL_DTO, ERROR_DTO, HA> implements Closeable, SoftlyAsserter {

    private final IDualResponse<SUCCESSFUL_DTO, ERROR_DTO> response;
    private final List<Throwable> errors = new ArrayList<>();

    public ResponseAsserterBase(@Nonnull IDualResponse<SUCCESSFUL_DTO, ERROR_DTO> response) {
        Objects.requireNonNull(response, "Response required");
        this.response = response;
    }

    @EverythingIsNonNull
    public abstract ResponseAsserter<SUCCESSFUL_DTO, ERROR_DTO> assertHeaders(Consumer<HA> consumer);

    @Nonnull
    public IDualResponse<SUCCESSFUL_DTO, ERROR_DTO> getResponse() {
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
