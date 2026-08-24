package com.ecommerce.security.exceptions;

public record ApiError(
    Integer status,
    String error,
    String message,
    String path,
    String timestamp
) {
}
