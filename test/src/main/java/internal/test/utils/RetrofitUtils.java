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

import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RetrofitUtils {

    private RetrofitUtils() {
    }

    public static Annotation[] getCallMethodAnnotations(final String... headers) {
        List<Annotation> result = new ArrayList<>();
        result.add(getHeadersAnnotation(headers));
        result.add(getPostAnnotation(UUID.randomUUID().toString()));
        return result.toArray(new Annotation[]{});
    }

    public static Headers getHeadersAnnotation(final String... headers) {
        return new Headers() {
            public Class<? extends Annotation> annotationType() {
                return Headers.class;
            }

            public String[] value() {
                return headers;
            }
        };
    }

    public static POST getPostAnnotation(String url) {
        return new POST() {
            public Class<? extends Annotation> annotationType() {
                return POST.class;
            }

            public String value() {
                return url;
            }
        };
    }

}
