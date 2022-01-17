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

import veslo.client.converter.typed.ByteArrayConverter;
import veslo.client.converter.typed.FileConverter;
import veslo.client.converter.typed.RawBodyConverter;
import veslo.client.converter.typed.ResourceFileConverter;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;

import java.io.File;

/**
 * Converter for reference java types:
 * RawBody.class, Byte[].class, File.class, ResourceFile.class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.12.2021
 */
public class RawBodyTypeConverter extends BaseAggregatedConverter {

    /**
     * {@link RawBodyTypeConverter} constant
     */
    public static final RawBodyTypeConverter INSTANCE = new RawBodyTypeConverter();

    public RawBodyTypeConverter() {
        addConverter(RawBodyConverter.INSTANCE, RawBody.class);
        addConverter(ByteArrayConverter.INSTANCE, Byte[].class, byte[].class);
        addConverter(FileConverter.INSTANCE, File.class);
        addConverter(ResourceFileConverter.INSTANCE, ResourceFile.class);
    }

}
