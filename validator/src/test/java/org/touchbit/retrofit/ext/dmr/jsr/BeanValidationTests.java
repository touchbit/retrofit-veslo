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

package org.touchbit.retrofit.ext.dmr.jsr;

import internal.test.utils.TestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.client.adapter.DualCallAdapterFactory;
import org.touchbit.retrofit.ext.dmr.jackson.JacksonDualConverterFactory;
import org.touchbit.retrofit.ext.dmr.jsr.client.JakartaMockClient;
import org.touchbit.retrofit.ext.dmr.jsr.client.model.UserDTO;
import org.touchbit.retrofit.ext.dmr.jsr.client.model.UserPassport;

import java.util.Locale;
import java.util.UUID;

import static internal.test.utils.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class BeanValidationTests {

    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    protected static final JakartaMockClient MOCK_CLIENT = TestClient
            .build(JakartaMockClient.class, new DualCallAdapterFactory(), new JacksonDualConverterFactory());

    @Test
    @DisplayName("Successfully fetching the DTO of the model if no validation errors occurred.")
    public void test1636916993000() {
        final UserDTO successfulDTO = MOCK_CLIENT.getUser(200, genUserDTO()).getSuccessfulDTO().assertConsistency();
        assertThat("UserDTO", successfulDTO, notNullValue());
    }

    @Test
    @DisplayName("If contract is violated, bean validation error is expected (UserDTO)")
    public void test1636916993187() {
        String uuid = UUID.randomUUID().toString();
        assertThrow(() -> MOCK_CLIENT.getUser(200, genUserDTO().firstName(uuid))
                .assertResponse(response -> response.assertSuccessfulBody(UserDTO::assertConsistency)))
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Model property: UserDTO.firstName\n" +
                        "Expected: size must be between 1 and 10\n" +
                        "  Actual: " + uuid + "\n");
    }

    @Test
    @DisplayName("If contract is violated, bean validation error is expected (UserDTO.UserPassport)")
    public void test1636922771607() {
        String uuid = UUID.randomUUID().toString();
        UserDTO user = genUserDTO().passport(p -> p.number(uuid));
        Runnable runnable = () -> MOCK_CLIENT.getUser(200, user).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSuccessfulBody(UserDTO::assertConsistency));
        assertThrow(runnable)
                .assertClass(AssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Model property: UserDTO.passport.number\n" +
                        "Expected: must match \"^[0-9]{6}$\"\n" +
                        "  Actual: " + uuid + "\n");
    }

    private static UserDTO genUserDTO() {
        return new UserDTO()
                .firstName("FirstName")
                .lastName("LastName")
                .passport(new UserPassport()
                        .series("1234")
                        .number("123456"));
    }

}
