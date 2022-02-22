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

package veslo.client.converter.annotated;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConvertCallException;
import veslo.ConverterUnsupportedTypeException;
import veslo.bean.template.TemplateMapper;
import veslo.bean.template.TemplateSource;
import veslo.client.converter.api.ExtensionConverter;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static veslo.constant.ParameterNameConstants.*;

/**
 * Converter for annotated classes {@link TemplateSource}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 18.02.2022
 */
public class TemplateSourceConverter implements ExtensionConverter<Object> {

    /**
     * {@link TemplateSourceConverter} constant
     */
    public static final TemplateSourceConverter INSTANCE = new TemplateSourceConverter();

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
             * @param body - annotated classes {@link TemplateSource}
             * @return {@link RequestBody}
             * @throws ConverterUnsupportedTypeException unsupported body type
             */
            @Override
            @EverythingIsNonNull
            public RequestBody convert(Object body) {
                return createRequestBody(methodAnnotations, TemplateMapper.marshal(body));
            }

        };
    }

    /**
     * @see ExtensionConverter#responseBodyConverter(Type, Annotation[], Retrofit)
     */
    @Override
    @EverythingIsNonNull
    public ResponseBodyConverter<Object> responseBodyConverter(final Type type,
                                                               final Annotation[] methodAnnotations,
                                                               final Retrofit retrofit) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return new ResponseBodyConverter<Object>() {

            /**
             * @param responseBody - HTTP call {@link ResponseBody}
             * @throws ConvertCallException forbidden to use template classes to convert the response body
             */
            @Override
            @Nonnull
            public Object convert(@Nullable ResponseBody responseBody) {
                throw new ConvertCallException("Template classes with the @TemplateSource annotation are not " +
                        "allowed to be used to convert the response body.");
            }

        };
    }

}