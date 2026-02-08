package com.payment.starter.security.iam;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.payment.starter.security.config.SecurityProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
public final class JwtValidator {

    private static final long CLOCK_SKEW_SECONDS = 60;

    private final ConfigurableJWTProcessor<SecurityContext> processor;

    public JwtValidator(SecurityProperties.IamConfig config, RsaKeyProvider keyProvider) {

        RSAKey rsaKey = new RSAKey.Builder(keyProvider.getRsaJwk())
                .keyID(config.getKeyId())
                .build();

        JWKSource<SecurityContext> keySource =
                new ImmutableJWKSet<>(new JWKSet(rsaKey));

        DefaultJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor<>();

        jwtProcessor.setJWSKeySelector(
                new JWSVerificationKeySelector<>(
                        JWSAlgorithm.RS256,
                        keySource
                )
        );

        jwtProcessor.setJWTClaimsSetVerifier(
                buildClaimsVerifier(config)
        );

        this.processor = jwtProcessor;
    }

    public JWTClaimsSet validate(String token) throws BadJWTException {
        try {
            return processor.process(token, null);
        } catch (BadJWTException e) {
            throw e;
        } catch (Exception e) {
            throw new BadJWTException("JWT validation failed", e);
        }
    }

    private JWTClaimsSetVerifier<SecurityContext> buildClaimsVerifier(
            SecurityProperties.IamConfig config
    ) {
        return (claims, context) -> {

            Date now = new Date();
            Date nowMinusSkew = new Date(
                    now.getTime() - CLOCK_SKEW_SECONDS * 1000
            );
            Date nowPlusSkew = new Date(
                    now.getTime() + CLOCK_SKEW_SECONDS * 1000
            );

            if (claims.getExpirationTime() == null) {
                throw new BadJWTException("Missing exp");
            }
            if (claims.getExpirationTime().before(nowMinusSkew)) {
                throw new BadJWTException("Token expired");
            }

            if (claims.getNotBeforeTime() != null
                    && claims.getNotBeforeTime().after(nowPlusSkew)) {
                throw new BadJWTException("Token not active yet");
            }

            if (claims.getSubject() == null
                    || claims.getSubject().isBlank()) {
                throw new BadJWTException("Missing sub");
            }

            if (config.isValidateIssuer()) {
                if (!config.getIssuer().equals(claims.getIssuer())) {
                    throw new BadJWTException("Invalid issuer");
                }
            }

            if (config.isValidateAudience()) {
                List<String> aud = claims.getAudience();
                if (aud == null || !aud.contains(config.getAudience())) {
                    throw new BadJWTException("Invalid audience");
                }
            }
        };
    }
}
