package click.cleancode360.todo.shared.log.domain.entity;

import lombok.Builder;

@Builder
public record LogPayload(
    Object request,
    Object response,
    Integer status,
    Long durationMs
) {}
