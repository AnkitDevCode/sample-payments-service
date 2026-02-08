package com.payment.starter.security.config;

import com.nimbusds.jose.JOSEException;
import com.payment.starter.security.aspect.ExceptionHandlingAspect;
import com.payment.starter.security.aspect.PerformanceMonitoringAspect;
import com.payment.starter.security.filter.JwtAuthenticationFilter;
import com.payment.starter.security.filter.RequestLoggingFilter;
import com.payment.starter.security.iam.IAMTokenService;
import com.payment.starter.security.iam.IamAuthenticationProvider;
import com.payment.starter.security.iam.JwtTokenIssuer;
import com.payment.starter.security.iam.JwtValidator;
import com.payment.starter.security.iam.RsaKeyProvider;
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
@ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy
public class PaymentSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RsaKeyProvider rsaKeyProvider(SecurityProperties properties, ResourceLoader resourceLoader) {
        return new RsaKeyProvider(resourceLoader, properties.getIam());
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenIssuer jwtTokenIssuer(SecurityProperties securityProperties, RsaKeyProvider rsaKeyProvider) throws JOSEException {
        return new JwtTokenIssuer(rsaKeyProvider, securityProperties.getIam());
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtValidator jwtValidator(SecurityProperties securityProperties, RsaKeyProvider rsaKeyProvider) {
        return new JwtValidator(securityProperties.getIam(), rsaKeyProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public IAMTokenService iamTokenService(JwtTokenIssuer tokenIssuer, JwtValidator jwtValidator) {
        return new IAMTokenService(tokenIssuer, jwtValidator);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(IAMTokenService iamTokenValidator, SecurityProperties properties) {
        return new JwtAuthenticationFilter(iamTokenValidator, properties);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        log.info("Configuring jwtFilterRegistration Filter");
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setAsyncSupported(true);
        registration.setEnabled(false);
        return registration;
    }


    @Bean
    @ConditionalOnMissingBean
    public IamAuthenticationProvider iamAuthenticationProvider(IAMTokenService iamTokenValidator) {
        log.info("Configuring IAM Authentication Provider");
        return new IamAuthenticationProvider(iamTokenValidator);
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
                registry.addMapping("/**").allowedOrigins(cors.getAllowedOrigins()).allowedMethods(cors.getAllowedMethods()).allowedHeaders(cors.getAllowedHeaders()).allowCredentials(cors.isAllowCredentials()).maxAge(cors.getMaxAge());
            }
        };
    }
}