package com.ecommerce.security.dto;

import java.util.List;

public record UserInfoResponse(
    Long id,
    String username,
    List<String> authorities
) {
}
