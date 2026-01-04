package com.payment.starter.security.config;

import com.payment.starter.security.aspect.ExceptionHandlingAspect;
import com.payment.starter.security.aspect.PerformanceMonitoringAspect;
import com.payment.starter.security.filter.JwtAuthenticationFilter;
import com.payment.starter.security.filter.RequestLoggingFilter;
import com.payment.starter.security.iam.IamAuthenticationProvider;
import com.payment.starter.security.iam.IamTokenValidator;
import com.payment.starter.security.mock.MockJwksServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@AutoConfiguration
@Import(MockJwksServerConfig.class)
@EnableConfigurationProperties({SecurityProperties.class})
@EnableAspectJAutoProxy
public class PaymentSecurityAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public IamTokenValidator iamTokenValidator(SecurityProperties properties) {
        log.info("Configuring IAM Token Validator with JWKS endpoint: {}",
                properties.getIam().getJwksUri());
        return new IamTokenValidator(properties.getIam());
    }

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public IamAuthenticationProvider iamAuthenticationProvider(IamTokenValidator tokenValidator) {
        log.info("Configuring IAM Authentication Provider");
        return new IamAuthenticationProvider(tokenValidator);
    }

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtAuthenticationFilter jwtAuthenticationFilter(IamTokenValidator tokenValidator) {
        return new JwtAuthenticationFilter(tokenValidator);
    }

    @Bean
    @ConditionalOnMissingBean
    public PerformanceMonitoringAspect performanceMonitoringAspect() {
        log.info("Configuring Performance Monitoring Aspect");
        return new PerformanceMonitoringAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlingAspect exceptionHandlingAspect() {
        log.info("Configuring Exception Handling Aspect");
        return new ExceptionHandlingAspect();
    }

    // Response Logging Filter
    @Bean
    @ConditionalOnProperty(prefix = "payment.security.logging", name = "log-responses", havingValue = "true", matchIfMissing = false)
    public FilterRegistrationBean<RequestLoggingFilter> responseLoggingFilter(SecurityProperties properties) {
        log.info("Configuring Response Logging Filter");
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLoggingFilter(properties.getLogging()));
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.cors", name = "enabled", havingValue = "true", matchIfMissing = false)
    public WebMvcConfigurer corsConfigurer(SecurityProperties properties) {
        log.info("Configuring CORS");
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                SecurityProperties.CorsConfig cors = properties.getCors();
                registry.addMapping("/**")
                        .allowedOrigins(cors.getAllowedOrigins())
                        .allowedMethods(cors.getAllowedMethods())
                        .allowedHeaders(cors.getAllowedHeaders())
                        .allowCredentials(cors.isAllowCredentials())
                        .maxAge(cors.getMaxAge());
            }
        };
    }
}