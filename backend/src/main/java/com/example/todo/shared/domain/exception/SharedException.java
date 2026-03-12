package com.example.todo.shared.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SharedException extends RuntimeException {
    private final HttpStatus status;

    public SharedException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public SharedException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
