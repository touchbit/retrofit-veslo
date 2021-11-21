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

package org.touchbit.retrofit.ext.dmr.client.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;

/**
 * Created by Oleg Shaburov on 18.11.2021
 * shaburov.o.a@gmail.com
 */
public class ResourceFile {

    private final String resourceRelativePath;

    public ResourceFile(String resourceRelativePath) {
        requireExists(resourceRelativePath);
        this.resourceRelativePath = resourceRelativePath;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public byte[] getBytes() throws IOException {
        final String path = getResourceRelativePath();
        try (final InputStream stream = getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new MissingResourceException("Resource not exists: " + path, "", "");
            }
            byte[] result = new byte[stream.available()];
            stream.read(result);
            return result;
        }
    }

    public String getResourceRelativePath() {
        return resourceRelativePath;
    }

    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected void requireExists(String path) {
        final URL resource = getClassLoader().getResource(path);
        if (resource == null) {
            throw new MissingResourceException("Resource not exists: " + path, "", "");
        }
    }

}
