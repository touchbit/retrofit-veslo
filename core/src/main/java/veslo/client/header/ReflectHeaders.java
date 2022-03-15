/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo.client.header;

import veslo.util.CaseUtils;
import veslo.util.ReflectUtils;
import veslo.util.Utils;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 25.02.2022
 */
public abstract class ReflectHeaders extends HashMap<String, String> {

    /**
     * @return a set view of the mappings contained in this map and the values of serializable class fields
     */
    @SuppressWarnings("ConstantConditions")
    public Set<Map.Entry<String, String>> entrySet() {
        this.putAll(readHeadersFields());
        return super.entrySet();
    }

    /**
     * The method receives values from the fields of the class through reflection.
     * The key is formed from the field name (kebab case)
     * or taken from annotation: {@link HeaderKey#value()}.
     *
     * @return map of serialized (!transient) class field
     */
    protected Map<String, String> readHeadersFields() {
        final Map<String, String> result = new HashMap<>();
        for (final Field field : ReflectUtils.getAllSerializableFields(this, HashMap.class, AbstractMap.class)) {
            final HeaderKey annotation = field.getAnnotation(HeaderKey.class);
            final String headerName = Utils.isNullOrBlank(HeaderKey::value, annotation) ?
                    CaseUtils.toKebabCase(field.getName()) :
                    annotation.value().trim(); // trim to avoid throwing IllegalArgumentException
            final Object value = ReflectUtils.readFieldValue(this, field);
            if (value != null) {
                result.put(headerName, value.toString());
            }
        }
        return result;
    }

    /**
     * @return headers in format {@code key: value} separated by newline
     */
    @Override
    public String toString() {
        return entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

}
