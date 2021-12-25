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

import static org.touchbit.retrofit.veslo.example.tests.api.ErrorCodes.code1;
import static org.touchbit.retrofit.veslo.example.tests.api.ErrorCodes.code200;

@DisplayName("Delete pet: /v2/pet/{petId}")
public class DeletePetTests extends BasePetTest {

    @Test
    @DisplayName("Successful deleting a pet using an existing identifier")
    public void test1640105249957() {
        loginTestUser();
        final Pet pet = AddPetTests.addRandomPet();
        PET_API.deletePet(pet.id()).assertSucResponse(this::assertStatus200, code200(pet.id()));
    }

    @Test
    @DisplayName("An error is expected when deleting a pet by non-existent identifier")
    public void test1640100570201() {
        loginTestUser();
        PET_API.deletePet(922137203685775807L).assertErrResponse(this::assertStatus404, code1("Pet not found"));
    }

    @Test
    @DisplayName("An error is expected when deleting a pet without authentication token")
    public void test1640446272452() {
        loginTestUser();
        final Pet pet = AddPetTests.addRandomPet();
        logout();
        PET_API.deletePet(pet.id()).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(401)
                .assertErrBody(this::assertStatusModel, code1("Access to delete pet requires authentication.")));
    }

    @Test
    @DisplayName("An error is expected when deleting a pet with a pet identifier of non-Long type")
    public void test1640449284033() {
        loginTestUser();
        PET_API.deletePet("example").assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(400)
                .assertErrBody(this::assertStatusModel, code1("java.lang.NumberFormatException: For input string: \"example\"")));
    }

}
