package com.payments.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging.external")
public record ExternalLoggingProperties(
        boolean bodyEnabled
) {
}