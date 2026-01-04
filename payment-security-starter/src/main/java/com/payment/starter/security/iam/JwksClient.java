package com.payment.starter.security.iam;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JwksClient {

    private final WebClient webClient;
    private final Cache<String, JWKSet> jwksCache;
    private final String jwksUri;

    public JwksClient(String jwksUri, long cacheDurationMs, int connectTimeoutMs, int readTimeoutMs) {
        this.jwksUri = jwksUri;
        this.webClient = WebClient.builder()
                .baseUrl(jwksUri)
                .build();
        this.jwksCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheDurationMs, TimeUnit.MILLISECONDS)
                .maximumSize(10)
                .build();
        log.info("JWKS Client initialized with endpoint: {}", jwksUri);
    }

    public JWKSet getJwkSet() {
        try {
            // Try to get from cache first
            JWKSet cachedJwks = jwksCache.getIfPresent(jwksUri);
            if (cachedJwks != null) {
                log.debug("Returning cached JWKS");
                return cachedJwks;
            }

            // Fetch from endpoint
            log.info("Fetching JWKS from endpoint: {}", jwksUri);
            String jwksJson = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            if (jwksJson == null) {
                throw new RuntimeException("Failed to fetch JWKS: empty response");
            }

            JWKSet jwkSet = JWKSet.parse(jwksJson);

            // Cache the result
            jwksCache.put(jwksUri, jwkSet);

            log.info("Successfully fetched and cached JWKS with {} keys", jwkSet.getKeys().size());

            return jwkSet;

        } catch (Exception e) {
            log.error("Failed to fetch JWKS from {}: {}", jwksUri, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch JWKS", e);
        }
    }

    public void clearCache() {
        jwksCache.invalidateAll();
        log.info("JWKS cache cleared");
    }
}