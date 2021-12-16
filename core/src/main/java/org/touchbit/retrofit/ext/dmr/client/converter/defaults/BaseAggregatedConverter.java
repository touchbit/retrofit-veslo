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
import org.touchbit.retrofit.ext.dmr.exception.ConverterUnsupportedTypeException;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base default converter
 * Designed for aggregation of converters by any criterion
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
@SuppressWarnings("rawtypes")
public abstract class BaseAggregatedConverter implements ExtensionConverter {

    private final Map<ExtensionConverter<?>, List<Type>> defaultConverters = new HashMap<>();

    public void addConverter(ExtensionConverter<?> converter, Type... types) {
        defaultConverters.put(converter, Arrays.asList(types));
    }

    public ExtensionConverter<?> getConverterForType(Type type) {
        return defaultConverters.entrySet().stream()
                .filter(e -> e.getValue().contains(type))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public Type[] getSupportedTypes() {
        return defaultConverters.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .toArray(new Type[]{});
    }

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
        final ExtensionConverter<?> converter = getConverterForType(type);
        if (converter != null) {
            return converter.requestBodyConverter(type, paramAnnotations, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, getSupportedTypes());
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
        final ExtensionConverter<?> converter = getConverterForType(type);
        if (converter != null) {
            return converter.responseBodyConverter(type, methodAnnotations, retrofit);
        }
        throw new ConverterUnsupportedTypeException(this.getClass(), type, getSupportedTypes());
    }

}
