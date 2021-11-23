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

import org.touchbit.retrofit.ext.dmr.client.CallStage;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("ALL")
public class PublicExtensionConverterFactory extends ExtensionConverterFactory {

    @Override
    public ExtensionConverter<?> getExtensionConverter(@Nonnull Annotation converter,
                                                       @Nonnull Class<?> bodyClass) {
        return super.getExtensionConverter(converter, bodyClass);
    }

    @Override
    @EverythingIsNonNull
    public ExtensionConverter<?> newInstance(Class<? extends ExtensionConverter> converterClass) {
        return super.newInstance(converterClass);
    }

    @Nullable
    @Override
    public ResponseBodyConverter<?> getMimeResponseConverter(@Nonnull Class<?> bodyClass,
                                                             @Nonnull ContentType contentType,
                                                             @Nonnull Annotation[] mA,
                                                             @Nonnull Retrofit retrofit) {
        return super.getMimeResponseConverter(bodyClass, contentType, mA, retrofit);
    }

    @Nullable
    @Override
    public ResponseBodyConverter<?> getRawResponseConverter(@Nonnull Class<?> bodyClass,
                                                            @Nonnull Annotation[] mA,
                                                            @Nonnull Retrofit retrofit) {
        return super.getRawResponseConverter(bodyClass, mA, retrofit);
    }

    @Override
    @Nonnull
    public Map<String, Class<?>> getAnnotatedRequestConverters(Annotation[] mA) {
        return super.getAnnotatedRequestConverters(mA);
    }

    @Override
    @Nonnull
    public Map<String, Class<?>> getAnnotatedResponseConverters(Annotation[] mA) {
        return super.getAnnotatedResponseConverters(mA);
    }

    @Nullable
    @Override
    public ResponseBodyConverter<?> getResponseConverterFromAnnotation(@Nonnull Class<?> bodyClass,
                                                                       @Nonnull Annotation[] mA,
                                                                       @Nonnull Retrofit retrofit) {
        return super.getResponseConverterFromAnnotation(bodyClass, mA, retrofit);
    }

    @Nonnull
    @Override
    public String getSupportedConvertersInfo(@Nonnull CallStage callStage, @Nullable Annotation[] mA) {
        return super.getSupportedConvertersInfo(callStage, mA);
    }

    @Override
    @EverythingIsNonNull
    public Class<?> getResponseBodyClass(Type type) {
        return super.getResponseBodyClass(type);
    }
}
