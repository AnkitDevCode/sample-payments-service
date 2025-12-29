package com.payments.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "third-party.jsonplaceholder")
public record JsonPlaceholderConfig(String baseUrl) {}