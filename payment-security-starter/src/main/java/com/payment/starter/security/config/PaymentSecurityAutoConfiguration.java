package com.payment.starter.security.config;

import com.payment.starter.security.aspect.ExceptionHandlingAspect;
import com.payment.starter.security.aspect.PerformanceMonitoringAspect;
import com.payment.starter.security.filter.JwtAuthenticationFilter;
import com.payment.starter.security.filter.RequestLoggingFilter;
import com.payment.starter.security.iam.IAMTokenService;
import com.payment.starter.security.iam.IamAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({SecurityProperties.class})
@EnableAspectJAutoProxy
public class PaymentSecurityAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public IAMTokenService iamTokenValidator(SecurityProperties properties, ResourceLoader resourceLoader) {
        log.info("Configuring IAM Token Service with cert: {}",
                properties.getIam().getSslCertificatePath());
        return new IAMTokenService(properties.getIam(), resourceLoader);
    }

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public IamAuthenticationProvider iamAuthenticationProvider(IAMTokenService iamTokenService) {
        log.info("Configuring IAM Authentication Provider");
        return new IamAuthenticationProvider(iamTokenService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtAuthenticationFilter jwtAuthenticationFilter(IAMTokenService iamTokenService, SecurityProperties properties) {
        return new JwtAuthenticationFilter(iamTokenService, properties);
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