package com.example.todo.shared.domain.log;

public interface LogGateway {
    void info(String message, LogPayload payload);
    void error(String message, LogPayload payload, Exception ex);
}
