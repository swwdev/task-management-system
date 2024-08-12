package com.example.taskmanagment.util;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

@IT
@Sql({"classpath:insertData.sql"})
public abstract class IntegrationTestBase {


    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void startContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void setUrl(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}


