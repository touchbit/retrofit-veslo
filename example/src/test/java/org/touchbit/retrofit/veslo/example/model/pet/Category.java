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

package org.touchbit.retrofit.veslo.example.model.pet;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.retrofit.veslo.example.model.AssertableModel;
import org.touchbit.retrofit.veslo.example.utils.Generator;
import veslo.asserter.SoftlyAsserter;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.touchbit.retrofit.veslo.example.utils.StreamUtils.rangeStreamMap;

/**
 * Category
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class Category extends AssertableModel<Category> {

    private @NotNull @Min(0) Long id = null;
    private @Size(min = 1, max = 255) String name = null;

    @Override
    public Category match(SoftlyAsserter asserter, String parentName, Category expected) {
        asserter.ignoreNPE(true);
        asserter.softly(() -> assertThat(this.id()).as(getName(parentName, "category.id")).isEqualTo(expected.id()));
        asserter.softly(() -> assertThat(this.name()).as(getName(parentName, "category.name")).isEqualTo(expected.name()));
        return this;
    }

    public static List<Category> generate(int count) {
        return rangeStreamMap(count, Category::generate).collect(Collectors.toList());
    }

    public static Category generate() {
        return new Category()
                .id(Generator.positiveLong())
                .name(Generator.alphabetical(10));
    }

}
