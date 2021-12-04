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

package org.touchbit.retrofit.ext.dmr.jackson.client;

import org.touchbit.retrofit.ext.dmr.client.EndpointInfo;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.jackson.client.model.ErrorDTO;
import org.touchbit.retrofit.ext.dmr.jackson.client.model.UserDTO;
import retrofit2.http.*;

import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public interface MockClient {

    @POST("/api/mock/unprocessed/dto")
    @EndpointInfo("Processing two models into a integer")
    DualResponse<Integer, Integer> unprocessedDTO(@Query("status") int status,
                                                  @Body Object responseBody);

    @POST("/api/mock/text/plain/string")
    @Headers("Content-Type: text/plain")
    @EndpointInfo("Processing two models into a string")
    DualResponse<String, String> textPlainString(@Query("status") int status,
                                                 @Body Object responseBody);

    @FormUrlEncoded
    @POST("/api/mock/form/url/encoded/string")
    @EndpointInfo("Processing two models into a string")
    DualResponse<String, String> formUrlEncodedTextPlainString(@Query("status") int status,
                                                               @Field("method") Object responseBody);

    @POST("/api/mock/application/json/string")
    @Headers("Content-Type: application/json")
    @EndpointInfo("Processing two models into a JSON string")
    DualResponse<String, String> applicationJsonString(@Query("status") int status,
                                                       @Body Object responseBody);

    @POST("/api/mock/text/plain/absent")
    @Headers("Content-Type: text/plain")
    @EndpointInfo("Processing two models into a string")
    DualResponse<RawBody, RawBody> textPlainAnyBody(@Query("status") int status,
                                                    @Body Object responseBody);

    @POST("/api/mock/application/json/absent")
    @Headers("Content-Type: application/json")
    @EndpointInfo("Processing two models into a JSON string")
    DualResponse<RawBody, RawBody> applicationJsonAnyBody(@Query("status") int status,
                                                          @Body Object responseBody);

    @POST("/api/mock/application/json/absent")
    @Headers("Content-Type: application/json")
    @EndpointInfo("Processing two models into a JSON string")
    DualResponse<UserDTO, ErrorDTO> applicationJsonJacksonDTO(@Query("status") int status,
                                                              @Body Object responseBody);

    @POST("/api/mock/application/json/absent")
    @Headers("Content-Type: application/json")
    @EndpointInfo("Processing two models into a JSON string")
    DualResponse<Map<String, Object>, Map<String, Object>> applicationJsonJacksonMap(@Query("status") int status,
                                                                                     @Body Object responseBody);

    @POST("/api/mock/application/json/absent")
    @Headers("Content-Type: application/json")
    @EndpointInfo("Processing two models into a JSON string")
    DualResponse<Byte[], Byte[]> applicationJsonByteArray(@Query("status") int status,
                                                          @Body Object responseBody);

@EndpointInfo("Get user by id")
@Headers("Content-Type: application/json")
@GET("/api/mock/application/json/absent")
DualResponse<UserDTO, ErrorDTO> getUser(@Query("id") String id);
}
