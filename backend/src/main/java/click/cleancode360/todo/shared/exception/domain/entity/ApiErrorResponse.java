package click.cleancode360.todo.shared.exception.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    int status,
    String message,
    Map<String, String> errors
) {}
