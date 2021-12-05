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

package org.touchbit.retrofit.ext.dmr.asserter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.response.IDualResponse;
import retrofit2.internal.EverythingIsNonNull;

import java.util.function.Consumer;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"UnusedReturnValue", "rawtypes", "unchecked"})
@DisplayName("ResponseAsserterBase tests")
public class ResponseAsserterBaseUnitTests {

    @Test
    @DisplayName("NPE if constructor argument is null")
    public void test1639065947789() {
        assertThrow(() -> getResponseAsserterBase(null)).assertNPE("response");
    }

    @Test
    @DisplayName("#addError(Throwable) & #getErrors()")
    public void test1639065947795() {
        final IDualResponse mock = mock(IDualResponse.class);
        final ResponseAsserterBase responseAsserterBase = getResponseAsserterBase(mock);
        assertThat("ResponseAsserterBase.getErrors()", responseAsserterBase.getErrors(), notNullValue());
        assertThat("ResponseAsserterBase.getErrors() size", responseAsserterBase.getErrors().size(), is(0));
        final NumberFormatException expected = new NumberFormatException();
        responseAsserterBase.addErrors(expected);
        assertThat("ResponseAsserterBase.getErrors() size", responseAsserterBase.getErrors().size(), is(1));
    }

    @Test
    @DisplayName("#close() with AssertionError if errors present")
    public void test1639065947807() {
        final IDualResponse mock = mock(IDualResponse.class);
        final ResponseAsserterBase responseAsserterBase = getResponseAsserterBase(mock);
        final NumberFormatException expected = new NumberFormatException("test1637299122721");
        responseAsserterBase.addErrors(expected);
        assertThrow(responseAsserterBase::close)
                .assertClass(AssertionError.class)
                .assertMessageIs("Collected the following errors:\n\ntest1637299122721");
    }

    @Test
    @DisplayName("#close() without AssertionError if no errors")
    public void test1639065947819() {
        final IDualResponse mock = mock(IDualResponse.class);
        getResponseAsserterBase(mock).close();
    }

    @Test
    @DisplayName("#getResponse()")
    public void test1639065947826() {
        final IDualResponse exp = mock(IDualResponse.class);
        final ResponseAsserterBase responseAsserterBase = getResponseAsserterBase(exp);
        assertThat("ResponseAsserterBase.getResponse()", responseAsserterBase.getResponse(), is(exp));
    }

    private <S, E, A, M extends IDualResponse<S, E>> ResponseAsserterBase<S, E, A> getResponseAsserterBase(M o) {
        return new ResponseAsserterBase<S, E, A>(o) {
            @Override
            @EverythingIsNonNull
            public ResponseAsserter<S, E> assertHeaders(Consumer<A> consumer) {
                return null;
            }
        };
    }

}