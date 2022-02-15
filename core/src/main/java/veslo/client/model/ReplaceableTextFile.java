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

package veslo.client.model;

import org.apache.commons.lang3.reflect.FieldUtils;
import veslo.ReplaceableTextFileException;
import veslo.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static veslo.client.model.ReplaceableTextFile.TextFileType.RESOURCE_FILE;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.02.2022
 */
public abstract class ReplaceableTextFile {

    /**
     * Text file content
     */
    private final String fileContent;

    /**
     * @param textFileType - {@link TextFileType#OS_FILE} or {@link TextFileType#RESOURCE_FILE}
     * @param textFilePath - OS path or resource path to text file
     */
    protected ReplaceableTextFile(TextFileType textFileType, String textFilePath) {
        this(textFileType, textFilePath, UTF_8);
    }

    /**
     * @param textFileType - {@link TextFileType#OS_FILE} or {@link TextFileType#RESOURCE_FILE}
     * @param textFilePath - OS path or resource path to text file
     * @param charset - text file {@link Charset}
     */
    protected ReplaceableTextFile(TextFileType textFileType, String textFilePath, Charset charset) {
        Utils.parameterRequireNonNull(textFileType, "textFileType");
        Utils.parameterRequireNonNull(textFilePath, "textFilePath");
        Utils.parameterRequireNonNull(charset, "charset");
        if (RESOURCE_FILE.equals(textFileType)) {
            fileContent = ResourceFile.resourceToString(textFilePath, charset);
        } else {
            final File file = new File(textFilePath);
            try {
                final byte[] data = Files.readAllBytes(file.toPath());
                fileContent = new String(data, charset);
            } catch (IOException e) {
                throw new ReplaceableTextFileException("Unable to read file: " + file, e);
            }
        }
    }

    /**
     * @return text file content, parts of which are replaced by the regular expression {@link ReplaceAll} with values from the corresponding fields.
     */
    @Override
    public String toString() {
        final AtomicReference<String> result = new AtomicReference<>(fileContent);
        final Field[] declaredFields = this.getClass().getDeclaredFields();
        Arrays.stream(declaredFields)
                .filter(f -> f.isAnnotationPresent(ReplaceAll.class))
                .collect(Collectors.toMap(f -> f.getAnnotation(ReplaceAll.class).regex(), this::getReplacement))
                .forEach((regex, replacement) -> result.set(result.get().replaceAll(regex, replacement)));
        return result.get();
    }

    /**
     * @param field - declared field
     * @return stringed field value or empty string if the field value is null
     */
    protected String getReplacement(final Field field) {
        Utils.parameterRequireNonNull(field, "field");
        try {
            final Object rawValue = FieldUtils.readField(this, field.getName(), true);
            return String.valueOf(rawValue);
        } catch (Exception e) {
            throw new ReplaceableTextFileException("Unable to get value from field: " +
                    this.getClass().getSimpleName() + "." + field.getName(), e);
        }
    }

    /**
     * Type of the readable text file
     */
    public enum TextFileType {

        /**
         * read {@link File}
         */
        OS_FILE,
        /**
         * read {@link ResourceFile}
         */
        RESOURCE_FILE,

    }

}
