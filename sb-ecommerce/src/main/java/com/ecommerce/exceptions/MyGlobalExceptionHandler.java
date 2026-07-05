package com.ecommerce.exceptions;


import com.ecommerce.common.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new java.util.HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String errorField = error.getField();
            String message = error.getDefaultMessage();

            response.put(errorField, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException ex) {
        APIResponse response = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException ex) {
        APIResponse response = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
