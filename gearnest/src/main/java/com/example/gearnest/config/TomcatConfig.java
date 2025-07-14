package com.example.gearnest.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(20 * 1024 * 1024); // 20MB
            connector.setProperty("maxParameterCount", "10000"); // Increase parameter limit
            connector.setProperty("maxFileCount", "5");
        });
    }
}
