# Retrofit Veslo<sup>(paddle)</sup>

![](https://img.shields.io/badge/Java-8%2B-blue?style=plastic&logo=java)
![](https://maven-badges.herokuapp.com/maven-central/org.touchbit.retrofit.veslo/parent-pom/badge.svg?style=plastic)

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
- selection of the order of actions of the interceptor separately for Request and Response;
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
- jakarta java bean validation for any models (`BeanValidationModel`);
- base class with addition properties for jackson2 models (`JacksonModelAdditionalProperties`);
- allure framework support;

Primitive usage example:

```java
public static class ExampleTests {

    public interface ExampleClient {
        @GET("/api/example")
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
                    asserter.softly(() -> is("Pet.name", actual.name, expected.name));
                }));
    }
}
```

All examples below assume the use of the lombok library (for shorthand).

## Build project and run example tests

```bash
make ex
# or
mvn package -DskipTests=true
cd ./example 
mvn test -DskipTests=false -Dmaven.test.failure.ignore=true 
mvn allure:serve
```

## Modules (org.touchbit.retrofit.veslo)

- **all** - if you are not confused by unnecessary dependencies in the project;
- **jackson** - working with [Jackson2](https://github.com/FasterXML/jackson) data models;
- **gson** - working with [Gson](https://github.com/google/gson) data models;
- **allure** - build-in steps for API calls with request/response attachments;
- **bean** - data models with built-in JSR 303 bean validation (jakarta bean validator);

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
                        // Configure this client to follow redirects 
                        // (HTTP status 301, 302...).
                        .followRedirects(true)
                        // follow redirects (httpS -> http and http -> httpS)
                        // (for test environment)
                        .followSslRedirects(true) 
                        // instead of adding self signed certificates to the keystore 
                        // (for test environment)
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        // Interceptor with your call handling rules 
                        // (include `follow redirects`)
                        .addNetworkInterceptor(new CustomCompositeInterceptor())
                        .build())
                .baseUrl("https://petstore.swagger.io/")
                // for AResponse<> (with allure steps)
                .addCallAdapterFactory(new AllureCallAdapterFactory())
                // for DualResponse<> (default)
                // .addCallAdapterFactory(new UniversalCallAdapterFactory())
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

- **RawBodyConverter** - `RawBody` request/response bodies
  `AResponse<RawBody, ErrorModel> addPet(RawBody body);`
- **ByteArrayConverter** - `Byte[]/byte[]` request/response bodies
  `AResponse<Byte[], ErrorModel> addPet(Byte[] body);`
- **FileConverter** - read/write `File` request/response bodies
  `AResponse<File, ErrorModel> addPet(File body);`
- **ResourceFileConverter** - read resource `File` request body (not allowed for response body)
  `AResponse<Pet, ErrorModel> addPet(ResourceFile body);`

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
any inheritors of the `ExtensionConverterFactory` class. It is important to use the `@Headers` annotation to
automatically select the correct converter based on the Content-Type header.

**Client**

```java
public interface PetApi {

    /**
     * @param pet - Pet model that needs to be added to the store (required)
     */
    @POST("/v2/pet")
    @Headers({"Content-Type: application/json"})
    @Description("Add a new pet to the store")
    AResponse<Pet, Err> addPet(@Body Object pet);
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
        PET_API.addPet(ExtensionConverter.NULL_JSON_VALUE); // body -> <no body>
        // or
        PET_API.addPet(new File("src/test/java/transport/data/PetPositive.json")); // body -> <from file>
        // or
        PET_API.addPet(new ResourceFile("PetPositive.json")); // body -> <from project resource file>
    }
}
```

## Response

The response return type can be presented in the client interface in three options

- `Pet getPet(String id);` where `Pet` is a converted response body. If the API call is completed with the unsuccessful
  HTTP status code, then the method will return `null`.
- `DualResponse<Pet, Err> getPet(String id);` Where `Pet` is a converted response body in case of success, and `Err` in
  case of error.
- `AResponse<Pet, Err> getPet(String id);` The same as `DualResponse` only with the integration with the Allure
  framework.

`DualResponse` and `AResponse` inherited from `BaseDualResponse` and includes the following methods:

- `assertResponse(Consumer<ASSERTER> respAsserter)` - to check the response;
- `assertSucResponse(BiConsumer<ASSERTER, SUC_DTO> respAsserter, SUC_DTO expected)` - to check the success response;
- `assertErrResponse(BiConsumer<ASSERTER, ERR_DTO> respAsserter, ERR_DTO expected)` - to check the error response;
- `getErrDTO()` - return error response body model (nullable);
- `getSucDTO()` - return success response body model (nullable);
- `getEndpointInfo()` - return API method call info;
- `getResponse()` - return raw `okhttp3.Response` with readable body;
- `getCallAnnotations()` - return called API method annotations;

Examples of response assertions can be viewed in classes:

- `veslo.example.ExampleApiClientAssertions`
- `org.touchbit.retrofit.veslo.example.model.pet.Pet`
- `org.touchbit.retrofit.veslo.example.tests.BaseTest`

For clarity:

```java
public class ExampleTest {
    @Test
    public void test1640068360491() {
        final Pet expected = addRandomPet();
        PET_API.getPetById(expected.id()).assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(200)
                .assertSucBody(actual -> actual.assertPet(expected)));
    }

    @Test
    public void test1640460353980() {
        final Pet expected = generatePet();
        PET_API.addPet(expected).assertSucResponse(Pet::assertPetResponse, expected);
    }

    @Test
    public void test1640059907623() {
        PET_API.getPetById(-1).assertErrResponse(this::assertStatus404, PET_NOT_FOUND);
    }
}
```

### Custom response

`DualResponse` contains built-in asserters that can be expanded and override. To do this, you need to
implement `IResponseAsserter` and/or `IHeadersAsserter` and create your `CustomResponse` that should be inherited
from `BaseDualResponse`. For clarity:

![](.doc/img/CustomDualResponse.png?raw=true)

Next, when creating `retrifit` client, you need to explicitly specify which response should be used when creating an
instance of `IDualResponse`. Example for `new Retrofit.Builder()`:

- `.addCallAdapterFactory(new UniversalCallAdapterFactory(CustomResponse::new))` - for default call adapter factory
- `.addCallAdapterFactory(new AllureCallAdapterFactory(CustomResponse::new))` - for allure call adapter factory

## Assertions

### HeadersAsserter

Contains general methods for checking the response headers:

- `assertHeaderNotPresent(String headerName)`
- `assertHeaderIsPresent(String headerName)`
- `assertHeaderIs(String headerName, String expected)`
- `assertHeaderContains(String headerName, String expected)`

Provides similar methods for assert headers:

- Access-Control-Allow-Origin
- Connection
- Content-Type
- Etag
- Keep-Alive
- Server
- Set-Cookie
- Content-Encoding
- Transfer-Encoding
- Vary

### ResponseAsserter

Provides methods for checking HTTP status, Successful/Error response body.

Full description of methods looking in the
class [veslo.asserter.ResponseAsserter](./core/src/main/java/veslo/asserter/ResponseAsserter.java?raw=true)

### SoftlyAsserter

The interface provides the ability to accumulate errors when asserts failed

```java
public class Pet {

    private Long id;
    private Category category;

    public void assertPet(Pet expected) {
        try (SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.ignoreNPE(true); // ignore NPE if this.category() return null (row 2)
            asserter.softly(() -> assertThat(this.category()).as("Pet.category").isNotNull()); // 1
            asserter.softly(() -> this.category().assertCategory(asserter, expected.category())); // 2
            asserter.softly(() -> assertThat(this.id()).as("Pet.id").isEqualTo(expected.id())); // 3
        }
        // auto-closing throw exception if there are errors
    }

}
```

## Models

### RawBody

Raw request/response body. Stores the body in the byte representation.   
It is more suitable for requests violate the contract. For example, broken JSON.   
Sensitive to `No Content` responses (HTTP status code 204/205)
Contains built-in checks:

- `assertBodyIsNotNull()`
- `assertBodyIsNull()`
- `assertBodyIsNotEmpty()`
- `assertBodyIsEmpty()`
- `assertStringBodyContains(String... expectedStrings)`
- `assertStringBodyContainsIgnoreCase(String... expectedStrings)`
- `assertStringBodyIs(String expected)`
- `assertStringBodyIsIgnoreCase(String expected)`

### ResourceFile

Provides the ability to read files from project resources.   
Added to more convenient use in the API requests.   
`PET_API.addPet(new ResourceFile("PetPositive.json"));`

### JacksonModelAdditionalProperties

Allows you to handle the case when the response from the server contains extra fields without throwing an error when
converting (unlike Gson). Contains a method for checking the absence of extra fields `assertNoAdditionalProperties()`.

```java
public class Pet extends JacksonModelAdditionalProperties<Pet> {

    private Long id;

}
```

If the contract has changed and a response has come with new fields, then we can clearly check that the response body
does not contain extra fields without conversion errors.

```text
The presence of extra fields in the model: Pet
Expected: no extra fields
  Actual: {name=Puffy}
```

It is recommended to move the checks of the contract into separate tests.

```java
public class AddPetTests {

    @Test
    @DisplayName("Pet model complies with API contract")
    public void test1640455066880() {
        PET_API.addPet(generatePet()).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(Pet::assertNoAdditionalProperties));
    }
}
```

### BeanValidationModel

Inheriting models from this interface allows you to validate data for compliance with a contract using JSR 303
annotations. Contains method for assert data consistency `assertConsistency()`.   
It is recommended to move the checks of the contract into separate tests.

```java
public class AddPetTests {

    @Test
    @DisplayName("Pet model complies with API contract")
    public void test1640455066880() {
        PET_API.addPet(generatePet()).assertResponse(response -> response
                .assertHttpStatusCodeIs(200)
                .assertSucBody(Pet::assertConsistency));
    }
}
```

For clarity:

![](.doc/img/BeanValidationExample.png?raw=true)

---
Retrofit Veslo   
Copyright 2021-2022 Retrofit Veslo project authors and contributors.