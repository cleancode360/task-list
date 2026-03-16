package com.example.todo.shared.infrastructure.gatewayadapter.log;

import com.example.todo.shared.domain.log.LogPayload;
import com.example.todo.shared.domain.log.LogGateway;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SLF4JLogAdapter implements LogGateway {
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
