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
import veslo.bean.template.TemplateMapper;
import veslo.client.TestClient;
import veslo.client.TrustSocketHelper;
import veslo.constant.ParameterNameConstants;
import veslo.constant.SonarRuleConstants;
import veslo.example.ExampleApiClientAssertions;
import veslo.util.CaseUtils;
import veslo.util.OkhttpUtils;
import veslo.util.ReflectUtils;
import veslo.util.Utils;

@DisplayName("Utility class tests")
public class UtilityClassesTests extends BaseUnitTest {

    @Test
    @DisplayName("Utility classes")
    public void test1645534011438() {
        assertUtilityClassException(ExampleApiClientAssertions.Assertions.class);
        assertUtilityClassException(ExampleApiClientAssertions.class);
        assertUtilityClassException(ParameterNameConstants.class);
        assertUtilityClassException(SonarRuleConstants.class);
        assertUtilityClassException(TrustSocketHelper.class);
        assertUtilityClassException(TemplateMapper.class);
        assertUtilityClassException(ReflectUtils.class);
        assertUtilityClassException(OkhttpUtils.class);
        assertUtilityClassException(TestClient.class);
        assertUtilityClassException(CaseUtils.class);
        assertUtilityClassException(Utils.class);
    }

}
