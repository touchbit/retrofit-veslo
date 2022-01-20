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

package org.touchbit.retrofit.veslo.example.client;

import io.qameta.allure.Description;
import org.touchbit.retrofit.veslo.example.model.Status;
import org.touchbit.retrofit.veslo.example.model.pet.Pet;
import retrofit2.http.*;
import veslo.AResponse;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public interface PetApi {

    /**
     * @param body {@link Pet} object that needs to be added to the store (required)
     */
    @POST("/v2/pet")
    @Headers({"Content-Type: application/json"})
    @Description("Add a new pet to the store")
    AResponse<Pet, Status> addPet(@Body Object body);

    /**
     * @param petId {@link String} pet id to delete (required)
     */
    @DELETE("/v2/pet/{petId}")
    @Description("Delete a pet")
    AResponse<Status, Status> deletePet(@Path("petId") Object petId);

    /**
     * @param status Status values that need to be considered for filter (required)
     */
    @GET("/v2/pet/findByStatus")
    @Description("Finds Pets by status")
    AResponse<List<Pet>, Status> findPetsByStatus(@Query("status") Object... status);

    /**
     * @param tags Tags to filter by (required)
     */
    @GET("/v2/pet/findByTags")
    @Description("Finds Pets by tags")
    AResponse<List<Pet>, Status> findPetsByTags(@Query("tags") List<String> tags);

    /**
     * @param petId {@link Long} ID of pet to return (required)
     */
    @GET("/v2/pet/{petId}")
    @Description("Get a single pet by ID")
    AResponse<Pet, Status> getPetById(@Path("petId") Object petId);

    /**
     * @param body Pet object that needs to be added to the store (required)
     */
    @PUT("/v2/pet")
    @Description("Update an existing pet")
    AResponse<Status, Status> updatePet(@Body Pet body);

    /**
     * @param petId  ID of pet that needs to be updated (required)
     * @param name   (optional)
     * @param status (optional)
     */
    @FormUrlEncoded
    @POST("/v2/pet/{petId}")
    @Description("Updates a pet in the store with form data")
    AResponse<Status, Status> updatePetWithForm(@Path("petId") Long petId,
                                                @Field("name") String name,
                                                @Field("status") String status);

    /**
     * @param petId              ID of pet to update (required)
     * @param additionalMetadata (optional)
     * @param file               (optional)
     */
    @Multipart
    @POST("/v2/pet/{petId}/uploadImage")
    @Description("Uploads pet image")
    Status uploadFile(@Path("petId") Long petId,
                      @Part("additionalMetadata") String additionalMetadata,
                      @Part("file") File file);

}
