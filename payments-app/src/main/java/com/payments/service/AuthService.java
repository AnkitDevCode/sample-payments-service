package com.payments.service;

import com.auth.model.LoginRequest;
import com.auth.model.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
