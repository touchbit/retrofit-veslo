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
    public static final String COOKIE_NAME_PARAMETER = "cookieName";
    public static final String COOKIES_PARAMETER = "cookies";
    public static final String COOKIE_PARAMETER = "cookie";
    public static final String DOMAIN_PARAMETER = "domain";
    public static final String URL_PARAMETER = "url";
    public static final String RESPONSE_PARAMETER = "response";
    public static final String REQUEST_PARAMETER = "request";
    public static final String COLLECTOR_PARAMETER = "collector";
    public static final String HEADERS_ASSERTER_PARAMETER = "headersAsserter";
    public static final String HEADER_ASSERTER_CONSUMER_PARAMETER = "headerAsserterConsumer";
    public static final String ASSERTION_CONSUMER_PARAMETER = "assertionConsumer";
    public static final String EXPECTED_SUC_DTO_PARAMETER = "expectedSucDto";
    public static final String EXPECTED_ERR_DTO_PARAMETER = "expectedErrDto";
    public static final String TEMPLATE_PARAMETER = "template";
    public static final String FIELD_PARAMETER = "field";
    public static final String MODEL_PARAMETER = "model";
    public static final String MODEL_CLASS_PARAMETER = "modelClass";
    public static final String ENCODED_STRING_PARAMETER = "encodedString";
    public static final String CODING_CHARSET_PARAMETER = "codingCharset";
    public static final String FORM_FIELD_NAME_PARAMETER = "formFieldName";
    public static final String PARSED_PARAMETER = "parsed";
    public static final String HANDLED_PARAMETER = "handled";
    public static final String VALUE_PARAMETER = "value";
    public static final String FIELD_TYPE_PARAMETER = "fieldType";
    public static final String TARGET_TYPE_PARAMETER = "targetType";
    public static final String PARAMETERIZED_TYPE_PARAMETER = "parameterizedType";

    /**
     * Utility class. Forbidden instantiation.
     */
    private ParameterNameConstants() {
        throw new UtilityClassException();
    }

}
