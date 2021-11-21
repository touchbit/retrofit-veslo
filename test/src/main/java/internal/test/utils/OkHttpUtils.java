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

import okhttp3.RequestBody;
import okio.Buffer;

public class OkHttpUtils {

    private OkHttpUtils() {
    }

    public static String requestBodyToString(RequestBody requestBody) {
        try {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
