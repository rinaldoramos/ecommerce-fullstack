package com.ecommerce.controllers;


import com.ecommerce.security.dto.LoginCookieResponse;
import com.ecommerce.security.dto.LoginRequest;
import com.ecommerce.security.dto.LoginResponse;
import com.ecommerce.security.dto.SignupRequest;
import com.ecommerce.security.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final CsrfTokenRepository csrfTokenRepository;

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @PostMapping("/signing")
    public ResponseEntity<LoginResponse> signing(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LoginCookieResponse response = authenticationService.signing(loginRequest);
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, response.cookie().toString());

        CsrfToken rotated = csrfTokenRepository.generateToken(servletRequest);
        csrfTokenRepository.saveToken(rotated, servletRequest, servletResponse);

        return ResponseEntity.ok(response.body());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signup(signupRequest));
    }

    @GetMapping("/username")
    public String getUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Unknown";
    }

    @GetMapping("/user/role")
    public ResponseEntity<?> getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(authenticationService.getUserRole(authentication));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout() {
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authenticationService.signout().toString())
            .body("You have been signed out");
    }
}
