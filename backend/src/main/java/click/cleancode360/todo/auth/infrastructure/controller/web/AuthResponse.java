package click.cleancode360.todo.auth.infrastructure.controller.web;

public record AuthResponse(String username, String token, String refreshToken) {}
