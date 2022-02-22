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

package veslo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.converter.defaults.JavaPrimitiveTypeConverter;
import veslo.client.converter.defaults.JavaReferenceTypeConverter;
import veslo.client.converter.defaults.RawBodyTypeConverter;
import veslo.util.Utils;

import java.util.List;
import java.util.Map;

import static veslo.client.header.ContentTypeConstants.*;
import static veslo.constant.ParameterNameConstants.CONVERTER_PARAMETER;

/**
 * Gson DTO converter factory
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class GsonConverterFactory extends ExtensionConverterFactory {

    /**
     * Default constructor with class logger and converters:
     * - {@link RawBodyTypeConverter}
     * - {@link JavaPrimitiveTypeConverter}
     * - {@link JavaReferenceTypeConverter}
     * - {@link GsonConverter}
     */
    public GsonConverterFactory() {
        this(LoggerFactory.getLogger(GsonConverterFactory.class), GsonConverter.INSTANCE);
    }

    /**
     * Converter with converters:
     * - {@link RawBodyTypeConverter}
     * - {@link JavaPrimitiveTypeConverter}
     * - {@link JavaReferenceTypeConverter}
     * - {@link GsonConverter}
     *
     * @param logger - required Slf4J logger
     */
    public GsonConverterFactory(Logger logger) {
        this(logger, GsonConverter.INSTANCE);
    }

    /**
     * Converter with converters:
     * - {@link RawBodyTypeConverter}
     * - {@link JavaPrimitiveTypeConverter}
     * - {@link JavaReferenceTypeConverter}
     *
     * @param converter - converter for Gson models
     */
    public GsonConverterFactory(ExtensionConverter<Object> converter) {
        this(LoggerFactory.getLogger(GsonConverterFactory.class), converter);
    }

    /**
     * Converter with converters:
     * - {@link RawBodyTypeConverter}
     * - {@link JavaPrimitiveTypeConverter}
     * - {@link JavaReferenceTypeConverter}
     *
     * @param converter - converter for Gson models
     * @param logger    - required Slf4J logger
     */
    public GsonConverterFactory(Logger logger, ExtensionConverter<Object> converter) {
        super(logger);
        Utils.parameterRequireNonNull(converter,  CONVERTER_PARAMETER);
        registerMimeConverter(converter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        registerJavaTypeConverter(converter, Map.class, List.class);
        registerModelAnnotationConverter(converter, GsonModel.class);
    }

}
