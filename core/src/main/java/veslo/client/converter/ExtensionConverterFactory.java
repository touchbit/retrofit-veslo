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

package veslo.client.converter;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.ConvertCallException;
import veslo.ConverterNotFoundException;
import veslo.bean.template.TemplateSource;
import veslo.client.TransportEvent;
import veslo.client.converter.annotated.FormUrlEncodedConverter;
import veslo.client.converter.annotated.TemplateSourceConverter;
import veslo.client.converter.api.Converters;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.converter.api.ExtensionConverter.ResponseBodyConverter;
import veslo.client.converter.api.RequestConverter;
import veslo.client.converter.api.ResponseConverter;
import veslo.client.converter.defaults.JavaPrimitiveTypeConverter;
import veslo.client.converter.defaults.JavaReferenceTypeConverter;
import veslo.client.converter.defaults.RawBodyTypeConverter;
import veslo.client.header.ContentType;
import veslo.util.ConvertUtils;
import veslo.util.ReflectUtils;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.io.FilenameUtils.wildcardMatch;
import static veslo.client.TransportEvent.REQUEST;
import static veslo.client.TransportEvent.RESPONSE;
import static veslo.client.converter.api.ExtensionConverter.RequestBodyConverter;
import static veslo.client.header.ContentTypeConstants.*;
import static veslo.constant.ParameterNameConstants.*;
import static veslo.constant.SonarRuleConstants.SONAR_COGNITIVE_COMPLEXITY;
import static veslo.constant.SonarRuleConstants.SONAR_GENERIC_WILDCARD_TYPES;
import static veslo.util.ConvertUtils.isIDualResponse;

/**
 * Universal converter factory.
 * Allows you to match models and converters by:
 * - annotation {@link Converters}, {@link RequestConverter}, {@link ResponseConverter}
 * - package
 * - class
 * - Content-Type header value (MIME)
 * - Java type (reference/primitive)
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.11.2021
 */
@SuppressWarnings(SONAR_GENERIC_WILDCARD_TYPES)
public class ExtensionConverterFactory extends retrofit2.Converter.Factory {

    private final Map<Class<? extends Annotation>, ExtensionConverter<?>> modelAnnotationRequestConverters = new HashMap<>();
    private final Map<Class<? extends Annotation>, ExtensionConverter<?>> modelAnnotationResponseConverters = new HashMap<>();
    private final Map<String, ExtensionConverter<?>> packageRequestConverters = new HashMap<>();
    private final Map<String, ExtensionConverter<?>> packageResponseConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> rawRequestConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> rawResponseConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeRequestConverters = new HashMap<>();
    private final Map<ContentType, ExtensionConverter<?>> mimeResponseConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> javaTypeRequestConverters = new HashMap<>();
    private final Map<Type, ExtensionConverter<?>> javaTypeResponseConverters = new HashMap<>();
    private final Logger logger;

    /**
     * Default constructor with class logger
     */
    public ExtensionConverterFactory() {
        this(LoggerFactory.getLogger(ExtensionConverterFactory.class));
    }

    /**
     * Converter with default converters:
     * - {@link RawBodyTypeConverter}
     * - {@link JavaPrimitiveTypeConverter}
     * - {@link JavaReferenceTypeConverter}
     *
     * @param logger - required Slf4J logger
     */
    public ExtensionConverterFactory(final Logger logger) {
        Utils.parameterRequireNonNull(logger, LOGGER_PARAMETER);
        this.logger = logger;
        // Raw body converters
        final RawBodyTypeConverter rawBodyTypeConverter = RawBodyTypeConverter.INSTANCE;
        registerRawConverter(rawBodyTypeConverter, rawBodyTypeConverter.getSupportedTypes());
        // Java type primitive converters
        final JavaPrimitiveTypeConverter javaPrimitiveTypeConverter = JavaPrimitiveTypeConverter.INSTANCE;
        registerJavaTypeConverter(javaPrimitiveTypeConverter, javaPrimitiveTypeConverter.getSupportedTypes());
        // Java type reference converters
        final JavaReferenceTypeConverter javaReferenceTypeConverter = JavaReferenceTypeConverter.INSTANCE;
        registerJavaTypeConverter(javaReferenceTypeConverter, javaReferenceTypeConverter.getSupportedTypes());
        // Annotated java bean converters
        registerModelAnnotationConverter(TemplateSourceConverter.INSTANCE, TemplateSource.class);
        registerModelAnnotationConverter(FormUrlEncodedConverter.INSTANCE, FormUrlEncoded.class);
        // MIME types converters
        registerMimeConverter(FormUrlEncodedConverter.INSTANCE,
                APP_FORM_URLENCODED, APP_X_FORM_URLENCODED, APP_FORM_URLENCODED_UTF8, APP_X_FORM_URLENCODED_UTF8);
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
    @SuppressWarnings(SONAR_COGNITIVE_COMPLEXITY)
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
                RequestBodyConverter converter = getRequestConverterFromCallAnnotation(bodyClass, pA, mA, rtf);
                if (converter == null) {
                    converter = getRawRequestConverter(bodyClass, pA, mA, rtf);
                }
                if (converter == null) {
                    converter = getModelAnnotationRequestConverter(bodyClass, pA, mA, rtf);
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
                    logger.debug("Request converter found: {}", Utils.getTypeName(converter));
                    final RequestBody result = converter.convert(body);
                    logger.debug("Converted request body: {}", Utils.getTypeName(result));
                    return result;
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
    @SuppressWarnings(SONAR_COGNITIVE_COMPLEXITY)
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
                final Type bodyType = getResponseBodyType(type);
                final String bodyTypeName = Utils.getTypeName(bodyType);
                logger.debug("Definition of response converter for type: {}", bodyTypeName);
                ResponseBodyConverter<?> converter = getResponseConverterFromCallAnnotation(bodyType, mA, rtf);
                if (converter == null) {
                    converter = getRawResponseConverter(bodyType, mA, rtf);
                }
                if (converter == null) {
                    converter = getModelAnnotationResponseConverter(bodyType, mA, rtf);
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
                    logger.debug("Response converter found: {}", Utils.getTypeName(converter));
                    final Object result = converter.convert(respBody);
                    logger.debug("Response body successfully converted");
                    return result;
                }
            }
        };
    }

    /**
     * Get {@link RequestBodyConverter} by bodyClass
     * from called method annotations ({@link Converters} or {@link RequestConverter})
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    protected RequestBodyConverter getRequestConverterFromCallAnnotation(@Nonnull final Class<?> bodyClass,
                                                                         @Nonnull final Annotation[] parameterAnnotations,
                                                                         @Nonnull final Annotation[] methodAnnotations,
                                                                         @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
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
     * Get {@link RequestBodyConverter} by bodyClass
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
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final ExtensionConverter<?> converter = getRawRequestConverters().get(bodyClass);
        return converter == null ? null :
                converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
    }

    /**
     * Get {@link RequestBodyConverter} by bodyClass annotations
     *
     * @param bodyClass            - request method body class.
     * @param parameterAnnotations - API client called method parameters annotations
     * @param methodAnnotations    - API client called method annotations
     * @param retrofit             - see {@link Retrofit}
     * @return {@link RequestBodyConverter} or null
     */
    @Nullable
    public RequestBodyConverter getModelAnnotationRequestConverter(@Nonnull final Class<?> bodyClass,
                                                                   @Nonnull final Annotation[] parameterAnnotations,
                                                                   @Nonnull final Annotation[] methodAnnotations,
                                                                   @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        return getModelAnnotationRequestConverters().entrySet().stream()
                .filter(e -> bodyClass.isAnnotationPresent(e.getKey()))
                .map(Map.Entry::getValue)
                .map(c -> c.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit))
                // always contains one value or null
                .findFirst()
                .orElse(null);
    }

    /**
     * Get {@link RequestBodyConverter} by bodyClass package (wildcard match)
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
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final Package aPackage = bodyClass.getPackage();
        if (aPackage == null) {
            return null;
        }
        final ExtensionConverter<?> converter = getPackageRequestConverters()
                .entrySet().stream().filter(e -> wildcardMatch(aPackage.getName(), e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
        return converter == null ? null :
                converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
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
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final ContentType contentType = ConvertUtils.getContentType(methodAnnotations);
        final ExtensionConverter<?> converter = getMimeRequestConverters().get(contentType);
        return converter == null ? null :
                converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
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
        Utils.parameterRequireNonNull(bodyClass, BODY_CLASS_PARAMETER);
        Utils.parameterRequireNonNull(parameterAnnotations, PARAMETER_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final ExtensionConverter<?> converter = getJavaTypeRequestConverters().get(bodyClass);
        return converter == null ? null :
                converter.requestBodyConverter(bodyClass, parameterAnnotations, methodAnnotations, retrofit);
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
    protected Type getResponseBodyType(final Type type) {
        Utils.parameterRequireNonNull(type, TYPE_PARAMETER);
        return isIDualResponse(type) ? getParameterUpperBound(0, (ParameterizedType) type) : type;
    }

    /**
     * Get {@link ResponseBodyConverter} by bodyClass
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
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final Map<Type, ExtensionConverter<?>> rawConverters = getRawResponseConverters();
        final ExtensionConverter<?> converter = rawConverters.get(bodyType);
        return converter == null ? null : converter.responseBodyConverter(bodyType, methodAnnotations, retrofit);
    }

    /**
     * Get {@link ResponseBodyConverter} by bodyClass annotation
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getModelAnnotationResponseConverter(@Nonnull final Type bodyType,
                                                                           @Nonnull final Annotation[] methodAnnotations,
                                                                           @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        Class<?> bodyClass = TypeUtils.getRawType(bodyType, null);
        return bodyClass == null ? null :
                getModelAnnotationResponseConverters().entrySet().stream()
                        .filter(e -> bodyClass.isAnnotationPresent(e.getKey()))
                        .map(Map.Entry::getValue)
                        .map(c -> c.responseBodyConverter(bodyClass, methodAnnotations, retrofit))
                        // always contains one value or null
                        .findFirst()
                        .orElse(null);
    }

    /**
     * Get {@link ResponseBodyConverter} from bodyClass package (wildcard match)
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
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
        final Type responseBodyType = getResponseBodyType(bodyType);
        if (responseBodyType instanceof ParameterizedType) {
            // get converter by generic type package name
            // For example get converter for UserDTO from parameterized type List<UserDTO>
            ParameterizedType r = (ParameterizedType) bodyType;
            for (Type typeArgument : r.getActualTypeArguments()) {
                final ResponseBodyConverter<?> result = getPackageResponseConverter(typeArgument, methodAnnotations, retrofit);
                if (result != null) {
                    return result;
                }
            }
        }
        if (responseBodyType instanceof Class) {
            Class<?> bodyClass = (Class<?>) responseBodyType;
            if (bodyClass.isPrimitive()) {
                return null;
            }
            final String modelPackage = bodyClass.getPackage().getName();
            final ExtensionConverter<?> extensionConverter = getPackageRequestConverters()
                    .entrySet().stream().filter(e -> wildcardMatch(modelPackage, e.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(null);
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
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
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
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
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
     * Get {@link ResponseBodyConverter} by bodyClass
     * from called method annotations ({@link Converters} or {@link ResponseConverter})
     *
     * @param bodyType          - request method body java type.
     * @param methodAnnotations - API client called method annotations
     * @param retrofit          - see {@link Retrofit}
     * @return {@link ResponseBodyConverter} or null
     */
    @Nullable
    protected ResponseBodyConverter<?> getResponseConverterFromCallAnnotation(@Nonnull final Type bodyType,
                                                                              @Nonnull final Annotation[] methodAnnotations,
                                                                              @Nonnull final Retrofit retrofit) {
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
        Utils.parameterRequireNonNull(methodAnnotations, METHOD_ANNOTATIONS_PARAMETER);
        Utils.parameterRequireNonNull(retrofit, RETROFIT_PARAMETER);
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
     * If method A.bodyClasses() returned a non-empty list, a comparison by bodyClass will be performed,
     * and if there is a match, new instance of A.converter() will be returned otherwise null.
     *
     * @param annotation - {@link ResponseConverter} or {@link RequestConverter} annotation
     * @param bodyType   - model class
     * @return {@link ExtensionConverter} or null
     * @throws ConvertCallException if annotation differs from {@link ResponseConverter} or {@link RequestConverter}
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    protected ExtensionConverter<?> getExtensionConverter(@Nonnull final Annotation annotation,
                                                          @Nonnull final Type bodyType) {
        Utils.parameterRequireNonNull(annotation, ANNOTATION_PARAMETER);
        Utils.parameterRequireNonNull(bodyType, BODY_TYPE_PARAMETER);
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
            return ReflectUtils.invokeConstructor(converterClass);
        }
        for (Class<?> converterBodyClass : converterBodyClasses) {
            if (converterBodyClass.equals(bodyType)) {
                return ReflectUtils.invokeConstructor(converterClass);
            }
        }
        return null;
    }

    /**
     * Add request/response converter for specified content types (MIME)
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedContentTypes - array list of content types supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerMimeConverter(final ExtensionConverter<?> converter,
                                                           final ContentType... supportedContentTypes) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedContentTypes, SUPPORTED_CONTENT_TYPES_PARAMETER);
        registerMimeRequestConverter(converter, supportedContentTypes);
        registerMimeResponseConverter(converter, supportedContentTypes);
        return this;
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
    public ExtensionConverterFactory registerMimeRequestConverter(final ExtensionConverter<?> converter,
                                                                  final ContentType... supportedContentTypes) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedContentTypes, SUPPORTED_CONTENT_TYPES_PARAMETER);
        for (ContentType supportedContentType : supportedContentTypes) {
            Utils.parameterRequireNonNull(supportedContentType, SUPPORTED_CONTENT_TYPE_PARAMETER);
            getMimeRequestConverters().put(supportedContentType, converter);
        }
        return this;
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
    public ExtensionConverterFactory registerMimeResponseConverter(final ExtensionConverter<?> converter,
                                                                   final ContentType... supportedContentTypes) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedContentTypes, SUPPORTED_CONTENT_TYPES_PARAMETER);
        for (ContentType supportedContentType : supportedContentTypes) {
            Utils.parameterRequireNonNull(supportedContentType, SUPPORTED_CONTENT_TYPE_PARAMETER);
            getMimeResponseConverters().put(supportedContentType, converter);
        }
        return this;
    }

    /**
     * Add converter for specified classes
     *
     * @param converter           - Converter implementing interface {@link ExtensionConverter}
     * @param supportedRawClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerRawConverter(final ExtensionConverter<?> converter,
                                                          final Type... supportedRawClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedRawClasses, SUPPORTED_RAW_CLASSES_PARAMETER);
        registerRawRequestConverter(converter, supportedRawClasses);
        registerRawResponseConverter(converter, supportedRawClasses);
        return this;
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
    public ExtensionConverterFactory registerRawRequestConverter(final ExtensionConverter<?> converter,
                                                                 final Type... supportedRawClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedRawClasses, SUPPORTED_RAW_CLASSES_PARAMETER);
        for (Type supportedRawClass : supportedRawClasses) {
            Utils.parameterRequireNonNull(supportedRawClass, SUPPORTED_RAW_CLASS_PARAMETER);
            getRawRequestConverters().put(supportedRawClass, converter);
        }
        return this;
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
    public ExtensionConverterFactory registerRawResponseConverter(final ExtensionConverter<?> converter,
                                                                  final Type... supportedRawClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedRawClasses, SUPPORTED_RAW_CLASSES_PARAMETER);
        for (Type supportedRawClass : supportedRawClasses) {
            Utils.parameterRequireNonNull(supportedRawClass, SUPPORTED_RAW_CLASS_PARAMETER);
            getRawResponseConverters().put(supportedRawClass, converter);
        }
        return this;
    }

    /**
     * Add java type request/response converter for specified classes
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedJavaTypeClasses - array list of classes supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerJavaTypeConverter(final ExtensionConverter<?> converter,
                                                               final Type... supportedJavaTypeClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedJavaTypeClasses, SUPPORTED_JAVA_TYPE_CLASSES_PARAMETER);
        registerJavaTypeRequestConverter(converter, supportedJavaTypeClasses);
        registerJavaTypeResponseConverter(converter, supportedJavaTypeClasses);
        return this;
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
    public ExtensionConverterFactory registerJavaTypeRequestConverter(final ExtensionConverter<?> converter,
                                                                      final Type... supportedJavaTypeClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedJavaTypeClasses, SUPPORTED_JAVA_TYPE_CLASSES_PARAMETER);
        for (Type supportedJavaTypeClass : supportedJavaTypeClasses) {
            Utils.parameterRequireNonNull(supportedJavaTypeClass, SUPPORTED_JAVA_TYPE_CLASS_PARAMETER);
            getJavaTypeRequestConverters().put(supportedJavaTypeClass, converter);
        }
        return this;
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
    public ExtensionConverterFactory registerJavaTypeResponseConverter(final ExtensionConverter<?> converter,
                                                                       final Type... supportedJavaTypeClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedJavaTypeClasses, SUPPORTED_JAVA_TYPE_CLASSES_PARAMETER);
        for (Type supportedJavaTypeClass : supportedJavaTypeClasses) {
            Utils.parameterRequireNonNull(supportedJavaTypeClass, SUPPORTED_JAVA_TYPE_CLASS_PARAMETER);
            getJavaTypeResponseConverters().put(supportedJavaTypeClass, converter);
        }
        return this;
    }

    /**
     * Add request/response converter for specified model annotations
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedModelAnnotation - annotated model supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerModelAnnotationConverter(final ExtensionConverter<?> converter,
                                                                      final Class<? extends Annotation> supportedModelAnnotation) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedModelAnnotation, SUPPORTED_MODEL_ANNOTATION_PARAMETER);
        registerModelAnnotationRequestConverter(converter, supportedModelAnnotation);
        registerModelAnnotationResponseConverter(converter, supportedModelAnnotation);
        return this;
    }

    /**
     * @return Map of request model annotations converters where key - model annotation, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Class<? extends Annotation>, ExtensionConverter<?>> getModelAnnotationRequestConverters() {
        return modelAnnotationRequestConverters;
    }

    /**
     * Add request converter for specified model annotations
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedModelAnnotation - annotated model supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerModelAnnotationRequestConverter(final ExtensionConverter<?> converter,
                                                                             final Class<? extends Annotation> supportedModelAnnotation) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedModelAnnotation, SUPPORTED_MODEL_ANNOTATION_PARAMETER);
        getModelAnnotationRequestConverters().put(supportedModelAnnotation, converter);
        return this;
    }

    /**
     * @return Map of response model annotations converters where key - model annotation, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<Class<? extends Annotation>, ExtensionConverter<?>> getModelAnnotationResponseConverters() {
        return modelAnnotationResponseConverters;
    }

    /**
     * Add response converter for specified model annotations
     *
     * @param converter                - Converter implementing interface {@link ExtensionConverter}
     * @param supportedModelAnnotation - annotated model supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerModelAnnotationResponseConverter(final ExtensionConverter<?> converter,
                                                                              final Class<? extends Annotation> supportedModelAnnotation) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedModelAnnotation, SUPPORTED_MODEL_ANNOTATION_PARAMETER);
        getModelAnnotationResponseConverters().put(supportedModelAnnotation, converter);
        return this;
    }

    /**
     * Add converter for specified package names (wildcard)
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package wildcard names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerPackageConverter(final ExtensionConverter<?> converter,
                                                              final String... supportedPackageNames) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedPackageNames, SUPPORTED_PACKAGE_NAMES_PARAMETER);
        registerPackageRequestConverter(converter, supportedPackageNames);
        registerPackageResponseConverter(converter, supportedPackageNames);
        return this;
    }

    /**
     * Add converter for specified package names
     *
     * @param converter         - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackages - array list of packages supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerPackageConverter(final ExtensionConverter<?> converter,
                                                              final Package... supportedPackages) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedPackages, SUPPORTED_PACKAGES_PARAMETER);
        final String[] packageNames = Arrays.stream(supportedPackages)
                .filter(Objects::nonNull)
                .map(Package::getName)
                .toArray(String[]::new);
        registerPackageConverter(converter, packageNames);
        return this;
    }

    /**
     * Add converter for specified package names
     *
     * @param converter               - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageClasses - array list of class packages supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerPackageConverter(final ExtensionConverter<?> converter,
                                                              final Class<?>... supportedPackageClasses) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedPackageClasses, SUPPORTED_PACKAGE_CLASSES_PARAMETER);
        final Package[] packages = Arrays.stream(supportedPackageClasses)
                .filter(Objects::nonNull)
                .map(Class::getPackage)
                .toArray(Package[]::new);
        registerPackageConverter(converter, packages);
        return this;
    }

    /**
     * @return Map of request converters where key - package prefix, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<String, ExtensionConverter<?>> getPackageRequestConverters() {
        return packageRequestConverters;
    }

    /**
     * Add converter for specified package names (wildcard)
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package wildcard names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerPackageRequestConverter(final ExtensionConverter<?> converter,
                                                                     final String... supportedPackageNames) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedPackageNames, SUPPORTED_PACKAGE_NAMES_PARAMETER);
        for (String supportedPackageName : supportedPackageNames) {
            Utils.parameterRequireNonNull(supportedPackageName, SUPPORTED_PACKAGE_NAME_PARAMETER);
            getPackageRequestConverters().put(supportedPackageName, converter);
        }
        return this;
    }

    /**
     * @return Map of response converters where key - package prefix, value - {@link ExtensionConverter}
     */
    @Nonnull
    public Map<String, ExtensionConverter<?>> getPackageResponseConverters() {
        return packageResponseConverters;
    }

    /**
     * Add converter for specified package names (wildcard)
     *
     * @param converter             - Converter implementing interface {@link ExtensionConverter}
     * @param supportedPackageNames - array list of package wildcard names supported by the {@link ExtensionConverter}
     */
    @EverythingIsNonNull
    public ExtensionConverterFactory registerPackageResponseConverter(final ExtensionConverter<?> converter,
                                                                      final String... supportedPackageNames) {
        Utils.parameterRequireNonNull(converter, CONVERTER_PARAMETER);
        Utils.parameterRequireNonNull(supportedPackageNames, SUPPORTED_PACKAGE_NAMES_PARAMETER);
        for (String supportedPackageName : supportedPackageNames) {
            Utils.parameterRequireNonNull(supportedPackageName, SUPPORTED_PACKAGE_NAME_PARAMETER);
            getPackageResponseConverters().put(supportedPackageName, converter);
        }
        return this;
    }

    /**
     * Builds a converter map from {@link Converters} and {@link RequestConverter} annotations
     * received directly during the API call.
     *
     * @param mA - API client called method annotations
     * @return Map where key - request DTO class name, value - Converter class
     */
    @Nonnull
    protected Map<String, Type> getCallMethodAnnotationRequestConverters(@Nullable final Annotation[] mA) {
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
    protected Map<String, Type> getCallMethodAnnotationResponseConverters(@Nullable final Annotation[] mA) {
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
        Utils.parameterRequireNonNull(transportEvent, TRANSPORT_EVENT_PARAMETER);
        final Map<String, Type> annotated;
        final Map<Type, ExtensionConverter<?>> raw;
        final Map<String, ExtensionConverter<?>> pack;
        final Map<ContentType, ExtensionConverter<?>> mime;
        final Map<Type, ExtensionConverter<?>> javaType;
        if (transportEvent == REQUEST) {
            raw = getRawRequestConverters();
            pack = getPackageRequestConverters();
            mime = getMimeRequestConverters();
            javaType = getJavaTypeRequestConverters();
            annotated = getCallMethodAnnotationRequestConverters(mA);
            annotated.putAll(getModelAnnotationRequestConverters().entrySet().stream()
                    .collect(Collectors.toMap(e -> "@" + e.getKey().getName(), e -> e.getValue().getClass())));
        } else {
            raw = getRawResponseConverters();
            pack = getPackageResponseConverters();
            mime = getMimeResponseConverters();
            javaType = getJavaTypeResponseConverters();
            annotated = getCallMethodAnnotationResponseConverters(mA);
            annotated.putAll(getModelAnnotationResponseConverters().entrySet().stream()
                    .collect(Collectors.toMap(e -> "@" + e.getKey().getName(), e -> e.getValue().getClass())));
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
