package com.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eduCore360OpenAPI() {

        return new OpenAPI()

            //Server Info
            .addServersItem(new Server()
                .url("http://localhost:9098")
                .description("Local Development Server"))

            //API Info
            .info(new Info()
                .title("EduCore360 - User Management API")
                .version("1.0.0")
                .description("""
                    This API handles:

                    • User Registration
                    • Role-based Login (email + password + role)
                    • User retrieval

                    Roles Supported:
                    STUDENT, INSTRUCTOR, REGISTRAR,
                    EXAM_COORDINATOR, FINANCE_OFFICER
                """)
                .contact(new Contact()
                    .name("Development Team")
                    .email("support@educore360.com"))
                .license(new License()
                    .name("Apache License 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}