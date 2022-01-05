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

package veslo.client;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import veslo.client.gson.ErrDTO;
import veslo.client.gson.SucDTO;
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;
import veslo.client.response.DualResponse;

import java.io.File;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "rawtypes"})
public interface GsonDualConverterFactoryClient {

    // Raw types

    @POST("/api/mock/application/json/ByteArray")
    @Headers("Content-Type: application/json")
    DualResponse<Byte[], Byte[]> returnByteArray(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/RawBody")
    @Headers("Content-Type: application/json")
    DualResponse<RawBody, RawBody> returnRawBody(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/File")
    @Headers("Content-Type: application/json")
    DualResponse<File, File> returnFile(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/ResourceFile")
    @Headers("Content-Type: application/json")
    DualResponse<ResourceFile, ResourceFile> returnResourceFile(@Query("status") int status, @Body Object responseBody);

    // Reference types

    @POST("/api/mock/application/json/Object")
    @Headers("Content-Type: application/json")
    DualResponse<Object, Object> returnObject(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/ListString")
    @Headers("Content-Type: application/json")
    DualResponse<List<SucDTO>, List<ErrDTO>> returnListJacksonModel(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/RawList")
    @Headers("Content-Type: application/json")
    DualResponse<List, List> returnRawList(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/MapStringObject")
    @Headers("Content-Type: application/json")
    DualResponse<Map<String, Object>, Map<String, Object>> returnMapStringObject(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/RawMap")
    @Headers("Content-Type: application/json")
    DualResponse<Map, Map> returnRawMap(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/SucDTOErrDTO")
    @Headers("Content-Type: application/json")
    DualResponse<SucDTO, ErrDTO> returnJacksonModel(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/String")
    @Headers("Content-Type: application/json")
    DualResponse<String, String> returnString(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Character")
    @Headers("Content-Type: application/json")
    DualResponse<Character, Character> returnCharacter(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Boolean")
    @Headers("Content-Type: application/json")
    DualResponse<Boolean, Boolean> returnBoolean(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Byte")
    @Headers("Content-Type: application/json")
    DualResponse<Byte, Byte> returnByte(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Integer")
    @Headers("Content-Type: application/json")
    DualResponse<Integer, Integer> returnInteger(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Double")
    @Headers("Content-Type: application/json")
    DualResponse<Double, Double> returnDouble(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Float")
    @Headers("Content-Type: application/json")
    DualResponse<Float, Float> returnFloat(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Long")
    @Headers("Content-Type: application/json")
    DualResponse<Long, Long> returnLong(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/application/json/Short")
    @Headers("Content-Type: application/json")
    DualResponse<Short, Short> returnShort(@Query("status") int status, @Body Object responseBody);

}
