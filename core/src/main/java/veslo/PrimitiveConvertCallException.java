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

import java.lang.reflect.Type;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.03.2022
 */
public class PrimitiveConvertCallException extends ConvertCallException {

    public PrimitiveConvertCallException(Type type) {
        super("Cannot convert empty response body to primitive type: " + type);
    }

}
