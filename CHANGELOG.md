Change Log
==========

* **New** Register converter for annotated models (`ExtensionConverterFactory`)
* **New** Reading and modifying template text files through a helper class (`TemplateMapper`)
* **Change** `ResourceFile` - added charset support

## Version 1.0.1

* **New** utility classes for building test clients: 
  `TestClient`, `JacksonTestClient`, `GsonTestClient`, `Veslo4Test`
* **Change** packaging for `all` module changed from `pom` to `jar`
* **Update** up slf4j-api version from 1.7.32 to 1.7.35

## Version 1.0.0

* **New**: `JavaTypeCallAdapterFactory` to convert the response body to primitive or reference java types
* **New**: `UniversalCallAdapterFactory` to convert the response body to `IDualResponse<SUC, ERR>` java type
* **New**: `ExtensionConverterFactory` universal factory with the ability to use convectors according to the rules:
  * by annotations: `ExtensionConverter`, `RequestConverter`, `ResponseConverter`;
  * by raw body types (`File`,`RawBody`,`ResourceFile`, `Byte[]`);
  * by package name (exact match); by Content-Type header (MIME);
  * by primitive/reference java types (`Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`, `String`);
* **New**: Converters for primitive/reference types: `Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`
  , `String`
* **New**: Converters for raw body types: `File`,`RawBody`,`ResourceFile`, `Byte[]`
* **New**: `CompositeInterceptor` support for internal actions with control of the order of processing of outgoing and
  incoming events.
* **New**: `RawBody` byte response body with embedded checks.
* **New**: `ResourceFile` reading request body from project resources.
* **New**: `ReflectQueryMap` simplified mechanism for working with QueryMap. Reading values from variables through
  reflection. Parameter naming format control. Managing rules for handling `null` values.
* **New**: `ReflectQueryMap` simplified mechanism for working with QueryMap.
* **New**: `DualResponse` basic implementation of `IDualResponse` with inline request headers and body assertions.
* **New**: `TrustSocketHelper` trust any certificates and domains (for test environments)
  `OkHttpClient.Builder().sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)`.
* **New**: `BriefAssertionError` with control of the stack trace length (by condition).
* **New**: `JacksonConverter`, `JacksonConverterFactory` to work with Jackson2 data models.
* **New**: `JacksonModelAdditionalProperties` additional unknown fields, unmarshalling an incomplete JSON or a JSON that
  doesn't contain all the fields in the Java class (Jackson2).
* **New**: `GsonConverter`, `GsonConverterFactory` to work with Gson data models.
* **New**: `BooleanGsonTypeAdapter` boolean Gson adapter with support for 1/0 values.
* **New**: `AllureCallAdapterFactory`, `AResponse` module contains inline steps for API calls.
* **New**: `AllureAction` for `CompositeInterceptor` adds request/response attachments to a step.
* **New**: `BeanValidation` interface for data models with built-in JSR 303 bean validation (jakarta bean validator).
