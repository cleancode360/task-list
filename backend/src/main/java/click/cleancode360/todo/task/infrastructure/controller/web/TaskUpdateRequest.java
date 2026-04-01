package click.cleancode360.todo.task.infrastructure.controller.web;

import jakarta.validation.constraints.Size;
import java.util.List;

public record TaskUpdateRequest(
    @Size(max = 200)
    String title,
    @Size(max = 2000)
    String description,
    Boolean completed,
    List<String> tagNames
) {}
