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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that who class is the URI model.
 * Requests made with this annotation will have application/x-www-form-urlencoded MIME type.
 * Field names and values will be UTF-8 encoded before being URI-encoded in accordance to RFC-3986.
 * Simple Example:
 * <pre><code>
 * &#64;FormUrlEncoded()
 * public class ExampleModel {
 *
 *     &#64;FormUrlEncodedField("foo")
 *     private String foo;
 *
 *     &#64;FormUrlEncodedField("bar")
 *     private List<Object> bar;
 *
 * }
 * </code></pre>
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncodedMapper
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface FormUrlEncoded {
}
