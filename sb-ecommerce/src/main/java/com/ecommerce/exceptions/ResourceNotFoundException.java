package com.ecommerce.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException{

    private final HttpStatus status;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId, HttpStatus status) {
        super(String.format("%s not found for %s: %s", resourceName, fieldName, fieldId));
        this.status = status;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue, HttpStatus status) {
        super(String.format("%s not found for %s: %s", resourceName, fieldName, fieldValue));
        this.status = status;
    }
}
