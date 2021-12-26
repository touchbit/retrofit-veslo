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

package org.touchbit.retrofit.veslo.example.client.transport;

import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.client.header.ContentType;
import org.touchbit.retrofit.veslo.jackson.JacksonConverter;
import org.touchbit.retrofit.veslo.jackson.JacksonConverterFactory;

public class CustomJacksonConverterFactory extends JacksonConverterFactory {

    public CustomJacksonConverterFactory() {
        super(LoggerFactory.getLogger(CustomJacksonConverterFactory.class));
        // Used by JacksonConverter to convert body if Content-Type header not present
        registerMimeConverter(JacksonConverter.INSTANCE, ContentType.NULL);
    }

}
