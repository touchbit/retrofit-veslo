# Retrofit Veslo

This library is intended for testing HTTP API.   
The main task of the library is to provide the ability to work with two models of the response body from the server for
positive/negative test cases.  
Primitive usage example:

```java
public static class ExampleTests {

    private static final ExampleClient CLIENT = buildClient();

    public interface ExampleClient {
        @POST("/api/example")
        DualResponse<SuccessDTO, ErrorDTO> get();
    }

    public void test1639328754881() {
        SuccessDTO expected = new SuccessDTO("example");
        CLIENT.get()
        //  Response contains built-in soft assertions for checking the status, headers, and body of the response.
                .assertResponse(respAsserter -> respAsserter
                .assertHttpStatusCodeIs(200)
                .assertHttpStatusMessageIs("OK")
                .assertHeaders(headersAsserter -> headersAsserter
                        .contentTypeIs("application/json; charset=utf-8")
                        .assertHeaderIsPresent("X-Request-Id")
                        .accessControlAllowOriginIs("*"))
                .assertSucBody((asserter, actual) -> {
                    asserter.softly(actual::assertConsistency);
                    asserter.softly(() -> is("SuccessDTO.msg", actual.msg, expected.msg));
                }));
    }
}
```

All examples below assume the use of the lombok library (for shorthand)

## Modules (org.touchbit.retrofit.veslo)

- **all** - If you are not confused by unnecessary dependencies in the project
- **jackson** - working with [Jackson2](https://github.com/FasterXML/jackson) data models
- **gson** - working with [Gson](https://github.com/google/gson) data models
- **allure** - build-in steps for API calls with request/response attachments.
- **bean** - data models with built-in JSR 303 bean validation (hibernate validator).

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
                        // for test environment
                        .hostnameVerifier(TRUST_ALL_HOSTNAME)
                        .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
                        // Interceptor with your call handling rules
                        .addInterceptor(new CustomCompositeInterceptor())
                        .build())
                .baseUrl("https://petstore.swagger.io/")
                .addCallAdapterFactory(new AllureCallAdapterFactory()) // for AResponse<> (with allure steps)
                // .addCallAdapterFactory(new UniversalCallAdapterFactory()) // for DualResponse<>
                .addConverterFactory(new JacksonConverterFactory())
                // .addConverterFactory(new GsonConverterFactory())
                .build()
                .create(clientClass);
    }
}
```

## Converters

The mechanism for converting requests and responses is implemented in the class `ExtensionConverterFactory` from which
the `JacksonConverterFactory` and `GsonConverterFactory`. You can implement your generic factory by analogy.   
`ExtensionConverterFactory` allows to use convectors according to the rules in sequence:

* by annotations: `@ExtensionConverter`, `@RequestConverter`, `@ResponseConverter`;
* by raw body types (`RawBody`, `File`, `ResourceFile`, `Byte[]`, `byte[]`);
* by package name (strict match);
* by Content-Type header (MIME);
* by primitive/reference java types (`Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`, `String`);

```java
public class CustomConverterFactory extends ExtensionConverterFactory {

    public CustomConverterFactory() {
        super(LoggerFactory.getLogger(CustomConverterFactory.class));
        final JacksonConverter<Object> jacksonConverter = new JacksonConverter<>();
        final CustomRawConverter<Object> rawConverter = new CustomRawConverter<>();
        registerMimeConverter(jacksonConverter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
        registerJavaTypeConverter(jacksonConverter, Map.class, List.class);
        registerRawConverter(rawConverter, CustomRawBody.class);
        registerPackageConverter(new GsonConverter(), "com.example.model.gson");
    }

}
```

### Register converter by annotation (force)

```java
public interface PetApi {

    @POST("/v2/pet")
    @RequestConverter(bodyClasses = {Pet.class}, converter = JacksonConverter.class)
    @ResponseConverter(bodyClasses = {Pet.class, Status.class}, converter = JacksonConverter.class)
    // or array of converters
    @Converters(
            request = {@RequestConverter(bodyClasses = Pet.class, converter = JacksonConverter.class)},
            response = {@ResponseConverter(bodyClasses = {Pet.class, Status.class}, converter = JacksonConverter.class)}
    )
    AResponse<Pet, Status> addPet(@Body Object body);
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
which is not available in the base retrofit implementation.   
Action can implement three interfaces:

- `veslo.client.inteceptor.RequestInterceptAction` for processing `okhttp3.Chain` and `okhttp3.Request`
- `veslo.client.inteceptor.ResponseInterceptAction` for processing `java.lang.Throwable` and `okhttp3.Response`
- `veslo.client.inteceptor.InterceptAction` extends `RequestInterceptAction` and `ResponseInterceptAction`

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

Client for `LoginUserQueryMap` examples

```java
public interface UserApi {

    /**
     * @param queryMap user name & password for login (all required)
     */
    @GET("/v2/user/login")
    @Description("Logs user into the system")
    ExampleCustomResponse<AuthResult, Status> loginUser(@QueryMap() LoginUserQueryMap queryMap);

    default void authenticateUser(LoginUserQueryMap queryMap) {
        final AuthResult result = loginUser(queryMap)
                .assertResponse(a -> a.assertHttpStatusCodeIs(200).assertSucBodyNotNull()).getSucDTO();
        AuthAction.setToken(result.token());
    }

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

@QueryMapParameterRules(caseRule = SNAKE_CASE)
public class LoginUserQueryMap extends ReflectQueryMap {

    @QueryMapParameter(name = "lastName")
    private Object username;
    // <...>
}
```

#### Managing rules for handling `null` values (two options)

```java

@QueryMapParameterRules(nullRule = RULE_NULL_MARKER)
public class LoginUserQueryMap extends ReflectQueryMap {

    @QueryMapParameter(nullRule = RULE_EMPTY_STRING)
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

The current implementation of converters allows you to use Object as a Body, which allows you to pass as a request body
an object of any type that is supported by `ExtensionConverterFactory`.

Client

```java
public interface PetApi {

    /**
     * @param body {@link Pet} object that needs to be added to the store (required)
     */
    @POST("/v2/pet")
    @Headers({"Content-Type: application/json"})
    @Description("Add a new pet to the store")
    AResponse<Pet, Status> addPet(@Body Object body);

}
```

Usage

```java
public class AddPetTests extends BasePetTest {
    @Test
    public void test1640455066880() {
        PET_API.addPet(new Pet()); // body -> '{}'
        // or
        PET_API.addPet("fooBar"); // body -> '"fooBar"'
        // or
        PET_API.addPet(true); // body -> 'true'
        // or
        PET_API.addPet(ExtensionConverter.NULL_JSON_VALUE); // body -> 'null'
    }
}
```

