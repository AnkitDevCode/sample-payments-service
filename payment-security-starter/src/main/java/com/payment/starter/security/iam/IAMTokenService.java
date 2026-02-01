package com.payment.starter.security.iam;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.payment.starter.security.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class IAMTokenService {

    private static final long CLOCK_SKEW_SECONDS = 60;
    private final SecurityProperties.IamConfig config;
    private final ResourceLoader resourceLoader;
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public IAMTokenService(SecurityProperties.IamConfig config, ResourceLoader resourceLoader) {
        this.config = config;
        this.resourceLoader = resourceLoader;
        this.jwtProcessor = initProcessor();
    }

    private ConfigurableJWTProcessor<SecurityContext> initProcessor() {
        try {
            RSAPublicKey publicKey = loadPublicKey(config.getSslCertificatePath());

            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .keyID(config.getKeyId() != null ? config.getKeyId() : "default")
                    .build();

            JWKSet jwkSet = new JWKSet(rsaKey);
            JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);

            DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();

            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);

            processor.setJWSKeySelector(keySelector);
            processor.setJWTClaimsSetVerifier(buildClaimsVerifier());

            log.info("JWT validator initialized successfully using key from: {}",
                    config.getSslCertificatePath());

            return processor;

        } catch (Exception e) {
            log.error("Failed to initialize JWT validator", e);
            throw new IllegalStateException("Failed to initialize JWT validator", e);
        }
    }

    private JWTClaimsSetVerifier<SecurityContext> buildClaimsVerifier() {
        return (claims, context) -> {

            Date nowWithSkew = new Date(System.currentTimeMillis() - (CLOCK_SKEW_SECONDS * 1000));

            if (claims.getExpirationTime() == null) {
                throw new BadJWTException("Token missing expiration time");
            }
            if (claims.getExpirationTime().before(nowWithSkew)) {
                throw new BadJWTException("Token expired at: " + claims.getExpirationTime());
            }

            if (claims.getNotBeforeTime() != null &&
                    claims.getNotBeforeTime().after(new Date(System.currentTimeMillis() + (CLOCK_SKEW_SECONDS * 1000)))) {
                throw new BadJWTException("Token not valid yet, not before: " + claims.getNotBeforeTime());
            }

            if (config.isValidateIssuer()) {
                if (config.getIssuer() == null || config.getIssuer().isBlank()) {
                    throw new BadJWTException("Issuer validation enabled but issuer not configured");
                }
                if (!config.getIssuer().equals(claims.getIssuer())) {
                    throw new BadJWTException(String.format(
                            "Invalid issuer: expected=%s, actual=%s",
                            config.getIssuer(), claims.getIssuer()));
                }
            }

            if (config.isValidateAudience()) {
                if (config.getAudience() == null || config.getAudience().isBlank()) {
                    throw new BadJWTException("Audience validation enabled but audience not configured");
                }
                List<String> audience = claims.getAudience();
                if (audience == null || !audience.contains(config.getAudience())) {
                    throw new BadJWTException(String.format(
                            "Invalid audience: expected=%s, actual=%s",
                            config.getAudience(), audience));
                }
            }
        };
    }

    private RSAPublicKey loadPublicKey(String path) throws Exception {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(
                    "publicKeyPath must be configured for JWT validation");
        }

        log.info("Loading public key from: {}", path);

        try {
            return loadPublicKeyFromPem(path);
        } catch (Exception e) {
            log.error("Failed to load public key from PEM file: {}", path, e);
            throw new IllegalStateException("Failed to load public key from: " + path, e);
        }
    }

    private RSAPublicKey loadPublicKeyFromPem(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Public key file not found: " + path);
        }

        String publicKeyPEM = getPublicKeyPEM(path, resource);

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);

        log.info("Successfully loaded RSA public key from PEM file");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private static String getPublicKeyPEM(String path, Resource resource) {
        String pem;
        try (InputStream inputStream = resource.getInputStream()) {
            pem = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read public key file: " + path, e);
        }

        // Remove PEM headers and whitespace
        return pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }

    public JWTClaimsSet validateAndExtract(String token) {
        try {
            JWTClaimsSet claims = jwtProcessor.process(token, null);
            log.debug("Token validated successfully for subject: {}", claims.getSubject());
            return claims;
        } catch (BadJWTException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            return null;
        }
    }

    public boolean isValid(String token) {
        return validateAndExtract(token) != null;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(config.getTokenExpirationMs());

        try {
            return Jwts.builder()
                    // ===== Header =====
                    .header()
                    .keyId(config.getKeyId())
                    .and()

                    // ===== Claims =====
                    .issuer(config.getIssuer())
                    .audience().add(config.getAudience()).and()
                    .subject(subject)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiry))
                    .claims(claims)

                    // ===== Signature =====
                    .signWith(loadPublicKey(config.getSslCertificatePath()))
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}