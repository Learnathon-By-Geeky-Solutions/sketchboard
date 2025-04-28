package com.example.lostnfound.config;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(10 * 1024 * 1024); // 10MB
            connector.setMaxSavePostSize(10 * 1024 * 1024); // Optional
        });
    }
}

