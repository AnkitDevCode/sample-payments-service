package com.payments.service.impl;

import com.payments.entity.User;
import com.payments.exception.DuplicateUsernameException;
import com.payments.exception.UserNotFoundException;
import com.payments.mapper.UserMapper;
import com.payments.model.UserRequest;
import com.payments.model.UserResponse;
import com.payments.model.UserUpdateRequest;
import com.payments.repository.UserRepository;
import com.payments.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(request.getUsername());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Check if username is being changed and if new username already exists
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(request.getUsername());
        }

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        user.setRoles(request.getRoles());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    public UserResponse partialUpdateUser(Long id, UserUpdateRequest request) {
        log.info("Partially updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }

        User updatedUser = userRepository.save(user);
        log.info("User partially updated successfully with id: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    public UserResponse setUserEnabled(Long id, boolean enabled) {
        log.info("Setting user {} enabled status to: {}", id, enabled);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}
