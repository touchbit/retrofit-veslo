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
import veslo.UtilityClassException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.touchbit.retrofit.veslo.example.utils.StreamUtils.rangeStreamMap;

public class Generator {

    public static final Faker EN_FAKER = Faker.instance(new Locale("en-GB"));

    private Generator() {
        throw new UtilityClassException();
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
        final String letterString = rangeStreamMap(length, "?").collect(Collectors.joining());
        return EN_FAKER.letterify(letterString);
    }

    public static String url() {
        return EN_FAKER.internet().url();
    }

    public static List<String> urls(int count) {
        return rangeStreamMap(count, Generator::url).collect(Collectors.toList());
    }

    public static String animalName() {
        return EN_FAKER.animal().name();
    }

}
