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
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.model.ResourceFile;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * {@link ResourceFile} java type converter
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 16.12.2021
 */
public class ResourceFileConverter implements ExtensionConverter<ResourceFile> {

    /**
     * {@link ResourceFileConverter} constant
     */
    public static final ResourceFileConverter INSTANCE = new ResourceFileConverter();

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
             * @param body - {@link ResourceFile}
             * @return {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                assertSupportedBodyType(INSTANCE, body, ResourceFile.class);
                final ResourceFile resourceFile = (ResourceFile) body;
                return createRequestBody(methodAnnotations, resourceFile.read());
            }

        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<ResourceFile> responseBodyConverter(final Type type,
                                                                     final Annotation[] methodAnnotations,
                                                                     final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<ResourceFile>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             * @throws ConvertCallException forbidden to use the ResourceFile type to convert the response body
             */
            @Override
            @Nonnull
            public ResourceFile convert(@Nullable ResponseBody responseBody) {
                assertSupportedBodyType(INSTANCE, type, ResourceFile.class);
                throw new ConvertCallException("It is forbidden to use the ResourceFile type to convert the response body.");
            }

        };
    }

}
