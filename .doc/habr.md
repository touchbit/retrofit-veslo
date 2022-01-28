


Статья расскажет о расширении для [square retrofit](https://square.github.io/retrofit/) клиента предназначенного в большей степени для функционального тестирования API. Создан в первую очередь для упрощения и ускорения разработки API тестов. Расширение позволяет использовать сразу две модели данных в ответе от сервера для позитивных и негативных тестов, динамически выбирать нужный конвертер, содержит встроенные мягкие проверки (softly assert) и еще много всяких полезностей.

<cut/>

<spoiler title="Почему весло?">

Весло - специальное приспособление (движитель) в виде узкой лопаты для приведения судов (в том числе военных <sup><a href="https://ru.wikipedia.org/wiki/%D0%93%D0%B0%D0%BB%D0%B5%D1%80%D0%B0">галера</a></sup>) в движение посредством гребли (действует по принципу рычага).

</spoiler>

<anchor>toc</anchor>

## Содержание

* <a href="#prerequisites">Предпосылки</a>
* <a href="#modules">Модули</a>
* <a href="#client">Клиент</a>
* <a href="#requests">Запросы</a>
  * <a href="#objectRequestBody">Object в качестве тела запроса</a>
  * <a href="#reflectQueryMap">Формирование параметров запроса (ReflectQueryMap)</a>
    * <a href="#reflectQueryMapNullRule">Правила обработки `null` (QueryParameterNullValueRule)</a>
    * <a href="#reflectQueryMapCaseRule">Правила наименования параметров (QueryParameterCaseRule)</a>
* <a href="#responses">Ответы</a>
  * <a href="#responseasserters">Asserters</a>
  * <a href="#customresponseassertion">Кастомизация встроенных проверок</a>
* <a href="#models">Модели</a>
* <a href="#converters">Конвертеры</a>

<anchor>prerequisites</anchor>

## Предпосылки

Изначально данную библиотеку я начинал писать для себя с целью аккумулирования всех своих наработок связанных с тестированием API. Но на середине пути понял, что данное решение может быть полезно не только мне, что повлияло и на функциональность и на архитектуру решения. Некоторые архитектурные решения могут показаться странными, но важно понимать, что это решение предназначено строго для тестирования и при разработке я руководствовался следующими принципами в ущерб некоторым архитектурным канонам:

- минимизация порога знаний в java (целился в джунов);
- расширяемость/изменяемость текущей реализации;
- все должно работать "из коробки" с минимальными телодвижениями;
- самый лучший тест - однострочный;

<spoiler title="Список реализованной функциональности">

**Клиент**

- динамический выбор конвертера по java type, пакету модели, `Content-Type` заголовку (MIME) или через аннотацию метода API;
- конвертеры копируют тело ответа вместо вычитывания буфера;
- сетевой перехватчик с возможностью выбора последовательности действий применяемых отдельно для запроса и ответа;
- перехватчик для логирования запроса и ответа пишет два `LoggingEvent` соответственно (вместо `okhttp3.logging.HttpLoggingInterceptor`);
- исключить необходимость добавления самоподписных сертификатов в java keystore;
- поддержка allure;

**Запросы**

- использование `Object type` для тела запроса в API методе (динамический выбор конвертера в рантайме);
- упрощенная работа с `@QueryMap` (reflection);
- чтение тела запроса из файла

**Ответы**

- работа сразу с двумя моделями ответов для позитивных/негативных тестов;
- встроенные в ответ softly asserts;
- fluent API для проверки ответа;
- использование функциональных интерфейсов для проверки ответа;
- запись тела ответа в файл;

**Частности**

- softly asserts с автоматическим закрытием и возможностью игнорирования NPE (`SoftlyAsserter`);
- добавление проверок в модель для удобного использования в `ResponseAsserter`;
- примеры использования softly asserts для ответа (`ExampleApiClientAssertions`);
- базовый класс с дополнительными полями для моделей jackson2 (`JacksonModelAdditionalProperties`);
- jakarta java bean validation (`BeanValidationModel`);

</spoiler>

<spoiler title="Пример использования">

```java
public static class ExampleTests {

  public interface ExampleClient {
    @POST("/api/example")
    DualResponse<Pet, Err> get(@Query("id") String id);
  }

  private static final ExampleClient CLIENT = buildClient();

  // Пример теста с выносом проверок (для примера в модель)
  public void test1639328754880() {
    final Pet expected = new Pet().name("example");
    CLIENT.get("id_1").assertSucResponse(Pet::assertGET, expected);
  }

  // Пример теста с проверкой непосредственно в тесте
  public void test1639328754881() {
    final Pet expected = new Pet().name("example");
    // Ответ содержит встроенные softly asserts для проверки статуса, заголовков и тела ответа. 
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

</spoiler>

<a href="#toc">К содержанию</a>

<anchor>modules</anchor>

## Модули

- **all** - если вас не смущают лишние зависимости в проекте;
- **jackson** - работа с [Jackson2](https://github.com/FasterXML/jackson) моделями;
- **gson** - работа с [Gson](https://github.com/google/gson) моделями;
- **allure** - встроенные шаги для вызовов API с вложениями запроса/ответа;
- **bean** - модели данных со встроенной JSR 303 валидацией (jakarta bean validator);

Пример:

![](https://maven-badges.herokuapp.com/maven-central/org.touchbit.retrofit.veslo/parent-pom/badge.svg?style=plastic&subject=veslo.version)
```xml
<dependency>
    <groupId>org.touchbit.retrofit.veslo</groupId>
    <artifactId>jackson</artifactId>
    <version>${veslo.version}</version>
    <scope>compile</scope>
</dependency>
```

<a href="#toc">К содержанию</a>

<anchor>client</anchor>

## Клиент
Ниже представлен пример создания API клиента (копипастнуть и удалить ненужное).

```java
public class BaseTest {

  protected static final PetApi PET_API = createJacksonClient(PetApi.class);

  private static <C> C createJacksonClient(final Class<C> cliClass) {
    return new Retrofit.Builder()
        .client(new OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true) 
            .hostnameVerifier(TRUST_ALL_HOSTNAME)
            .sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, TRUST_ALL_CERTS_MANAGER)
            .addNetworkInterceptor(new CompositeInterceptor())
            .build())
        .baseUrl("https://petstore.swagger.io/")
        .addCallAdapterFactory(new AllureCallAdapterFactory())
        // или
        .addCallAdapterFactory(new UniversalCallAdapterFactory())
        .addConverterFactory(new JacksonConverterFactory())
        // или    
        .addConverterFactory(new GsonConverterFactory())
        .build()
        .create(cliClass);
  }
}
```

Пояснение методов:
- `#followRedirects(Boolean)` - автоматический переход по редирект статусам (301, 302...);
- `#followSslRedirects(Boolean)` - автоматический переход httpS <-> http по редирект статусам (для тестового окружения);
- `#hostnameVerifier(HostnameVerifier)` - если вызываемый домен не соответствует домену в сертификате (для тестового окружения);
- `#sslSocketFactory(SSLSocketFactory, X509TrustManager)` - вместо добавления самоподписанных сертификатов в keystore (для тестового окружения);
- `#addNetworkInterceptor(Interceptor)` - добавление сетевого перехватчика (важно использовать именно этот метод, иначе не будут перехватываться редиректы). `CompositeInterceptor` описан ниже;
- `#addConverterFactory(Converter.Factory)` - добавить фабрику конвертеров для сериализации/десериализации объектов;
  - `JacksonConverterFactory` для Jackson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
  - `GsonConverterFactory` для Gson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
  - `ExtensionConverterFactory` для примитивных/ссылочных типов (смотреть раздел <a href="#converters">конвертеры</a>);
- `#addCallAdapterFactory(CallAdapter.Factory)` - поддержка специфического возвращаемого типа в методе API клиента, отличных от `retrofit2.Call`;
  - `UniversalCallAdapterFactory` - фабрика для `DualResponse`;
  - `AllureCallAdapterFactory` - фабрика для `AResponse` с поддержкой allure шагов;

Если вы хотите использовать allure, то нужно добавить в зависимости allure модуль.
В таком случае возвращаемый класс будет `veslo.AResponse`. Так же настоятельно рекомендуется использовать аннотацию `io.qameta.allure.Description`.

```java
public interface AllureCallAdapterFactoryClient {
    @GET("/api/example")
    @Description("Get pet")
    AResponse<Pet, Err> get();
}
```

Иначе возвращаемый класс будет `veslo.client.response.DualResponse`. Так же настоятельно рекомендуется использовать аннотацию `veslo.client.EndpointInfo`.

```java
public interface UniversalCallAdapterFactoryClient {
    @GET("/api/example")
    @EndpointInfo("Get pet")
    DualResponse<Pet, Err> get();
}
```

Более детальное описание смотреть в разделе <a href="#responses">ответы</a>.

<a href="#toc">К содержанию</a>

<anchor>requests</anchor>
## Запросы
<anchor>objectRequestBody</anchor>
### `Object` в качестве тела запроса
Текущая реализация конвертеров позволяет использовать `Object` в качестве `@Body`, что позволяет отправлять в качестве тела запроса объект любого типа, который поддерживается `GsonConverterFactory` или `JacksonConverterFactory`. Механизм работает для любых наследников класса `ExtensionConverterFactory`. Важно использовать аннотацию `@Headers` для автоматического выбора правильного конвертера на основе заголовка `Content-Type`. Механизм выбора нужного конвертера описан в разделе <a href="#converters">конвертеры</a>.

```java
public interface PetApi {

  /** @param pet - {@link Pet} model (required) */
  @POST("/v2/pet")
  @Headers({"Content-Type: application/json"})
  @Description("Add a new pet to the store")
  AResponse<Pet, Err> addPet(@Body Object pet);
  //                               ^^^^^^
}
```

```java
public class AddPetTests extends BasePetTest {

  @Test
  public void test1640455066880() {
    // body -> {"name":"fooBar"}
    PET_API.addPet(new Pet().name("fooBar"));
    // body (json string) -> "fooBar"
    PET_API.addPet("fooBar");
    // body (string) -> fooBar
    PET_API.addPet(new RawBody("fooBar"));
    // body -> true
    PET_API.addPet(true);
    // body -> <отсутствует>
    PET_API.addPet(ExtensionConverter.NULL_BODY_VALUE);
    // body -> <из файла>
    final File file = new File("src/test/java/transport/data/PetPositive.json");
    PET_API.addPet(file);
    // body -> <из файла ресурсов проекта>
    final ResourceFile resourceFile = new ResourceFile("PetPositive.json");
    PET_API.addPet(resourceFile); 
  }
}
```

<a href="#toc">К содержанию</a>

<anchor>reflectQueryMap</anchor>

### Формирование параметров запроса (ReflectQueryMap)

Вы можете создать свой собственный `@QueryMap` для запросов, унаследовавшись от ReflectQueryMap, который позволяет вам считывать пары ключ-значение из переменных класса. Этот механизм является дополнением к стандартной работе с `Map`. От данной реализации может пойти кровь из глаз, но к сожалению разработчики retrofit не предоставили API для кастомизации обработки `@QueryMap`. Ниже представлен пример `QueryMap` с fluent методами с использованием `lombok` библиотеки.

**LoginUserQueryMap**

```java
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class LoginUserQueryMap extends ReflectQueryMap {

  private Object username;
  private Object password;
  
  // пример заполнения экземпляра
  static {
    new LoginUserQueryMap().username("test").password("abc123");     
  }
}
```

**Usage (client)**

```java
public interface UserApi {

  @GET("/v2/user/login")
  @Description("Logs user into the system")
  AResponse<Suc, Err> login(@QueryMap() LoginUserQueryMap queryMap);
  //                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
}
```

<spoiler title="Для примера сгенерированный QueryMap">

Пример ниже - результат генерации QueryMap по Swagger спецификации.
Писать подобные классы автоматизатору ручками немножко накладно.
А вариант наполнения `Map` в тесте я даже не рассматриваю.
```java
public class GeneratedQueryMap extends HashMap<String, Object> {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  public GeneratedQueryMap(HttpUrl httpUrl) {
    if (httpUrl == null) {
      throw new NullPointerException("HttpUrl cannot be null");
    }
    username(httpUrl.queryParameter(USERNAME));
    password(httpUrl.queryParameter(PASSWORD));
  }

  public GeneratedQueryMap username(Object username) {
    put(USERNAME, EncodingUtils.encode(username));
    return this;
  }

  public GeneratedQueryMap password(Object password) {
    put(PASSWORD, EncodingUtils.encode(password));
    return this;
  }
}
```

</spoiler>

<a href="#toc">К содержанию</a>

<anchor>reflectQueryMapNullRule</anchor>

#### Управление правилами обработки null значений (QueryParameterNullValueRule)

```java
// для всех переменных класса
@QueryMapParameterRules(nullRule = RULE_NULL_MARKER)
public class LoginUserQueryMap extends ReflectQueryMap {

  // только для определенной переменной
  @QueryMapParameter(nullRule = RULE_EMPTY_STRING)
  private Object password;
}
```

**QueryParameterNullValueRule**

- RULE_IGNORE - Игнорировать параметры c значением `null`
- RULE_NULL_MARKER - заменить `null` на `null marker` -> `/api/call?foo=%00`
- RULE_EMPTY_STRING - заменить `null` на пустую строку -> `/api/call?foo=`
- RULE_NULL_STRING - заменить `null` на `null` строку -> `/api/call?foo=null`

<a href="#toc">К содержанию</a>

<anchor>reflectQueryMapCaseRule</anchor>

#### Управление правилами наименования параметров (QueryParameterCaseRule)

По умолчанию используется имя переменной класса, но вы можете задать правило конвертации имен параметров запроса для всего класса, а так же явно задать имя параметра.

```java
// для всех переменных класса будет применен snake_case
@QueryMapParameterRules(caseRule = SNAKE_CASE)
public class LoginUserQueryMap extends ReflectQueryMap {

  // имя параметра для определенной переменной
  @QueryMapParameter(name = "userName")
  private Object username;
}
```

**QueryParameterCaseRule**

- CAMEL_CASE - camelCase
- KEBAB_CASE - kebab-case
- SNAKE_CASE - snake_case
- DOT_CASE - dot.case
- PASCAL_CASE - PascalCase

<a href="#toc">К содержанию</a>

<anchor>responses</anchor>

## Ответы 

Тип возвращаемого ответа может быть представлен в клиентском интерфейсе в трёх вариантах:
- `DualResponse<Pet, Err> getPet(String id);`, где `Pet` это конвертированное тело ответа в случае успеха, а `Err` — в случае ошибки.
- `AResponse<Pet, Err> getPet(String id);` То же, что и `DualResponse`, только с интеграцией с фреймворком Allure.
- Так же можно использовать модели и "простые" типы в качестве возвращаемого типа. В случае ошибок (status code 400+) конвертер попытается замапить тело ответа в возвращаемый тип и если не получилось, то вернется `null`. Так же можно использовать примитивы, но **не рекомендуется**.
  Например, если у нас есть API вызов `/api/live` (health check) который возвращает:
  - Строковый `OK/ERROR` -> `String live();`
  - Логический `true/false` -> `Boolean live();`
  - JSON объект -> `LiveProbeModel live()`
  ```java
  public interface UniversalCallAdapterFactoryClient {
    @GET("/api/live")
    @EndpointInfo("Service liveness probe")
    LiveProbeModel live();
  }
  ```

`DualResponse` и `AResponse` унаследованы от `BaseDualResponse` и включают следующие методы:
- `assertResponse(Consumer<ASSERTER> respAsserter)` - для проверки ответа от сервера;
- `assertSucResponse(BiConsumer<ASSERTER, SUC_DTO> respAsserter, SUC_DTO expected)` - для проверки успешного ответа от сервера;
- `assertErrResponse(BiConsumer<ASSERTER, ERR_DTO> respAsserter, ERR_DTO expected)` - для проверки ошибочного ответа от сервера;
- `getErrDTO()` - возвращает модель тела ответа в случае ошибки (nullable);
- `getSucDTO()` - возвращает модель тела ответа в случае успеха (nullable);
- `getEndpointInfo()` - возвращает информацию о вызове метода API;
- `getResponse()` - возвращает сырой ответ представленный классом `okhttp3.Response` с читаемым телом;
- `getCallAnnotations()` - возвращает список аннотаций вызванного клиентского API метода:

<a href="#toc">К содержанию</a>

<anchor>customresponseassertion</anchor>

### Кастомизация встроенных проверок

`DualResponse` содержит встроенные проверки, которые можно расширить и переопределить. Для этого вам нужно реализовать `IResponseAsserter` и/или `IHeadersAsserter`. Далее создать свой `CustomResponse`, который должен быть унаследован от `BaseDualResponse`. В классе `CustomResponse` нужно реализовать методы:

- `public IHeadersAsserter getHeadersAsserter();`
- `public IResponseAsserter getResponseAsserter();`

<spoiler title="Для наглядности">

![](https://github.com/touchbit/retrofit-veslo/raw/main/.doc/img/CustomDualResponse.png)

</spoiler>

Далее, при создании клиента `retrofit`, вам необходимо явно указать, какой ответ следует использовать при создании экземпляра `CustomResponse`.
Пример для `new Retrofit.Builder().addCallAdapterFactory(CallAdapter.Factory)` (фабрики библиотеки):

- `new UniversalCallAdapterFactory(CustomResponse::new)` или
- `new AllureCallAdapterFactory(CustomResponse::new)`

<a href="#toc">К содержанию</a>

<anchor>converters</anchor>

## Конвертеры

В классе `ExtensionConverterFactory` и его наследниках `JacksonConverterFactory` и `GsonConverterFactory` реализован механизм выбора нужного конвертера для преобразования запросов и ответов.
Конвертер выбирается в следующей последовательности:

* по аннотации вызываемого клиентского метода: `@Converters`, `@RequestConverter`, `@ResponseConverter`;
* по "сырому" типу тела (`RawBody`, `File`, `ResourceFile`, `Byte[]`, `byte[]`);
* по java пакету (на момент написания статьи это строгое соответствие, но планируется поддержка wildcard);
* по `Content-Type` заголовку (MIME);
* по примитивному/ссылочному java типу (`Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`, `String`);

Примеры использования встроенных java type конвертеров
- `AResponse<String, String> addPet(@Body String body);`
- `AResponse<RawBody, Err> addPet(@Body RawBody body);`
- `AResponse<Byte[], Err> addPet(@Body byte[] body);`
- `AResponse<File, Err> addPet(@Body File body);`
- `AResponse<Pet, Err> addPet(@Body ResourceFile body);` (только для запросов)

По аналогии с существующими фабриками Вы можете реализовать свою универсальную фабрику.

```java
public class CustomConverterFactory extends ExtensionConverterFactory {

  public CustomConverterFactory() {
    super(LoggerFactory.getLogger(CustomConverterFactory.class));
    final JacksonConverter<Object> jacksonConverter = new JacksonConverter<>();
    // использовать JacksonConverter для application/json, text/json
    registerMimeConverter(jacksonConverter, APP_JSON, APP_JSON_UTF8, TEXT_JSON, TEXT_JSON_UTF8);
    // использовать JacksonConverter если `Content-Type` не передан 
    registerMimeConverter(jacksonConverter, ContentType.NULL);
    // использовать JacksonConverter для Map, List типов
    registerJavaTypeConverter(jacksonConverter, Map.class, List.class);
    // использовать специфический конвертер для специфического типа сырого тела
    registerRawConverter(new CustomConverter<>(), CustomBody.class);
    // использовать GsonConverter для любой модели 
    // из пакета "com.example.model.gson" (строгое соответствие)
    registerPackageConverter(new GsonConverter(), "com.example.model.gson");
  }

}
```

При осуществлении запросов содержащих тело, предполагается наличие аннотации `retrofit2.http.Headers` c заголовком `Content-Type`. 

```java
public interface PetApi {
  @GET("/api/example")
  @Headers({"Content-Type: application/json"})
  AResponse<Pet, Err> get();
}
```

Если у вас `Content-Type` заголовок заполняются в рантайме через мапку, то фабрика не найдет конвертер для MIME типа.
```java
public interface PetApi {
  @GET("/api/example")
  AResponse<Pet, Err> get(@HeaderMap Map<String, String> headers);
}
```
К сожалению это особенность реализации retrofit и у нас нет возможности получить значение переменной метода.
В таком случае можно создать свой конвертер по аналогии с примером `CustomConverterFactory` с указанием конвертера для пакета или java типа;
Так же можно явно указать конвертер при помощи аннотации `@RequestConverter`, `@ResponseConverter`;
Данная функция как раз добавлена для обработки частных случаев, когда фабрика не может определить какой конвертер использовать.

```java
public interface PetApi {
    
    @POST("/v2/pet")
    @RequestConverter(bodyClasses = {Pet.class}, converter = JacksonConverter.class)
    @ResponseConverter(bodyClasses = {Pet.class, Err.class}, converter = JacksonConverter.class)
    AResponse<Pet, Err> addPet(@Body Object body);
    
}
```

Если поле `bodyClasses` оставить пустым, то конвертер будет применен ко всем моделям из сигнатуры метода. Например `@ResponseConverter(converter = JacksonConverter.class)` будет применен для `Pet` или `Err` моделей в ответе.

<a href="#toc">К содержанию</a>