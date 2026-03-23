package com.example.todo.shared.infrastructure.gatewayadapter.jpa;

import com.example.todo.shared.domain.log.LogGateway;
import com.example.todo.shared.domain.log.LogPayload;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class JpaQueryLogger {

    private final LogGateway logGateway;
    private final DataSource dataSource;

    private String datasourceUrl;

    @PostConstruct
    void resolveDatasourceUrl() {
        try (Connection conn = dataSource.getConnection()) {
            datasourceUrl = conn.getMetaData().getURL();
        } catch (SQLException e) {
            datasourceUrl = "unknown";
        }
    }

    public <T> T queryAndLog(String operation, Supplier<T> supplier) {
        long startNs = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            logGateway.info(
                    "JPA " + datasourceUrl + " " + operation,
                    LogPayload.builder()
                            .durationMs(durationMs)
                            .build()
            );
        }
    }
}
