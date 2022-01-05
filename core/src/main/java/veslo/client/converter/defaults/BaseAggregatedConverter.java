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

package veslo.client.converter.defaults;

import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final Map<ExtensionConverter<?>, Set<Type>> defaultConverters = new HashMap<>();

    /**
     * Add supported converter for types
     *
     * @param converter - implementation of {@link ExtensionConverter}
     * @param types     - types supported by the converter
     */
    @EverythingIsNonNull
    public void addConverter(ExtensionConverter<?> converter, Type... types) {
        Utils.parameterRequireNonNull(converter, "converter");
        Utils.parameterRequireNonNull(types, "types");
        defaultConverters.put(converter, Stream.of(types).collect(Collectors.toSet()));
    }

    /**
     * @param type - type to convert
     * @return {@link ExtensionConverter} or null if converter not found
     */
    @Nullable
    public ExtensionConverter<?> getConverterForType(@Nonnull Type type) {
        Utils.parameterRequireNonNull(type, "type");
        final List<? extends ExtensionConverter<?>> result = defaultConverters.entrySet().stream()
                .filter(e -> e.getValue().contains(type))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() > 1) {
            throw new ConvertCallException("Found more than one converters for type " + type.getTypeName() + ":\n"
                    + result.stream().map(e -> e.getClass().getTypeName()).collect(Collectors.joining("\n")));
        }
        return result.get(0);
    }

    /**
     * @return all supported types
     */
    @EverythingIsNonNull
    public Type[] getSupportedTypes() {
        return defaultConverters.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
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

    /**
     * @return registered converters
     */
    public Map<ExtensionConverter<?>, Set<Type>> getDefaultConverters() {
        return defaultConverters;
    }

}
