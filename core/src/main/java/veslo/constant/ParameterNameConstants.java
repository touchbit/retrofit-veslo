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

package veslo.constant;

import veslo.UtilityClassException;

/**
 * Method parameters name constants
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 22.02.2022
 */
public class ParameterNameConstants {

    public static final String DATA_PARAMETER = "data";
    public static final String BYTES_PARAMETER = "bytes";
    public static final String PATH_PARAMETER = "path";
    public static final String CHARSET_PARAMETER = "charset";
    public static final String FILE_PARAMETER = "file";

    /**
     * Utility class. Forbidden instantiation.
     */
    private ParameterNameConstants() {
        throw new UtilityClassException();
    }

}
