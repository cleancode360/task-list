package com.example.todo.shared.domain.log;

import lombok.Builder;

@Builder
public record LogPayload(
    Object request,
    Object response,
    Integer status,
    Long durationMs
) {}
