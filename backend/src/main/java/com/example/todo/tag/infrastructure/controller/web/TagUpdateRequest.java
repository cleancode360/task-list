package com.example.todo.tag.infrastructure.controller.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagUpdateRequest(
    @NotBlank
    @Size(max = 100)
    String name
) {}
