package com.payments.service;

import com.payments.exception.DuplicateUsernameException;
import com.payments.exception.UserNotFoundException;
import com.payments.model.UserRequest;
import com.payments.model.UserResponse;
import com.payments.model.UserUpdateRequest;

import java.util.List;

public interface UserService {

    /**
     * Create a new user
     *
     * @param request User creation request
     * @return Created user response
     * @throws DuplicateUsernameException if username already exists
     */
    UserResponse createUser(UserRequest request);

    /**
     * Get user by id
     *
     * @param id User id
     * @return User response
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserById(Long id);

    /**
     * Get user by username
     *
     * @param username Username
     * @return User response
     * @throws UserNotFoundException if user not found
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get all users
     *
     * @return List of user responses
     */
    List<UserResponse> getAllUsers();

    /**
     * Update user completely
     *
     * @param id User id
     * @param request User update request
     * @return Updated user response
     * @throws UserNotFoundException if user not found
     * @throws DuplicateUsernameException if new username already exists
     */
    UserResponse updateUser(Long id, UserRequest request);

    /**
     * Partially update user
     *
     * @param id User id
     * @param request Partial update request
     * @return Updated user response
     * @throws UserNotFoundException if user not found
     */
    UserResponse partialUpdateUser(Long id, UserUpdateRequest request);

    /**
     * Delete user
     *
     * @param id User id
     * @throws UserNotFoundException if user not found
     */
    void deleteUser(Long id);

    /**
     * Enable or disable user
     *
     * @param id User id
     * @param enabled Enable status
     * @return Updated user response
     * @throws UserNotFoundException if user not found
     */
    UserResponse setUserEnabled(Long id, boolean enabled);
}
