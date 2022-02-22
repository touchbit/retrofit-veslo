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

package veslo.constant;

import veslo.UtilityClassException;

/**
 * Method parameters name constants
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 22.02.2022
 */
public class ParameterNameConstants {

    public static final String DATA_PARAMETER = "data";
    public static final String BYTES_PARAMETER = "bytes";
    public static final String PATH_PARAMETER = "path";
    public static final String CHARSET_PARAMETER = "charset";
    public static final String FILE_PARAMETER = "file";
    public static final String COOKIE_NAME_PARAMETER = "cookieName";
    public static final String COOKIES_PARAMETER = "cookies";
    public static final String COOKIE_PARAMETER = "cookie";
    public static final String DOMAIN_PARAMETER = "domain";
    public static final String URL_PARAMETER = "url";
    public static final String RESPONSE_PARAMETER = "response";
    public static final String REQUEST_PARAMETER = "request";
    public static final String COLLECTOR_PARAMETER = "collector";
    public static final String HEADERS_ASSERTER_PARAMETER = "headersAsserter";
    public static final String HEADER_ASSERTER_CONSUMER_PARAMETER = "headerAsserterConsumer";
    public static final String ASSERTION_CONSUMER_PARAMETER = "assertionConsumer";
    public static final String EXPECTED_SUC_DTO_PARAMETER = "expectedSucDto";
    public static final String EXPECTED_ERR_DTO_PARAMETER = "expectedErrDto";
    public static final String TEMPLATE_PARAMETER = "template";
    public static final String FIELD_PARAMETER = "field";
    public static final String MODEL_PARAMETER = "model";
    public static final String TYPE_PARAMETER = "type";
    public static final String TYPES_PARAMETER = "types";
    public static final String BODY_PARAMETER = "body";
    public static final String BODY_TYPE_PARAMETER = "bodyType";
    public static final String BODY_CLASS_PARAMETER = "bodyClass";
    public static final String MODEL_CLASS_PARAMETER = "modelClass";
    public static final String ENCODED_STRING_PARAMETER = "encodedString";
    public static final String CODING_CHARSET_PARAMETER = "codingCharset";
    public static final String FORM_FIELD_NAME_PARAMETER = "formFieldName";
    public static final String PARSED_PARAMETER = "parsed";
    public static final String HANDLED_PARAMETER = "handled";
    public static final String VALUE_PARAMETER = "value";
    public static final String FIELD_TYPE_PARAMETER = "fieldType";
    public static final String TARGET_TYPE_PARAMETER = "targetType";
    public static final String PARAMETERIZED_TYPE_PARAMETER = "parameterizedType";
    public static final String PARAMETER_ANNOTATIONS_PARAMETER = "parameterAnnotations";
    public static final String METHOD_ANNOTATIONS_PARAMETER = "methodAnnotations";
    public static final String RETROFIT_PARAMETER = "retrofit";
    public static final String CONVERTER_PARAMETER = "converter";
    public static final String EXPECTED_TYPES_PARAMETER = "expectedTypes";
    public static final String ANNOTATION_PARAMETER = "annotation";
    public static final String ANNOTATIONS_PARAMETER = "annotations";
    public static final String CONVERTER_CLASS_PARAMETER = "converterClass";
    public static final String SUPPORTED_CONTENT_TYPES_PARAMETER = "supportedContentTypes";
    public static final String SUPPORTED_CONTENT_TYPE_PARAMETER = "supportedContentType";
    public static final String SUPPORTED_RAW_CLASSES_PARAMETER = "supportedRawClasses";
    public static final String SUPPORTED_RAW_CLASS_PARAMETER = "supportedRawClass";
    public static final String SUPPORTED_JAVA_TYPE_CLASSES_PARAMETER = "supportedJavaTypeClasses";
    public static final String SUPPORTED_JAVA_TYPE_CLASS_PARAMETER = "supportedJavaTypeClass";
    public static final String SUPPORTED_MODEL_ANNOTATION_PARAMETER = "supportedModelAnnotation";
    public static final String SUPPORTED_PACKAGE_NAMES_PARAMETER = "supportedPackageNames";
    public static final String SUPPORTED_PACKAGE_NAME_PARAMETER = "supportedPackageName";
    public static final String REQUEST_INTERCEPT_ACTIONS_PARAMETER = "requestInterceptActions";
    public static final String REQUEST_INTERCEPT_ACTION_PARAMETER = "requestInterceptAction";
    public static final String RESPONSE_INTERCEPT_ACTIONS_PARAMETER = "responseInterceptActions";
    public static final String RESPONSE_INTERCEPT_ACTION_PARAMETER = "responseInterceptAction";
    public static final String SUPPORTED_PACKAGES_PARAMETER = "supportedPackages";
    public static final String SUPPORTED_PACKAGE_CLASSES_PARAMETER = "supportedPackageClasses";
    public static final String RETURN_TYPE_PARAMETER = "returnType";
    public static final String SUCCESS_TYPE_PARAMETER = "successType";
    public static final String ERROR_TYPE_PARAMETER = "errorType";
    public static final String ENDPOINT_INFO_PARAMETER = "endpointInfo";
    public static final String CALL_PARAMETER = "call";
    public static final String TRANSPORT_EVENT_PARAMETER = "transportEvent";
    public static final String LOGGER_PARAMETER = "logger";
    public static final String LOG_LEVEL_PARAMETER = "logLevel";
    public static final String RESOURCE_RELATIVE_PATH_PARAMETER = "resourceRelativePath";
    public static final String DUAL_RESPONSE_CONSUMER_PARAMETER = "dualResponseConsumer";
    public static final String EXPECTED_STRINGS_PARAMETER = "expectedStrings";
    public static final String EXPECTED_STRING_PARAMETER = "expectedString";
    public static final String EXPECTED_PARAMETER = "expected";
    public static final String ACTUAL_PARAMETER = "actual";
    public static final String ASSERTER_CONSUMER_PARAMETER = "asserterConsumer";
    public static final String THROWABLE_PARAMETER = "throwable";
    public static final String THROWABLE_RUNNABLE_PARAMETER = "throwableRunnable";
    public static final String BASE_URL_PARAMETER = "baseUrl";
    public static final String INTERCEPTOR_PARAMETER = "interceptor";
    public static final String CALL_ADAPTER_FACTORY_PARAMETER = "callAdapterFactory";
    public static final String CONVERTER_FACTORY_PARAMETER = "converterFactory";
    public static final String CLIENT_CLASS_PARAMETER = "clientClass";
    public static final String TRUNCATION_PREDICATE_PARAMETER = "truncationPredicate";

    /**
     * Utility class. Forbidden instantiation.
     */
    private ParameterNameConstants() {
        throw new UtilityClassException();
    }

}
