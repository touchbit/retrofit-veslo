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

package veslo;

import internal.test.utils.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.model.AP;

@SuppressWarnings({"ConstantConditions", "UnnecessaryLocalVariable"})
@DisplayName("JacksonModelAdditionalProperties.class unit tests")
public class JacksonModelAdditionalPropertiesUnitTests extends BaseUnitTest {

    @Test
    @DisplayName("assertNoAdditionalProperties(): ContractViolationException if contains AP")
    public void test1647375603126() {
        final AP ap = new AP();
        ap.additionalProperty("foo", "bar");
        assertThrow(ap::assertNoAdditionalProperties)
                .assertClass(ContractViolationException.class)
                .assertMessageIs("The presence of extra fields in the model: AP\n" +
                                 "Expected: no extra fields\n" +
                                 "  Actual: {foo=bar}\n");
    }

    @Test
    @DisplayName("assertNoAdditionalProperties(): return POJO if not contains AP")
    public void test1647376053724() {
        final AP ap = new AP();
        final AP result = ap.assertNoAdditionalProperties();
        assertIs(result, ap);
    }

    @Test
    @DisplayName("equals() return true if same content")
    public void test1647376119400() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar");
        final AP ap2 = new AP();
        ap2.additionalProperty("foo", "bar");
        assertTrue(ap1.equals(ap2));
    }

    @Test
    @DisplayName("equals() return true if same objects")
    public void test1647376239653() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar");
        final AP ap2 = ap1;
        assertTrue(ap1.equals(ap2));
    }

    @Test
    @DisplayName("equals() return false if different content")
    public void test1647376129243() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar2");
        final AP ap2 = new AP();
        ap2.additionalProperty("foo", "bar1");
        assertFalse(ap1.equals(ap2));
    }

    @Test
    @DisplayName("equals() return false if different classes")
    public void test1647376213421() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar2");
        assertFalse(ap1.equals(new Object()));
    }

    @Test
    @DisplayName("equals() return false if o == null")
    public void test1647376328539() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar2");
        assertFalse(ap1.equals(null));
    }

    @Test
    @DisplayName("hashCode")
    public void test1647376359271() {
        final AP ap1 = new AP();
        ap1.additionalProperty("foo", "bar2");
        assertIs(ap1.hashCode(), ap1.additionalProperties().hashCode());
    }

}
