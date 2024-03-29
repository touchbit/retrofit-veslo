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

package veslo.client.model;

import veslo.util.Utils;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static veslo.constant.ParameterNameConstants.CHARSET_PARAMETER;
import static veslo.constant.ParameterNameConstants.RESOURCE_RELATIVE_PATH_PARAMETER;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 18.11.2021
 */
public class ResourceFile {

    private final String resourceRelativePath;
    private final Charset charset;

    public ResourceFile(String resourceRelativePath) {
        this(resourceRelativePath, UTF_8);
    }

    public ResourceFile(String resourceRelativePath, Charset charset) {
        Utils.parameterRequireNonNull(resourceRelativePath, RESOURCE_RELATIVE_PATH_PARAMETER);
        Utils.parameterRequireNonNull(charset, CHARSET_PARAMETER);
        this.resourceRelativePath = resourceRelativePath;
        this.charset = charset;
    }

    public String read() {
        return Utils.readResourceFile(getResourceRelativePath(), getCharset());
    }

    public String getResourceRelativePath() {
        return resourceRelativePath;
    }

    public Charset getCharset() {
        return charset;
    }

}
