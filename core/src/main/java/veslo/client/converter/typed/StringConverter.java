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

package veslo.client.converter.typed;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * String java type converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.12.2021
 */
public class StringConverter implements ExtensionConverter<String> {

    /**
     * {@link StringConverter} constant
     */
    public static final StringConverter INSTANCE = new StringConverter();

    /**
     * @see ExtensionConverter#requestBodyConverter(Type, Annotation[], Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new RequestBodyConverter() {

            /**
             * @param body - {@link String} body
             * @return HTTP {@link RequestBody} or null if {@link ExtensionConverter#NULL_BODY_VALUE} present
             */
            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                assertSupportedBodyType(INSTANCE, body, String.class);
                if (!isForceNullBodyValue(body)) {
                    return createRequestBody(methodAnnotations, body.toString());
                }
                return null;
            }

        };

    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<String> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<String>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @return null if body == null or empty otherwise {@link String}
             * @throws IOException                       body bytes not readable
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Nullable
            @Override
            public String convert(@Nullable ResponseBody responseBody) throws IOException {
                final String body = copyBody(responseBody);
                if (body != null && body.length() != 0) {
                    assertSupportedBodyType(INSTANCE, type, String.class);
                    return body;
                }
                return null;
            }
        };
    }

}
