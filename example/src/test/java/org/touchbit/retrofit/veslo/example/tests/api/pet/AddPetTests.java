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

import java.util.Collections;
import java.util.Set;

import static org.touchbit.retrofit.veslo.example.tests.api.ErrorCodes.code1;
import static org.touchbit.retrofit.veslo.example.utils.DataGenerator.generatePet;

@DisplayName("Add pet: POST /v2/pet")
public class AddPetTests extends BasePetTest {

    @Test
    @DisplayName("Checking the Pet model contract")
    public void test1640455066880() {
        loginTestUser();
        PET_API.addPet(generatePet()).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> body
                        .assertNoAdditionalProperties()
                        .assertConsistency()));
    }

    @Test
    @DisplayName("Successful creating a pet using an existing identifier")
    public void test1640069747665() {
        loginTestUser();
        final Pet expected = generatePet();
        PET_API.addPet(expected).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(200)
                .assertSucBody((softly, act) -> act.assertPet(asserter, expected)));
    }

    @Test
    @DisplayName("Successful creating a pet using random identifier")
    public void test1640460353980() {
        loginTestUser();
        final Pet expected = generatePet();
        PET_API.addPet(expected).assertSucResponse(Pet::assertPetResponse, expected);
    }


    @Test
    @DisplayName("An error is expected when creating a pet with a Pet.id of non-Long type")
    public void test1640450206604() {
        loginTestUser();
        final Pet pet = generatePet().id(null).additionalProperty("id", "example");
        PET_API.addPet(pet).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(400)
                .assertErrBody(this::assertStatusModel, code1("java.lang.NumberFormatException: For input string: \"example\"")));
    }

    @Test
    @DisplayName("An error is expected when creating a pet with a non-Pet type object (List)")
    public void test1640452385090() {
        loginTestUser();
        final Set<Pet> pets = Collections.singleton(generatePet());
        PET_API.addPet(pets).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(400)
                .assertErrBody(this::assertStatusModel, code1("An object was expected, but an array was received.")));
    }

}
