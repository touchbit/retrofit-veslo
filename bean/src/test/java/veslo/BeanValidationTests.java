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

import internal.test.utils.asserter.ThrowableRunnable;
import internal.test.utils.client.TestClientBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import veslo.client.JakartaMockClient;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.model.UserDTO;
import veslo.client.model.UserPassport;

import java.util.Locale;
import java.util.UUID;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SuppressWarnings("ConstantConditions")
public class BeanValidationTests {

    protected static final JakartaMockClient MOCK_CLIENT = TestClientBuilder
            .build(JakartaMockClient.class, new UniversalCallAdapterFactory(), new JacksonConverterFactory());

    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    private static UserDTO genUserDTO() {
        return new UserDTO()
                .firstName("FirstName")
                .lastName("LastName")
                .passport(new UserPassport()
                        .series("1234")
                        .number("123456"));
    }

    @Test
    @DisplayName("Successfully fetching the DTO of the model if no validation errors occurred.")
    public void test1639065952585() {
        final UserDTO successfulDTO = MOCK_CLIENT.getUser(200, genUserDTO()).getSucDTO().assertConsistency();
        assertThat("UserDTO", successfulDTO, notNullValue());
    }

    @Test
    @DisplayName("If contract is violated, bean validation error is expected (UserDTO)")
    public void test1639065952592() {
        String uuid = UUID.randomUUID().toString();
        assertThrow(() -> MOCK_CLIENT.getUser(200, genUserDTO().firstName(uuid))
                .assertResponse(response -> response.assertSucBody(UserDTO::assertConsistency)))
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Model property: UserDTO.firstName\n" +
                        "Expected: size must be between 1 and 10\n" +
                        "  Actual: " + uuid + "\n");
    }

    @Test
    @DisplayName("If contract is violated, bean validation error is expected (UserDTO.UserPassport)")
    public void test1639065952606() {
        final String uuid = UUID.randomUUID().toString();
        final UserDTO user = genUserDTO().passport(p -> p.number(uuid));
        final ThrowableRunnable runnable = () -> MOCK_CLIENT.getUser(200, user).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(UserDTO::assertConsistency));
        assertThrow(runnable)
                .assertClass(BriefAssertionError.class)
                .assertMessageIs("" +
                        "Collected the following errors:\n\n" +
                        "Model property: UserDTO.passport.number\n" +
                        "Expected: must match \"^[0-9]{6}$\"\n" +
                        "  Actual: " + uuid + "\n");
    }

}