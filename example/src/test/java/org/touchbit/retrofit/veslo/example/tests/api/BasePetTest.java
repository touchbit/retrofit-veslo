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

package org.touchbit.retrofit.veslo.example.tests.api;

import org.touchbit.retrofit.veslo.example.model.pet.Pet;
import org.touchbit.retrofit.veslo.example.model.pet.PetStatus;
import org.touchbit.retrofit.veslo.example.tests.BaseTest;

import java.util.Collections;
import java.util.List;

public abstract class BasePetTest extends BaseTest {

    /**
     * Helper method
     *
     * @return generated {@link Pet} (added to the system)
     */
    public static Pet addRandomPet() {
        final Pet gen = Pet.generate();
        return PET_API.addPet(gen).assertResponse(respAsserter -> respAsserter
                        .assertHttpStatusCodeIs(200)
                        .assertSucBody(Pet::assertConsistency))
                .getSucDTO();
    }

    /**
     * Helper method
     *
     * @return existing {@link Pet} with random {@link PetStatus}
     */
    public static Pet findRandomPet() {
        return findRandomPet(PetStatus.values());
    }

    /**
     * Helper method
     *
     * @param statuses expected {@link PetStatus} array
     * @return existing {@link Pet} with specifying {@link PetStatus} array
     */
    public static Pet findRandomPet(PetStatus... statuses) {
        final List<Pet> list = PET_API.findPetsByStatus((Object[]) statuses)
                .assertResponse(respAsserter -> respAsserter
                        .assertHttpStatusCodeIs(200)
                        .assertSucBodyNotNull()).getSucDTO();
        //noinspection ConstantConditions
        Collections.shuffle(list);
        return list.get(0);
    }

}
