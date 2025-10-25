package com.payments.config;


import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class AppConfig {

    @Bean
    TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        // Use virtual threads for handling requests
        return protocolHandler -> protocolHandler.setExecutor(
                java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()
        );
    }
}
