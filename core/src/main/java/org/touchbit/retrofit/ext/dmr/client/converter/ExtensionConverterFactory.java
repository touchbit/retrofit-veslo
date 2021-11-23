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

package org.touchbit.retrofit.ext.dmr.client.converter;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.touchbit.retrofit.ext.dmr.client.CallStage;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.*;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.AnyBody;
import org.touchbit.retrofit.ext.dmr.client.model.ResourceFile;
import org.touchbit.retrofit.ext.dmr.exception.ConvertCallException;
import org.touchbit.retrofit.ext.dmr.exception.ConverterNotFoundException;
import org.touchbit.retrofit.ext.dmr.util.ConvertUtils;
import org.touchbit.retrofit.ext.dmr.util.Utils;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.retrofit.ext.dmr.client.CallStage.REQUEST;
import static org.touchbit.retrofit.ext.dmr.client.CallStage.RESPONSE;
import static org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import static org.touchbit.retrofit.ext.dmr.client.header.ContentTypeConstants.*;
import static org.touchbit.retrofit.ext.dmr.util.ConvertUtils.isIDualResponse;

/**
 * Universal converter factory.
 * Allows you to match models and converters by:
 * - annotation {@link Converters}, {@link RequestConverter}, {@link ResponseConverter}
 * - package
 * - class
 * - Content-Type header value (MIME)
 *
 * <p>
 * Created by Oleg Shaburov on 20.11.2021
 * shaburov.o.a@gmail.com
 */
public class ExtensionConverterFactory extends retrofit2.Converter.Factory {

    private final Map<String, ExtensionConverter<?>> packageRequestConverters = new HashMap<>();
    private final Map<String, ExtensionConverter<?>> packageResponseConverters = new HashMap<>();
    private final Map<Class<?>, ExtensionConverter<?>> rawRequestConverters = new HashMap<>();
    private final Map<Class<?>, ExtensionConverter<?>> rawResponseConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeRequestConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeResponseConverters = new HashMap<>();

    public ExtensionConverterFactory() {
        final AnyBodyConverter anyBodyConverter = new AnyBodyConverter();
        final ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
        final FileConverter fileConverter = new FileConverter();
        final ResourceFileConverter resourceFileConverter = new ResourceFileConverter();
        final StringConverter stringConverter = new StringConverter();
        // raw request
        addRawRequestConverter(anyBodyConverter, AnyBody.class);
        addRawRequestConverter(byteArrayConverter, Byte[].class);
        addRawRequestConverter(fileConverter, File.class);
        addRawRequestConverter(resourceFileConverter, ResourceFile.class);
        // mime request
        addMimeRequestConverter(stringConverter, TEXT_PLAIN, TEXT_PLAIN_UTF8, TEXT_HTML, TEXT_HTML_UTF8);
        // raw response
        addRawResponseConverter(anyBodyConverter, AnyBody.class);
        addRawResponseConverter(fileConverter, File.class);
        addRawResponseConverter(byteArrayConverter, Byte[].class);
        // mime response
        addMimeResponseConverter(stringConverter,
                TEXT_PLAIN, TEXT_PLAIN_UTF8, TEXT_HTML, TEXT_HTML_UTF8, APP_FORM_URLENCODED, APP_FORM_URLENCODED_UTF8);
    }

    /**
     * Returns a Converter for converting type to an HTTP request body
     *
     * @param type - request method body type.
     * @param pA   - API client called method parameters annotations
     * @param mA   - API client called method annotations
     * @param rtf  - see {@link Retrofit}
     * @return {@link retrofit2.Converter}
     * @throws ConverterNotFoundException if converter for a request body undefined
     */
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type type,
                                                     final Annotation[] pA,
                                                     final Annotation[] mA,
                                                     final Retrofit rtf) {
        return new RequestBodyConverter() {

            /**
             * Converting DTO model to their HTTP {@link RequestBody} representation
             *
             * @param body - DTO model
             * @return HTTP {@link RequestBody}
             */
            @EverythingIsNonNull
            public RequestBody convert(final Object body) {
                final Class<?> bodyClass = body.getClass();
                final RequestBodyConverter aConverter = getRequestConverterFromAnnotation(bodyClass, pA, mA, rtf);
                if (aConverter != null) {
                    return aConverter.convert(body);
                }
                final RequestBodyConverter rawConverter = getRawRequestConverter(bodyClass, pA, mA, rtf);
                if (rawConverter != null) {
                    return rawConverter.convert(body);
                }
                final RequestBodyConverter packRequestConverter = getPackageRequestConverter(bodyClass, pA, mA, rtf);
                if (packRequestConverter != null) {
                    return packRequestConverter.convert(body);
                }
                final RequestBodyConverter mimeConverter = getMimeRequestConverter(bodyClass, pA, mA, rtf);
                if (mimeConverter != null) {
                    return mimeConverter.convert(body);
                }
                final String info = getSupportedConvertersInfo(REQUEST, mA);
                throw new ConverterNotFoundException(REQUEST, ConvertUtils.getContentType(mA), bodyClass, info);
            }

        };
    }

    /**
     * @param type - response body type.
     * @param mA   - API client called method annotations
     * @param rtf  - see {@link Retrofit}
     * @return {@link retrofit2.Converter}
     */
    @EverythingIsNonNull
    public ResponseBodyConverter<?> responseBodyConverter(final Type type,
                                                          final Annotation[] mA,
                                                          final Retrofit rtf) {
        return new ResponseBodyConverter<Object>() {

            /**
             * Converting HTTP {@link ResponseBody} to their DTO model representation
             *
             * @param respBody - HTTP {@link ResponseBody}
             * @return DTO model
             */
            @Override
            @Nullable
            public Object convert(@Nullable final ResponseBody respBody) {
                final Class<?> bodyClass = getResponseBodyClass(type);
                final ResponseBodyConverter<?> aConverter = getResponseConverterFromAnnotation(bodyClass, mA, rtf);
                if (aConverter != null) {
                    return aConverter.convert(respBody);
                }
                final ResponseBodyConverter<?> rawConverter = getRawResponseConverter(bodyClass, mA, rtf);
                if (rawConverter != null) {
                    return rawConverter.convert(respBody);
                }
                final ResponseBodyConverter<?> packageConverter = getPackageResponseConverter(bodyClass, mA, rtf);
                if (packageConverter != null) {
                    return packageConverter.convert(respBody);
                }
                final ResponseBodyConverter<?> mimeConverter = getMimeResponseConverter(respBody, bodyClass, mA, rtf);
                if (mimeConverter != null) {
                    return mimeConverter.convert(respBody);
                }
                final String info = getSupportedConvertersInfo(RESPONSE, mA);
                throw new ConverterNotFoundException(RESPONSE, ConvertUtils.getContentType(respBody), bodyClass, info);
            }
        };
    }

    /**
     * Get {@link RequestBodyConverter} by {@param bodyClass}
     * from called method annotations ({@link Converters} or {@link RequestConverter})
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    protected RequestBodyConverter getRequestConverterFromAnnotation(@Nonnull final Class<?> bodyClass,
                                                                     @Nonnull final Annotation[] parameterAnnotations,
                                                                     @Nonnull final Annotation[] methodAnnotations,
                                                                     @Nonnull final Retrofit retrofit) {
        final RequestConverter aRequestConverter = Utils.getAnnotation(methodAnnotations, RequestConverter.class);
        final Converters aConverters = Utils.getAnnotation(methodAnnotations, Converters.class);
        if (aRequestConverter != null && aConverters != null) {
            throw new ConvertCallException("API method contains concurrent annotations.\n" +
                    "Use only one of:\n" +
                    " * " + RequestConverter.class + "\n" +
                    " * " + Converters.class);
        }
        if ((aRequestConverter == null && aConverters == null)) {
            return null;
        }
        if (aConverters != null) {
            final RequestConverter[] aRequestConverters = aConverters.request();
            for (RequestConverter aConverter : aRequestConverters) {
                final ExtensionConverter<?> converter = getExtensionConverter(aConverter, bodyClass);
                if (converter != null) {
                    return converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
                }
            }
        } else {
            final ExtensionConverter<?> converter = getExtensionConverter(aRequestConverter, bodyClass);
            if (converter != null) {
                return converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
            }
        }
        return null;
    }

    /**
     * Get {@link RequestBodyConverter} by {@param bodyClass}
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    public RequestBodyConverter getRawRequestConverter(@Nonnull final Class<?> bodyClass,
                                                       @Nonnull final Annotation[] parameterAnnotations,
                                                       @Nonnull final Annotation[] methodAnnotations,
                                                       @Nonnull final Retrofit retrofit) {
        final ExtensionConverter<?> converter = getRawRequestConverters().get(bodyClass);
        if (converter != null) {
            return converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link RequestBodyConverter} by {@param bodyClass} package
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    public RequestBodyConverter getPackageRequestConverter(@Nonnull final Class<?> bodyClass,
                                                           @Nonnull final Annotation[] parameterAnnotations,
                                                           @Nonnull final Annotation[] methodAnnotations,
                                                           @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyClass, "bodyClass");
        Utils.parameterRequireNonNull(parameterAnnotations, "parameterAnnotations");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        final String packageName = bodyClass.getPackage().getName();
        final ExtensionConverter<?> converter = getPackageRequestConverters().get(packageName);
        if (converter != null) {
            return converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link RequestBodyConverter} by Content-Type header
     * from {@link retrofit2.http.Headers} annotation
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    public RequestBodyConverter getMimeRequestConverter(@Nonnull final Class<?> bodyClass,
                                                        @Nonnull final Annotation[] parameterAnnotations,
                                                        @Nonnull final Annotation[] methodAnnotations,
                                                        @Nonnull final Retrofit retrofit) {
        final ContentType contentType = ConvertUtils.getContentType(methodAnnotations);
        final ExtensionConverter<?> converter = getMimeRequestConverters().get(contentType);
        if (converter != null) {
            return converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * *    public interface Client {
     * *
     * *       @EndpointInfo("Get user by id")
     * *       @Headers("Content-Type: application/json")
     * *       @GET("/api/mock/application/json/absent")
     * *       DualResponse<UserDTO, ErrorDTO> getUser(@Query("id") String id);
     * *
     * *    }
     *
     * @param type - returned API method {@link Type} (DualResponse from example)
     * @return body type real {@link Class} (UserDTO from example)
     */
    @EverythingIsNonNull
    protected Class<?> getResponseBodyClass(Type type) {
        Utils.parameterRequireNonNull(type, "type");
        final Type bodyType;
        if (isIDualResponse(type)) {
            bodyType = getParameterUpperBound(0, (ParameterizedType) type);
        } else {
            bodyType = type;
        }
        return getRawType(bodyType);
    }

    /**
     * Get {@link ResponseBodyConverter} by {@param bodyClass}
     *
     * @param bodyClass         - request method body class.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getRawResponseConverter(@Nonnull final Class<?> bodyClass,
                                                               @Nonnull final Annotation[] methodAnnotations,
                                                               @Nonnull final Retrofit retrofit) {
        final Map<Class<?>, ExtensionConverter<?>> rawConverters = getRawResponseConverters();
        final ExtensionConverter<?> rawConverter = rawConverters.get(bodyClass);
        if (rawConverter != null) {
            return rawConverter.responseBodyConverter(bodyClass, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link ResponseBodyConverter} from {@param bodyClass} package
     *
     * @param bodyClass         - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link retrofit2.Converter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getPackageResponseConverter(@Nonnull final Class<?> bodyClass,
                                                                   @Nonnull final Annotation[] methodAnnotations,
                                                                   @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyClass, "bodyClass");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        final String modelPackage = bodyClass.getPackage().getName();
        final ExtensionConverter<?> extensionConverter = getPackageResponseConverters().get(modelPackage);
        if (extensionConverter == null) {
            return null;
        }
        return extensionConverter.responseBodyConverter(bodyClass, methodAnnotations, retrofit);
    }

    /**
     * Get {@link ResponseBodyConverter} by Content-Type header
     * from {@link retrofit2.http.Headers} annotation
     *
     * @param bodyClass         - request method body class.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getMimeResponseConverter(@Nullable final ResponseBody responseBody,
                                                                @Nonnull final Class<?> bodyClass,
                                                                @Nonnull final Annotation[] methodAnnotations,
                                                                @Nonnull final Retrofit retrofit) {
        final ContentType contentType;
        if (responseBody == null) {
            contentType = new ContentType(null);
        } else {
            contentType = new ContentType(responseBody.contentType());
        }
        final ExtensionConverter<?> extensionConverter = getMimeResponseConverters().get(contentType);
        if (extensionConverter != null) {
            return extensionConverter.responseBodyConverter(bodyClass, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link ResponseBodyConverter} by {@param bodyClass}
     * from called method annotations ({@link Converters} or {@link ResponseConverter})
     *
     * @param bodyClass         - request method body class.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getResponseConverterFromAnnotation(@Nonnull final Class<?> bodyClass,
                                                                          @Nonnull final Annotation[] methodAnnotations,
                                                                          @Nonnull final Retrofit retrofit) {
        final ResponseConverter responseConverter = Utils.getAnnotation(methodAnnotations, ResponseConverter.class);
        final Converters converters = Utils.getAnnotation(methodAnnotations, Converters.class);
        if (responseConverter != null && converters != null) {
            throw new ConvertCallException("API method contains concurrent annotations.\n" +
                    "Use only one of:\n" +
                    " * " + ResponseConverter.class + "\n" +
                    " * " + Converters.class);
        }
        if ((responseConverter == null && converters == null)) {
            return null;
        }
        if (converters != null) {
            final ResponseConverter[] responseConverters = converters.response();
            for (ResponseConverter converter : responseConverters) {
                final ExtensionConverter<?> extensionConverter = getExtensionConverter(converter, bodyClass);
                if (extensionConverter != null) {
                    return extensionConverter.responseBodyConverter(bodyClass, methodAnnotations, retrofit);
                }
            }
        } else {
            final ExtensionConverter<?> extensionConverter = getExtensionConverter(responseConverter, bodyClass);
            if (extensionConverter != null) {
                return extensionConverter.responseBodyConverter(bodyClass, methodAnnotations, retrofit);
            }
        }
        return null;
    }

    /**
     * Retrieves ExtensionConverter from {@link ResponseConverter} or {@link RequestConverter} annotations (Hereinafter, A).
     * If method A.bodyClasses() returned an empty array, new instance of 'A.converter()' will be returned.
     * If method A.bodyClasses() returned a non-empty list, a comparison by {@param bodyClass} will be performed,
     * and if there is a match, new instance of A.converter() will be returned otherwise null.
     *
     * @param annotation - {@link ResponseConverter} or {@link RequestConverter} annotation
     * @param bodyClass - model class
     * @return {@link ExtensionConverter} or null
     * @throws ConvertCallException if {@param annotation} differs from {@link ResponseConverter} or {@link RequestConverter}
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    protected ExtensionConverter<?> getExtensionConverter(@Nonnull final Annotation annotation,
                                                          @Nonnull final Class<?> bodyClass) {
        Utils.parameterRequireNonNull(annotation, "annotation");
        Utils.parameterRequireNonNull(bodyClass, "bodyClass");
        final Class<?>[] converterBodyClasses;
        final Class<? extends ExtensionConverter> converterClass;
        if (annotation instanceof ResponseConverter) {
            final ResponseConverter responseConverter = (ResponseConverter) annotation;
            converterBodyClasses = responseConverter.bodyClasses();
            converterClass = responseConverter.converter();
        } else if (annotation instanceof RequestConverter) {
            final RequestConverter requestConverter = (RequestConverter) annotation;
            converterBodyClasses = requestConverter.bodyClasses();
            converterClass = requestConverter.converter();
        } else {
            throw new ConvertCallException("Received an unsupported annotation type: " + annotation.getClass());
        }
        if (converterBodyClasses.length == 0) {
            return newInstance(converterClass);
        }
        for (Class<?> converterBodyClass : converterBodyClasses) {
            if (converterBodyClass.equals(bodyClass)) {
                return newInstance(converterClass);
            }
        }
        return null;
    }

    /**
     * @param converterClass - The class of the object that implements the {@link ExtensionConverter}
     * @return - new instance of {@param converterClass}
     * @throws ConvertCallException if there were errors while creating an instance of {@param converterClass}
     */
    @EverythingIsNonNull
    @SuppressWarnings("rawtypes")
    protected ExtensionConverter<?> newInstance(@Nonnull final Class<? extends ExtensionConverter> converterClass) {
        Utils.parameterRequireNonNull(converterClass, "converterClass");
        try {
            return converterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ConvertCallException("" +
                    "Unable to create new instance of " + converterClass + "\n" +
                    "See details below.", e);
        }
    }

    /**
     * @return Map of request converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Class<?>, ExtensionConverter<?>> getRawRequestConverters() {
        return rawRequestConverters;
    }

    /**
     * Add converter for specified classes
     *
     * @param converter  - Converter implementing interface {@link ExtensionConverter}
     * @param rawClasses - array list of classes supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addRawRequestConverter(ExtensionConverter<?> converter, Class<?>... rawClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Class<?> rawClass : rawClasses) {
            Utils.parameterRequireNonNull(rawClass, "rawClass");
            getRawRequestConverters().put(rawClass, converter);
        }
    }

    /**
     * @return Map of request converters where key - {@link ContentType} (MIME), value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<ContentType, ExtensionConverter<?>> getMimeRequestConverters() {
        return mimeRequestConverters;
    }

    /**
     * Add converter for specified content types (MIME)
     *
     * @param converter    - Converter implementing interface {@link ExtensionConverter}
     * @param contentTypes - array list of content types supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addMimeRequestConverter(ExtensionConverter<?> converter, ContentType... contentTypes) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (ContentType contentType : contentTypes) {
            Utils.parameterRequireNonNull(contentType, "contentType");
            getMimeRequestConverters().put(contentType, converter);
        }
    }

    /**
     * @return Map of response converters where key - {@link ContentType} (MIME), value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<ContentType, ExtensionConverter<?>> getMimeResponseConverters() {
        return mimeResponseConverters;
    }

    /**
     * Add converter for specified content types (MIME)
     *
     * @param converter    - Converter implementing interface {@link ExtensionConverter}
     * @param contentTypes - array list of content types supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addMimeResponseConverter(ExtensionConverter<?> converter, ContentType... contentTypes) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (ContentType contentType : contentTypes) {
            Utils.parameterRequireNonNull(contentType, "contentType");
            getMimeResponseConverters().put(contentType, converter);
        }
    }

    /**
     * @return Map of response converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Class<?>, ExtensionConverter<?>> getRawResponseConverters() {
        return rawResponseConverters;
    }

    /**
     * Add converter for specified classes
     *
     * @param converter   - Converter implementing interface {@link ExtensionConverter}
     * @param bodyClasses - array list of classes supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addRawResponseConverter(ExtensionConverter<?> converter, Class<?>... bodyClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Class<?> bodyClass : bodyClasses) {
            Utils.parameterRequireNonNull(bodyClass, "bodyClass");
            getRawResponseConverters().put(bodyClass, converter);
        }
    }

    /**
     * @return Map of request converters where key - package prefix, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<String, ExtensionConverter<?>> getPackageRequestConverters() {
        return packageRequestConverters;
    }

    /**
     * Add converter for specified package names
     *
     * @param converter    - Converter implementing interface {@link ExtensionConverter}
     * @param packageNames - array list of package names supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addPackageRequestConverter(ExtensionConverter<?> converter, String... packageNames) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (String packageName : packageNames) {
            Utils.parameterRequireNonNull(packageName, "packageName");
            if (!packageName.matches("^[a-z]+(\\.[a-z0-9]+)*$")) {
                throw new IllegalArgumentException("Invalid package name: " + packageName);
            }
            getPackageRequestConverters().put(packageName, converter);
        }
    }

    /**
     * @return Map of response converters where key - package prefix, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<String, ExtensionConverter<?>> getPackageResponseConverters() {
        return packageResponseConverters;
    }

    /**
     * Add converter for specified package names
     *
     * @param converter    - Converter implementing interface {@link ExtensionConverter}
     * @param packageNames - array list of package names supported by the {@param converter}
     */
    @EverythingIsNonNull
    public void addPackageResponseConverter(ExtensionConverter<?> converter, String... packageNames) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (String packageName : packageNames) {
            Utils.parameterRequireNonNull(packageName, "packageName");
            if (!packageName.matches("^[a-z]+(\\.[a-z0-9]+)*$")) {
                throw new IllegalArgumentException("Invalid package name: " + packageName);
            }
            getPackageResponseConverters().put(packageName, converter);
        }
    }

    /**
     * Builds a converter map from {@link Converters} and {@link RequestConverter} annotations
     * received directly during the API call.
     *
     * @param mA - API client called method annotations
     * @return Map where key - request DTO class name, value - Converter class
     */
    @Nonnull
    protected Map<String, Class<?>> getAnnotatedRequestConverters(@Nullable final Annotation[] mA) {
        Map<String, Class<?>> result = new HashMap<>();
        List<RequestConverter> requestConverters = new ArrayList<>();
        final Converters converters = Utils.getAnnotation(mA, Converters.class);
        if (converters != null) {
            Collections.addAll(requestConverters, converters.request());
        }
        final RequestConverter requestConverter = Utils.getAnnotation(mA, RequestConverter.class);
        if (requestConverter != null) {
            requestConverters.add(requestConverter);
        }
        for (RequestConverter rc : requestConverters) {
            if (rc.bodyClasses().length == 0) {
                result.put("<any model class>", rc.converter());
            } else {
                for (Class<?> aClass : rc.bodyClasses()) {
                    result.put(aClass.getName(), rc.converter());
                }
            }
        }
        return result;
    }

    /**
     * Builds a converter map from {@link Converters} and {@link ResponseConverter} annotations
     * received directly during the API call.
     *
     * @param mA - API client called method annotations
     * @return Map where key - response DTO class name, value - Converter class
     */
    @Nonnull
    protected Map<String, Class<?>> getAnnotatedResponseConverters(@Nullable final Annotation[] mA) {
        Map<String, Class<?>> result = new HashMap<>();
        List<ResponseConverter> responseConverters = new ArrayList<>();
        final Converters converters = Utils.getAnnotation(mA, Converters.class);
        if (converters != null) {
            Collections.addAll(responseConverters, converters.response());
        }
        final ResponseConverter responseConverter = Utils.getAnnotation(mA, ResponseConverter.class);
        if (responseConverter != null) {
            responseConverters.add(responseConverter);
        }
        for (ResponseConverter rc : responseConverters) {
            if (rc.bodyClasses().length == 0) {
                result.put("<any model class>", rc.converter());
            } else {
                for (Class<?> aClass : rc.bodyClasses()) {
                    result.put(aClass.getName(), rc.converter());
                }
            }
        }
        return result;
    }

    /**
     * @param callStage - {@link CallStage}
     * @param mA        - API client called method annotations
     * @return information on the factory-supported converters for the specified call stage
     */
    @Nonnull
    @SuppressWarnings("DuplicatedCode")
    protected String getSupportedConvertersInfo(@Nonnull final CallStage callStage,
                                                @Nullable final Annotation[] mA) {
        Utils.parameterRequireNonNull(callStage, "callStage");
        final Map<String, Class<?>> annotated;
        final Map<Class<?>, ExtensionConverter<?>> raw;
        final Map<ContentType, ExtensionConverter<?>> mime;
        final Map<String, ExtensionConverter<?>> pack;
        if (callStage == REQUEST) {
            annotated = getAnnotatedRequestConverters(mA);
            raw = getRawRequestConverters();
            mime = getMimeRequestConverters();
            pack = getPackageRequestConverters();
        } else {
            annotated = getAnnotatedResponseConverters(mA);
            raw = getRawResponseConverters();
            mime = getMimeResponseConverters();
            pack = getPackageResponseConverters();
        }
        String annInfo = "Annotated converters:" + (annotated.isEmpty() ? " <absent>" : "\n");
        String packInfo = "Package converters:" + (pack.isEmpty() ? " <absent>" : "\n");
        String rawInfo = "Raw converters:" + (raw.isEmpty() ? " <absent>" : "\n");
        String mimeInfo = "Content type converters:" + (mime.isEmpty() ? " <absent>" : "\n");
        StringJoiner annSJ = new StringJoiner("\n", annInfo, "");
        Map<String, Set<String>> annSummary = new TreeMap<>();
        annotated.forEach((k, v) -> annSummary.computeIfAbsent(v.getName(), a -> new TreeSet<>()).add(k));
        annSummary.forEach((converter, classNames) -> annSJ.add(converter + classNames.stream()
                .map(name -> "\n    " + name + "")
                .collect(Collectors.joining())));
        StringJoiner packSJ = new StringJoiner("\n", packInfo, "");
        Map<String, Set<String>> packSummary = new TreeMap<>();
        pack.forEach((prefix, converter) -> packSummary
                .computeIfAbsent(converter.getClass().getName(), a -> new TreeSet<>()).add(prefix));
        packSummary.forEach((converter, prefixes) -> packSJ.add(converter + prefixes.stream()
                .map(prefix -> "\n    " + prefix + ".*")
                .collect(Collectors.joining())));
        StringJoiner rawSJ = new StringJoiner("\n", rawInfo, "");
        Map<String, Set<String>> rawSummary = new TreeMap<>();
        raw.forEach((k, v) -> rawSummary.computeIfAbsent(v.getClass().getName(), a -> new TreeSet<>())
                .add((!k.isArray() ? k.toString() : (k.toString().replace("[L", "").replace(";", "") + "[]"))));
        rawSummary.forEach((converter, classes) -> rawSJ.add(converter + classes.stream()
                .map(aClass -> "\n    " + aClass + "")
                .collect(Collectors.joining())));
        StringJoiner mimeSJ = new StringJoiner("\n", mimeInfo, "");
        Map<String, Set<String>> mimeSummary = new TreeMap<>();
        mime.forEach((k, v) -> mimeSummary.computeIfAbsent(v.getClass().getName(), a -> new TreeSet<>()).add(k.toString()));
        mimeSummary.forEach((converter, contentTypes) -> mimeSJ.add(converter + contentTypes.stream()
                .map(contentType -> "\n    " + contentType + "")
                .collect(Collectors.joining())));
        return "SUPPORTED " + callStage + " CONVERTERS:\n" + annSJ + "\n\n" + packSJ + "\n\n" + rawSJ + "\n\n" + mimeSJ;
    }

    /**
     * @return information on initialized converters
     */
    @Override
    public String toString() {
        return "Converter factory: " + this.getClass() + "\n\n" +
                getSupportedConvertersInfo(REQUEST, new Annotation[]{}) + "\n\n" +
                getSupportedConvertersInfo(RESPONSE, new Annotation[]{});
    }

}
