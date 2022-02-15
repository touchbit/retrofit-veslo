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

import veslo.ResourceFileException;
import veslo.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        Utils.parameterRequireNonNull(resourceRelativePath, "resourceRelativePath");
        Utils.parameterRequireNonNull(charset, "charset");
        requireExists(resourceRelativePath);
        this.resourceRelativePath = resourceRelativePath;
        this.charset = charset;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public byte[] getBytes() {
        final String path = getResourceRelativePath();
        try (final InputStream stream = getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new ResourceFileException("Resource not exists: " + path);
            }
            byte[] result = new byte[stream.available()];
            stream.read(result);
            return result;
        } catch (IOException ioException) {
            throw new ResourceFileException("Resource not readable: " + path, ioException);
        }
    }

    @Override
    public String toString() {
        return new String(getBytes(), charset);
    }

    public static String resourceToString(String resourceRelativePath, Charset charset) {
        return new ResourceFile(resourceRelativePath, charset).toString();
    }

    public static String resourceToString(String resourceRelativePath) {
        return new ResourceFile(resourceRelativePath).toString();
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
            throw new ResourceFileException("Resource not exists: " + path);
        }
    }

}
