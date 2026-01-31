package com.example.todo.web.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record TaskUpdateRequest(
    @Size(max = 200)
    String title,
    @Size(max = 2000)
    String description,
    Boolean completed,
    List<Long> tagIds
) {}
