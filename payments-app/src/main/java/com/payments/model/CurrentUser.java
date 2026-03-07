package com.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Set;

@Data
@Component
@RequestScope
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {
    private Long id;
    private String username;
    private Set<String> roles;
}