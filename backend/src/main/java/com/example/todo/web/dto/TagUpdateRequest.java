package com.example.todo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagUpdateRequest(
    @NotBlank
    @Size(max = 100)
    String name
) {}
