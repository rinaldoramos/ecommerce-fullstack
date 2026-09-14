package com.ecommerce.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateResourceException extends RuntimeException{

    private final String field;
    private final HttpStatus status;

    public DuplicateResourceException(String message, String field, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }
}
