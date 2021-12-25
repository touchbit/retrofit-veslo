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

import org.touchbit.retrofit.veslo.example.model.store.Order;
import retrofit2.Callback;
import retrofit2.http.*;

import java.util.Map;

public interface StoreApi {

    /**
     * Delete purchase order by ID
     * Sync method
     * For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors
     *
     * @param orderId ID of the order that needs to be deleted (required)
     * @return Void
     */
    @DELETE("/store/order/{orderId}")
    Void deleteOrder(@Path("orderId") Long orderId);

    /**
     * Delete purchase order by ID
     * Async method
     *
     * @param orderId ID of the order that needs to be deleted (required)
     * @param cb      callback method
     */
    @DELETE("/store/order/{orderId}")
    void deleteOrder(@Path("orderId") Long orderId, Callback<Void> cb);

    /**
     * Returns pet inventories by status
     * Sync method
     * Returns a map of status codes to quantities
     *
     * @return Map&lt;String, Integer&gt;
     */
    @GET("/store/inventory")
    Map<String, Integer> getInventory();

    /**
     * Returns pet inventories by status
     * Async method
     *
     * @param cb callback method
     */
    @GET("/store/inventory")
    void getInventory(Callback<Map<String, Integer>> cb);

    /**
     * Find purchase order by ID
     * Sync method
     * For valid response try integer IDs with value &gt;&#x3D; 1 and &lt;&#x3D; 10. Other values will generated exceptions
     *
     * @param orderId ID of pet that needs to be fetched (required)
     * @return Order
     */
    @GET("/store/order/{orderId}")
    Order getOrderById(@Path("orderId") Long orderId);

    /**
     * Find purchase order by ID
     * Async method
     *
     * @param orderId ID of pet that needs to be fetched (required)
     * @param cb      callback method
     */
    @GET("/store/order/{orderId}")
    void getOrderById(@Path("orderId") Long orderId, Callback<Order> cb);

    /**
     * Place an order for a pet
     * Sync method
     *
     * @param body order placed for purchasing the pet (required)
     * @return Order
     */
    @POST("/store/order")
    Order placeOrder(@Body Order body);

    /**
     * Place an order for a pet
     * Async method
     *
     * @param body order placed for purchasing the pet (required)
     * @param cb   callback method
     */
    @POST("/store/order")
    void placeOrder(@Body Order body, Callback<Order> cb);

}
