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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for fluent model API
 * For example text file {@code testNote.xml}
 * <pre>
 * {@code
 *   <note>
 *     <to>Tove</to>
 *     <from>Jani</from>
 *     <heading>Reminder</heading>
 *     <body>{note_body}</body>
 *   </note>
 * }
 * </pre>
 * TestFile class
 * <pre>
 * {@code
 *   @Getter
 *   @Setter
 *   @Accessors(chain = true, fluent = true)
 *   public class NoteFile extends TestFile {
 *
 *       public NoteFile() {
 *           super("testNote.xml");
 *       }
 *
 *       @ReplaceAll(regex = "\\{note_body}")
 *       private String body;
 *
 *   }
 * }
 * </pre>
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.02.2022
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface ReplaceAll {

    /**
     * @return replaceable regex. For example @Replace("{first_name}").
     */
    String regex();

}

