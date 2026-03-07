package com.example.todo.domain.model;

import lombok.Builder;

@Builder
public record LogPayload(
    Object request,
    Object response,
    Integer status,
    Long durationMs
) {}
