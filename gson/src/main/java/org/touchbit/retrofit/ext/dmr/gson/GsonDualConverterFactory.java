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

package org.touchbit.retrofit.ext.dmr.gson;

import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;

import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;

/**
 * Gson DTO converter factory
 * <p>
 * Created by Oleg Shaburov on 01.12.2021
 * shaburov.o.a@gmail.com
 */
public class GsonDualConverterFactory extends ExtensionConverterFactory {

    public GsonDualConverterFactory() {
        super();
        final GsonConverter<Object> gson = new GsonConverter<>();
        addMimeRequestConverter(gson, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        addMimeResponseConverter(gson, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
    }

}
