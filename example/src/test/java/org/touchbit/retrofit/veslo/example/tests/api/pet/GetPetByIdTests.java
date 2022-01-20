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

package org.touchbit.retrofit.veslo.example.tests.api.pet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.example.model.pet.Pet;
import org.touchbit.retrofit.veslo.example.tests.api.BasePetTest;

import static org.touchbit.retrofit.veslo.example.client.transport.querymap.LoginUserQueryMap.ADMIN;
import static org.touchbit.retrofit.veslo.example.tests.api.ErrorCodes.code1;

@DisplayName("GetPetById: /v2/pet/{petId}")
public class GetPetByIdTests extends BasePetTest {

    @Test
    @DisplayName("Successful getting of a pet info using an existing identifier (created pet)")
    public void test1640068360491() {
        USER_API.authenticateUser(ADMIN);
        final Pet expected = addRandomPet();
        PET_API.getPetById(expected.id()).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(200)
                .assertSucBody(pet -> pet.assertPet(expected)));
    }

    @Test
    @DisplayName("An error is expected when getting a pet by non-existent identifier (-1)")
    public void test1640059907623() {
        USER_API.authenticateUser(ADMIN);
        PET_API.getPetById(-1).assertErrResponse(this::assertStatus404, code1("Pet not found"));
    }

}
