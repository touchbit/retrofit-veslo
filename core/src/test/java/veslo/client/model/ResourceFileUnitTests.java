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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.ResourceFileException;

import java.io.IOException;
import java.io.InputStream;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ResourceFile tests")
public class ResourceFileUnitTests {

    @Test
    @DisplayName("Successfully instantiating class where resource file exists")
    public void test1639065952256() {
        new ResourceFile("test/data/test1637486628431.txt");
    }

    @Test
    @DisplayName("Successfully instantiating class where resource exists")
    public void test1639065952262() {
        new ResourceFile("test/data");
    }

    @Test
    @DisplayName("Failed to instantiate class where resource not exists")
    public void test1639065952268() {
        assertThrow(() -> new ResourceFile("test/data/test1637486983022.txt"))
                .assertClass(ResourceFileException.class)
                .assertMessageIs("Resource not exists: test/data/test1637486983022.txt");
    }

    @Test
    @DisplayName("getBytes() successful read of an existing resource file")
    public void test1639065952276() {
        final byte[] bytes = new ResourceFile("test/data/test1637487087922.txt").getBytes();
        assertThat("", new String(bytes), is("test1637487087922"));
    }

    @Test
    @DisplayName("getBytes() error reading non-existent file")
    public void test1639065952283() {
        final ResourceFile resourceFile = mock(ResourceFile.class);
        when(resourceFile.getResourceRelativePath()).thenReturn("test/data/test1637487260260");
        when(resourceFile.getClassLoader()).thenCallRealMethod();
        when(resourceFile.getBytes()).thenCallRealMethod();
        assertThrow(resourceFile::getBytes)
                .assertClass(ResourceFileException.class)
                .assertMessageIs("Resource not exists: test/data/test1637487260260");
    }

    @Test
    @DisplayName("getBytes() error reading not readable file")
    public void test1639065952295() throws IOException {
        final ResourceFile resourceFile = mock(ResourceFile.class);
        final InputStream inputStream = mock(InputStream.class);
        final ClassLoader classLoader = mock(ClassLoader.class);

        when(inputStream.available()).thenThrow(new IOException("test1637545665265"));
        when(classLoader.getResourceAsStream("/test/data/test1637545665265")).thenReturn(inputStream);
        when(resourceFile.getResourceRelativePath()).thenReturn("/test/data/test1637545665265");
        when(resourceFile.getClassLoader()).thenReturn(classLoader);
        when(resourceFile.getBytes()).thenCallRealMethod();

        assertThrow(resourceFile::getBytes)
                .assertClass(ResourceFileException.class)
                .assertMessageIs("Resource not readable: /test/data/test1637545665265");
    }

}