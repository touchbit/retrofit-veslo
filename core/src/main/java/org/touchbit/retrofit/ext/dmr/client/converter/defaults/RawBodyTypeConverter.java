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

import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.*;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Converter for reference java types:
 * RawBody.class, Byte[].class, File.class, ResourceFile.class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
@SuppressWarnings("rawtypes")
public class RawBodyTypeConverter implements ExtensionConverter {

    public static final RawBodyTypeConverter INSTANCE = new RawBodyTypeConverter();

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] paramAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(paramAnnotations, "parameterAnnotations");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (type.equals(RawBody.class)) {
            return RawBodyConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(Byte[].class) || type.equals(byte[].class)) {
            return ByteArrayConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }

        if (type.equals(File.class)) {
            return FileConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        if (type.equals(ResourceFile.class)) {
            return ResourceFileConverter.INSTANCE.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type,
                RawBody.class, Byte[].class, File.class, ResourceFile.class);
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<?> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, "type");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (type.equals(RawBody.class)) {
            return RawBodyConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Character.class)) {
            return CharacterConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(Byte[].class) || type.equals(byte[].class)) {
            return ByteArrayConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(File.class)) {
            return FileConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        if (type.equals(ResourceFile.class)) {
            return ResourceFileConverter.INSTANCE.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type,
                RawBody.class, Byte[].class, File.class, ResourceFile.class);
    }

}
