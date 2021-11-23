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

package org.touchbit.retrofit.ext.test;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.test.model.RawDTO;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@SuppressWarnings("ALL")
public class TestConverter implements ExtensionConverter<RawDTO> {

    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(Type type,
                                                     Annotation[] parameterAnnotations,
                                                     Annotation[] methodAnnotations,
                                                     Retrofit retrofit) {
        return new RequestBodyConverter() {
            @Nullable
            @Override
            public RequestBody convert(@Nonnull Object body) {
                return RequestBody.create(null, String.valueOf(body));
            }
        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<RawDTO> responseBodyConverter(Type type,
                                                               Annotation[] methodAnnotations,
                                                               Retrofit retrofit) {
        return new ResponseBodyConverter<RawDTO>() {
            @Nullable
            @Override
            public RawDTO convert(@Nullable ResponseBody body) {
                try {
                    return new RawDTO(body.bytes());
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        };
    }
}
