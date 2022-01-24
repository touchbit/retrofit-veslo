


Статья расскажет о расширении для [square retrofit](https://square.github.io/retrofit/) клиента предназначенного в большей степени для функционального тестирования API. Создан в первую очередь для упрощения и ускорения разработки API тестов. Расширение позволяет использовать сразу две модели данных в ответе от сервера для позитивных и негативных тестов, динамически выбирать нужный конвертер, содержит встроенные мягкие проверки (softly assert) и еще много всяких полезностей.

<spoiler title="Весло">
Весло - специальное приспособление (движитель) в виде узкой лопаты для приведения судов в движение посредством **гребли** (действует по принципу рычага).
</spoiler>

<cut/>

<anchor>toc</anchor>

## Содержание

* <a href="#prerequisites">Предпосылки</a>
* <a href="#modules">Модули</a>
* <a href="#client">Клиент</a>
* <a href="#converters">Конвертеры</a>

<anchor>prerequisites</anchor>

## Предпосылки

Изначально данную библиотеку я начинал писать для себя с целью аккумулирования всех своих наработок связанных с тестированием API. Но на середине пути понял, что данное решение может быть полезно не только мне, что повлияло и на функциональность и на архитектуру решения. Некоторые архитектурные решения могут показаться странными, но важно понимать, что это решение предназначено строго для тестирования и при разработке я руководствовался следующими принципами в ущерб некоторым архитектурным канонам:

- минимизация порога знаний в java (целился в джунов);
- расширяемость/изменяемость текущей реализации;
- все должно работать "из коробки" с минимальными телодвижениями;
- самый лучший тест - однострочный;

Опишу сразу краткий список реализованной функциональности.

**Клиент**

- динамический выбор конвертера по java type, пакету модели, `Content-Type` заголовку (MIME) или через аннотацию метода API;
- конвертеры копируют тело ответа вместо вычитывания буфера;
- сетевой перехватчик с возможностью выбора последовательности действий применяемых отдельно для запроса и ответа;
- перехватчик для логирования запроса и ответа пишет два `LoggingEvent` соответственно (
  вместо `okhttp3.logging.HttpLoggingInterceptor`);
- исключить необходимость добавления самоподписных сертификатов в java keystore;
- поддержка allure;

**Запросы**

- использование `Object` типа для тела запроса в API методе (динамический выбор конвертера в рантайме);
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

<spoiler title="Пример использования">

```java
public static class ExampleTests {

  public interface ExampleClient {
    @POST("/api/example")
    DualResponse<Pet, Err> get(@Query("id") String id);
  }

  private static final ExampleClient CLIENT = buildClient();

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
Ниже представлен пример создания API клиента с комментариями (копипастнуть и удалить ненужное).

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
- `#followSslRedirects(Boolean)` - автоматический переход httpS <-> http (для тестового окружения);
- `#hostnameVerifier(HostnameVerifier)` - если вызываемый домен не соответствует домену в сертификате (для тестового окружения);
- `#sslSocketFactory(SSLSocketFactory, X509TrustManager)` - вместо добавления самоподписанных сертификатов в keystore (для тестового окружения);
- `#addNetworkInterceptor(Interceptor)` - добавление сетевого перехватчика (важно использовать именно этот метод, иначе не будут перехватываться редиректы). `CompositeInterceptor` описан ниже;
- `#addConverterFactory(Converter.Factory)` - добавить фабрику конвертеров для сериализации/десериализации объектов;
  - `JacksonConverterFactory` для Jackson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
  - `GsonConverterFactory` для Gson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
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
    // использовать GsonConverter для любой модели из пакета "com.example.model.gson" (строгое соответствие)
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