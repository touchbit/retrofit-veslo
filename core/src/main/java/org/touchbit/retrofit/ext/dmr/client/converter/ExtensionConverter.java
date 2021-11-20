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

package org.touchbit.retrofit.ext.dmr.client.converter;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ExtensionConverter<DTO> {

    @EverythingIsNonNull
    RequestBodyConverter requestBodyConverter(Type type,
                                              Annotation[] parameterAnnotations,
                                              Annotation[] methodAnnotations,
                                              Retrofit retrofit);

    @EverythingIsNonNull
    ResponseBodyConverter<DTO> responseBodyConverter(Type type,
                                                     Annotation[] methodAnnotations,
                                                     Retrofit retrofit);

    interface RequestBodyConverter extends Converter<Object, RequestBody> {

        @Override
        @EverythingIsNonNull
        RequestBody convert(Object value) throws IOException;

    }

    interface ResponseBodyConverter<DTO> extends Converter<ResponseBody, DTO> {

        @Override
        @Nullable
        DTO convert(@Nullable ResponseBody value) throws IOException;

    }

}
