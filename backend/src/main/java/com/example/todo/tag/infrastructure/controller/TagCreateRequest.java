package com.example.todo.tag.infrastructure.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagCreateRequest(
    @NotBlank
    @Size(max = 100)
    String name
) {}
