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

package veslo.client.request;

import org.touchbit.www.form.urlencoded.marshaller.FormUrlMarshaller;

import java.util.*;

/**
 * * @FormUrlEncoded
 * * public class ExampleQueryMap extends FormUrlQueryData {
 * *
 * *    @FormUrlEncodedField("limit")
 * *    private Integer limit;
 * *
 * *    @FormUrlEncodedField("offset")
 * *    private Integer offset;
 * * }
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public abstract class FormUrlQueryData extends HashMap<String, Object> {

    protected transient FormUrlMarshaller marshaller = FormUrlMarshaller.INSTANCE;

    @Override
    public Set<Entry<String, Object>> entrySet() {
        final Set<Entry<String, Object>> entries = new HashSet<>(super.entrySet());
        for (Entry<String, List<String>> entry : marshaller.marshalToMap(this).entrySet()) {
            for (String value : entry.getValue()) {
                entries.add(new SimpleEntry<>(entry.getKey(), value));
            }
        }
        return entries;
    }

    @Override
    @SuppressWarnings("Java8MapForEach")
    public String toString() {
        final StringJoiner stringJoiner = new StringJoiner("&");
        this.entrySet().forEach(e -> stringJoiner.add(e.getKey() + "=" + e.getValue()));
        return stringJoiner.toString();
    }

}
