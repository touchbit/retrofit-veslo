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

package org.touchbit.retrofit.veslo.example.tests.api.pet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.veslo.example.model.Status;
import org.touchbit.retrofit.veslo.example.model.pet.Pet;
import org.touchbit.retrofit.veslo.example.tests.api.BasePetTest;

import java.util.List;

import static org.touchbit.retrofit.veslo.example.client.transport.querymap.LoginUserQueryMap.ADMIN;
import static org.touchbit.retrofit.veslo.example.model.Status.CODE_1;

@DisplayName("Add pet: POST /v2/pet")
public class AddPetTests extends BasePetTest {

    @Test
    @DisplayName("Checking the Pet model contract (PropertyNamingStrategy.SnakeCaseStrategy)")
    public void test1640455066880() {
        USER_API.authenticateUser(ADMIN);
        PET_API.addPet(Pet.generate()).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> body.assertNoAdditionalProperties().assertConsistency()));
    }

    @Test
    @DisplayName("Successful creating a pet using an existing identifier")
    public void test1640069747665() {
        USER_API.authenticateUser(ADMIN);
        final Pet expected = Pet.generate();
        PET_API.addPet(expected).assertResponse(asserter -> asserter             // ----\
                .assertHttpStatusCodeIs(200)                                     //      |
                .assertSucBody((softly, act) -> act.match(asserter, expected))); // ----/|
    }                                                                            //      |
                                                                                 //      | assertions
    @Test                                                                        //      |    are
    @DisplayName("Successful creating a pet using random identifier")            //      | equivalent
    public void test1640460353980() {                                            //      |
        USER_API.authenticateUser(ADMIN);                                        //      |
        final Pet expected = Pet.generate();                                     //      |
        PET_API.addPet(expected).assertSucResponse(Pet::assertPOST, expected);   // -----|
    }

    @Test
    @DisplayName("An error is expected when creating a pet with a Pet.id of non-Long type")
    public void test1640450206604() {
        USER_API.authenticateUser(ADMIN);
        final Pet pet = Pet.generate().id(null).additionalProperty("id", "example");
        PET_API.addPet(pet).assertErrResponse(Status::assert400,
                CODE_1.message("java.lang.NumberFormatException: For input string: \"example\""));
    }

    @Test
    @DisplayName("An error is expected when creating a pet with a non-Pet type object (List)")
    public void test1640452385090() {
        USER_API.authenticateUser(ADMIN);
        final List<Pet> pets = Pet.generate(1);
        PET_API.addPet(pets).assertErrResponse(Status::assert400,
                CODE_1.message("An object was expected, but an array was received."));
    }

}
