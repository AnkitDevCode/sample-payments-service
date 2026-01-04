package com.payment.starter.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "payment.security")
public class SecurityProperties {

    private IamConfig iam = new IamConfig();
    private MtlsConfig mtls = new MtlsConfig();
    private AuditConfig audit = new AuditConfig();
    private LoggingConfig logging = new LoggingConfig();
    private CorsConfig cors = new CorsConfig();

    @Getter
    @Setter
    public static class IamConfig {
        private boolean enabled = true;
        private String issuer;
        private String audience;
        private String jwksUri;
        private long tokenExpirationMs = 3600000; // 1 hour
        private long jwksCacheDurationMs = 3600000; // 1 hour cache
        private boolean validateIssuer = true;
        private boolean validateAudience = true;
        private int jwksConnectTimeoutMs = 5000;
        private int jwksReadTimeoutMs = 5000;

    }

    @Getter
    @Setter
    public static class MtlsConfig {
        private boolean enabled = false;
        private String trustStorePath;
        private String trustStorePassword;
        private String keyStorePath;
        private String keyStorePassword;
        private String keyAlias;
    }

    @Getter
    @Setter
    public static class AuditConfig {
        private boolean enabled = true;
        private String[] includedPaths = {"/api/**"};
        private String[] excludedPaths = {"/actuator/**", "/health"};
    }

    @Getter
    @Setter
    public static class LoggingConfig {
        private boolean logRequests = true;
        private boolean logResponses = true;
        private boolean logHeaders = false;
        private boolean logBody = true;
        private int maxBodyLength = 1000;
    }

    @Getter
    @Setter
    public static class CorsConfig {
        private boolean enabled = true;
        private String[] allowedOrigins = {"*"};
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        private String[] allowedHeaders = {"*"};
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }
}