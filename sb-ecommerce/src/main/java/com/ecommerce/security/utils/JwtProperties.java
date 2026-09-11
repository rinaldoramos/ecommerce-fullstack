package com.ecommerce.security.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "spring.app")
public record JwtProperties(
    @NotBlank String secret,
    @NotBlank String issuer,
    @NotNull Duration expiration,
    @NotBlank String jwtCookie,
    @DefaultValue(value = "true") Boolean cookieSecure
    ) {
    public JwtProperties {
        if (expiration == null || (expiration.isNegative() || expiration.isZero())) {
            throw new IllegalArgumentException("Token validation duration must be positive");
        }
    }
}