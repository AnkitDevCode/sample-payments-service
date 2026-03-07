package com.payments.advice;

import com.payments.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalSecurityAdvice {

    private final CurrentUser currentUser;

    @ModelAttribute
    public void populateUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser.setUsername(authentication.getName());
            currentUser.setRoles(
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet())
            );
        }
    }
}