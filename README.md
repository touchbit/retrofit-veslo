# Retrofit Veslo<sup>(paddle)</sup>

![](https://img.shields.io/badge/Java-8%2B-blue?style=plastic&logo=java)

- intended for testing HTTP API using the `rertofit2` library;
- designed to simplify the development of autotests as much as possible to the detriment of some architectural canons
  and is designed to qualify a strong junior in test automation;
- designed with the ability to change the current implementation;
- ready to use project with autotests is presented in the `example` module;
- if you want to ask how to do something, or to understand why something isn't working the way you expect it to,
  use [telegram group](https://t.me/veslo_retrofit);

## Prerequisites

**Client**

- choosing a converter by java type, model package name, `Content-Type` header;
- converters copies the response body instead of reading the `BufferedSource`;
- selection of the order of actions of the interceptor separately for Request and Response.;
- logging interceptor for request/response with two `LoggingEvent`, respectively;
- exclude the need to add self-signed certificates in java keystore;

**Request**

- using `Object` type for request body in API method (dynamic converter selection);
- reading request body from file;
- simplified work with @QueryMap (reflection);
- specifying the converter for the request via the API method annotation;

**Response**

- work with two response models at once for positive/negative tests;
- built-in softly assertions engine;
- writing the response body to a file;
- specifying the converter for the response via the API method annotation;

**Other**

- soft assertions with auto-close and the option to ignore NPE (`SoftlyAsserter`);
- ability to add soft assertions to the model for further use in `ResponseAsserter`;
- examples of using soft assertions for response (`ExampleApiClientAssertions`);
- jakarta java bean validation for any models (`BeanValidation`);
- base class with addition properties for jackson2 models (`JacksonModelAdditionalProperties`);
- allure framework support;

Primitive usage example:

```java
public static class ExampleTests {

    public interface ExampleClient {
        @POST("/api/example")
        DualResponse<Pet, Err> get();
    }

    private static final ExampleClient CLIENT = buildClient();

    public void test1639328754881() {
        Pet expected = new Pet().name("example");
        //  Response contains built-in soft assertions for checking the status, headers, and body of the response.
        CLIENT.get("id_1").assertResponse(respAsserter -> respAsserter
                .assertHttpStatusCodeIs(200)
                .assertHttpStatusMessageIs("OK")
                .assertHeaders(headersAsserter -> headersAsserter
                        .contentTypeIs("application/json; charset=utf-8")
                        .assertHeaderIsPresent("X-Request-Id")
                        .accessControlAllowOriginIs("*"))
                .assertSucBody((asserter, actual) -> {
                    asserter.softly(actual::assertConsistency);
                    asserter.softly(() -> is("SuccessDTO.msg", actual.name, expected.name));
                }));
    }
}
```

All examples below assume the use of the lombok library (for shorthand).

## Modules (org.touchbit.retrofit.veslo)

- **all** - if you are not confused by unnecessary dependencies in the project;
- **jackson** - working with [Jackson2](https://github.com/FasterXML/jackson) data models;
- **gson** - working with [Gson](https://github.com/google/gson) data models;
- **allure** - build-in steps for API calls with request/response attachments;
- **bean** - data models with built-in JSR 303 bean validation (hibernate validator);

Example:

```xml

<dependency>
    <groupId>org.touchbit.retrofit.veslo</groupId>
    <artifactId>jackson</artifactId>
    <version>${veslo.version}</version>
    <scope>compile</scope>
</dependency>
```

## Client

```java
public class BaseTest {

    protected static final PetApi PET_API = createJacksonClient(PetApi.class);

    static {
        // localisation (jakarta assertions)
        Locale.setDefault(Locale.ENGLISH);
    }

    private static <CLIENT> CLIENT createJacksonClient(final Class<CLIENT> clientClass) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        // Configure this client to follow redirects (HTTP status 301, 302...).
                        .followRedirects(true)
                        // instead of adding self signed certificates to the keystore (for test environment)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        // Interceptor with your call handling rules
                        .addInterceptor(new CustomCompositeInterceptor())
                        .build())
                .baseUrl("https://petstore.swagger.io/")
                .addCallAdapterFactory(new AllureCallAdapterFactory()) // for AResponse<> (with allure steps)
                // .addCallAdapterFactory(new UniversalCallAdapterFactory()) // for DualResponse<> (default)
                .addConverterFactory(new JacksonConverterFactory())
                // .addConverterFactory(new GsonConverterFactory())
                .build()
                .create(clientClass);
    }
}
```

## Converters

The mechanism for choosing the necessary converter for converting requests and responses is implemented in the classes
`ExtensionConverterFactory`,` JacksonConverterFactory` and `GsonConverterFactory`. The converter is selected in the
following sequence:

* by annotations: `@Converters`, `@RequestConverter`, `@ResponseConverter`;
* by raw body types (`RawBody`, `File`, `ResourceFile`, `Byte[]`, `byte[]`);
* by models package name (strict match);
* by Content-Type header (MIME);
* by primitive/reference java types (`Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`, `String`);

You can implement your generic factory by analogy.

```java
public class CustomConverterFactory extends ExtensionConverterFactory {

    public CustomConverterFactory() {
        super(LoggerFactory.getLogger(CustomConverterFactory.class));
        final JacksonConverter<Object> jacksonConverter = new JacksonConverter<>();
        // use JacksonConverter for application/json, text/json content type
        registerMimeConverter(jacksonConverter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        // use JacksonConverter for Map, List java types
        registerJavaTypeConverter(jacksonConverter, Map.class, List.class);
        // use custom converter for custom model
        registerRawConverter(new CustomConverter<>(), CustomBody.class);
        // use GsonConverter for any models in the package "com.example.model.gson" (strict match)
        registerPackageConverter(new GsonConverter(), "com.example.model.gson");
    }

}
```

### Register converter by annotation

```java
public interface PetApi {

    @POST("/v2/pet")
    @RequestConverter(bodyClasses = {Pet.class}, converter = JacksonConverter.class)
    @ResponseConverter(bodyClasses = {Pet.class, Err.class}, converter = JacksonConverter.class)
    // or array of converters
    @Converters(
            request = {@RequestConverter(bodyClasses = Pet.class, converter = JacksonConverter.class)},
            response = {@ResponseConverter(bodyClasses = {Pet.class, Err.class}, converter = JacksonConverter.class)}
    )
    AResponse<Pet, Err> addPet(@Body Object body);
}
```

### Raw types converters (built-in)

- **RawBodyConverter** - `AResponse<RawBody, ErrorModel> addPet(RawBody body);`
- **ByteArrayConverter** - `AResponse<Byte[], ErrorModel> addPet(Byte[] body);`
- **FileConverter** - `AResponse<File, ErrorModel> addPet(File body);`
- **ResourceFileConverter** - `AResponse<(not allow for response), ErrorModel> addPet(ResourceFile body);`

## CompositeInterceptor

Interceptor allows you to store multiple request, response and exception actions (handlers).   
The main feature of this interceptor is the ability to control the sequence of Actions calls for requests and responses,
which is not available in the base retrofit implementation. In other words, you yourself choose the order of the applied
Actions separately for Request and Response.      
Action can implement three interfaces:

- `RequestInterceptAction` for processing `okhttp3.Chain` and `okhttp3.Request`
- `ResponseInterceptAction` for processing `okhttp3.Response` and `java.lang.Throwable` (network errors)
- `InterceptAction` extends `RequestInterceptAction` and `ResponseInterceptAction`

```java
public class CustomCompositeInterceptor extends CompositeInterceptor {

    public CustomCompositeInterceptor() {
        super(LoggerFactory.getLogger(CustomCompositeInterceptor.class));
        withRequestInterceptActionsChain(AuthAction.INSTANCE, LoggingAction.INSTANCE, AllureAction.INSTANCE);
        withResponseInterceptActionsChain(LoggingAction.INSTANCE, AllureAction.INSTANCE);
    }

}
```

Built-in Actions:

- `LoggingAction` - logs request/response or transport error (see logging implementation in the example module)
- `CookieAction` - managing cookies headers on a thread
- `AllureAction` - add request/response attachments to step
  ![](.doc/img/AllureReportStep.png?raw=true)

## Request

### ReflectQueryMap

You can create your own simple QueryMap for queries by inheriting from ReflectQueryMap which allows you to read
key-value pairs from class variables. This mechanism is in addition to the standard reading of parameters from the `Map`
.

Client for `LoginUserQueryMap` examples

```java
public interface UserApi {
    
    @GET("/v2/user/login")
    AResponse<Auth, Err> loginUser(@QueryMap() LoginUserQueryMap queryMap);

}
```

Reading values from variables through reflection.

```java

@lombok.Getter
@lombok.Setter
@lombok.experimental.Accessors(chain = true, fluent = true)
public class LoginUserQueryMap extends ReflectQueryMap {

    public static final LoginUserQueryMap ADMIN = new LoginUserQueryMap().username("test").password("abc123");

    private Object username;
    private Object password;

}
```

#### Parameter naming control (two options)

```java

@QueryMapParameterRules(caseRule = SNAKE_CASE) // by rule
public class LoginUserQueryMap extends ReflectQueryMap {

    @QueryMapParameter(name = "lastName") // explicit name
    private Object username;
    // <...>
}
```

#### Managing rules for handling `null` values (two options)

```java

@QueryMapParameterRules(nullRule = RULE_NULL_MARKER) // for all class variables
public class LoginUserQueryMap extends ReflectQueryMap {

    @QueryMapParameter(nullRule = RULE_EMPTY_STRING) // only for a specific variable 
    private Object password;
    // <...>
}
```

#### QueryParameterNullValueRule

- RULE_IGNORE - Ignore null value parameters
- RULE_NULL_MARKER - replace null to null marker -> `/api/call?foo=%00`
- RULE_EMPTY_STRING - replace null to empty string -> `/api/call?foo=`
- RULE_NULL_STRING - replace null to null string -> `/api/call?foo=null`

#### QueryParameterCaseRule

- CAMEL_CASE - camelCase
- KEBAB_CASE - kebab-case
- SNAKE_CASE - snake_case
- DOT_CASE - dot.case
- PASCAL_CASE - PascalCase

### Request data model

The current implementation of converters allows you to use Object as a @Body, which allows you to send as a request body
an object of any type that is supported by `GsonConverterFactory` or `JacksonConverterFactory`. The mechanism works for
any inheritors of the `ExtensionConverterFactory` class.

**Client**

```java
public interface PetApi {

    /**
     * @param body - {@link Pet} object that needs to be added to the store (required)
     */
    @POST("/v2/pet")
    @Headers({"Content-Type: application/json"})
    @Description("Add a new pet to the store")
    AResponse<Pet, Err> addPet(@Body Object body);
//                                   ^^^^^^
}
```

**Request body** (jackson/gson converter)

```java
public class AddPetTests extends BasePetTest {

    @Test
    public void test1640455066880() {
        PET_API.addPet(new Pet().name("fooBar")); // body -> {"name":"fooBar"}
        // or
        PET_API.addPet("fooBar"); // body -> "fooBar"
        // or
        PET_API.addPet(new RawBody("fooBar")); // body -> fooBar
        // or
        PET_API.addPet(true); // body -> true
        // or
        PET_API.addPet(ExtensionConverter.NULL_JSON_VALUE); // body -> null
        // or
        PET_API.addPet(new File("src/test/java/transport/data/PetPositive.json")); // body -> <from file>
        // or
        PET_API.addPet(new ResourceFile("PetPositive.json")); // body -> <from project resource file>
    }
}
```

