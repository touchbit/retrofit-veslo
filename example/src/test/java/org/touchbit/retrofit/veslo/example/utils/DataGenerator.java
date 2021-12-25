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

package org.touchbit.retrofit.veslo.example.utils;

import com.github.javafaker.Faker;
import org.touchbit.retrofit.veslo.example.model.pet.Category;
import org.touchbit.retrofit.veslo.example.model.pet.Pet;
import org.touchbit.retrofit.veslo.example.model.pet.Tag;
import org.touchbit.retrofit.veslo.exception.UtilityClassException;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataGenerator {

    private static final Faker EN_FAKER = new Faker(new Locale("en-GB"));

    private DataGenerator() {
        throw new UtilityClassException();
    }

    public static List<Pet> generatePets(int count) {
        return rangeMap(count, DataGenerator::generatePet).collect(Collectors.toList());
    }

    public static Pet generatePet() {
        return new Pet()
                .id(positiveLong())
                .name(EN_FAKER.animal().name())
                .photoUrls(urls(1))
                .tags(generateTags(2))
                .category(generateCategory());
    }

    public static List<Tag> generateTags(int count) {
        return rangeMap(count, DataGenerator::generateTag).collect(Collectors.toList());
    }

    public static Tag generateTag() {
        return new Tag()
                .id(positiveLong())
                .name(alphabetical(10));
    }

    public static List<Category> generateCategories(int count) {
        return rangeMap(count, DataGenerator::generateCategory).collect(Collectors.toList());
    }

    public static Category generateCategory() {
        return new Category()
                .id(positiveLong())
                .name(alphabetical(10));
    }

    public static Long positiveLong() {
        return EN_FAKER.number().numberBetween(0L, Integer.MAX_VALUE);
    }

    /**
     * alphabetical(10) equivalent to FAKER.letterify("??????????")
     *
     * @param length random string length
     * @return [a-z] random string
     */
    public static String alphabetical(int length) {
        final String letterString = rangeMap(length, "?").collect(Collectors.joining());
        return EN_FAKER.letterify(letterString);
    }

    public static String url() {
        return EN_FAKER.internet().url();
    }

    public static List<String> urls(int count) {
        return rangeMap(count, DataGenerator::url).collect(Collectors.toList());
    }

    public static Stream<Integer> range(int length) {
        return IntStream.range(0, length).boxed();
    }

    public static <R> Stream<R> rangeMap(int length, Supplier<? extends R> supplier) {
        return range(length).map(i -> supplier.get());
    }

    public static <R> Stream<R> rangeMap(int length, R value) {
        return range(length).map(i -> value);
    }

}
