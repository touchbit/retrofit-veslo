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
import jakarta.validation.Valid;
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
import veslo.asserter.HeadersAsserter;
import veslo.asserter.ResponseAsserter;
import veslo.asserter.SoftlyAsserter;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.touchbit.retrofit.veslo.example.utils.StreamUtils.rangeStreamMap;

/**
 * Pet
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class Pet extends AssertableModel<Pet> {

    private @NotNull @Min(0) Long id = null;
    private @NotNull @Size(max = 10) List<@Size(min = 1, max = 255) String> photoUrls;
    private @Valid Category category = null;
    private @Size(min = 1, max = 255) String name = null;
    private @NotNull @Size(max = 10) List<@Valid Tag> tags;
    private PetStatus status;

    public static void assertGET(ResponseAsserter<Pet, ?, HeadersAsserter> asserter, Pet expected) {
        asserter.assertHttpStatusCodeIs(200).assertSucBody(actual -> actual.match(expected));
    }

    public static void assertPOST(ResponseAsserter<Pet, ?, HeadersAsserter> asserter, Pet expected) {
        asserter.assertHttpStatusCodeIs(200).assertSucBody(actual -> actual.match(expected));
    }

    public static void assertPATCH(ResponseAsserter<Pet, ?, HeadersAsserter> asserter) {
        asserter.assertHttpStatusCodeIs(204).assertSucBodyIsNull().assertErrBodyIsNull();
    }

    @Override
    public Pet match(SoftlyAsserter asserter, String parentName, Pet expected) {
        asserter.ignoreNPE(true);
        asserter.softly(() -> assertThat(this.id()).as(getName(parentName, "pet.id")).isEqualTo(expected.id()));
        asserter.softly(() -> assertThat(this.name()).as(getName(parentName, "pet.name")).isEqualTo(expected.name()));
        asserter.softly(() -> assertThat(this.photoUrls()).as(getName(parentName, "pet.photoUrls")).isEqualTo(expected.photoUrls()));
        asserter.softly(() -> assertThat(this.tags()).as(getName(parentName, "pet.tags")).containsExactlyInAnyOrderElementsOf(expected.tags()));
        asserter.softly(() -> assertThat(this.category()).as(getName(parentName, "pet.category")).isNotNull());
        asserter.softly(() -> this.category().match(asserter, "pet", expected.category()));
        return this;
    }

    public static List<Pet> generate(int count) {
        return rangeStreamMap(count, Pet::generate).collect(Collectors.toList());
    }

    public static Pet generate() {
        return new Pet()
                .id(Generator.positiveLong())
                .name(Generator.animalName())
                .photoUrls(Generator.urls(1))
                .tags(Tag.generate(2))
                .category(Category.generate());
    }

}
