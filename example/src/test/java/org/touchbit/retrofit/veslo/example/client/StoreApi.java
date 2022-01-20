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
import org.touchbit.retrofit.veslo.example.model.store.Order;
import retrofit2.http.*;
import veslo.AResponse;

import java.util.Map;

public interface StoreApi {

    /**
     * Delete purchase order by ID
     * Sync method
     * For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors
     *
     * @param orderId is {@link Long} ID of the order that needs to be deleted (required)
     * @return Void
     */
    @DELETE("/store/order/{orderId}")
    @Description("Delete purchase order by ID")
    AResponse<Status, Status> deleteOrder(@Path("orderId") Object orderId);

    /**
     * Returns pet inventories by status
     * Sync method
     * Returns a map of status codes to quantities
     *
     * @return Map&lt;String, Integer&gt;
     */
    @GET("/store/inventory")
    @Description("Return pet inventories by status")
    AResponse<Map<String, Integer>, Status> getInventory();

    /**
     * Find purchase order by ID
     * Sync method
     * For valid response try integer IDs with value &gt;&#x3D; 1 and &lt;&#x3D; 10. Other values will generated exceptions
     *
     * @param orderId is {@link Long} ID of pet that needs to be fetched (required)
     * @return Order
     */
    @GET("/store/order/{orderId}")
    @Description("Find purchase order by ID")
    AResponse<Order, Status> getOrderById(@Path("orderId") Object orderId);

    /**
     * Place an order for a pet
     * Sync method
     *
     * @param body is {@link Order} placed for purchasing the pet (required)
     * @return Order
     */
    @POST("/store/order")
    @Description("Place an order for a pet")
    AResponse<Order, Status> placeOrder(@Body Object body);

}
