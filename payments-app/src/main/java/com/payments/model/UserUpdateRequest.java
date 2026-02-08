package com.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    private String password;
    private Boolean enabled;
    private Set<String> roles;
}