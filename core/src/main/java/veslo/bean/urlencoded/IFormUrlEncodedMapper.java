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

package veslo.bean.urlencoded;

import retrofit2.internal.EverythingIsNonNull;
import veslo.util.Utils;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static veslo.constant.ParameterNameConstants.*;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncoded
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
 */
public interface IFormUrlEncodedMapper {

    /**
     * Model to string conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model         - FormUrlEncoded model
     * @param codingCharset - URL form data coding charset
     * @param indexedArray  - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return model string representation
     */
    @EverythingIsNonNull
    String marshal(Object model, Charset codingCharset, boolean indexedArray);

    /**
     * Model to string conversion
     *
     * @param model - FormUrlEncoded model
     * @return model string representation
     */
    @EverythingIsNonNull
    default String marshal(Object model) {
        Utils.parameterRequireNonNull(model, MODEL_PARAMETER);
        return marshal(model, UTF_8, false);
    }

    /**
     * Model to string conversion
     *
     * @param model        - FormUrlEncoded model
     * @param indexedArray - flag for indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     * @return model string representation
     */
    @EverythingIsNonNull
    default String marshal(Object model, boolean indexedArray) {
        return marshal(model, UTF_8, indexedArray);
    }

    /**
     * String to model conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - String data to conversation
     * @param codingCharset - URL form data coding charset
     * @param <M>           - FormUrlEncoded model type
     * @return completed model
     */
    @EverythingIsNonNull
    <M> M unmarshal(Class<M> modelClass, String encodedString, Charset codingCharset);

    /**
     * String to model conversion
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation (UTF-8 encode charset)
     * @param <M>           - FormUrlEncoded model type
     * @return completed model
     */
    @EverythingIsNonNull
    default <M> M unmarshal(Class<M> modelClass, String encodedString) {
        Utils.parameterRequireNonNull(modelClass, MODEL_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        return unmarshal(modelClass, encodedString, UTF_8);
    }

}
