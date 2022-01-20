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

import veslo.UtilityClassException;

import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {
        throw new UtilityClassException();
    }

    /**
     * @param length - fori length (int i = 0; i < 10; i++)
     * @return fori Stream
     */
    public static Stream<Integer> rangeStream(int length) {
        return IntStream.range(0, length).boxed();
    }

    /**
     * StreamUtils.rangeStreamMap(5, Object::new) will return a stream of objects of size 5
     *
     * @param length   - fori length (int i = 0; i < 10; i++)
     * @param supplier - cycle payload
     * @return stream of mapped payload
     */
    public static <R> Stream<R> rangeStreamMap(int length, Supplier<? extends R> supplier) {
        return rangeStream(length).map(i -> supplier.get());
    }

    /**
     * StreamUtils.rangeStreamMap(3, "?") will return stream of ["?", "?", "?"]
     *
     * @param length - fori length (int i = 0; i < 10; i++)
     * @param value  - mappet object
     * @return stream of mapped objects
     */
    public static <R> Stream<R> rangeStreamMap(int length, R value) {
        return rangeStream(length).map(i -> value);
    }

}
