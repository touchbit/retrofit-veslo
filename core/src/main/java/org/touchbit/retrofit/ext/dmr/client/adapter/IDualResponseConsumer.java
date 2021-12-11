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

package org.touchbit.retrofit.ext.dmr.client.adapter;

import okhttp3.Response;
import org.touchbit.retrofit.ext.dmr.client.response.BaseDualResponse;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Consumer for {@link BaseDualResponse} constructor
 * Used to add customized {@link BaseDualResponse} instances in the {@link DualResponseCallAdapterFactory}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.12.2021
 */
@FunctionalInterface
public interface IDualResponseConsumer<R> {

    /**
     * Constructor signature for {@link BaseDualResponse}
     *
     * @param sucDTO          - nullable success response DTO
     * @param errDTO          - nullable error response DTO
     * @param response        - okhttp raw response
     * @param endpointInfo    - called method info
     * @param callAnnotations - called method annotations
     * @return new instance of {@link IDualResponse}
     */
    @Nonnull
    R accept(final @Nullable Object sucDTO,
             final @Nullable Object errDTO,
             final @Nonnull Response response,
             final @Nonnull String endpointInfo,
             final @Nonnull Annotation[] callAnnotations);

}
