package com.payment.starter.security.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.payment.starter.security.iam.IamTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final IamTokenValidator tokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter {}", request.getRequestURI());
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                JWTClaimsSet claims = tokenValidator.extractAndValidateClaims(token);

                if (claims != null) {
                    String username = claims.getSubject();
                    List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, token, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated user: {} with authorities: {}", username, authorities);
                } else {
                    log.warn("Invalid token received");
                }

            } catch (Exception e) {
                log.error("Authentication failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> extractAuthorities(JWTClaimsSet claims) {
        try {
            Object rolesClaim = claims.getClaim("roles");
            if (rolesClaim == null) {
                rolesClaim = claims.getClaim("authorities");
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
}