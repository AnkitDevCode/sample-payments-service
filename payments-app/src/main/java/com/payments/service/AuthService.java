package com.payments.service;

import com.auth.model.LoginRequest;
import com.auth.model.LoginResponse;
import com.auth.model.TokenValidationResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    TokenValidationResponse isValidToken(String token);
}
