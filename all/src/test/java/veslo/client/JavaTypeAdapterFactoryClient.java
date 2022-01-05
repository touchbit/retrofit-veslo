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
import veslo.client.model.RawBody;
import veslo.client.model.ResourceFile;

import java.io.File;

@SuppressWarnings("UnusedReturnValue")
public interface JavaTypeAdapterFactoryClient {

    // Raw types

    @POST("/api/mock/ByteArray")
    @Headers("Content-Type: text/plain")
    Byte[] returnByteArrayReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/RawBody")
    @Headers("Content-Type: text/plain")
    RawBody returnRawBodyReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/File")
    @Headers("Content-Type: text/plain")
    File returnFileReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/ResourceFile")
    @Headers("Content-Type: text/plain")
    ResourceFile returnResourceFileReferenceType(@Query("status") int status, @Body Object responseBody);

    // Reference types

    @POST("/api/mock/Object")
    @Headers("Content-Type: text/plain")
    Object returnObjectReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/String")
    @Headers("Content-Type: text/plain")
    String returnStringReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Character")
    @Headers("Content-Type: text/plain")
    Character returnCharacterReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Boolean")
    @Headers("Content-Type: text/plain")
    Boolean returnBooleanReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Byte")
    @Headers("Content-Type: text/plain")
    Byte returnByteReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Integer")
    @Headers("Content-Type: text/plain")
    Integer returnIntegerReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Double")
    @Headers("Content-Type: text/plain")
    Double returnDoubleReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Float")
    @Headers("Content-Type: text/plain")
    Float returnFloatReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Long")
    @Headers("Content-Type: text/plain")
    Long returnLongReferenceType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/Short")
    @Headers("Content-Type: text/plain")
    Short returnShortReferenceType(@Query("status") int status, @Body Object responseBody);

    // Primitive types

    @POST("/api/mock/char")
    @Headers("Content-Type: text/plain")
    char returnCharPrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/boolean")
    @Headers("Content-Type: text/plain")
    boolean returnBooleanPrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/byte")
    @Headers("Content-Type: text/plain")
    byte returnBytePrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/int")
    @Headers("Content-Type: text/plain")
    int returnIntPrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/double")
    @Headers("Content-Type: text/plain")
    double returnDoublePrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/float")
    @Headers("Content-Type: text/plain")
    float returnFloatPrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/long")
    @Headers("Content-Type: text/plain")
    long returnLongPrimitiveType(@Query("status") int status, @Body Object responseBody);

    @POST("/api/mock/short")
    @Headers("Content-Type: text/plain")
    short returnShortPrimitiveType(@Query("status") int status, @Body Object responseBody);

}
