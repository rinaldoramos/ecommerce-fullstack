package com.ecommerce.security.services;

import com.ecommerce.security.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    LoginCookieResponse signing(@Valid LoginRequest loginRequest);

    SignupResponse signup(@Valid SignupRequest signupRequest);

    UserInfoResponse getUserRole(Authentication authentication);

    ResponseCookie signout();
}