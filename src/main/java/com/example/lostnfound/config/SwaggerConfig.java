package com.example.lostnfound.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;


@Configuration
public class SwaggerConfig {
    private String bearerAuth="BearerAuth";
    private String basicAuth="BasicAuth";
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("LostNFound API Documentation")
                .version("1.0")
                .description("by team Sketchboard"))
            .addSecurityItem(
                new SecurityRequirement().addList(bearerAuth) // JWT Token
            )
            .addSecurityItem(
                new SecurityRequirement().addList(basicAuth) // Username & Password
            )
            .components(new Components()
                .addSecuritySchemes(bearerAuth,
                    new SecurityScheme()
                        .name(bearerAuth)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter the JWT token"))
                .addSecuritySchemes(basicAuth,
                    new SecurityScheme()
                        .name(basicAuth)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Or Enter your username and password"))
            );
    }
}
