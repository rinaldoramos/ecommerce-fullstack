package com.ecommerce.security.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthDenyHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthDenyHandler.class);

    private final ApiErrorWriter apiErrorWriter;

    public AuthDenyHandler(ApiErrorWriter apiErrorWriter) {
        this.apiErrorWriter = apiErrorWriter;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (auth != null) ? auth.getName() : "unknown";

        logger.warn("{} User: {} tried to access a protected resources at {} {}", accessDeniedException.getMessage(), principal, request.getMethod(), request.getRequestURI());

        this.apiErrorWriter.write(request, response, "Forbidden", HttpStatus.FORBIDDEN, "Access Denied! you don't have permission to access this resource");
    }
}
