package com.payment.starter.security.iam;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.payment.starter.security.config.SecurityProperties;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public final class JwtTokenIssuer {

    private final JWSSigner signer;
    private final String issuer;
    private final String audience;
    private final long ttlMs;
    private final String keyId;

    public JwtTokenIssuer(RsaKeyProvider keyProvider, SecurityProperties.IamConfig config) throws JOSEException {
        this.signer = new RSASSASigner(keyProvider.getRsaJwk().toPrivateKey());
        this.issuer = config.getIssuer();
        this.audience = config.getAudience();
        this.ttlMs = config.getTokenExpirationMs();
        this.keyId = keyProvider.getRsaJwk().getKeyID();
    }

    public String issue(String subject, Map<String, Object> claims) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .audience(audience)
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusMillis(ttlMs)));
            claims.forEach(builder::claim);
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(), builder.build());
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("JWT signing failed", e);
        }
    }
}
