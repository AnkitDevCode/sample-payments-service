package com.payments.service.impl;

import com.auth.model.LoginRequest;
import com.auth.model.LoginResponse;
import com.payment.starter.security.config.SecurityProperties;
import com.payment.starter.security.iam.IAMTokenService;
import com.payments.entity.User;
import com.payments.repository.UserRepository;
import com.payments.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAMTokenService iamTokenService;
    private final SecurityProperties config;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login attempt failed: Username {} not found", request.getUsername());
                    return new BadCredentialsException("Invalid credentials");
                });

        if (!user.isEnabled()) {
            log.warn("Login attempt failed: Account {} is disabled", user.getUsername());
            throw new DisabledException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login attempt failed: Wrong password for user {}", user.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        Map<String, Object> claims = Map.of(
                "roles", user.getRoles(),
                "type", "USER"
        );

        String token = iamTokenService.generateToken(user.getUsername(), claims);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(token);
        loginResponse.setExpiresIn((int) config.getIam().getTokenExpirationMs());

        return loginResponse;
    }
}
