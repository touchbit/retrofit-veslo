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

import org.touchbit.retrofit.ext.dmr.client.converter.typed.ByteArrayConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.FileConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.RawBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ResourceFileConverter;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;

import java.io.File;

/**
 * Converter for reference java types:
 * RawBody.class, Byte[].class, File.class, ResourceFile.class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
public class RawBodyTypeConverter extends BaseDefaultConverter {

    public static final RawBodyTypeConverter INSTANCE = new RawBodyTypeConverter();

    public RawBodyTypeConverter() {
        addConverter(RawBodyConverter.INSTANCE, RawBody.class);
        addConverter(ByteArrayConverter.INSTANCE, Byte[].class, byte[].class);
        addConverter(FileConverter.INSTANCE, File.class);
        addConverter(ResourceFileConverter.INSTANCE, ResourceFile.class);
    }

}
