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
import org.touchbit.retrofit.veslo.example.model.pet.PetStatus;
import org.touchbit.retrofit.veslo.example.tests.api.BasePetTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.touchbit.retrofit.veslo.example.client.transport.querymap.LoginUserQueryMap.ADMIN;

@DisplayName("Find pet by stats: /v2/pet/findByStatus")
public class FindPetsByStatusTests extends BasePetTest {

    @Test
    @DisplayName("Successfully getting a list of pets by status 'available'")
    public void test1640453029688() {
        USER_API.authenticateUser(ADMIN);
        PET_API.findPetsByStatus(PetStatus.available).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> {
                    final Set<PetStatus> statuses = body.stream().map(Pet::status).collect(Collectors.toSet());
                    assertThat(statuses).as("Pet list statuses").containsOnly(PetStatus.available);
                }));
    }

    @Test
    @DisplayName("Successfully getting a list of pets by status 'pending'")
    public void test1640454410473() {
        USER_API.authenticateUser(ADMIN);
        PET_API.findPetsByStatus(PetStatus.pending).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> {
                    final Set<PetStatus> statuses = body.stream().map(Pet::status).collect(Collectors.toSet());
                    assertThat(statuses).as("Pet list statuses").containsOnly(PetStatus.pending);
                }));
    }

    @Test
    @DisplayName("Successfully getting a list of pets by status 'sold'")
    public void test1640454419524() {
        USER_API.authenticateUser(ADMIN);
        PET_API.findPetsByStatus(PetStatus.sold).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> {
                    final Set<PetStatus> statuses = body.stream().map(Pet::status).collect(Collectors.toSet());
                    assertThat(statuses).as("Pet list statuses").containsOnly(PetStatus.sold);
                }));
    }

    @Test
    @DisplayName("Successfully getting an empty list of pets by status 'fooBar'")
    public void test1640454480699() {
        USER_API.authenticateUser(ADMIN);
        PET_API.findPetsByStatus("fooBar").assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(body -> {
                    final Set<PetStatus> statuses = body.stream().map(Pet::status).collect(Collectors.toSet());
                    assertThat(statuses).as("Pet list statuses").isEmpty();
                }));
    }

}
