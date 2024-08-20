# Task Management System

## Table of Contents
1. [Introduction](#introduction)
2. [Technologies](#technologies)
3. [Features](#features)
4. [Installation](#installation)
5. [Usage](#usage)
6. [API Documentation](#api-documentation)
7. [License](#license)

## Introduction
Task Management System — это Java Spring Boot приложение, предназначенное для управления задачами.
Оно поддерживает аутентификацию и авторизацию с использованием JWT токенов,
а также включает функции управления задачами и их статусами, назначения исполнителей и добавления комментариев.
Приложение поддерживает фильтрацию созданных и назначенных задач, а так же их пагинированный вывод.
Так же доступно получение гибкое пользователя и задач с дочерними сущностями, настраиваемое при помощи параметров запроса
API документация доступна через Swagger UI.
Так же вы можете протестировать приложение при мощи postman коллекции, помещенную в репозитроий

## Technologies
Этот проект построен с использованием следующих технологий:

- **Spring Boot**: Фреймворк для упрощения разработки Java приложений.
    - `spring-boot-starter-data-jpa`: Поддержка работы с базами данных.
    - `spring-boot-starter-security`: Реализация аутентификации и авторизации.
    - `spring-boot-starter-validation`: Валидация данных.
    - `spring-boot-starter-web`: Создание RESTful веб-сервисов.
    - `spring-boot-starter-test`: Набор инструментов для тестирования.
- **JWT (JSON Web Tokens)**: Для обеспечения безопасности приложения.
    - `jjwt-api`: Библиотека для работы с JWT.
    - `jjwt-impl`
    - `jjwt-jackson`
- **SpringDoc OpenAPI**: Для документирования API.
    - `springdoc-openapi-starter-webmvc-ui`
- **Liquibase**: Инструмент для управления миграциями базы данных.
    - `liquibase-core`
- **PostgreSQL**: База данных для хранения информации.
    - `postgresql`
- **Testcontainers**: Для интеграционного тестирования с использованием PostgreSQL.
    - `org.testcontainers.postgresql`
- **MapStruct**: Для маппинга между DTO и сущностями.
    - `mapstruct`
    - `lombok-mapstruct-binding`
- **Lombok**: Для уменьшения шаблонного кода.
    - `lombok`

## Features
- **Security**: JWT токены для аутентификации и авторизации пользователей.
- **Task Management**: Создание, редактирование, удаление и просмотр задач.
- **Commenting**: Возможность добавления комментариев к задачам.
- **Task Assignment**: Назначение задач исполнителям и изменение их статуса.
- **Error Handling**: Корректная обработка ошибок с понятными сообщениями для пользователя.
- **Pagination and Filtering**: Пагинация и фильтрация задач по автору и исполнителю.

## Installation
1. Клонируйте репозиторий:
    ```sh
    git clone git@github.com:swwdev/task-management-system.git
    cd task-management-system
    ```

2. Запустите приложение с использованием Docker:
    - Убедитесь, что Docker установлен и запущен.
    - Запустите приложение при помощи Docker c помощи следующих команд:
        ```sh
        cd docker
        docker-compose up -d
        ```

## Usage
- Доступ к приложению можно получить по адресу `http://localhost:8080`.
- Используйте документацию API, доступную по адресу `http://localhost:8080/swagger-ui.html`, для изучения доступных конечных точек и их использования.

## API Documentation
API задокументирован с использованием OpenAPI и доступен по адресу:
`http://localhost:8080/swagger-ui.html`

## License
Этот проект лицензирован под лицензией MIT.