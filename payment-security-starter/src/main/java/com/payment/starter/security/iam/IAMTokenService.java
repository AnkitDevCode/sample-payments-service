package com.payment.starter.security.iam;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public record IAMTokenService(JwtTokenIssuer tokenIssuer, JwtValidator jwtValidator) {

    public String issueToken(String subject, Map<String, Object> claims) {
        return tokenIssuer.issue(subject, claims);
    }

    public JWTClaimsSet validateAndExtract(String token) throws BadJWTException {
        return jwtValidator.validate(token);
    }
}