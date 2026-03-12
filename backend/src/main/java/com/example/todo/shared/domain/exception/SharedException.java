package com.example.todo.shared.domain.exception;

import lombok.Getter;

@Getter
public class SharedException extends RuntimeException {
    private final int status;

    public SharedException(int status, String message) {
        super(message);
        this.status = status;
    }

    public SharedException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
