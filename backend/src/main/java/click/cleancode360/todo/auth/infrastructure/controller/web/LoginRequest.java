package click.cleancode360.todo.auth.infrastructure.controller.web;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String username,
    @NotBlank
    String password
) {}
