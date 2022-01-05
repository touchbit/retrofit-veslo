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

package org.touchbit.retrofit.veslo.example.client.transport;

import okhttp3.Headers;
import okhttp3.Request;
import veslo.client.inteceptor.RequestInterceptAction;

import javax.annotation.Nonnull;

public class AuthAction implements RequestInterceptAction {

    public static final AuthAction INSTANCE = new AuthAction();
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    @Nonnull
    @Override
    public Request requestAction(@Nonnull Request request) {
        final String token = TOKEN.get();
        if (token != null) {
            final Headers headers = request.headers().newBuilder().add("api_key", token).build();
            return request.newBuilder().headers(headers).build();
        }
        return request;
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static void removeToken() {
        TOKEN.remove();
    }

}
