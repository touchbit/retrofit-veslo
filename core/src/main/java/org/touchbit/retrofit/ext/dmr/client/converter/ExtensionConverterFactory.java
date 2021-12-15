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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.ext.dmr.client.TransportEvent;
import org.touchbit.retrofit.ext.dmr.client.converter.api.Converters;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.RequestConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.api.ResponseConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaPrimitiveTypeConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.defaults.JavaReferenceTypeConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ByteArrayConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.FileConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.RawBodyConverter;
import org.touchbit.retrofit.ext.dmr.client.converter.typed.ResourceFileConverter;
import org.touchbit.retrofit.ext.dmr.client.header.ContentType;
import org.touchbit.retrofit.ext.dmr.client.model.RawBody;
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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.REQUEST;
import static org.touchbit.retrofit.ext.dmr.client.TransportEvent.RESPONSE;
import static org.touchbit.retrofit.ext.dmr.client.converter.api.ExtensionConverter.RequestBodyConverter;
import static org.touchbit.retrofit.ext.dmr.util.ConvertUtils.isIDualResponse;

/**
 * Universal converter factory.
 * Allows you to match models and converters by:
 * - annotation {@link Converters}, {@link RequestConverter}, {@link ResponseConverter}
 * - package
 * - class
 * - Content-Type header value (MIME)
 * - Java type (reference/primitive)
 *
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.11.2021
 */
public class ExtensionConverterFactory extends retrofit2.Converter.Factory {

    private final Map<String, ExtensionConverter<?>> packageRequestConverters = new HashMap<>();
    private final Map<String, ExtensionConverter<?>> packageResponseConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> rawRequestConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> rawResponseConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeRequestConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeResponseConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> javaTypeRequestConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> javaTypeResponseConverters = new HashMap<>();
    private final Logger logger;

    public ExtensionConverterFactory() {
        this(LoggerFactory.getLogger(ExtensionConverterFactory.class));
    }

    public ExtensionConverterFactory(Logger logger) {
        Utils.parameterRequireNonNull(logger, "logger");
        this.logger = logger;
        // raw request converters
        registerRawRequestConverter(RawBodyConverter.INSTANCE, RawBody.class);
        registerRawRequestConverter(ByteArrayConverter.INSTANCE, Byte[].class, byte[].class);
        registerRawRequestConverter(FileConverter.INSTANCE, File.class);
        registerRawRequestConverter(ResourceFileConverter.INSTANCE, ResourceFile.class);
        // raw response converters
        registerRawResponseConverter(RawBodyConverter.INSTANCE, RawBody.class);
        registerRawResponseConverter(ByteArrayConverter.INSTANCE, Byte[].class, byte[].class);
        registerRawResponseConverter(FileConverter.INSTANCE, File.class);
        registerRawResponseConverter(ResourceFileConverter.INSTANCE, ResourceFile.class);
        // Java type primitive request converters
        registerJavaTypeRequestConverter(JavaPrimitiveTypeConverter.INSTANCE, Character.TYPE, Boolean.TYPE, Byte.TYPE,
                Integer.TYPE, Double.TYPE, Float.TYPE, Long.TYPE, Short.TYPE);
        // Java type primitive response converters
        registerJavaTypeResponseConverter(JavaPrimitiveTypeConverter.INSTANCE, Character.TYPE, Boolean.TYPE, Byte.TYPE,
                Integer.TYPE, Double.TYPE, Float.TYPE, Long.TYPE, Short.TYPE);
        // Java type reference request converters
        registerJavaTypeRequestConverter(JavaReferenceTypeConverter.INSTANCE, String.class, Character.class, Boolean.class,
                Byte.class, Integer.class, Double.class, Float.class, Long.class, Short.class);
        // Java type reference response converters
        registerJavaTypeResponseConverter(JavaReferenceTypeConverter.INSTANCE, String.class, Character.class, Boolean.class,
                Byte.class, Integer.class, Double.class, Float.class, Long.class, Short.class);
    }

    /**
     * Returns a Converter for converting type to an HTTP request body
     *
     * @param typeIgnored - request method body type (unused)
     * @param pA          - API client called method parameters annotations
     * @param mA          - API client called method annotations
     * @param rtf         - see {@link Retrofit}
     * @return {@link retrofit2.Converter}
     * @throws ConverterNotFoundException if converter for a request body undefined
     */
    @Override
    @EverythingIsNonNull
    public RequestBodyConverter requestBodyConverter(final Type typeIgnored,
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
            public RequestBody convert(final Object body) throws IOException {
                final Class<?> bodyClass = body.getClass();
                final String bodyTypeName = Utils.getTypeName(bodyClass);
                logger.debug("Definition of request converter for type {}", bodyTypeName);
                try {
                    RequestBodyConverter converter = getRequestConverterFromAnnotation(bodyClass, pA, mA, rtf);
                    if (converter == null) {
                        converter = getRawRequestConverter(bodyClass, pA, mA, rtf);
                    }
                    if (converter == null) {
                        converter = getPackageRequestConverter(bodyClass, pA, mA, rtf);
                    }
                    if (converter == null) {
                        converter = getMimeRequestConverter(bodyClass, pA, mA, rtf);
                    }
                    if (converter == null) {
                        converter = getJavaTypeRequestConverter(bodyClass, pA, mA, rtf);
                    }
                    if (converter == null) {
                        logger.error("Request converter not found");
                        final String info = getSupportedConvertersInfo(REQUEST, mA);
                        throw new ConverterNotFoundException(REQUEST, ConvertUtils.getContentType(mA), bodyClass, info);
                    } else {
                        logger.debug("Request converter found: " + Utils.getTypeName(converter));
                        final RequestBody result = converter.convert(body);
                        logger.debug("Converted request body: {}", Utils.getTypeName(result));
                        return result;
                    }
                } catch (IOException | RuntimeException e) {
                    logger.error("Error converting request body:\n{}", e.toString());
                    throw e;
                }
            }
        };
    }

    /**
     * @param type - response body type.
     * @param mA   - API client called method annotations
     * @param rtf  - see {@link Retrofit}
     * @return {@link retrofit2.Converter}
     */
    @Override
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
            public Object convert(@Nullable final ResponseBody respBody) throws IOException {
                final String responseBodyTypeName = ResponseBody.class.getTypeName();
                logger.debug("Convert {} to type: {}", responseBodyTypeName, type);
                final Type bodyType = getResponseBodyClass(type);
                final String bodyTypeName = Utils.getTypeName(bodyType);
                logger.debug("Definition of response converter for type: {}", bodyTypeName);
                try {
                    ResponseBodyConverter<?> converter = getResponseConverterFromAnnotation(bodyType, mA, rtf);
                    if (converter == null) {
                        converter = getRawResponseConverter(bodyType, mA, rtf);
                    }
                    if (converter == null) {
                        converter = getPackageResponseConverter(bodyType, mA, rtf);
                    }
                    if (converter == null && respBody == null) {
                        // It makes no sense to look for a converter further if there is no ResponseBody.
                        logger.debug("{} not present. Nothing to convert.", responseBodyTypeName);
                        return null;
                    }
                    if (converter == null) {
                        converter = getMimeResponseConverter(respBody, bodyType, mA, rtf);
                    }
                    if (converter == null) {
                        converter = getJavaTypeResponseConverter(bodyType, mA, rtf);
                    }
                    if (converter == null) {
                        logger.error("Response converter not found");
                        final String info = getSupportedConvertersInfo(RESPONSE, mA);
                        final ContentType contentType = ConvertUtils.getContentType(respBody);
                        throw new ConverterNotFoundException(RESPONSE, contentType, bodyType, info);
                    } else {
                        logger.debug("Response converter found: " + Utils.getTypeName(converter));
                        final Object result = converter.convert(respBody);
                        logger.debug("Response body successfully converted");
                        return result;
                    }
                } catch (IOException | RuntimeException e) {
                    logger.error("Error converting request body:\n{}", e.toString());
                    throw e;
                }
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
        final Package aPackage = bodyClass.getPackage();
        if (aPackage == null) {
            return null;
        }
        final ExtensionConverter<?> converter = getPackageRequestConverters().get(aPackage.getName());
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
     * Get java type {@link RequestBodyConverter} by body class type
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    public RequestBodyConverter getJavaTypeRequestConverter(@Nonnull final Class<?> bodyClass,
                                                            @Nonnull final Annotation[] parameterAnnotations,
                                                            @Nonnull final Annotation[] methodAnnotations,
                                                            @Nonnull final Retrofit retrofit) {
        final ExtensionConverter<?> converter = getJavaTypeRequestConverters().get(bodyClass);
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
    protected Type getResponseBodyClass(Type type) {
        Utils.parameterRequireNonNull(type, "type");
        final Type bodyType;
        if (isIDualResponse(type)) {
            bodyType = getParameterUpperBound(0, (ParameterizedType) type);
        } else {
            bodyType = type;
        }
        return bodyType;
    }

    /**
     * Get {@link ResponseBodyConverter} by {@param bodyClass}
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getRawResponseConverter(@Nonnull final Type bodyType,
                                                               @Nonnull final Annotation[] methodAnnotations,
                                                               @Nonnull final Retrofit retrofit) {
        final Map<Type, ExtensionConverter<?>> rawConverters = getRawResponseConverters();
        final ExtensionConverter<?> rawConverter = rawConverters.get(bodyType);
        if (rawConverter != null) {
            return rawConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link ResponseBodyConverter} from {@param bodyClass} package
     *
     * @param bodyType          - response body type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link retrofit2.Converter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getPackageResponseConverter(@Nonnull final Type bodyType,
                                                                   @Nonnull final Annotation[] methodAnnotations,
                                                                   @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyType, "bodyType");
        Utils.parameterRequireNonNull(methodAnnotations, "methodAnnotations");
        Utils.parameterRequireNonNull(retrofit, "retrofit");
        if (bodyType instanceof ParameterizedType) {
            ParameterizedType r = (ParameterizedType) bodyType;
            for (Type typeArgument : r.getActualTypeArguments()) {
                return getPackageResponseConverter(typeArgument, methodAnnotations, retrofit);
            }
        }
        if (bodyType instanceof Class) {
            Class<?> bodyClass = (Class<?>) bodyType;
            if (bodyClass.isPrimitive()) {
                return null;
            }
            final String modelPackage = bodyClass.getPackage().getName();
            final ExtensionConverter<?> extensionConverter = getPackageResponseConverters().get(modelPackage);
            if (extensionConverter == null) {
                return null;
            }
            return extensionConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link ResponseBodyConverter} by Content-Type header
     * from {@link retrofit2.http.Headers} annotation
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getMimeResponseConverter(@Nullable final ResponseBody responseBody,
                                                                @Nonnull final Type bodyType,
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
            return extensionConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get java type {@link ResponseBodyConverter} by body class type
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getJavaTypeResponseConverter(@Nonnull final Type bodyType,
                                                                    @Nonnull final Annotation[] methodAnnotations,
                                                                    @Nonnull final Retrofit retrofit) {
        final ExtensionConverter<?> extensionConverter;
        if (bodyType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) bodyType;
            extensionConverter = getJavaTypeResponseConverters().get(pType.getRawType());
        } else {
            extensionConverter = getJavaTypeResponseConverters().get(bodyType);
        }
        if (extensionConverter != null) {
            return extensionConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
        }
        return null;
    }

    /**
     * Get {@link ResponseBodyConverter} by {@param bodyClass}
     * from called method annotations ({@link Converters} or {@link ResponseConverter})
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getResponseConverterFromAnnotation(@Nonnull final Type bodyType,
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
                final ExtensionConverter<?> extensionConverter = getExtensionConverter(converter, bodyType);
                if (extensionConverter != null) {
                    return extensionConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
                }
            }
        } else {
            final ExtensionConverter<?> extensionConverter = getExtensionConverter(responseConverter, bodyType);
            if (extensionConverter != null) {
                return extensionConverter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
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
     * @param bodyType   - model class
     * @return {@link ExtensionConverter} or null
     * @throws ConvertCallException if {@param annotation} differs from {@link ResponseConverter} or {@link RequestConverter}
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    protected ExtensionConverter<?> getExtensionConverter(@Nonnull final Annotation annotation,
                                                          @Nonnull final Type bodyType) {
        Utils.parameterRequireNonNull(annotation, "annotation");
        Utils.parameterRequireNonNull(bodyType, "bodyType");
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
            if (converterBodyClass.equals(bodyType)) {
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
     * Add request/response converter for specified content types (MIME)
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedContentTypes - array list of content types supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerMimeConverter(ExtensionConverter<?> converter, ContentType... supportedContentTypes) {
        registerMimeRequestConverter(converter, supportedContentTypes);
        registerMimeResponseConverter(converter, supportedContentTypes);
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
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedContentTypes - array list of content types supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerMimeRequestConverter(ExtensionConverter<?> converter, ContentType... supportedContentTypes) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (ContentType supportedContentType : supportedContentTypes) {
            Utils.parameterRequireNonNull(supportedContentType, "supportedContentType");
            getMimeRequestConverters().put(supportedContentType, converter);
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
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedContentTypes - array list of content types supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerMimeResponseConverter(ExtensionConverter<?> converter, ContentType... supportedContentTypes) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (ContentType supportedContentType : supportedContentTypes) {
            Utils.parameterRequireNonNull(supportedContentType, "supportedContentType");
            getMimeResponseConverters().put(supportedContentType, converter);
        }
    }

    /**
     * @return Map of request converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Type, ExtensionConverter<?>> getRawRequestConverters() {
        return rawRequestConverters;
    }

    /**
     * Add converter for specified classes
     *
     * @param converter           - Converter implementing interface {@link ExtensionConverter}
     * @param supportedRawClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerRawRequestConverter(ExtensionConverter<?> converter, Type... supportedRawClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Type supportedRawClass : supportedRawClasses) {
            Utils.parameterRequireNonNull(supportedRawClass, "supportedRawClass");
            getRawRequestConverters().put(supportedRawClass, converter);
        }
    }

    /**
     * @return Map of response converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Type, ExtensionConverter<?>> getRawResponseConverters() {
        return rawResponseConverters;
    }

    /**
     * Add converter for specified classes
     *
     * @param converter           - Converter implementing interface {@link ExtensionConverter}
     * @param supportedRawClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerRawResponseConverter(ExtensionConverter<?> converter, Type... supportedRawClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Type supportedRawClass : supportedRawClasses) {
            Utils.parameterRequireNonNull(supportedRawClass, "supportedRawClass");
            getRawResponseConverters().put(supportedRawClass, converter);
        }
    }

    /**
     * Add java type request/response converter for specified classes
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedJavaTypeClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerJavaTypeConverter(ExtensionConverter<?> converter, Type... supportedJavaTypeClasses) {
        registerJavaTypeRequestConverter(converter, supportedJavaTypeClasses);
        registerJavaTypeResponseConverter(converter, supportedJavaTypeClasses);
    }

    /**
     * @return Map of request java type converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Type, ExtensionConverter<?>> getJavaTypeRequestConverters() {
        return javaTypeRequestConverters;
    }

    /**
     * Add java type converter for specified classes
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedJavaTypeClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerJavaTypeRequestConverter(ExtensionConverter<?> converter, Type... supportedJavaTypeClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Type supportedJavaTypeClass : supportedJavaTypeClasses) {
            Utils.parameterRequireNonNull(supportedJavaTypeClass, "supportedJavaTypeClass");
            getJavaTypeRequestConverters().put(supportedJavaTypeClass, converter);
        }
    }

    /**
     * @return Map of response java type converters where key - model class, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Type, ExtensionConverter<?>> getJavaTypeResponseConverters() {
        return javaTypeResponseConverters;
    }

    /**
     * Add java type converter for specified classes
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedJavaTypeClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerJavaTypeResponseConverter(ExtensionConverter<?> converter, Type... supportedJavaTypeClasses) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (Type supportedJavaTypeClass : supportedJavaTypeClasses) {
            Utils.parameterRequireNonNull(supportedJavaTypeClass, "supportedJavaTypeClass");
            getJavaTypeResponseConverters().put(supportedJavaTypeClass, converter);
        }
    }

    /**
     * Add converter for specified package names
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerPackageConverter(ExtensionConverter<?> converter, String... supportedPackageNames) {
        registerPackageRequestConverter(converter, supportedPackageNames);
        registerPackageResponseConverter(converter, supportedPackageNames);
    }

    /**
     * Add converter for specified package names
     *
     * @param converter         - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackages - array list of packages supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerPackageConverter(ExtensionConverter<?> converter, Package... supportedPackages) {
        Utils.parameterRequireNonNull(supportedPackages, "supportedPackages");
        final String[] packageNames = Arrays.stream(supportedPackages)
                .filter(Objects::nonNull)
                .map(Package::getName)
                .toArray(String[]::new);
        registerPackageConverter(converter, packageNames);
    }

    /**
     * Add converter for specified package names
     *
     * @param converter               - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageClasses - array list of class packages supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerPackageConverter(ExtensionConverter<?> converter, Class<?>... supportedPackageClasses) {
        Utils.parameterRequireNonNull(supportedPackageClasses, "supportedPackageClasses");
        final Package[] packages = Arrays.stream(supportedPackageClasses)
                .filter(Objects::nonNull)
                .map(Class::getPackage)
                .toArray(Package[]::new);
        registerPackageConverter(converter, packages);
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
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerPackageRequestConverter(ExtensionConverter<?> converter, String... supportedPackageNames) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (String supportedPackageName : supportedPackageNames) {
            Utils.parameterRequireNonNull(supportedPackageName, "supportedPackageName");
            if (!supportedPackageName.matches("^[a-z]+(\\.[a-z0-9]+)*$")) {
                throw new IllegalArgumentException("Invalid package name: " + supportedPackageName);
            }
            getPackageRequestConverters().put(supportedPackageName, converter);
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
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public void registerPackageResponseConverter(ExtensionConverter<?> converter, String... supportedPackageNames) {
        Utils.parameterRequireNonNull(converter, "converter");
        for (String supportedPackageName : supportedPackageNames) {
            Utils.parameterRequireNonNull(supportedPackageName, "supportedPackageName");
            if (!supportedPackageName.matches("^[a-z]+(\\.[a-z0-9]+)*$")) {
                throw new IllegalArgumentException("Invalid package name: " + supportedPackageName);
            }
            getPackageResponseConverters().put(supportedPackageName, converter);
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
    protected Map<String, Type> getAnnotatedRequestConverters(@Nullable final Annotation[] mA) {
        Map<String, Type> result = new HashMap<>();
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
    protected Map<String, Type> getAnnotatedResponseConverters(@Nullable final Annotation[] mA) {
        Map<String, Type> result = new HashMap<>();
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
     * @param transportEvent - {@link TransportEvent}
     * @param mA             - API client called method annotations
     * @return information on the factory-supported converters for the specified call stage
     */
    @Nonnull
    @SuppressWarnings("DuplicatedCode")
    protected String getSupportedConvertersInfo(@Nonnull final TransportEvent transportEvent,
                                                @Nullable final Annotation[] mA) {
        Utils.parameterRequireNonNull(transportEvent, "transportEvent");
        final Map<String, Type> annotated;
        final Map<Type, ExtensionConverter<?>> raw;
        final Map<String, ExtensionConverter<?>> pack;
        final Map<ContentType, ExtensionConverter<?>> mime;
        final Map<Type, ExtensionConverter<?>> javaType;
        if (transportEvent == REQUEST) {
            annotated = getAnnotatedRequestConverters(mA);
            raw = getRawRequestConverters();
            pack = getPackageRequestConverters();
            mime = getMimeRequestConverters();
            javaType = getJavaTypeRequestConverters();
        } else {
            annotated = getAnnotatedResponseConverters(mA);
            raw = getRawResponseConverters();
            pack = getPackageResponseConverters();
            mime = getMimeResponseConverters();
            javaType = getJavaTypeResponseConverters();
        }
        return "SUPPORTED " + transportEvent + " CONVERTERS:\n" +
                buildSummary("Annotated converters:", annotated, Type::getTypeName, p -> p) + "\n" +
                buildSummary("Raw converters:", raw, v -> v.getClass().getName(), Type::getTypeName) + "\n" +
                buildSummary("Package converters:", pack, v -> v.getClass().getName(), p -> p) + "\n" +
                buildSummary("Content type converters:", mime, v -> v.getClass().getName(), ContentType::toString) + "\n" +
                buildSummary("Java type converters:", javaType, v -> v.getClass().getName(), Type::getTypeName);
    }

    protected <K, V> String buildSummary(final String convertersInfo,
                                         final Map<K, V> converters,
                                         final Function<V, String> converterNameFunction,
                                         final Function<K, String> modelFunction) {
        if (converters.isEmpty()) {
            return convertersInfo + " <absent>";
        }
        Map<String, Set<String>> summary = new TreeMap<>();
        converters.forEach((k, v) -> summary.computeIfAbsent(converterNameFunction.apply(v),
                a -> new TreeSet<>()).add(modelFunction.apply(k)));
        StringJoiner joiner = new StringJoiner("\n", convertersInfo + "\n", "");
        summary.forEach((k, v) -> joiner.add(k + v.stream().map(s -> "\n    " + s + "").collect(Collectors.joining())));
        return joiner.toString();
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

    public Logger getLogger() {
        return logger;
    }

}
