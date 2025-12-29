package com.payments.dto;

public record UserDto(
    Long id,
    String name,
    String username,
    String email
) {}