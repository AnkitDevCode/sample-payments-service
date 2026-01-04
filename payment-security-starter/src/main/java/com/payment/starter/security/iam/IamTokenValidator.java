package com.payment.starter.security.iam;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.payment.starter.security.config.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IamTokenValidator {

    private static final Logger log = LoggerFactory.getLogger(IamTokenValidator.class);
    private final SecurityProperties.IamConfig config;
    private JwksClient jwksClient;
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (config.getJwksUri() == null || config.getJwksUri().isBlank()) {
            log.warn("JWKS URI not configured, token validation will be limited");
            return;
        }
        log.info("Initializing JWKS client from {}", config.getJwksUri());
        this.jwksClient = new JwksClient(
                config.getJwksUri(),
                config.getJwksCacheDurationMs(),
                config.getJwksConnectTimeoutMs(),
                config.getJwksReadTimeoutMs()
        );

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
                initializeJwtProcessor();
            } catch (Exception e) {
                log.warn("JWKS preload failed: {}", e.getMessage());
            }
        }, 2, TimeUnit.SECONDS);

        log.info("JWKS client initialized successfully");
    }

    public IamTokenValidator(SecurityProperties.IamConfig config) {
        this.config = config;
    }

    private void initializeJwtProcessor() {
        try {
            // Fetch JWKS
            JWKSet jwkSet = jwksClient.getJwkSet();

            // Create JWT processor
            jwtProcessor = new DefaultJWTProcessor<>();

            // Set up key selector with JWKS
            JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256; // Change based on your needs
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);

            jwtProcessor.setJWSKeySelector(keySelector);

            log.info("JWT processor initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT processor: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize JWT processor", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            JWTClaimsSet claims = extractAndValidateClaims(token);
            return claims != null;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public JWTClaimsSet extractAndValidateClaims(String token) {
        try {
            if (jwtProcessor == null) {
                log.error("JWT processor not initialized");
                return null;
            }

            // Parse and verify the JWT
            JWTClaimsSet claims = jwtProcessor.process(token, null);

            // Validate issuer
            if (config.isValidateIssuer() && config.getIssuer() != null) {
                if (!config.getIssuer().equals(claims.getIssuer())) {
                    log.warn("Invalid token issuer: expected={}, actual={}",
                            config.getIssuer(), claims.getIssuer());
                    return null;
                }
            }

            // Validate audience
            if (config.isValidateAudience() && config.getAudience() != null) {
                List<String> audience = claims.getAudience();
                if (audience == null || !audience.contains(config.getAudience())) {
                    log.warn("Invalid token audience: expected={}, actual={}",
                            config.getAudience(), audience);
                    return null;
                }
            }

            // Check expiration
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                log.warn("Token has expired at: {}", expirationTime);
                return null;
            }

            // Check not before
            Date notBeforeTime = claims.getNotBeforeTime();
            if (notBeforeTime != null && notBeforeTime.after(new Date())) {
                log.warn("Token not valid yet, not before: {}", notBeforeTime);
                return null;
            }

            log.debug("Token validated successfully for subject: {}", claims.getSubject());
            return claims;

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return null;
        }
    }

    public void refreshJwks() {
        if (jwksClient != null) {
            jwksClient.clearCache();
            initializeJwtProcessor();
            log.info("JWKS refreshed");
        }
    }
}