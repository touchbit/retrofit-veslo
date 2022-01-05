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

import java.util.List;
import java.util.Map;

import static veslo.client.header.ContentTypeConstants.*;

/**
 * Gson DTO converter factory
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class GsonConverterFactory extends ExtensionConverterFactory {

    public GsonConverterFactory() {
        this(LoggerFactory.getLogger(GsonConverterFactory.class), new GsonConverter<>());
    }

    public GsonConverterFactory(Logger logger) {
        this(logger, new GsonConverter<>());
    }

    public GsonConverterFactory(ExtensionConverter<Object> gsonConverter) {
        this(LoggerFactory.getLogger(GsonConverterFactory.class), gsonConverter);
    }

    public GsonConverterFactory(Logger logger, ExtensionConverter<Object> gsonConverter) {
        super(logger);
        registerMimeConverter(gsonConverter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        registerJavaTypeConverter(gsonConverter, Map.class, List.class);
    }

}
