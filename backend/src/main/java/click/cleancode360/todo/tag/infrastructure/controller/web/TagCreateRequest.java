package click.cleancode360.todo.tag.infrastructure.controller.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagCreateRequest(
    @NotBlank
    @Size(max = 100)
    String name
) {}
