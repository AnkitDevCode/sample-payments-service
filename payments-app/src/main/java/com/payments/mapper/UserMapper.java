package com.payments.mapper;

import com.payments.entity.PaymentMethod;
import com.payments.entity.User;
import com.payments.entity.UserProfile;
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
        setDefaultUserProfile(user);
        setDefaultPaymentMethod(user);
        return user;
    }

    private void setDefaultPaymentMethod(User user) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(com.payments.model.PaymentMethod.CASH_ON_DELIVERY);
        user.addPaymentMethod(paymentMethod);
    }

    private void setDefaultUserProfile(User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setPhone("123456789");
        userProfile.setAddress("address");
        user.addProfile(userProfile);
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