package com.payments.mapper;

import com.payments.entity.User;
import com.payments.model.UserRequest;
import com.payments.model.UserResponse;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserMapper {
    
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        user.setRoles(request.getRoles() != null ? request.getRoles() : new HashSet<>());
        return user;
    }
    
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEnabled(user.isEnabled());
        response.setRoles(user.getRoles());
        return response;
    }
}