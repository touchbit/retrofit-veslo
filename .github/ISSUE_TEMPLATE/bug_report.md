---
name: Bug report about: A reproducible problem title: ''
labels: bug assignees: ''

---
Descriptions in English and Russian are allowed. The error description should contain answers to the questions what ?,
where ?, provided ?, what was expected? and the actual problem in the form of a stack trace and/or a screenshot and/or
an incorrect state message. Indicate the version of this library as well as the retrofit and okhttp3 libraries. It is
desirable to attach the result of calling the command `mvn dependency: tree` or` gradle -q dependencies`.

Допускается описание на английском и русском языках. Описание ошибки должно содержать ответы на вопросы что?, где?, при
условии?, что ожидалось? и фактическую проблему в виде стэктрэйса и/или скриншота и/или сообщения о некорректном
состоянии. Указать версию данной библиотеки а так же библиотек retrofit и okhttp3. Желательно приложить результат вызова
команды `mvn dependency:tree` или `gradle -q dependencies`.

Example/Пример:
> **Title:** MismatchedInputException: No content to map due to end-of-input
> **Description:**
> - **EN**: An exception is thrown when converting a response from the server using JacksonConverter, if the response from the server contains an empty body and the header `Content-Length: -1`.
> - **RU**: Исключение возникает при конвертации ответа от сервера с помощью JacksonConverter, если ответ от сервера содержит пустое тело и заголовок `Content-Length: -1`.
> ```text
> veslo.ConvertCallException: 
> Response body not convertible to type class veslo.model.ErrorDTO
> No content to map due to end-of-input at [Source: (String)""; line: 1, column: 0]
> 	at veslo.JacksonConverter$2.convert(JacksonConverter.java:137)
> 	at veslo.JacksonConverterUnitTests.test1639065954858(JacksonConverterUnitTests.java:235)
> ```
> org.touchbit.retrofit.veslo - 1.0.0
> com.squareup.okhttp3 - 3.14.9
> com.squareup.retrofit2 - 2.9.0
