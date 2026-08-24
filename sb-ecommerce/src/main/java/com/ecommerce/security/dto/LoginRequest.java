package com.ecommerce.security.dto;

public record LoginRequest(
    String username,
    String password
) {
}
