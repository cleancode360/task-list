package com.example.todo.task.infrastructure.controller.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TaskCreateRequest(
    @NotBlank
    @Size(max = 200)
    String title,
    @Size(max = 2000)
    String description,
    List<Long> tagIds
) {}
