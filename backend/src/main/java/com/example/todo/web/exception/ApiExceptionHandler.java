package com.example.todo.web.exception;

import com.example.todo.application.exception.ApiException;
import com.example.todo.domain.model.LogPayload;
import com.example.todo.infrastructure.repository.LogRepository;
import com.example.todo.web.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    private final LogRepository logRepository;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatus(), ex.getMessage(), ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiErrorResponse body = createBody(HttpStatus.BAD_REQUEST, "Validation failed", errors);
        return logAndReturn(body, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error", ex, request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
        HttpStatus status,
        String message,
        Exception ex,
        HttpServletRequest request
    ) {
        ApiErrorResponse body = createBody(status, message, null);
        return logAndReturn(body, ex, request);
    }

    private ApiErrorResponse createBody(HttpStatus status, String message, Map<String, String> errors) {        
        return new ApiErrorResponse(
            status.value(),
            message,
            errors
        );
    }

    private ResponseEntity<ApiErrorResponse> logAndReturn(
        ApiErrorResponse body,
        Exception ex,
        HttpServletRequest request
    ) {
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("method", request.getMethod());
        requestPayload.put("uri", request.getRequestURI());
        requestPayload.put("body", getCachedBody(request));

        logRepository.error(
            body.message(),
            LogPayload.builder()
                .request(requestPayload)
                .response(body)
                .status(body.status())
                .durationMs(null)
                .build(),
            ex
        );
        return ResponseEntity.status(body.status()).body(body);
    }

    private String getCachedBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper wrappedRequest)) {
            return null;
        }

        byte[] cachedBody = wrappedRequest.getContentAsByteArray();
        if (cachedBody.length == 0) {
            return null;
        }

        return new String(cachedBody, StandardCharsets.UTF_8);
    }
}
