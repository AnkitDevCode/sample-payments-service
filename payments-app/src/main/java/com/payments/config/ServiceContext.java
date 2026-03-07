package com.payments.config;

import com.payments.interceptor.RestClientLoggingInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({ExternalLoggingProperties.class})
public class ServiceContext {

    @Bean
    public RestClientLoggingInterceptor loggingInterceptor(ExternalLoggingProperties props) {
        return new RestClientLoggingInterceptor(props);
    }

    @Bean
    public RestClient restClient(RestClientLoggingInterceptor loggingInterceptor) {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .requestInterceptor(loggingInterceptor)
                .build();
    }
}
