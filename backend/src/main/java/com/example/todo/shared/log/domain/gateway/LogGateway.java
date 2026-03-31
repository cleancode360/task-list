package com.example.todo.shared.log.domain.gateway;

import com.example.todo.shared.log.domain.entity.LogPayload;

public interface LogGateway {
    void info(String message, LogPayload payload);
    void error(String message, LogPayload payload, Exception ex);
}
