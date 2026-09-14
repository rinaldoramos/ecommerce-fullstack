package com.ecommerce.exceptions;


import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyGlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        pd.setTitle("Validation Error");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        pd.setProperty("errors", errors);

        return pd;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Constraint violation");
        pd.setTitle("Validation Error");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(error -> {
            errors.put(fieldName(error.getPropertyPath()), error.getMessage());
        });
        pd.setProperty("errors", errors);

        return pd;
    }

    private String fieldName(Path propertyPath) {
        String field = null;

        for (Path.Node node : propertyPath) {
            field = node.getName();
        }

        return (field != null) ? field : propertyPath.toString();
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle("Resource Not Found");

        return pd;
    }

    @ExceptionHandler(value = APIException.class)
    public ProblemDetail handleApi(APIException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle("Request Error");
        return pd;
    }

    @ExceptionHandler(value = DuplicateResourceException.class)
    public ProblemDetail myDuplicateResourceException(DuplicateResourceException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());

        pd.setTitle("Duplicate Resource");
        pd.setProperty("field", ex.getField());
        return pd;
    }
}
