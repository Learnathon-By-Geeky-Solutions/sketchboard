package com.example.lostnfound.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
        .info(
            new Info().title("LostNFound API Documentation").version("1.0").description(" by team Sketchboard")
        )
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Local server"),
                new Server().url("https://chair-succeed-peter-psi.trycloudflare.com/").description("Hosted server")
            )
        );
    }
}