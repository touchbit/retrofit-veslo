---
name: Feature request
about: Suggest an idea
title: Support okhttp3.Response return type
labels: enhancement
assignees: shaburov

---

Descriptions in English and Russian are allowed. Describe one or more use cases for the new functionality. If this is a fundamentally new mechanism or an extension of existing functions, then attach an example code as you see the mechanics of using.

Допускается описание на английском и русском языках. Опишите один или несколько вариантов использования новой функции. Если это принципиально новый механизм или расширение существующих функций, приложите пример кода, как вы видите механизм использования.

Example/Пример:
> **Title:** Support okhttp3.Response return type
> **Description:**
> - **EN**: During testing, calls are made to live/ready probes to obtain information about the state of the service under test. Different data (body/headers) are returned in different service states and it would be convenient to use raw `okhttp3.Response` instead of `DualResponse <RawBody, RawBody>`.
> - **RU**: При тестировании осуществляются вызовы к live/ready пробам для получения информации о состоянии тестируемого сервиса. В разных состояниях сервиса возвращаются разные данные (body/headers) и было бы удобно использовать сырой `okhttp3.Response` вместо `DualResponse<RawBody, RawBody>`.
