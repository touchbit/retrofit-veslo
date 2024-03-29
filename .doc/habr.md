


Статья расскажет о расширении для HTTP клиента [retrofit](https://square.github.io/retrofit/) предназначенного в большей степени для функционального тестирования API. Создан в первую очередь для упрощения и ускорения разработки API тестов. Расширение позволяет использовать сразу две модели данных в ответе от сервера для позитивных и негативных тестов, динамически выбирать нужный конвертер, содержит встроенные мягкие проверки (softly assert) и еще много всяких полезностей.

![](https://habrastorage.org/webt/ol/lf/aw/ollfawiswjqiyiznfkmiyylu3rq.gif)

<cut/>

<spoiler title="Почему весло?">

Весло - специальное приспособление (движитель) в виде узкой лопаты для приведения судов (в том числе военных <sup><a href="https://ru.wikipedia.org/wiki/%D0%93%D0%B0%D0%BB%D0%B5%D1%80%D0%B0">галера</a></sup>) в движение посредством гребли (действует по принципу рычага).

</spoiler>

<anchor>anchor_TOC</anchor>

## Содержание

* <a href="#anchor_Prerequisites">Предпосылки</a>
* <a href="#anchor_Modules">Модули</a>
* <a href="#anchor_Requests">Запросы к серверу</a>
  * <a href="#anchor_ObjectRequestBody">Object в качестве тела запроса</a>
  * <a href="#anchor_ReflectQueryMap">Формирование параметров запроса (ReflectQueryMap)</a>
     * <a href="#anchor_QueryParameterNullValueRule">Правила обработки 'null' (QueryParameterNullValueRule)</a>
     * <a href="#anchor_QueryParameterCaseRule">Правила наименования параметров (QueryParameterCaseRule)</a>
* <a href="#anchor_Responses">Ответы от сервера</a>
  * <a href="#anchor_SoftlyAsserter">Softly Asserter</a>
  * <a href="#anchor_ResponseAsserter">Response Asserter</a>
  * <a href="#anchor_HeaderAsserter">Header Asserter</a>
  * <a href="#anchor_BodyAsserter">Body Asserter</a>
  * <a href="#anchor_CustomResponseAssertion">Кастомизация встроенных проверок</a>
* <a href="#anchor_Models">Модели</a>
  * <a href="#anchor_RawBody">RawBody</a>
  * <a href="#anchor_ResourceFile">ResourceFile</a>
  * <a href="#anchor_Jackson2Model">Jackson2 модели</a>
  * <a href="#anchor_BeanValidationModel">Jakarta Bean Validation</a>
* <a href="#anchor_Client">Клиент</a>
  * <a href="#anchor_CompositeInterceptor">Сетевой перехватчик (CompositeInterceptor)</a>
* <a href="#anchor_Converters">Конвертеры</a>
* <a href="#anchor_Usefulness">Полезности</a>
  * <a href="#anchor_UsefulnessLogging">Лог-файл для каждого теста</a>
  * <a href="#anchor_UsefulnessLiveTemplates">Шаблонизация тестовых методов</a>
  * <a href="#anchor_UsefulnessPlugins">Плагины IntelliJ IDEA</a>
* <a href="#anchor_Finally">В заключение</a>

<anchor>anchor_Prerequisites</anchor>

## Предпосылки

Изначально данную библиотеку я начинал писать для себя с целью аккумулирования своих наработок связанных с тестированием API. Но в середине пути понял, что данное решение может быть полезно не только для меня, что повлияло и на функциональность, и на архитектуру решения. Некоторые архитектурные решения могут показаться странными, но важно понимать, что это решение предназначено строго для тестирования и при разработке я руководствовался следующими принципами в ущерб некоторым архитектурным канонам:

- минимизация порога вхождения (целился в джунов).
- пользователь может расширить/изменить/поправить текущую реализацию;
- подключение/переход с минимальными телодвижениями;
- самый лучший тест - однострочный;

Данная статья получилась не маленькая, так как описывает почти все фичи библиотеки. Если вы больший сторонник чтения кода или вам интереснее посмотреть работоспособность решения, то милости прошу в [репу](https://github.com/touchbit/retrofit-veslo). Достаточно клонировать репозиторий и можно сразу [погонять](https://github.com/touchbit/retrofit-veslo#build-project-and-run-example-tests) тесты из модуля `example` (java 8+).

<spoiler title="Наглядный пример использования">

```java
public static class ExampleTests {

  public interface ExampleClient {
    @POST("/api/example")
    DualResponse<Pet, Err> get(@Query("id") String id);
  }

  private static final ExampleClient CLIENT = buildClient(ExampleClient.class);

  // Пример теста с выносом проверок в отдельный метод
  public void test1639328754880() {
    final Pet expected = new Pet().name("example");
    CLIENT.get("id_1").assertSucResponse(Asserter::assertGetPet, expected);
  }

  // Пример теста с проверкой непосредственно в тесте
  public void test1639328754881() {
    final Pet expected = new Pet().name("example");
    // Ответ содержит встроенные softly asserts 
    // для проверки статуса, заголовков и тела ответа. 
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


<spoiler title="Про модуль 'example'">

В `example` модуле уже настроена интеграция с allure и логирование каждого автотеста в **отдельный лог файл**. Стоит учесть, что бОльшая часть тестов падают умышленно для наглядности. По сути, если вам нужно внедрить API тесты, то вы можете взять код из модуля `example`, поправить `pom.xml` (groupId, artifactId, комментарии), определить API клиент, модели по образу и подобию с существующими, и приступать писать тесты.

</spoiler>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Modules</anchor>

## Модули

- **all** - всё и сразу (если вас не смущают лишние зависимости в проекте);
- **jackson** - работа с [Jackson2](https://github.com/FasterXML/jackson) моделями;
- **gson** - работа с [Gson](https://github.com/google/gson) моделями;
- **allure** - встроенные шаги для вызовов API с вложениями запроса/ответа;
- **bean** - модели данных со встроенной JSR 303 валидацией (jakarta bean validator);
- **core** - ядро решения. Подтягивается с модулями `all`, `jackson`, `gson`, `allure`;

Пример:

![](https://maven-badges.herokuapp.com/maven-central/org.touchbit.retrofit.veslo/parent-pom/badge.svg?style=plastic&subject=veslo.version)
```xml
<dependency>
    <groupId>org.touchbit.retrofit.veslo</groupId>
    <artifactId>all</artifactId>
    <version>${veslo.version}</version>
</dependency>
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Requests</anchor>

## Запросы к серверу

<anchor>anchor_ObjectRequestBody</anchor>

### `Object` в качестве тела запроса
Текущая реализация конвертеров позволяет использовать `Object` тип в `@Body` запроса. Механизм работает для любых наследников класса `ExtensionConverterFactory`. Для json моделей важно использовать аннотацию `@Headers` для автоматического выбора нужного конвертера на основе заголовка `Content-Type`. Механизм выбора нужного конвертера для тела запроса описан в разделе <a href="#anchor_Converters">конвертеры</a>.

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

`Object` позволяет отправлять в качестве тела запроса любую ересь.

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

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_ReflectQueryMap</anchor>

### Формирование параметров запроса (ReflectQueryMap)

Вы можете создать свой собственный `@QueryMap` для запросов, унаследовавшись от ReflectQueryMap, который получает пары ключ-значение **из переменных класса**. Этот механизм является дополнением к стандартной работе с `Map`. Если посмотреть реализацию `ReflectQueryMap`, то может пойти кровь из глаз, но к сожалению разработчики retrofit не предоставили API для кастомизации обработки `@QueryMap`. Ниже представлен лаконичный пример `QueryMap` с fluent методами с использованием `lombok` библиотеки.

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

**Использование в клиенте**

```java
public interface UserApi {

  @GET("/v2/user/login")
  @Description("Logs user into the system")
  AResponse<Suc, Err> login(@QueryMap() LoginUserQueryMap queryMap);
  //                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  //                        /v2/user/login?password=abc123&username=test
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

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_QueryParameterNullValueRule</anchor>

#### ReflectQueryMap - управление правилами обработки null значений

`ReflectQueryMap` позволяет задать правило обработки `null` значений.

**QueryParameterNullValueRule**

- `RULE_IGNORE` - Игнорировать параметры c значением `null` (по умолчанию)
- `RULE_NULL_MARKER` - заменить `null` на `null marker` -> `/api/call?foo=%00`
- `RULE_EMPTY_STRING` - заменить `null` на пустую строку -> `/api/call?foo=`
- `RULE_NULL_STRING` - заменить `null` на `null` строку -> `/api/call?foo=null`

```java
// для всех переменных класса
@QueryMapParameterRules(nullRule = RULE_NULL_MARKER)
public class LoginUserQueryMap extends ReflectQueryMap {

  // только для определенной переменной
  @QueryMapParameter(nullRule = RULE_EMPTY_STRING)
  private Object password;
}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_QueryParameterCaseRule</anchor>

#### ReflectQueryMap - управление правилами наименования параметров

По умолчанию используется имя переменной класса, но вы можете задать правило конвертации имён параметров запроса для всего класса, а так же явно задать имя параметра.

**QueryParameterCaseRule**

- CAMEL_CASE - camelCase (по умолчанию)
- KEBAB_CASE - kebab-case
- SNAKE_CASE - snake_case
- DOT_CASE - dot.case
- PASCAL_CASE - PascalCase

```java
// для всех переменных класса будет применен snake_case
@QueryMapParameterRules(caseRule = SNAKE_CASE)
public class LoginUserQueryMap extends ReflectQueryMap {

  // имя параметра запроса для определенной переменной
  @QueryMapParameter(name = "userName")
  private Object username;
}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Responses</anchor>

## Ответы от сервера

Тип возвращаемого ответа может быть представлен в клиентском интерфейсе в трёх вариантах:   

`DualResponse<Pet, Err>`, где 
- `Pet` модель ответа в случае успеха;
- `Err` модель ответа случае ошибки;

`DualResponse` обрабатывается `UniversalCallAdapterFactory` (смотреть подробности в разделе "<a href="#anchor_ClientMethodDescription">Клиент</a>").   

```java
public interface DualResponseClient {

  @GET("/api/pet")
  @EndpointInfo("Get pet by ID")
  DualResponse<Pet, Err> getPet(String id);
}
```

`AResponse<Pet, Err>` То же, что и `DualResponse`, только с allure интеграцией.   
`AResponse` обрабатывается `AllureCallAdapterFactory` (смотреть подробности в разделе "<a href="#anchor_ClientMethodDescription">Клиент</a>").   

```java
public interface AResponseClient {

  @GET("/api/pet")
  @EndpointInfo("Get pet by ID")
  AResponse<Pet, Err> getPet(String id);
}
```

Так же можно использовать модели и "простые" типы в качестве возвращаемого типа. В случае ошибок (status code 400+) конвертер попытается замапить тело ответа в возвращаемый тип и если не получилось, то вернется `null`.   
Например, если у нас есть API вызов `/api/live` (health check) который возвращает:
- Строковый `OK/ERROR` -> `String live();`
- Логический `true/false` -> `Boolean live();`
- JSON объект -> `LiveProbeModel live()`

```java
public interface Client {

  @GET("/api/live")
  @EndpointInfo("Service liveness probe")
  LiveProbeModel live();
}
```

`DualResponse` и `AResponse` унаследованы от `BaseDualResponse` и включают следующие методы:
- `assertResponse()` - для проверки ответа от сервера;
- `assertSucResponse()` - для проверки успешного ответа от сервера;
- `assertErrResponse()` - для проверки ошибочного ответа от сервера;
- `getErrDTO()` - возвращает модель тела ответа в случае ошибки (nullable);
- `getSucDTO()` - возвращает модель тела ответа в случае успеха (nullable);
- `getEndpointInfo()` - возвращает информацию о вызове метода API;
- `getResponse()` - возвращает сырой ответ представленный классом `okhttp3.Response` с читаемым телом;
- `getCallAnnotations()` - возвращает список аннотаций вызванного клиентского API метода:

Помимо работы с двумя моделями данных в ответе, классы `DualResponse` и `AResponse` предоставляют возможность мягких проверок с автозакрытием (Closeable). Данные методы на вход принимают consumer-функции одним из обязательных аргументов которой является `IResponseAsserter`.
По умолчанию используется `ResponseAsserter` для классов `DualResponse` и `AResponse`.

Тривиальный пример теста для `assertResponse`
```java
public class ExampleTests {

  public void example() {
    CLIENT.updatePet(new Pet().id(100L).name("example"))
        .assertResponse(asserter -> asserter
                .assertHttpStatusCodeIs(204)
                .assertHttpStatusMessageIs("No Content"));
  }
}
```

Исключение с накопленными ошибками. 
```text
veslo.BriefAssertionError: Collected the following errors:

HTTP status code
Expected: is  204
  Actual: was 200

HTTP status message
Expected: is  No Content
  Actual: was ОК
```

Методы `assertSucResponse` и `assertErrResponse` однотипные и на вход принимают `IResponseAsserter` и ожидаемую модель для проверки. По большей части они предназначены для выноса проверок в отдельные методы. Пример из `example` модуля:

[![](https://habrastorage.org/webt/xg/xh/6r/xgxh6rt8ahtwtcbaeiiktuojuwg.png)](https://habrastorage.org/webt/xg/xh/6r/xgxh6rt8ahtwtcbaeiiktuojuwg.png)

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_SoftlyAsserter</anchor>

#### Softly Asserter

Механизм мягких проверок в ответе от сервера реализован при помощи интерфейса `SoftlyAsserter`, который в свою очередь реализует интерфейс `AutoCloseable`.   
Это позволяет использовать `try-with-resources` оператор который гарантирует вызов метода `close()`. При вызове метода `close()` происходит "склеивание" накопленных исключений и бросок финального `AssertionError`.   

```java
public class Example {
    
  @Test
  public void test1643976548733() {
    try (SoftlyAsserter asserter = SoftlyAsserter.get()) {
      // сохраняем исключение
      asserter.softly(() -> { assert 1 == 0; });
      // сохраняем исключение
      asserter.softly(() -> { assert 1 != 1; }); 
    } // автоматически вызывается SoftlyAsserter.close()
  }
}
```

Метод `softly()` на вход принимает функциональный интерфейс `ThrowableRunnable` который предполагает возможность возникновения любых ошибок.   
Т.е. `softly(() -> { любой код запущенный тут })` и бросивший `Throwable` не прервет исполнение. `SoftlyAsserter` сохранит в себе брошенный `Throwable` до окончания выполнения `try-with-resources` блока или пока **явно** не будет вызван метод `.close()`.

Пример использования с `hamcrest`

```java
public class Example {
    
  @Test
  public void test1643977714496() {
    try (SoftlyAsserter asserter = SoftlyAsserter.get()) {
      asserter.softly(() -> assertThat("Body", "act", is("exp")));
    }
  }
}
```

Так же `SoftlyAsserter` содержит одноименный статический метод с встроенным `try-with-resources` блоком.

```java
public class Example {
    
  @Test
  public void test1643978036764() {
    softlyAsserter(asserter -> asserter
        .softly(() -> assertThat("Body", "act", is("exp")))
        .softly(() -> assertThat("Body", 1, is(2))));
  }
}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_ResponseAsserter</anchor>

#### Response Asserter
Любые наследники класса `BaseDualResponse` содержат в себе встроенные проверки реализующие интерфейс `IResponseAsserter`. По умолчанию используется `ResponseAsserter` который можно расширить дополнительными проверками или заменить собственной реализацией (смотреть <a href="#anchor_CustomResponseAssertion">"кастомизация встроенных проверок"</a>).

**Обычные методы проверки**
- `assertHttpStatusCodeIs(int)` - точное совпадение `HTTP status code`
- `assertHttpStatusMessageIs(String)` - точное совпадение `HTTP status message`
- `assertErrBodyIsNull()` - запрос завершился с ошибкой и тело отсутствует
- `assertErrBodyNotNull()` - запрос завершился с ошибкой и тело присутствует
- `assertIsErrHttpStatusCode()` - статус код в промежутке 300...599
- `assertSucBodyIsNull()` - запрос завершился успешно и тело отсутствует
- `assertSucBodyNotNull()` - запрос завершился успешно и тело присутствует
- `assertIsSucHttpStatusCode()` - статус код в промежутке 200...299

**Функциональные методы проверки**   

- `assertSucBody()` - проверяем модель (объект) успешного ответа;
- `assertErrBody()` - проверяем модель (объект) ответа в случае ошибки;

Ниже представлены методы, описание и примеры использования для `assertSucBody()`.   
В примерах функция одна и та же и представлена в двух вариантах:  

- `Lambda:` лямбда выражение для наглядности сигнатуры метода;   
- `Reference:` сокращенное представление лямбда выражения;   

Методов добавил "на все случаи жизни". В примерах я пометил звездочкой методы, которые рекомендую к использованию. Так же все примеры использования каждого конкретного метода указаны в javadoc класса `ResponseAsserter` и в классе `ExampleApiClientAssertions` в ядре (хоть это и не канонично).    

**`assertHeaders(Consumer<IHeadersAsserter>)`**   
Смотреть раздел <a href="#anchor_HeaderAsserter">Header Asserter</a>.

**`assertSucBody(Consumer<SUC_DTO>)`**   
Метод предоставляет только модель и ее методы   

Пример вызова метода модели без параметров   
Lambda: `.assertSucBody(pet -> pet.assertConsistency())`   
Reference: `.assertSucBody(Pet::assertConsistency)`   

Пример для встроенного в модель метода сверки  
Lambda: `.assertSucBody(pet -> pet.match(expected))`<sup>*</sup>   
Reference: отсутствует   

**`assertSucBody(BiConsumer<SUC_DTO, SUC_DTO>, SUC_DTO)`**   
Предоставляет только actual и expected модели для метода сверки.   
Пример для статического метода сверки моделей   
Lambda: `.assertSucBody((act, exp) -> Asserts.assertPet(act, exp), expected)`   
Reference: `.assertSucBody(Asserts::assertPet, expected)`   

Пример для встроенного в модель метода сверки моделей   
Lambda: `.assertSucBody((pet, exp) -> pet.match(exp), expected)`   
Reference: `.assertSucBody(Pet::match, expected)`<sup>*</sup>   

**`assertSucBody(BiConsumer<SoftlyAsserter, SUC_DTO>)`**   
Предоставляет ассертер и actual модель для проверки модели.   
Пример для статического метода проверки модели   
Lambda: `.assertSucBody((sa, act) -> Asserts.assertPet(sa, act, expected))`   
Reference: отсутствует   

**`assertSucBody(TripleConsumer<SoftlyAsserter, SUC_DTO, SUC_DTO>, SUC_DTO)`**   
Предоставляет ассертер, actual и expected модель для проверки.    
Lambda: `.assertSucBody((sa, act, exp) -> Asserts.assertPet(sa, act, exp), expected)`   
Reference: `.assertSucBody(Asserts::assertPet, expected)`<sup>*</sup>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_HeaderAsserter</anchor>

#### Header Asserter

Содержит общие методы проверки заголовков ответа:

- `assertHeaderNotPresent(String headerName)`
- `assertHeaderIsPresent(String headerName)`
- `assertHeaderIs(String headerName, String expected)`
- `assertHeaderContains(String headerName, String expected)`

А так же аналогичные методы проверки для заголовков:

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

Пример использования:
```java
public static class ExampleTests { 
  
  // явно в тесте
  public void test1639328754881() {
    CLIENT.get().assertResponse(respAsserter -> respAsserter
        .assertHeaders(headersAsserter -> headersAsserter
            .contentTypeIs("application/json; charset=utf-8")
            .assertHeaderIsPresent("X-Request-Id")
            .accessControlAllowOriginIs("*")));
  }

  // или вынести проверку заголовков в отдельный метод
  public void example1639330184783() {
    CLIENT.get().assertResponse(respAsserter -> respAsserter
            .assertHeaders(Asserts::assertHeaders));
  }

}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_BodyAsserter</anchor>

### Body Asserter

Я рекомендую выносить проверки в модель это и удобно и логично. Мыслю так: если у нас в модели есть метод `equals(Object)` для проверки равенства, то почему бы не иметь метод `match(Model)` для проверки соответствия? По-моему, звучит здраво, да и выглядит неплохо. Особенно, если через интерфейс сделать.

```java
public class Category implements AssertableModel<Category> {

    private Long id = null;
    private String name = null;

    @Override
    public Category match(Category expected) {
        try (final SoftlyAsserter asserter = SoftlyAsserter.get()) {
            asserter.softly(() -> assertThat(this.id()).as("category.id").isNotNull().isPositive());
            asserter.softly(() -> assertThat(this.name()).as("category.name").isEqualTo(expected.name()));
        }
        return this;
    }

}

public interface AssertableModel<DTO> {

  DTO match(DTO expected);

}
```

В результате мы можем проверять тело ответа вот так:
- `.assertSucBody(actual -> actual.match(expected))`
- `.assertSucBody(Category::match, expected)`

Если вам не хочется заморачиваться с <a href="#anchor_CustomResponseAssertion">кастомизацией встроенных проверок</a>, то в принципе можно вынести в модель и проверку всего ответа, если у вас обычный CRUD (отличный повод плюнуть автору в лицо за такую рекомендацию).

Проверка будет выглядеть вот так:
`.assertSucResponse(Category::assertPOST, expected);`

```java
public class Category implements AssertableModel<Category> {

  private Long id = null;
  private String name = null;

  @Override
  public Category match(Category expected) {
      // collapsed
  }

  public static void assertGET(ResponseAsserter<Category, ?, HeadersAsserter> asserter,
                               Category expected) {
    asserter.assertHttpStatusCodeIs(200).assertSucBody(actual -> actual.match(expected));
  }

  public static void assertPOST(ResponseAsserter<Category, ?, HeadersAsserter> asserter, 
                                Category expected) {
    asserter.assertHttpStatusCodeIs(200).assertSucBody(actual -> actual.match(expected));
  }

  public static void assertPATCH(ResponseAsserter<Category, ?, HeadersAsserter> asserter) {
    asserter.assertHttpStatusCodeIs(204).assertSucBodyIsNull().assertErrBodyIsNull();
  }

}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_CustomResponseAssertion</anchor>

### Кастомизация встроенных проверок

`DualResponse` содержит встроенные проверки, которые можно расширить или переопределить. Для этого вам нужно создать свой `CustomResponse`, который должен быть унаследован от `BaseDualResponse` и реализовать в нем методы:

- `public IResponseAsserter getResponseAsserter();` где `IResponseAsserter` реализация ассертера для всего ответа от сервера. Лучше унаследоваться от `ResponseAsserter`.
- `public IHeadersAsserter getHeadersAsserter();` где `IHeadersAsserter` реализация ассертера для заголовков. Лучше унаследоваться от `HeadersAsserter`.

<spoiler title="Для наглядности">

[![](https://habrastorage.org/webt/8v/dy/-k/8vdy-kp5hkjo3rcohz13-0ppegu.png)](https://habrastorage.org/webt/8v/dy/-k/8vdy-kp5hkjo3rcohz13-0ppegu.png)

</spoiler>

Далее, при создании <a href="#anchor_Client">клиента</a> retrofit, вам необходимо явно указать, какой ответ следует использовать при создании экземпляра `IDualResponse`.   
Метод `.addCallAdapterFactory(CallAdapter.Factory)`:   

- `new UniversalCallAdapterFactory(CustomResponse::new)` default
- `new AllureCallAdapterFactory(CustomResponse::new)` allure

Данный подход позволит вам вынести пул однотипных проверочных методов в отдельную реализацию `IResponseAsserter`.
Например `PetStoreAsserter`:

```java
public static class ExampleTests { 
    
  public void test1639328754881() {
    Pet expected = Pet.generate();
    CLIENT.addPet(expected);
    CLIENT.getPet(expected.getId())
        // по аналогии с assertResponse(Consumer)
        .assertResponse(asserter -> asserter.assertGetPet(expected)) 
        // или c assertSucResponse(BiConsumer, SUC_DTO)
        .assertResponse(PetStoreAsserter::assertGetPet, expected);
  }
}
```

Если не хочется заморачиваться с выносом проверок в ассертеры, то можно вынести просто в `CustomResponse`. 
В таком случае у вас есть выбор, или пользоваться внутренним `ResponseAsserter`, или использовать свое собственное решение мягких проверок (если они вам вообще нужны).

```java
public static class ExampleTests { 
    
  public void test1639328754881() {
    Pet expected = Pet.generate();
    CLIENT.addPet(expected);
    CLIENT.getPet(expected.getId()).assertGetPet(expected);
  }
}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Models</anchor>

## Модели

<anchor>anchor_RawBody</anchor>

## RawBody модель

`RawBody` может применяться для запросов и ответов. Хранит тело в байтовом представлении.   
Позволяет отправлять строку "как есть" в обход MIME конвертеров. В большей степени подходит для запросов, нарушающих контракт. Например, битый JSON.   
Не чувствителен к ответам без тела (HTTP status code 204/205). Т.е. при использовании в ответах от сервера, не может быть `null` (формируется всегда).   
Содержит встроенные проверки:  

- `assertBodyIsNotNull()`
- `assertBodyIsNull()`
- `assertBodyIsNotEmpty()`
- `assertBodyIsEmpty()`
- `assertStringBodyContains(String... expectedStrings)`
- `assertStringBodyContainsIgnoreCase(String... expectedStrings)`
- `assertStringBodyIs(String expected)`
- `assertStringBodyIsIgnoreCase(String expected)`

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_ResourceFile</anchor>

## ResourceFile модель

Предоставляет возможность чтения файлов из ресурсов проекта.  
Добавлено для более удобного использования в запросах API для тех, кому приходится мучиться с файлами.   
Имеет встроенные проверки на существование файла и его читабельность.  
В скором времени появится [небольшая доработка](https://github.com/touchbit/retrofit-veslo/issues/4) с возможностью модификации текстовых файлов через fluent API.   

```java
public class AddPetTests extends BasePetTest {

  @Test
  public void test1640455066880() {
    final ResourceFile resourceFile = new ResourceFile("PetPositive.json");
    PET_API.addPet(resourceFile); 
  }
}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Jackson2Model</anchor>

## Jackson2 модели

На момент написания статьи речь идет о `Jackson2` версии `2.13.1`.   
В отличие от Gson библиотека `Jackson2` позволяет обрабатывать случай, когда ответ от сервера содержит лишние поля, не выдавая ошибку при конвертации.   
Именно по этой причине я **настоятельно** рекомендую использовать `Jackson2` для ваших json/yaml моделей.  

Обработка лишних полей уже реализована в классе `JacksonModelAdditionalProperties` и вам достаточно унаследовать модель от этого класса.   
Если контракт изменился и пришел ответ с новыми полями, то мы можем это проверить при помощи базового метода `assertNoAdditionalProperties()` и получить **вменяемую** ошибку.   

```text
The presence of extra fields in the model: Pet
Expected: no extra fields
  Actual: {nickname=Puffy}
```

Рекомендуется вынести проверки контракта в отдельные тесты.

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

Так же `additionalProperties` позволяют отправлять "битые" данные. Например, в модели `Pet` у нас есть поле `id` с типом `Long` и мы ходим проверить, как себя поведет сервер, если отправить число большее чем `Long.MAX_VALUE` или строку вместо `Long`.

```java
public class AddPetTests {

  @Test 
  public void test1640455066881() {
    Pet pet = generatePet();
    // id > Long.MAX_VALUE
    pet.id(null).additionalProperty("id", new BigInteger(Long.MAX_VALUE + "000"));
    // id != Long
    pet.id(null).additionalProperty("id", "fooBar");
    PET_API.addPet(pet);
  }
}
```

Кто-то скажет, что преимущество Gson в том, что не надо использовать аннотации для каждого поля. Однако `Jackson2` такую возможность тоже предоставляет, но только не по умолчанию. Чтоб не вешать на каждое поле аннотацию `@JsonProperty` достаточно навесить на класс аннотацию `@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)`. Так же мы можем управлять правилами наименования полей при помощи аннотации `@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)`.

Ниже пример модели с использованием lombok библиотеки (рекомендую такой подход).

```java
// копипастный блок с аннотациями
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonAutoDetect(creatorVisibility = ANY, fieldVisibility = ANY)
public class Tag extends JacksonModelAdditionalProperties<Tag> {

    private Long id = null;
    private String name = null;

}
```

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_BeanValidationModel</anchor>

## Jakarta Bean Validation

Наследование моделей от интерфейса `BeanValidationModel` позволяет проверять данные на соответствие контракту с помощью аннотаций (спецификации) JSR 303 (метод `assertConsistency()`).

Рекомендуется вынести проверки контракта в отдельный тест.

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

Пример модели c JSR 303 аннотациями и исключением

[![](https://habrastorage.org/webt/jo/xd/qe/joxdqemtiw9qg2ubbgivup2nida.png)](https://habrastorage.org/webt/jo/xd/qe/joxdqemtiw9qg2ubbgivup2nida.png)

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Client</anchor>

## Клиент

Для удобного создания тестового клиента добавлены вспомогательные классы:

- `TestClient` - из модуля `core`. Ничего не знает про jackson, gson и allure.
- `JacksonTestClient` - из модуля `jackson`. Строит клиент с jackson конвертером. Ничего не знает про allure.
- `GsonTestClient`- из модуля `gson`. Строит клиент с gson конвертером. Ничего не знает про allure.
- `Veslo4Test` - из модуля `all`. Содержит методы построения клиентов для различных конвертеров.

Тестовый клиент по умолчанию 
- следует по редиректам, в том числе https -> http
- игнорирует ошибки сертификата
  - несоответствия домена
  - самоподписной сертификат
  - протухший сертификат
- `CompositeInterceptor` с/без allure интеграцией (`Veslo4Test`)

<anchor>anchor_ClientMethodDescription</anchor>

<spoiler title="Более подробное описание тестового клиента">

Ниже представлен пример метода для создания API клиента (копипастнуть и удалить ненужное).

```java
public class BaseTest {

  protected static final PetApi PET_API = buildClient(PetApi.class);

  private static <C> C buildClient(final Class<C> cliClass) {
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
- `#followRedirects()` - автоматический переход по редирект статусам (301, 302...);
- `#followSslRedirects()` - автоматический переход httpS <-> http по редирект статусам;
- `#hostnameVerifier()` - если вызываемый домен не соответствует домену в сертификате (для тестового окружения);
- `#sslSocketFactory()` - вместо добавления самоподписанных сертификатов в keystore (для тестового окружения);
- `#addNetworkInterceptor()` - добавление сетевого перехватчика (важно использовать именно этот метод, иначе не будут перехватываться редиректы). `CompositeInterceptor` описан <a href="#anchor_CompositeInterceptor">тут</a>;
- `#addConverterFactory()` - добавить фабрику конвертеров для сериализации/десериализации объектов;
  - `JacksonConverterFactory` для Jackson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
  - `GsonConverterFactory` для Gson моделей + конвертеры по умолчанию из `ExtensionConverterFactory`;
  - `ExtensionConverterFactory` для примитивных/ссылочных "простых" типов (смотреть раздел <a href="#anchor_Converters">конвертеры</a>);
- `#addCallAdapterFactory()` - поддержка специфического возвращаемого типа в методе API клиента, отличных от `retrofit2.Call`;
  - `UniversalCallAdapterFactory` - фабрика для `DualResponse`;
  - `AllureCallAdapterFactory` - фабрика для `AResponse` с поддержкой allure шагов;

Если вы хотите использовать allure, то нужно добавить в зависимости allure модуль. В таком случае возвращаемый класс будет `veslo.AResponse`. Так же нужно реализовать и добавить в okhttp клиент (`#addNetworkInterceptor()`) свой собственный `CompositeInterceptor` и зарегистрировать `AllureAction.INSTANCE` как в <a href="#anchor_CompositeInterceptorExample">примере</a>.   
Настоятельно рекомендуется использовать аннотацию `io.qameta.allure.Description`.

```java
public interface AllureCallAdapterFactoryClient {
    @GET("/api/example")
    @Description("Get pet")
    AResponse<Pet, Err> get();
}
```

Если вам allure не нужен, то возвращаемый класс будет `veslo.client.response.DualResponse`. Так же настоятельно рекомендуется использовать аннотацию `veslo.client.EndpointInfo`.

```java
public interface UniversalCallAdapterFactoryClient {
    @GET("/api/example")
    @EndpointInfo("Get pet")
    DualResponse<Pet, Err> get();
}
```

</spoiler>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_CompositeInterceptor</anchor>

## Сетевой перехватчик (CompositeInterceptor)

Главной особенностью `CompositeInterceptor` является возможность управления последовательностью вызовов обработчиков (далее `Action`) для запросов и ответов, что недоступно в базовой реализации `retrofit`. Другими словами, вы сами выбираете порядок применяемых `Action` отдельно для Запроса и Ответа.   
`Action` может реализовывать три интерфейса:

- `RequestInterceptAction` для обработки `okhttp3.Chain` и `okhttp3.Request`
- `ResponseInterceptAction` для обработки `okhttp3.Response` и `java.lang.Throwable` (сетевые ошибки)
- `InterceptAction` включает в себя `RequestInterceptAction` и `ResponseInterceptAction`

<anchor>CompositeInterceptorExample</anchor>

```java
public class PetStoreInterceptor extends CompositeInterceptor {

  public PetStoreInterceptor() {
    super(LoggerFactory.getLogger(PetStoreInterceptor.class));
    
    // строгий порядок обработки запроса
    withRequestInterceptActionsChain(
            AuthAction.INSTANCE,
            CookieAction.INSTANCE,
            LoggingAction.INSTANCE, 
            AllureAction.INSTANCE);
    
    // строгий порядок обработки ответа
    withResponseInterceptActionsChain(
            LoggingAction.INSTANCE,
            AllureAction.INSTANCE,
            CookieAction.INSTANCE);
  }
}
```

Существующие actions:

`CookieAction` - управление cookie-заголовками в потоке;   
`LoggingAction` - логирует запрос/ответ или транспортную ошибку двумя `LogEvent`;

<spoiler title="Пример из лог файла">

```text
03:40:37.675 INFO  - API call: Logs user into the system
03:40:37.680 INFO  - REQUEST:
GET https://petstore.swagger.io/v2/user/login?password=abc123&username=test
Headers:
  Host: petstore.swagger.io
  Connection: Keep-Alive
  Accept-Encoding: gzip
  User-Agent: okhttp/3.14.9
Body: (absent)

03:40:37.831 INFO  - RESPONSE:
200 https://petstore.swagger.io/v2/user/login?password=abc123&username=test
Headers:
  date: Tue, 01 Feb 2022 00:40:37 GMT
  content-type: application/json
  access-control-allow-origin: *
  access-control-allow-methods: GET, POST, DELETE, PUT
  access-control-allow-headers: Content-Type, api_key, Authorization
  x-expires-after: Tue Feb 01 01:40:37 UTC 2022
  x-rate-limit: 5000
  server: Jetty(9.2.9.v20150224)
  Content-Length: -1
Body: (78-byte body)
  {"code":200,"type":"unknown","message":"logged in user session:1643676037840"}

```

</spoiler>

`AllureAction` - добавляет в шаг вложения запроса и ответа;

<spoiler title="Пример allure отчета">

[![](https://habrastorage.org/webt/qg/ub/6t/qgub6tsi7xxvdowijfe20u82ioc.png)](https://habrastorage.org/webt/qg/ub/6t/qgub6tsi7xxvdowijfe20u82ioc.png)

</spoiler>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Converters</anchor>

## Конвертеры

В классе `ExtensionConverterFactory` и его наследниках `JacksonConverterFactory` и `GsonConverterFactory` реализован механизм выбора нужного конвертера для преобразования запросов и ответов.
Конвертер выбирается в следующей последовательности:

* по аннотации вызываемого клиентского метода: `@Converters`, `@RequestConverter`, `@ResponseConverter`;
* по "сырому" типу тела (`RawBody`, `File`, `ResourceFile`, `Byte[]`, `byte[]`);
* по java пакету (на момент написания статьи это строгое соответствие, но планируется поддержка wildcard);
* по `Content-Type` заголовку (MIME);
* по примитивному/ссылочному java типу (`Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short`, `String`);

Примеры для различных java типов
- `AResponse<String, String> addPet(@Body String body);`
- `AResponse<RawBody, Err> addPet(@Body RawBody body);`
- `AResponse<Byte[], Err> addPet(@Body byte[] body);`
- `AResponse<File, Err> addPet(@Body File body);`
- `AResponse<Pet, Err> addPet(@Body ResourceFile body);` (только для запросов)

По аналогии с существующими фабриками `JacksonConverterFactory` и `GsonConverterFactory` Вы можете реализовать свою универсальную фабрику и зарегистрировать или перерегистрировать конвертеры под ваши нужды.

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

При осуществлении **запросов** содержащих тело, предполагается наличие аннотации `retrofit2.http.Headers` c заголовком `Content-Type`. 

```java
public interface PetApi {
  @POST("/api/example")
  @Headers({"Content-Type: application/json"})
  AResponse<Pet, Err> addPet(@Body() Object pet);
}
```

Если у вас `Content-Type` заголовок заполняются в рантайме через мапку, то фабрика **не найдет конвертер** для MIME типа.
```java
public interface PetApi {
  @POST("/api/example")
  AResponse<Pet, Err> addPet(@HeaderMap Map<String, String> headers, @Body() Object pet);
  // не перехватываются      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
}
```

К сожалению это особенность реализации `retrofit` и у нас нет возможности получить значение переменной метода. В таком случае можно создать свой конвертер по аналогии с примером `CustomConverterFactory` с указанием конвертера для пакета или java типа.
Так же можно явно указать конвертер при помощи аннотации `@RequestConverter`, `@ResponseConverter`. Данная функция как раз добавлена для обработки частных случаев, когда фабрика не может определить какой конвертер использовать.

```java
public interface PetApi {
    
    @POST("/v2/pet")
    @RequestConverter(bodyClasses = {Pet.class}, converter = JacksonConverter.class)
    @ResponseConverter(bodyClasses = {Pet.class, Err.class}, converter = JacksonConverter.class)
    AResponse<Pet, Err> addPet(@Body Object body, @HeaderMap Map<String, String> headers);

}
```

Если поле `bodyClasses` оставить пустым, то конвертер будет применен ко всем моделям из сигнатуры метода. 
Например `@ResponseConverter(converter = JacksonConverter.class)` будет применен для конвертации тела ответа в модель `Pet` или `Err` (в зависимости от статуса).

В случае, если конвертер не найден, то будет брошен `ConverterNotFoundException` следующего содержания:

```text
veslo.ConverterNotFoundException: Converter not found
Transport event: RESPONSE
Content-Type: null
DTO type: class org.touchbit.retrofit.veslo.example.model.Status

SUPPORTED RESPONSE CONVERTERS:
<Список встроенных конвертеров>
<Смотреть спойлер ниже>
```

Из исключения видно, что конвертер не найден при попытке конвертации тела ответа в класс `org.touchbit.retrofit.veslo.example.model.Status`. Это json модель, однако заголовок `Content-Type` в ответе отсутствует (`null`). Можем заводить баг на отсутствие MIME заголовка. Если это ожидаемое поведение (такое тоже бывает), то в вашем `CustomConverterFactory` нужно дополнительно зарегистрировать `JacksonConverter` для конвертации тела ответа, если заголовок `Content-Type` отсутствует:   
`registerMimeConverter(JacksonConverter.INSTANCE, ContentType.NULL);`.

<spoiler title="Список встроенных конвертеров в `JacksonConverterFactory` (для примера)">

```text
Raw converters:
  veslo.client.converter.defaults.RawBodyTypeConverter
      byte[]
      java.io.File
      java.lang.Byte[]
      veslo.client.model.RawBody
      veslo.client.model.ResourceFile
Content type converters:
  veslo.JacksonConverter
      application/json
      application/json; charset=utf-8
      text/json
      text/json; charset=utf-8
Java type converters:
  veslo.JacksonConverter
      java.util.List
      java.util.Map
  veslo.client.converter.defaults.JavaPrimitiveTypeConverter
      boolean
      byte
      char
      double
      float
      int
      long
      short
  veslo.client.converter.defaults.JavaReferenceTypeConverter
      java.lang.Boolean
      java.lang.Byte
      java.lang.Character
      java.lang.Double
      java.lang.Float
      java.lang.Integer
      java.lang.Long
      java.lang.Short
      java.lang.String
```

</spoiler>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Usefulness</anchor>

## Полезности

<anchor>anchor_UsefulnessLogging</anchor>

<spoiler title="Логирование каждого теста в отдельный лог файл">

В модуле `example` реализовано логирование каждого теста в отдельный лог файл. Это предполагает уникальные имена тестовых методов. Я использую `LiveTemplates` (об этом ниже) для создания тестовых методов в формате `test<timestamp>`, например `test1640455066880()`. Я всегда использую аннотацию `@DisplayName("")` с вменяемым описанием теста, чтобы как раз не пыжиться с названием тестового метода в классе. Если вы не сторонних наименования тестов в подобном формате, то можно дополнительно использовать пакет тестового класса в имени файла лога. Смотреть метод `JunitExecutionListener.executionStarted()`.   
Детально вдаваться в подробности реализации логирования я не буду, но готовое решение вы можете копипастнуть из модуля `example` и модифицировать под свои нужды.   

[![](https://habrastorage.org/webt/vq/jh/ys/vqjhysyzsx3jklt3tw6tef3d4a0.png)](https://habrastorage.org/webt/vq/jh/ys/vqjhysyzsx3jklt3tw6tef3d4a0.png)

<spoiler title="Пример лога теста">

Пример лог файла теста `AddPetTests.test1640455066880()`   
```text
14:38:04.786 INFO  - Test started: Checking the Pet model contract (PropertyNamingStrategy.SnakeCaseStrategy)
14:38:05.962 INFO  - API call: Logs user into the system
14:38:05.974 INFO  - REQUEST:
GET https://petstore.swagger.io/v2/user/login?password=abc123&username=test
Headers:
  Host: petstore.swagger.io
  Connection: Keep-Alive
  Accept-Encoding: gzip
  User-Agent: okhttp/3.14.9
Body: (absent)

14:38:06.112 INFO  - RESPONSE:
200 https://petstore.swagger.io/v2/user/login?password=abc123&username=test
Headers:
  date: Fri, 04 Feb 2022 11:38:06 GMT
  content-type: application/json
  access-control-allow-origin: *
  access-control-allow-methods: GET, POST, DELETE, PUT
  access-control-allow-headers: Content-Type, api_key, Authorization
  x-expires-after: Fri Feb 04 12:38:06 UTC 2022
  x-rate-limit: 5000
  server: Jetty(9.2.9.v20150224)
  Content-Length: -1
Body: (78-byte body)
  {"code":200,"type":"unknown","message":"logged in user session:1643974686051"}

14:38:06.115 INFO  - Response check completed without errors.
14:38:06.206 INFO  - API call: Add a new pet to the store
14:38:06.817 INFO  - REQUEST:
POST https://petstore.swagger.io/v2/pet
Headers:
  Content-Type: application/json
  Content-Length: 291
  Host: petstore.swagger.io
  Connection: Keep-Alive
  Accept-Encoding: gzip
  User-Agent: okhttp/3.14.9
  api_key: special-key
Body: (291-byte body)
  {
    "id" : 496977483,
    "photoUrls" : [ "www.shelba-bogisich.co.uk" ],
    "category" : {
      "id" : 1082547212,
      "name" : "erjtrtczfi"
    },
    "name" : "gorilla",
    "tags" : [ {
      "id" : 1231521466,
      "name" : "ybabllzaxw"
    }, {
      "id" : 2012930363,
      "name" : "vgobbqhmxu"
    } ]
  }

14:38:07.132 INFO  - RESPONSE:
200 https://petstore.swagger.io/v2/pet
Headers:
  date: Fri, 04 Feb 2022 11:38:07 GMT
  content-type: application/json
  access-control-allow-origin: *
  access-control-allow-methods: GET, POST, DELETE, PUT
  access-control-allow-headers: Content-Type, api_key, Authorization
  server: Jetty(9.2.9.v20150224)
  Content-Length: -1
Body: (209-byte body)
  {"id":496977483,"category":{"id":1082547212,"name":"erjtrtczfi"},"name":"gorilla","photoUrls":["www.shelba-bogisich.co.uk"],"tags":[{"id":1231521466,"name":"ybabllzaxw"},{"id":2012930363,"name":"vgobbqhmxu"}]}

14:38:07.306 INFO  - Response check completed without errors.
14:38:07.386 INFO  - SUCCESSFUL: Checking the Pet model contract (PropertyNamingStrategy.SnakeCaseStrategy)
  file:///Users/ra/repo/src/github.com/touchbit/retrofit-veslo/example/target/logs/test/test1640455066880.log
```

</spoiler>

При запуске теста в Intellij IDEA вы получите следующий вывод в случае падения    
[![](https://habrastorage.org/webt/ez/fj/u4/ezfju4fb9pg_fowurwadhylmlam.png)](https://habrastorage.org/webt/ez/fj/u4/ezfju4fb9pg_fowurwadhylmlam.png)

Так же в `JunitExecutionListener` реализовано добавление лога теста в allure отчет. Смотреть метод `addTestLogAttachment()`.   
[![](https://habrastorage.org/webt/7y/nv/ns/7ynvnsvoy7hneulnpnobtr-vh78.png)](https://habrastorage.org/webt/7y/nv/ns/7ynvnsvoy7hneulnpnobtr-vh78.png)

</spoiler>

<anchor>anchor_UsefulnessLiveTemplates</anchor>

<spoiler title="Шаблонизация тестовых методов">

Прежде чем писать автотесты, я предпочитаю накидать чек-лист. В самом простом варианте, это текстовый файл вида:

```text
Успешное аутентификация пользователя в магазине, если статус пользователя 'NEW'
Успешное аутентификация пользователя в магазине, если статус пользователя 'UNCONFIRMED'
Успешное аутентификация пользователя в магазине, если статус пользователя 'MEMBER'
Ошибка (403) при аутентификации пользователя в магазине, если статус пользователя 'BLOCKED'
Ошибка (400) при аутентификации пользователя в магазине, если username не передан
```

Кто-то скажет `многабуков`, но я сторонник предельной ясности в отчете и в логах теста.

Для ускорения создания тест-метода я использую `LiveTemplates` (IntelliJ IDEA). Копирую строку из чек-листа и набираю в классе аббревиатуру нужного шаблона. Далее по шаблону создается тестовый метод, который я уже реализую. Выглядит это вот так:   
[![](https://habrastorage.org/webt/n0/nx/86/n0nx86tplplqjow5casy8mcvx6a.gif)](https://habrastorage.org/webt/fd/n7/ky/fdn7kymbug3mq5iozdtscu80j0c.gif)

Сам шаблон выглядит вот так:   
[![](https://habrastorage.org/webt/45/cb/i6/45cbi6ip2x5pr044mltk6m86ud8.png)](https://habrastorage.org/webt/45/cb/i6/45cbi6ip2x5pr044mltk6m86ud8.png)

Тело шаблона:   
```text
@org.junit.jupiter.api.Test
@org.junit.jupiter.api.DisplayName("$DESCRIPTION$")
public void test$ID$() {
    $END$
}
```

Настройки шаблона:   
```text
DESCRIPTION - clipboard()
ID - groovyScript("return new Date().getTime();")
```

</spoiler>

<anchor>anchor_UsefulnessPlugins</anchor>

<spoiler title="Плагины IntelliJ IDEA">

**RoboPOJOGenerator**   
[Homepage](https://plugins.jetbrains.com/plugin/8634-robopojogenerator)   
Генерирует POJO классы по JSON структуре под различные фрэймворки.   
File -> New -> Generate POJO from JSON   
[![](https://habrastorage.org/webt/p9/9k/g2/p99kg2mex26fgqctkhq63zi8qek.png)](https://habrastorage.org/webt/p9/9k/g2/p99kg2mex26fgqctkhq63zi8qek.png)

**String Manipulation**   
[Homepage](https://plugins.jetbrains.com/plugin/2162-string-manipulation)   
Мощнейший плагин по форматированию строк. Возможности просто колоссальные. Больше примеров смотрите на странице плагина.   
[![](https://habrastorage.org/webt/kw/ck/_z/kwck_z3t5ct4xwsnxgrsykfxqxs.gif)](https://habrastorage.org/webt/kw/ck/_z/kwck_z3t5ct4xwsnxgrsykfxqxs.gif)   

[![](https://habrastorage.org/webt/x9/sz/cs/x9szcspxzqs5b-pucbzefz6fwcq.gif)](https://habrastorage.org/webt/x9/sz/cs/x9szcspxzqs5b-pucbzefz6fwcq.gif)   

<spoiler title="Рекомендуется настройка хоткеев">

[![](https://habrastorage.org/webt/jj/cs/av/jjcsavmmkrrep6ppl9efr_tpqok.png)](https://habrastorage.org/webt/jj/cs/av/jjcsavmmkrrep6ppl9efr_tpqok.png)

</spoiler>

**Rainbow Brackets**   
[Homepage](https://plugins.jetbrains.com/plugin/10080-rainbow-brackets)   
Цветовая дифференциация переменных, скобок, тегов и т.д.
[![](https://habrastorage.org/webt/zx/h6/sv/zxh6svlpm1oevhxkygxm3w5akgg.png)](https://habrastorage.org/webt/zx/h6/sv/zxh6svlpm1oevhxkygxm3w5akgg.png)

**Archive Browser**   
[Homepage](https://plugins.jetbrains.com/plugin/9491-archive-browser)   
Позволяет просматривать архивы непосредственно в проекте.   
[![](https://habrastorage.org/webt/be/2v/pk/be2vpkqjqawsmhcz35js2u2be38.png)](https://habrastorage.org/webt/be/2v/pk/be2vpkqjqawsmhcz35js2u2be38.png)

**Fluent setter generator**   
[Homepage](https://plugins.jetbrains.com/plugin/7903-fluent-setter-generator)      
Старенький, но все еще безотказно работающий генератор fluent сеттеров. Это на случай, если вы все еще не пользуетесь lombok.   
[![](https://habrastorage.org/webt/uw/md/jw/uwmdjwgih5avtb5gsyykkczppcu.gif)](https://habrastorage.org/webt/uw/md/jw/uwmdjwgih5avtb5gsyykkczppcu.gif)

</spoiler>

<a href="#anchor_TOC">К содержанию</a>

<anchor>anchor_Finally</anchor>

## В заключение

Какие для себя выводы я сделал

1. Решился на OpenSource проект - делай для людей, а не для себя.
2. OpenSource библиотеку писать значительно сложнее, чем для личного пользования. Тем более в одно лицо.
3. Продумывать архитектуру решения с учетом конечного пользователя и возможного развития продукта крайне занимательно.
4. Хоть я парень опытный, но в разработке юнит-тестов дилетант.
5. Инженерам по автоматизации тестирования обязательно нужно уметь писать юнит-тесты. А лучше писать их на постоянной основе в помощь разработчикам.
6. Стремиться в 100% code coverage дело крайне полезное, особенно если пишешь решение на базе другого.
7. На практике юнит-тестами все не проверишь. Обязательно что-нибудь вылезет на этапе функционального/интеграционного тестирования, причем в работе со сторонними библиотеками.
8. Сторонние библиотеки могут работать непредсказуемо и существенно изменяться на минорных версиях.
9. Переделать архитектуру, когда решение почти готово - больно, но конечный результат вдохновляет.
10. Возможно стоило переписать сам `retrofit` клиент, а не делать расширение.
11. Проверяй свое решение на всех поддерживаемых версиях java.
12. С лицензированием кода не все так просто, но процедура разовая.
13. Пиши примеры и документацию как для дефективных, потом сам себе спасибо скажешь.
14. Фиксируй идеи в трекере, а не на бумажке, иначе любимый питомец бумажку съест.
15. Писать подобные решения возможно скорее будучи безработным (как раз поэтому и написал).

Разработка данной библиотеки с учетом реализации, автотестов, документации, примеров и данной статьи заняло примерно 2 месяца чистого рабочего времени. И это определенно не предел, так как продукт будет в дальнейшем развиваться в силу моей сферы деятельности и позиции.   
Из глобальных планов - добавить конвертер для xml и protobuf, продумать удобную работу с SOAP конвертом и сделать видео гайд.   
Если у вас есть идеи как можно улучшить данное решение, то прошу [сюда](https://github.com/touchbit/retrofit-veslo/issues).   
Если вы хотите позаимствовать себе какие-то части данной библиотеки, то не стесняйтесь, так как библиотека распространяется под лицензией [Apache 2.0](https://github.com/touchbit/retrofit-veslo/blob/main/LICENSE).   

Напоследок список возможно полезных ресурсов:

- [Автор сего решения](https://shaburov.github.io/)
- [Git репозиторий проекта](https://github.com/touchbit/retrofit-veslo)
- [Проект в maven central](https://mvnrepository.com/artifact/org.touchbit.retrofit.veslo)
- [TG группа: Veslo Q&A (retrofit)](https://t.me/veslo_retrofit)
- [TG группа: QA — Automation](https://t.me/qa_automation)
- [TG группа: QA — русскоговорящее сообщество](https://t.me/qa_ru)
- [TG группа: QA — вакансии и аналитика рынка вакансий](https://t.me/qa_jobs)
- [TG группа: QA — Резюме](https://t.me/qa_resumes)

Вот, собственно, и все. Буду рад фидбэку.

<a href="#anchor_TOC">К содержанию</a>
