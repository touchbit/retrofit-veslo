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

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Convert 1/0 to boolean type
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class BooleanTypeAdapter implements JsonDeserializer<Boolean> {

    /**
     * @param jsonElement - The Json data being deserialized
     * @param typeOfT     - The type of the Object to deserialize to
     * @param context     - Context for deserialization
     * @return {@link Boolean}
     * @throws JsonParseException if json is not Boolean
     */
    public Boolean deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonPrimitive primitive = (JsonPrimitive) jsonElement;
        if (primitive.isBoolean()) {
            return jsonElement.getAsBoolean();
        }
        if (primitive.isString()) {
            String jsonValue = jsonElement.getAsString();
            if (jsonValue.equalsIgnoreCase("true")) {
                return true;
            }
            if (jsonValue.equalsIgnoreCase("false")) {
                return false;
            }
            throw new JsonParseException("Expected a boolean but was: '" + jsonValue + "'");
        }
        int code = primitive.getAsInt();
        if (code != 0 && code != 1) {
            throw new JsonParseException("Expected a boolean but was: " + code);
        }
        return code == 1;
    }

}
