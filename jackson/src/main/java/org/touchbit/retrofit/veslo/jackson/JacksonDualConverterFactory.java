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

package org.touchbit.retrofit.veslo.jackson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.veslo.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.veslo.util.Utils;

import java.util.List;
import java.util.Map;

import static org.touchbit.retrofit.veslo.client.header.ContentTypeConstants.*;

/**
 * Jackson 2 DTO converter factory
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 03.11.2021
 */
public class JacksonDualConverterFactory extends ExtensionConverterFactory {

    public JacksonDualConverterFactory() {
        this(LoggerFactory.getLogger(JacksonDualConverterFactory.class), new JacksonConverter<>());
    }

    public JacksonDualConverterFactory(Logger logger) {
        this(logger, new JacksonConverter<>());
    }

    public JacksonDualConverterFactory(ExtensionConverter<Object> jacksonConverter) {
        this(LoggerFactory.getLogger(JacksonDualConverterFactory.class), jacksonConverter);
    }

    public JacksonDualConverterFactory(Logger logger, ExtensionConverter<Object> jacksonConverter) {
        super(logger);
        Utils.parameterRequireNonNull(jacksonConverter, "jacksonConverter");
        registerMimeConverter(jacksonConverter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        registerJavaTypeConverter(jacksonConverter, Map.class, List.class);
    }

}
