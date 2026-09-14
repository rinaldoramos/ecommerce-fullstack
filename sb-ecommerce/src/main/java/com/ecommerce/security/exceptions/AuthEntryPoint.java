package com.ecommerce.security.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);

    private final ApiErrorWriter apiErrorWriter;

    public AuthEntryPoint(ApiErrorWriter apiErrorWriter) {
        this.apiErrorWriter = apiErrorWriter;
    }

    @Override
    public void commence(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException authException) throws IOException {

        logger.warn("Unauthorized access attempt: {} {} -> {}", authException.getMessage(), request.getRequestURI(), authException.getClass().getName());

        this.apiErrorWriter.write(request, response,"Unauthorized", HttpStatus.UNAUTHORIZED, "Unauthorized! Please authenticate with valid username and password");
    }
}
