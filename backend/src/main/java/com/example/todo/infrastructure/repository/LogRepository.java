package com.example.todo.infrastructure.repository;

import com.example.todo.domain.model.Log;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class LogRepository {

    public void info(String message, Log logEntry) {
        log.info(
            message,
            StructuredArguments.keyValue("payload", logEntry)
        );
    }
}
