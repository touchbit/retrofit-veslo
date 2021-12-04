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

package org.touchbit.retrofit.ext.dmr.client.converter.defaults;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileConverter implements ExtensionConverter<File> {

    public static final FileConverter INSTANCE = new FileConverter();

    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) throws IOException {
                Utils.parameterRequireNonNull(body, "body");
                if (body instanceof File) {
                    File file = (File) body;
                    if (!file.exists()) {
                        throw new ConvertCallException("Request body file not exists: " + file);
                    }
                    if (!file.isFile()) {
                        throw new ConvertCallException("Request body file is not a readable file: " + file);
                    }
                    final byte[] data = Files.readAllBytes(file.toPath());
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    return RequestBody.create(mediaType, data);
                }
                throw new ConverterUnsupportedTypeException(FileConverter.class, File.class, body.getClass());
            }

        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<File> responseBodyConverter(final Type type,
                                                             final Annotation[] methodAnnotations,
                                                             final Retrofit retrofit) {
        return new ResponseBodyConverter<File>() {

            @Override
            @Nullable
            public File convert(@Nullable ResponseBody body) throws IOException {
                if (body == null) {
                    return null;
                }
                final Path tempFile = Files.createTempFile(null, null);
                return Files.write(tempFile, body.bytes()).toFile();
            }

        };
    }
}
