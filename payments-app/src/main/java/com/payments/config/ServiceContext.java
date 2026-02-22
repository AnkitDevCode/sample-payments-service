package com.payments.config;

import com.payments.interceptor.RestClientLoggingInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({ExternalLoggingProperties.class})
public class ServiceContext {

    @Bean
    public RestClient restClient(RestClient.Builder builder, ExternalLoggingProperties properties) {
        return builder
                .requestFactory(bufferedRequestFactory())
                .requestInterceptor(new RestClientLoggingInterceptor(properties))
                .build();
    }

    private ClientHttpRequestFactory bufferedRequestFactory() {
        return new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
    }
}
