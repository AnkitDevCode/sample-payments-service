package com.payment.starter.security.iam;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record IamAuthenticationProvider(IamTokenValidator tokenValidator) implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();

        JWTClaimsSet claims = tokenValidator.extractAndValidateClaims(token);

        if (claims == null) {
            throw new BadCredentialsException("Invalid token");
        }

        String username = claims.getSubject();

        // Extract roles from claims
        List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

        return new UsernamePasswordAuthenticationToken(username, token, authorities);
    }

    private List<SimpleGrantedAuthority> extractAuthorities(JWTClaimsSet claims) {
        try {
            // Try different claim names for roles
            Object rolesClaim = claims.getClaim("roles");
            if (rolesClaim == null) {
                rolesClaim = claims.getClaim("authorities");
            }
            if (rolesClaim == null) {
                rolesClaim = claims.getClaim("groups");
            }

            if (rolesClaim instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) rolesClaim;
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}