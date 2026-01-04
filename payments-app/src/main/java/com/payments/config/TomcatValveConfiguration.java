package com.payments.config;

import com.payments.valve.AccessLogValve;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatValveConfiguration {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> valveCustomizer() {
        return factory -> {
            AccessLogValve accessValve = new AccessLogValve();
            accessValve.setLogAccessInfo(true);
            // Enable custom access logging
            factory.addContextValves(accessValve);
        };
    }
}