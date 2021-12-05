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

package internal.test.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class UniqueTestMethodsGenerator extends BaseUnitTest {

    public static void main(String[] args) throws IOException {
        final List<Path> testClasses = Files.walk(Paths.get(System.getProperty("user.dir") + "/.."))
                .filter(path -> path.toFile().isFile())
                .filter(path -> path.toString().contains("/src/test/java"))
                .filter(path -> path.toString().endsWith("Tests.java"))
                .collect(Collectors.toList());
        long time = new Date().getTime();
        for (Path testClass : testClasses) {
            StringJoiner result = new StringJoiner("\n");
            final List<String> lines = Files.readAllLines(testClass);
            for (String line : lines) {
                time += 1;
                result.add(line.replaceAll("test\\d*\\(\\)", "test" + time + "()"));
            }
            Files.write(testClass, result.toString().getBytes());
        }
    }

}