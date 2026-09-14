package com.ecommerce.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
@Order
public class FallbackExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FallbackExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ProblemDetail handleException(Exception ex) throws Exception {

        if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
            throw ex;
        }

        if (ex instanceof ErrorResponse errorResponse) {
            return errorResponse.getBody();
        }

        logger.error("Unhandled exception: ", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}
