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

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.RuntimeIOException;

@DisplayName("ResourceFile.class unit tests")
public class ResourceFileUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("Successfully instantiating class where resource file exists")
    public void test1639065952256() {
        final String result = new ResourceFile("test/data/test1637486628431.txt").read();
        assertNotNull(result);
    }

    @Test
    @DisplayName("Successfully instantiating class where resource exists")
    public void test1639065952262() {
        final String result = new ResourceFile("test/data").read();
        assertNotNull(result);
    }

    @Test
    @DisplayName("Failed to instantiate class where resource not exists")
    public void test1639065952268() {
        assertThrow(() -> new ResourceFile("test/data/test1637486983022.txt").read())
                .assertClass(RuntimeIOException.class)
                .assertMessageIs("Resource file not readable: test/data/test1637486983022.txt");
    }

}
