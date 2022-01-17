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

package veslo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.ConvertUtils;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.gson.ToNumberPolicy.LONG_OR_DOUBLE;

/**
 * Gson converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 01.12.2021
 */
public class GsonConverter<DTO> implements ExtensionConverter<DTO> {

    /**
     * {@link GsonConverter} constant
     */
    public static final GsonConverter<Object> INSTANCE = new GsonConverter<>();

    /**
     * request body serializer
     */
    public final Gson requestGson;

    /**
     * response body deserializer
     */
    public final Gson responseGson;

    /**
     * Default constructor with default request/response jackson object mappers
     */
    public GsonConverter() {
        this(new GsonBuilder().serializeNulls().setPrettyPrinting().create(),
                new GsonBuilder()
                        .serializeNulls()
                        .setObjectToNumberStrategy(LONG_OR_DOUBLE)
                        .registerTypeAdapter(Boolean.class, new BooleanGsonTypeAdapter())
                        .registerTypeAdapter(boolean.class, new BooleanGsonTypeAdapter())
                        .create());
    }

    /**
     * Default constructor with default request/response jackson object mappers
     *
     * @param requestGson  - request body serializer
     * @param responseGson - response body deserializer
     */
    public GsonConverter(Gson requestGson, Gson responseGson) {
        this.requestGson = requestGson;
        this.responseGson = responseGson;
    }

    /**
     * @param type                 - request method body type.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link Converter}
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] parameterAnnotations,
                                                     final Annotation[] methodAnnotations,
                                                     final Retrofit retrofit) {
        return new RequestBodyConverter() {

            /**
             * Converting DTO model to their HTTP {@link RequestBody} representation
             *
             * @param body - DTO model
             * @return HTTP {@link RequestBody}
             */
            @Override
            @Nullable
            public RequestBody convert(@Nonnull Object body) {
                Utils.parameterRequireNonNull(body, "body");
                final Gson gson = getRequestGson();
                try {
                    final MediaType mediaType = ConvertUtils.getMediaType(methodAnnotations);
                    if (NULL_BODY_VALUE.equals(body)) {
                        return null;
                    } else if (NULL_JSON_VALUE.equals(body)) {
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

    /**
     * @param type              - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link Converter}
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<DTO> responseBodyConverter(final Type type,
                                                            final Annotation[] methodAnnotations,
                                                            final Retrofit retrofit) {
        return new ResponseBodyConverter<DTO>() {

            /**
             * Converting HTTP {@link ResponseBody} to their {@link DTO} model representation
             *
             * @param body - HTTP {@link ResponseBody}
             * @return {@link DTO} model representation
             */
            @Override
            @Nullable
            public DTO convert(@Nullable ResponseBody body) {
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

    /**
     * @return request body serializer
     */
    public Gson getRequestGson() {
        return requestGson;
    }

    /**
     * @return response body deserializer
     */
    public Gson getResponseGson() {
        return responseGson;
    }

}
