package com.example.todo.web.exception;

import com.example.todo.application.exception.ApiException;
import com.example.todo.web.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage(), ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiErrorResponse body = createBody(HttpStatus.BAD_REQUEST, "Validation failed", errors);
        return logAndReturn(body, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error", ex);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, Exception ex) {
        ApiErrorResponse body = createBody(status, message, null);
        return logAndReturn(body, status, ex);
    }

    private ApiErrorResponse createBody(HttpStatus status, String message, Map<String, String> errors) {
        HttpStatus safeStatus = Objects.requireNonNull(status, "status must not be null");
        
        return new ApiErrorResponse(
            safeStatus.value(),
            Instant.now().toString(),
            message,
            errors
        );
    }

    private ResponseEntity<ApiErrorResponse> logAndReturn(ApiErrorResponse body, HttpStatus status, Exception ex) {
        HttpStatus safeStatus = Objects.requireNonNull(status, "status must not be null");

        log.error(
            "api_exception",
            StructuredArguments.keyValue("status", safeStatus.value()),
            StructuredArguments.keyValue("body", body),
            ex
        );
        return ResponseEntity.status(safeStatus).body(body);
    }
}
