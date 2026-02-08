package com.payments.controller;

import com.auth.api.AuthenticationApi;
import com.auth.model.LoginRequest;
import com.auth.model.LoginResponse;
import com.auth.model.TokenValidationResponse;
import com.payments.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequestMapping
@AllArgsConstructor
public class AuthController implements AuthenticationApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("loginUser: {}", loginRequest.getUsername());
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Override
    public ResponseEntity<TokenValidationResponse> validateToken( @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("validateToken");
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.isValidToken(token));
    }
}
