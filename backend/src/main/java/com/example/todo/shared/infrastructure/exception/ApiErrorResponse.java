package com.example.todo.shared.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    int status,
    String message,
    Map<String, String> errors
) {}
