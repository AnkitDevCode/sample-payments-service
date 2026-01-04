package com.payment.starter.security.config;

import com.payment.starter.security.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@AutoConfiguration
@AutoConfigureBefore(org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class)
@EnableWebSecurity
public class SecurityAutoConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnBean(JwtAuthenticationFilter.class)
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .requestCache(cache -> cache.disable())// disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        // permitAll must match full path including context
                        .requestMatchers("/payments/**", "/.well-known/jwks.json", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
