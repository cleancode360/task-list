package com.example.todo.infrastructure.repository;

import com.example.todo.domain.model.LogPayload;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class LogRepository {
    private final String payloadKey = "payload";

    public void info(String message, LogPayload payload) {
        log.info(
                message,
                StructuredArguments.keyValue(payloadKey, payload)
        );
    }

    public void error(String message, LogPayload payload, Exception ex) {
        log.error(
                message,
                StructuredArguments.keyValue(payloadKey, payload),
                ex
        );
    }
}
