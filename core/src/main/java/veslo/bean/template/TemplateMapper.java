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

package veslo.bean.template;

import org.apache.commons.lang3.reflect.FieldUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.TemplateException;
import veslo.UtilityClassException;
import veslo.util.Utils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads a text template from a file using the {@link TemplateSource} annotation.
 * Replace strings in a template with values from fields using a regular expression from {@link TemplateReplaceAll} annotation.
 * <p>
 * * @Getter
 * * @Setter
 * * @Accessors(chain = true, fluent = true)
 * * @TemplateSource(type = RESOURCE, path = "Notes.xml")
 * * public static final class Notes {
 * *
 * *     @TemplateReplaceAll(regex = "\\[note.to]")
 * *     private String to;
 * *     @TemplateReplaceAll(regex = "replace_note_from")
 * *     private String from;
 * *     @TemplateReplaceAll(regex = "\\{note_body_content}")
 * *     private String bodyContent;
 * *
 * * }
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 18.02.2022
 */
public class TemplateMapper {

    /**
     * Utility class. Forbidden instantiation.
     */
    private TemplateMapper() {
        throw new UtilityClassException();
    }

    /**
     * Reads the template body according to the data from the {@link TemplateSource} annotation.
     * Replace blocks of text in the template that match a regular expression.
     * The regular expression is taken from the {@link TemplateReplaceAll#regex()}.
     * The value is taken from the corresponding field, which is marked with the {@link TemplateReplaceAll} annotation.
     *
     * @param template - java bean with {@link TemplateSource} annotation
     * @return filled template with values from fields marked with {@link TemplateReplaceAll} annotation
     * @throws NullPointerException if template is null
     */
    @EverythingIsNonNull
    public static String marshal(Object template) {
        Utils.parameterRequireNonNull(template, "template");
        final Field[] declaredFields = template.getClass().getDeclaredFields();
        final List<Field> replaceableFields = Arrays.stream(declaredFields)
                .filter(f -> f.isAnnotationPresent(TemplateReplaceAll.class))
                .collect(Collectors.toList());
        String content = readTemplate(template);
        for (Field replaceableField : replaceableFields) {
            final String replacement = getReplacement(template, replaceableField);
            final String regex = replaceableField.getAnnotation(TemplateReplaceAll.class).regex();
            content = content.replaceAll(regex, replacement);
        }
        return content;
    }

    /**
     * Reads the contents of the file passed in the {@link TemplateSource} annotation, where
     * - {@link TemplateSource#path()} - source file path
     * - {@link TemplateSource#type()} - source type ({@link TemplateSourceType#FILE} or {@link TemplateSourceType#RESOURCE})
     * - {@link TemplateSource#charset()} - source file charset (UTF-8 by default)
     *
     * @param template - java bean with {@link TemplateSource} annotation
     * @return file content from {@link TemplateSource} annotation
     * @throws TemplateException    if {@link TemplateSource} annotation not present
     * @throws TemplateException    if {@link TemplateSource#charset()} is not supported
     * @throws NullPointerException if template is null
     */
    @EverythingIsNonNull
    public static String readTemplate(Object template) {
        Utils.parameterRequireNonNull(template, "template");
        final TemplateSource source = template.getClass().getAnnotation(TemplateSource.class);
        if (source == null) {
            throw new TemplateException("The template class must contain an annotation.\n" +
                    "Template: " + template.getClass().getName() + "\n" +
                    "Annotation: " + TemplateSource.class.getName() + "\n");
        }
        final TemplateSourceType type = source.type();
        final String path = source.path();
        final Charset charset;
        try {
            charset = Charset.forName(source.charset());
        } catch (Exception e) {
            throw new TemplateException("TemplateSource annotation contains unsupported Charset.\n" +
                    "Template: " + template.getClass() + "\n" +
                    "Source path: " + source.path() + "\n" +
                    "Source type: " + source.type() + "\n" +
                    "Source charset: " + source.charset() + "\n", e);
        }
        if (type.equals(TemplateSourceType.RESOURCE)) {
            return Utils.readResourceFile(path, charset);
        } else {
            return Utils.readFile(path, charset);
        }
    }

    /**
     * @param template - java bean with {@link TemplateSource} annotation
     * @param field    - template declared field
     * @return stringed field value or 'null' string if the field value is null
     * @throws TemplateException    if template field not readable
     * @throws NullPointerException if template or field is null
     */
    @EverythingIsNonNull
    public static String getReplacement(final Object template, final Field field) {
        Utils.parameterRequireNonNull(template, "template");
        Utils.parameterRequireNonNull(field, "field");
        try {
            final Object rawValue = FieldUtils.readField(template, field.getName(), true);
            return String.valueOf(rawValue);
        } catch (Exception e) {
            throw new TemplateException("Unable to get value from field.\n" +
                    "Class: " + template.getClass().getName() + "\n" +
                    "Field: " + field.getName() + "\n", e);
        }
    }

}
