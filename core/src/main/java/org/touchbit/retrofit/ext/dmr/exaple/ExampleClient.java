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

package org.touchbit.retrofit.ext.dmr.exaple;

import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.exaple.dto.ErrDTO;
import org.touchbit.retrofit.ext.dmr.exaple.dto.SucDTO;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExampleClient {

    @POST("/api/mock/example")
    @Headers({"Content-Type: application/json", "X-Request-Id: random", "Access-Control-Allow-Origin: *"})
    DualResponse<SucDTO, ErrDTO> exampleApiCall(@Query("status") int status, @Body Object body);

    default DualResponse<SucDTO, ErrDTO> exampleSucCall(Object body) {
        return exampleApiCall(200, body);
    }

    default DualResponse<SucDTO, ErrDTO> exampleErrCall(Object body) {
        return exampleApiCall(500, body);
    }

}
