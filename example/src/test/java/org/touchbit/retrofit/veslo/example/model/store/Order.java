/*
 * Copyright 2021-2022 Shaburov Oleg
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

package org.touchbit.retrofit.veslo.example.model.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.retrofit.veslo.example.model.AssertableModel;
import veslo.asserter.SoftlyAsserter;

import java.time.OffsetDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Order
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class Order extends AssertableModel<Order> {

    private Long id = null;
    private Long petId = null;
    private Integer quantity = null;
    private OffsetDateTime shipDate = null;

    @Override
    public Order match(SoftlyAsserter asserter, String parentName, Order expected) {
        asserter.softly(() -> assertThat(this.id()).as(getName(parentName, "order.id")).isEqualTo(expected.id()));
        asserter.softly(() -> assertThat(this.petId()).as(getName(parentName, "order.petId")).isEqualTo(expected.petId()));
        asserter.softly(() -> assertThat(this.quantity()).as(getName(parentName, "order.quantity")).isEqualTo(expected.quantity()));
        asserter.softly(() -> assertThat(this.shipDate()).as(getName(parentName, "order.shipDate")).isEqualTo(expected.shipDate()));
        return this;
    }

}
