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

package org.touchbit.retrofit.ext.dmr.client.converter.typed;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ResourceFileConverter implements ExtensionConverter<ResourceFile> {

    public static final ResourceFileConverter INSTANCE = new ResourceFileConverter();

    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                Utils.parameterRequireNonNull(body, "body");
                if (body instanceof ResourceFile) {
                    final ResourceFile resourceFile = (ResourceFile) body;
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    return RequestBody.create(mediaType, resourceFile.getBytes());
                }
                final Class<?> bodyClass = body.getClass();
                throw new ConverterUnsupportedTypeException(ResourceFileConverter.class, bodyClass, ResourceFile.class);
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<ResourceFile> responseBodyConverter(final Type type,
                                                                     final Annotation[] methodAnnotations,
                                                                     final Retrofit retrofit) {
        return body -> {
            throw new ConvertCallException("It is forbidden to use the ResourceFile type to convert the response body.");
        };
    }

}
