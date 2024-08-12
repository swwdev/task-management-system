package com.example.taskmanagment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI setOpenApi() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Developer environment");
        Contact contact = new Contact();
        contact.setEmail("swtyy@yandex.ru");
        contact.setName("swtyy");
        contact.setUrl("https://github.com/swwdev");
        License licence = new License()
                .name("MIT Licence")
                .url("https://choosealicense.com/licenses/mit/");
        Info info = new Info()
                .title("Task Management Rest Api")
                .version("1.0")
                .contact(contact)
                .description("This API providers endpoints for management database of users, tasks and comments protection via jwt-token")
                .license(licence);
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(info)
                .servers(List.of(devServer));
    }
}
