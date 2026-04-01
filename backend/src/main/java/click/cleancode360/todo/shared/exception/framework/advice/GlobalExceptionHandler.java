package click.cleancode360.todo.shared.exception.framework.advice;

import click.cleancode360.todo.shared.exception.domain.entity.ApiErrorResponse;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String EXCEPTION_ATTRIBUTE = "requestException";

    @ExceptionHandler(ServletResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleServletResponseException(ServletResponseException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.valueOf(ex.getStatus()), ex.getMessage(), ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", ex, request, errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error", ex, request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, Exception ex, HttpServletRequest request) {
        return buildResponse(status, message, ex, request, null);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, Exception ex, HttpServletRequest request, Map<String, String> errors) {
        request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
        ApiErrorResponse body = new ApiErrorResponse(status.value(), message, errors);
        return ResponseEntity.status(status).body(body);
    }
}
