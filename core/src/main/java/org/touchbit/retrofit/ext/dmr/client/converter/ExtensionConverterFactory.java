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
import org.touchbit.retrofit.ext.dmr.client.converter.ext.*;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ConverterUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;
import static org.touchbit.retrofit.ext.dmr.util.ConverterUtils.isIDualResponse;

public class ExtensionConverterFactory extends Converter.Factory {

    private final Map<Class<?>, ExtensionConverter<?>> rawRequestConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeRequestConverters = new HashMap<>();
    private final Map<Class<?>, ExtensionConverter<?>> rawResponseConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeResponseConverters = new HashMap<>();

    public ExtensionConverterFactory() {
        final AnyBodyConverter anyBodyConverter = new AnyBodyConverter();
        final ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
        final FileConverter fileConverter = new FileConverter();
        final ResourceFileConverter resourceFileConverter = new ResourceFileConverter();
        final StringConverter stringConverter = new StringConverter();
        // raw request
        addRawRequestConverter(anyBodyConverter, AnyBody.class);
        addRawRequestConverter(byteArrayConverter, Byte[].class);
        addRawRequestConverter(fileConverter, File.class);
        addRawRequestConverter(resourceFileConverter, ResourceFile.class);
        // mime request
        addMimeRequestConverter(stringConverter, TEXT_PLAIN, TEXT_HTML);
        // raw response
        addRawResponseConverter(anyBodyConverter, AnyBody.class);
        addRawResponseConverter(byteArrayConverter, File.class);
        addRawResponseConverter(fileConverter, Byte[].class);
        // mime response
        addMimeResponseConverter(stringConverter,
                TEXT_PLAIN, TEXT_PLAIN_UTF8, TEXT_HTML_UTF8, APP_FORM_URLENCODED, APP_FORM_URLENCODED_UTF8);
    }

    /**
     * @param type     - request method body type.
     * @param pA       - API client called method parameters annotations
     * @param mA       - API client called method annotations
     * @param retrofit - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    public Converter<?, RequestBody> requestBodyConverter(final Type type,
                                                          final Annotation[] pA,
                                                          final Annotation[] mA,
                                                          final Retrofit retrofit) {
        return new Converter<Object, RequestBody>() {

            @EverythingIsNonNull
            public RequestBody convert(Object value) throws IOException {
                final Map<Class<?>, ExtensionConverter<?>> rawConverters = getRawRequestConverters();
                final ExtensionConverter<?> rawConverter = rawConverters.get(value.getClass());
                if (rawConverter != null) {
                    return rawConverter.requestBodyConverter(type, pA, mA, retrofit).convert(value);
                }
                final ContentType contentType = ConverterUtils.getContentType(mA);
                final Map<ContentType, ExtensionConverter<?>> mimeConverters = getMimeRequestConverters();
                final ExtensionConverter<?> mimeConverter = mimeConverters.get(contentType);
                if (mimeConverter != null) {
                    return mimeConverter.requestBodyConverter(type, pA, mA, retrofit).convert(value);
                }
                throw newNotFoundConverterException("request", contentType, value.getClass(), rawConverters, mimeConverters);
            }
        };
    }

    /**
     * @param type     - response body type.
     * @param mA       - API client called method annotations
     * @param retrofit - see {@link Retrofit}
     * @return {@link Converter}
     */
    @EverythingIsNonNull
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type,
                                                            final Annotation[] mA,
                                                            final Retrofit retrofit) {
        final Type bodyType;
        if (isIDualResponse(type)) {
            bodyType = getParameterUpperBound(0, (ParameterizedType) type);
        } else {
            bodyType = type;
        }
        final Class<?> bodyClass = getRawType(bodyType);

        return new Converter<ResponseBody, Object>() {

            @Override
            @EverythingIsNonNull
            public Object convert(final ResponseBody body) throws IOException {
                final Map<Class<?>, ExtensionConverter<?>> rawConverters = getRawResponseConverters();
                final ExtensionConverter<?> rawConverter = rawConverters.get(bodyClass);
                if (rawConverter != null) {
                    return rawConverter.responseBodyConverter(bodyClass, mA, retrofit).convert(body);
                }
                final Map<ContentType, ExtensionConverter<?>> mimeConverters = getMimeResponseConverters();
                final ContentType contentType = new ContentType(body.contentType());
                final ExtensionConverter<?> extensionConverter = mimeConverters.get(contentType);
                if (extensionConverter != null) {
                    return extensionConverter.responseBodyConverter(bodyClass, mA, retrofit).convert(body);
                }
                throw newNotFoundConverterException("response", contentType, bodyClass, rawConverters, mimeConverters);
            }

        };
    }

    @EverythingIsNonNull
    public ConvertCallException newNotFoundConverterException(final String callEvent,
                                                              final ContentType contentType,
                                                              final Class<?> body,
                                                              final Map<Class<?>, ExtensionConverter<?>> rawC,
                                                              final Map<ContentType, ExtensionConverter<?>> mimeC) {
        StringJoiner rawSJ = new StringJoiner("\n * ", "Supported raw converters:\n * ", "");
        rawC.forEach((k, v) -> rawSJ.add(k.getTypeName() + " -> " + v.getClass().getName()));
        StringJoiner mimeSJ = new StringJoiner("\n * ", "Supported content type converters:\n * ", "");
        mimeC.forEach((k, v) -> mimeSJ.add(k.toString() + " -> " + v.getClass().getName()));
        return new ConvertCallException("Converter not found\n" +
                "Call event: " + callEvent + "\n" +
                "Content-Type: " + contentType + "\n" +
                "DTO type: " + body + "\n\n"
                + rawSJ + "\n\n"
                + mimeSJ + "\n");
    }

    public Map<Class<?>, ExtensionConverter<?>> getRawRequestConverters() {
        return rawRequestConverters;
    }

    public Map<ContentType, ExtensionConverter<?>> getMimeRequestConverters() {
        return mimeRequestConverters;
    }

    public void addRawRequestConverter(ExtensionConverter<?> converter, Class<?>... rawClasses) {
        for (Class<?> rawClass : rawClasses) {
            getRawRequestConverters().put(rawClass, converter);
        }
    }

    public void addMimeRequestConverter(ExtensionConverter<?> converter, ContentType... contentTypes) {
        for (ContentType contentType : contentTypes) {
            getMimeRequestConverters().put(contentType, converter);
        }
    }


    public Map<ContentType, ExtensionConverter<?>> getMimeResponseConverters() {
        return mimeResponseConverters;
    }

    public void addMimeResponseConverter(ExtensionConverter<?> converter, ContentType... contentTypes) {
        Objects.requireNonNull(converter);
        for (ContentType contentType : contentTypes) {
            Objects.requireNonNull(contentType);
            mimeResponseConverters.put(contentType, converter);
        }
    }

    public Map<Class<?>, ExtensionConverter<?>> getRawResponseConverters() {
        return rawResponseConverters;
    }

    public void addRawResponseConverter(ExtensionConverter<?> converter, Class<?>... bodyClass) {
        for (Class<?> aClass : bodyClass) {
            Objects.requireNonNull(aClass);
            rawResponseConverters.put(aClass, converter);
        }
    }

}
