package com.ecommerce.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId) {
        super(String.format("%s not found for %s: %s", resourceName, fieldName, fieldId));
    }
}
