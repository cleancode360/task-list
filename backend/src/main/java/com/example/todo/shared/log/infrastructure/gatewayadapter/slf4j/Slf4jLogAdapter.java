package com.example.todo.shared.log.infrastructure.gatewayadapter.slf4j;

import com.example.todo.shared.log.domain.entity.LogPayload;
import com.example.todo.shared.log.domain.gateway.LogGateway;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class Slf4jLogAdapter implements LogGateway {
    private final String payloadKey = "payload";

    @Override
    public void info(String message, LogPayload payload) {
        log.info(
                message,
                StructuredArguments.keyValue(payloadKey, payload)
        );
    }

    @Override
    public void error(String message, LogPayload payload, Exception ex) {
        log.error(
                message,
                StructuredArguments.keyValue(payloadKey, payload),
                ex
        );
    }
}
