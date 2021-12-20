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

package org.touchbit.retrofit.veslo.client;

import org.touchbit.retrofit.veslo.client.model.RawBody;
import org.touchbit.retrofit.veslo.client.model.ResourceFile;
import org.touchbit.retrofit.veslo.client.response.DualResponse;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.File;

@SuppressWarnings("UnusedReturnValue")
public interface DualResponseAdapterFactoryClient {

    // Raw types

    @POST("/api/mock/ByteArray")
    @Headers("Content-Type: text/plain")
    DualResponse<Byte[], Byte[]> returnByteArray(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/RawBody")
    @Headers("Content-Type: text/plain")
    DualResponse<RawBody, RawBody> returnRawBody(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/File")
    @Headers("Content-Type: text/plain")
    DualResponse<File, File> returnFile(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/ResourceFile")
    @Headers("Content-Type: text/plain")
    DualResponse<ResourceFile, ResourceFile> returnResourceFile(@Query("status") int status, @Body Object responseBody);

    // Reference types

    @POST("/api/mock/Object")
    @Headers("Content-Type: text/plain")
    DualResponse<Object, Object> returnObject(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/String")
    @Headers("Content-Type: text/plain")
    DualResponse<String, String> returnString(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Character")
    @Headers("Content-Type: text/plain")
    DualResponse<Character, Character> returnCharacter(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Boolean")
    @Headers("Content-Type: text/plain")
    DualResponse<Boolean, Boolean> returnBoolean(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Byte")
    @Headers("Content-Type: text/plain")
    DualResponse<Byte, Byte> returnByte(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Integer")
    @Headers("Content-Type: text/plain")
    DualResponse<Integer, Integer> returnInteger(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Double")
    @Headers("Content-Type: text/plain")
    DualResponse<Double, Double> returnDouble(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Float")
    @Headers("Content-Type: text/plain")
    DualResponse<Float, Float> returnFloat(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Long")
    @Headers("Content-Type: text/plain")
    DualResponse<Long, Long> returnLong(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Short")
    @Headers("Content-Type: text/plain")
    DualResponse<Short, Short> returnShort(@Query("status") int status, @Body Object responseBody);

}
