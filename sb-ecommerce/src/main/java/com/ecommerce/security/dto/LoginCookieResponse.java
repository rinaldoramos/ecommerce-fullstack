package com.ecommerce.security.dto;

import org.springframework.http.ResponseCookie;

public record LoginCookieResponse(
    LoginResponse body,
    ResponseCookie cookie
) {
}
