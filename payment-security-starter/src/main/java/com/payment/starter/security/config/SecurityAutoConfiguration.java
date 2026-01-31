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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@AutoConfiguration
@AutoConfigureBefore(org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class)
@EnableWebSecurity
public class SecurityAutoConfiguration {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnBean(JwtAuthenticationFilter.class)
    @ConditionalOnProperty(prefix = "payment.security.iam", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties securityProperties, JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider iamAuthenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(" /api/v1/h2-console/**").disable())
                .requestCache(cache -> cache.disable())// disable CSRF for simplicity
                .authorizeHttpRequests(auth -> {
                            String[] excludedPaths = securityProperties.getAudit().getExcludedPaths();
                            log.info("Configuring excluded paths: {}", Arrays.toString(excludedPaths));
                            auth.requestMatchers(excludedPaths).permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                //for H2 DB
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(iamAuthenticationProvider)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
