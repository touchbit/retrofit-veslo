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

package veslo;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import veslo.client.response.DualResponse;

import java.lang.annotation.Annotation;

import static internal.test.utils.TestUtils.arrayOf;
import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("ConstantConditions")
@DisplayName("AllureDualCallAdapterFactory class tests")
public class AllureCallAdapterFactoryUnitTests extends BaseUnitTests {

    @Test
    @DisplayName("AllureDualCallAdapterFactory constructors")
    public void test1639065952668() {
        new AllureCallAdapterFactory();
        new AllureCallAdapterFactory(DualResponse::new);
        new AllureCallAdapterFactory(LoggerFactory.getLogger("test1638356144540"));
        new AllureCallAdapterFactory(LoggerFactory.getLogger("test1638356144540"), DualResponse::new);
    }

    @Test
    @DisplayName("#getEndpointInfo() return empty string if Description.value = ''")
    public void test1639065952677() {
        final Annotation[] array = arrayOf(getDescription(""));
        final String endpointInfo = new AllureCallAdapterFactory().getEndpointInfo(array);
        assertThat("", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("#getEndpointInfo() return empty string if Description.value = '   '")
    public void test1639065952685() {
        final Annotation[] array = arrayOf(getDescription("   "));
        final String endpointInfo = new AllureCallAdapterFactory().getEndpointInfo(array);
        assertThat("", endpointInfo, emptyString());
    }


    @Test
    @DisplayName("#getEndpointInfo() return empty string if Description not present")
    public void test1639065952694() {
        final String endpointInfo = new AllureCallAdapterFactory().getEndpointInfo(arrayOf());
        assertThat("", endpointInfo, emptyString());
    }

    @Test
    @DisplayName("#getEndpointInfo() return Description.value if value not blank")
    public void test1639065952701() {
        final Annotation[] array = arrayOf(getDescription("test1638356431777"));
        final String endpointInfo = new AllureCallAdapterFactory().getEndpointInfo(array);
        assertThat("", endpointInfo, is("test1638356431777"));
    }

    @Test
    @DisplayName("#getEndpointInfo() all parameters are required")
    public void test1639065952709() {
        assertThrow(() -> new AllureCallAdapterFactory().getEndpointInfo(null)).assertNPE("methodAnnotations");
    }
//
//    @Test
//    @DisplayName("#getCallAdapter() all parameters are required")
//    public void test1639065952715() {
//        final ParameterizedType type = mock(ParameterizedType.class);
//        final Retrofit retrofit = mock(Retrofit.class);
//        final Class<Object> oc = Object.class;
//        final AllureCallAdapterFactory factory = new AllureCallAdapterFactory();
//        assertThrow(() -> factory.getCallAdapter(null, oc, oc, "", array(), retrofit)).assertNPE("type");
//        assertThrow(() -> factory.getCallAdapter(type, null, oc, "", array(), retrofit)).assertNPE("successType");
//        assertThrow(() -> factory.getCallAdapter(type, oc, null, "", array(), retrofit)).assertNPE("errorType");
//        assertThrow(() -> factory.getCallAdapter(type, oc, oc, null, array(), retrofit)).assertNPE("endpointInfo");
//        assertThrow(() -> factory.getCallAdapter(type, oc, oc, "", null, retrofit)).assertNPE("methodAnnotations");
//        assertThrow(() -> factory.getCallAdapter(type, oc, oc, "", array(), null)).assertNPE("retrofit");
//    }
//
//    @Test
//    @DisplayName("#getCallAdapter() default call (super) if io.qameta.allure.Step annotation present")
//    public void test1639065952730() throws IOException {
//        final ParameterizedType type = mock(ParameterizedType.class);
//        final Retrofit retrofit = mock(Retrofit.class);
//        final Class<Object> oc = Object.class;
//        final AllureCallAdapterFactory factory = new AllureCallAdapterFactory();
//        final TestIdentifier testIdentifier = UnitTestInternalAllurePlatform.getTestIdentifier();
//        final UnitTestInternalAllurePlatform testPlatform = new UnitTestInternalAllurePlatform();
//        testPlatform.executionStarted(testIdentifier);
//        factory.getCallAdapter(type, oc, oc, "test1638357437756", array(getStep("test1638357437756")), retrofit);
//        testPlatform.executionFinished(testIdentifier, TestExecutionResult.successful());
//        final AllureResult allureResult = getAllureResult();
//        assertThat("", allureResult.getSteps(), empty());
//    }
//
//    @Test
//    @DisplayName("#getCallAdapter() default call (super) if stepInfo is blank")
//    public void test1639065952746() throws IOException {
//        final ParameterizedType type = mock(ParameterizedType.class);
//        final Retrofit retrofit = mock(Retrofit.class);
//        final Class<Object> oc = Object.class;
//        final AllureCallAdapterFactory factory = new AllureCallAdapterFactory();
//        final TestIdentifier testIdentifier = UnitTestInternalAllurePlatform.getTestIdentifier();
//        final UnitTestInternalAllurePlatform testPlatform = new UnitTestInternalAllurePlatform();
//        testPlatform.executionStarted(testIdentifier);
//        factory.getCallAdapter(type, oc, oc, "", array(), retrofit);
//        testPlatform.executionFinished(testIdentifier, TestExecutionResult.successful());
//        final AllureResult allureResult = getAllureResult();
//        assertThat("", allureResult.getSteps(), empty());
//    }
//
//    @Test
//    @DisplayName("#getCallAdapter() create allure step if stepInfo present")
//    public void test1639065952762() throws IOException {
//        final ParameterizedType type = mock(ParameterizedType.class);
//        final Retrofit retrofit = mock(Retrofit.class);
//        final Class<Object> oc = Object.class;
//        final AllureCallAdapterFactory factory = new AllureCallAdapterFactory();
//        final TestIdentifier testIdentifier = UnitTestInternalAllurePlatform.getTestIdentifier();
//        final UnitTestInternalAllurePlatform testPlatform = new UnitTestInternalAllurePlatform();
//        testPlatform.executionStarted(testIdentifier);
//        factory.getCallAdapter(type, oc, oc, "API CALL test1638358094530", array(), retrofit);
//        testPlatform.executionFinished(testIdentifier, TestExecutionResult.successful());
//        final AllureResult allureResult = getAllureResult();
//        allureResult.getSteps().stream()
//                .filter(step -> step.getName().equals("API CALL test1638358094530"))
//                .findFirst()
//                .orElseThrow(AssertionError::new);
//    }

    public static Step getStep(String value) {
        return new Step() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Step.class;
            }

            @Override
            public String value() {
                return value;
            }
        };
    }

    public static Description getDescription(String value) {
        return new Description() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Description.class;
            }

            @Override
            public String value() {
                return value;
            }

            @Override
            public boolean useJavaDoc() {
                return false;
            }
        };
    }

}