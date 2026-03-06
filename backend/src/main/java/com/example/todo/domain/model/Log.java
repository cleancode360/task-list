package com.example.todo.domain.model;

import lombok.Builder;

@Builder
public record Log(
    Object request,
    Object response,
    Integer status,
    Long durationMs
) {}
