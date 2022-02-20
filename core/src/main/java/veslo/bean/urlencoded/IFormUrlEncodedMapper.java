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

import java.nio.charset.Charset;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 */
public interface IFormUrlEncodedMapper {

    /**
     * Model to string conversion
     *
     * @param model - FormUrlEncoded model
     * @return model string representation
     */
    @EverythingIsNonNull
    String marshal(final Object model);

    /**
     * String to model conversion
     *
     * @param modelClass - FormUrlEncoded model class
     * @param encodedString       - String data to conversation
     * @param charset    - String data charset
     * @param <M>        - FormUrlEncoded model type
     * @return completed model
     */
    @EverythingIsNonNull
    <M> M unmarshal(final Class<M> modelClass, final String encodedString, final Charset charset);

    /**
     * String to model conversion
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation (UTF-8 encode charset)
     * @param <M>           - FormUrlEncoded model type
     * @return completed model
     */
    @EverythingIsNonNull
    <M> M unmarshal(final Class<M> modelClass, final String encodedString);

}
