package com.payments.config;

import com.payments.filter.WebClientLoggingFilter;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(JsonPlaceholderConfig.class)
public class ServiceContext {

    @Bean
    WebClient jsonPlaceholderClient(JsonPlaceholderConfig config,WebClientLoggingFilter filter) {
        return WebClient.builder()
                .baseUrl(config.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(filter.requestLogger())
                .filter(filter.responseLogger())
                .filter(filter.errorLogger())
                .build();
    }
}
