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

package org.touchbit.retrofit.ext.dmr.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.gson.ToNumberPolicy.LONG_OR_DOUBLE;

/**
 * Gson converter
 * <p>
 * Created by Oleg Shaburov on 01.12.2021
 * shaburov.o.a@gmail.com
 */
public class GsonConverter<T> implements ExtensionConverter<T> {

    public static final String JSON_NULL_VALUE = "JSON_NULL_VALUE";
    public final Gson requestGson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    public final Gson responseGson = new GsonBuilder()
            .serializeNulls()
            .setObjectToNumberStrategy(LONG_OR_DOUBLE)
            .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
            .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
            .create();

    public Gson getRequestGson() {
        return requestGson;
    }

    public Gson getResponseGson() {
        return responseGson;
    }

    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                Utils.parameterRequireNonNull(body, "body");
                final Gson gson = getRequestGson();
                try {
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    if (BODY_NULL_VALUE.equals(body)) {
                        return null;
                    } else if (JSON_NULL_VALUE.equals(body)) {

                        return RequestBody.create(mediaType, gson.toJson(null));
                    } else {
                        return RequestBody.create(mediaType, gson.toJson(body));
                    }
                } catch (Exception e) {
                    throw new ConvertCallException("Body not convertible to JSON. " +
                            "Body type: " + body.getClass().getTypeName(), e);
                }
            }
        };
    }

    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<T> responseBodyConverter(final Type type,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        return new ResponseBodyConverter<T>() {

            @Override
            @Nullable
            public T convert(@Nullable ResponseBody body) {
                if (body == null || body.contentLength() == 0) {
                    return null;
                }
                final String strBody;
                try {
                    strBody = body.string();
                } catch (Exception e) {
                    throw new ConvertCallException("Unable to read response body. See cause below.", e);
                }
                try {
                    return getResponseGson().fromJson(strBody, type);
                } catch (Exception e) {
                    throw new ConvertCallException("\nResponse body not convertible to type " + type + "\n" +
                            e.getMessage(), e);
                }
            }
        };
    }

}
