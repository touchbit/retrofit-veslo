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

package org.touchbit.retrofit.veslo.allure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.touchbit.retrofit.veslo.allure.model.AllureResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BaseUnitTests {

    protected static final Path RESULTS_PATH = new File("target/allure-results").toPath();

    static {
        System.setProperty("allure.results.directory", RESULTS_PATH.toString());
    }

    @BeforeEach
    public void clean() throws IOException {
        if (Files.exists(RESULTS_PATH)) {
            for (Path path : Files.list(RESULTS_PATH).collect(Collectors.toList())) {
                Files.delete(path);
            }
        }
    }

    protected static Path getResultJson() throws IOException {
        final Stream<Path> list = Files.list(RESULTS_PATH);
        for (Path path : list.collect(Collectors.toList())) {
            if (path.toString().contains("result.json")) {
                return path;
            }
        }
        throw new AssertionError("result.json file not found: " + list);
    }

    protected static AllureResult getAllureResult() throws IOException {
        final Path resultJson = getResultJson();
        final byte[] results = Files.readAllBytes(resultJson);
        return new ObjectMapper().readValue(results, AllureResult.class);
    }

}