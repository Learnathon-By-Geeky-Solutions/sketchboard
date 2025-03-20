package com.example.lostnfound.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
        .info(
            new Info().title("LostNFound API Documentation").version("1.0").description(" by team Sketchboard")
        )
        .addSecurityItem(
            new SecurityRequirement().addList("SketchboardSecurityScheme")
        )
        .components(new Components().addSecuritySchemes("SketchboardSecurityScheme",
         new SecurityScheme().name("SketchboardSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }
}
