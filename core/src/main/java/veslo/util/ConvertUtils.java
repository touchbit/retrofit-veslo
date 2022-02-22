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

package veslo.util;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.internal.EverythingIsNonNull;
import veslo.UtilityClassException;
import veslo.client.header.ContentType;
import veslo.client.response.IDualResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.TYPE_PARAMETER;

/**
 * Created: 08.11.2021
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 */
public class ConvertUtils {

    /**
     * Utility class
     */
    private ConvertUtils() {
        throw new UtilityClassException();
    }

    /**
     * @param type - model type
     * @return true if type is implements the interface {@link IDualResponse}
     */
    @EverythingIsNonNull
    public static boolean isIDualResponse(final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        return type instanceof ParameterizedType &&
                IDualResponse.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
    }

    /**
     * Method to extract {@link okhttp3.Headers} from the list of request method annotations
     *
     * @param methodAnnotations - list of request method annotations
     * @return {@link Headers}
     */
    @Nonnull
    public static Headers getAnnotationHeaders(@Nullable final Annotation[] methodAnnotations) {
        Headers.Builder headersBuilder = new Headers.Builder();
        final retrofit2.http.Headers aHeaders = Utils.getAnnotation(methodAnnotations, retrofit2.http.Headers.class);
        if (aHeaders != null) {
            for (String header : aHeaders.value()) {
                String[] split = header.split(":");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Invalid header value.\n" +
                            "Annotation: " + retrofit2.http.Headers.class + "\n" +
                            "Header: " + header + "\n" +
                            "Expected format: Header-Name: value; parameter-name=value\n" +
                            "Example: Content-Type: text/xml; charset=utf-8");
                }
                String name = split[0].trim();
                String value = split[1].trim();
                headersBuilder.add(name, value);
            }
        }
        return headersBuilder.build();
    }

    /**
     * Method to extract MediaType from the list of request method annotations
     *
     * @param methodAnnotations - list of request method annotations
     * @return {@link MediaType} or null
     */
    @Nullable
    public static MediaType getMediaType(@Nullable final Annotation[] methodAnnotations) {
        Headers headers = getAnnotationHeaders(methodAnnotations);
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            return null;
        }
        return MediaType.parse(contentType);
    }

    @Nonnull
    public static ContentType getContentType(@Nullable final Annotation[] methodAnnotations) {
        final MediaType mediaType = getMediaType(methodAnnotations);
        return new ContentType(mediaType);
    }

    @Nonnull
    public static ContentType getContentType(@Nullable final ResponseBody responseBody) {
        if (responseBody == null) {
            return new ContentType(null);
        }
        return new ContentType(responseBody.contentType());
    }

}
