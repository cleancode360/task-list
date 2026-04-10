package click.cleancode360.todo.task.infrastructure.controller.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TaskUpdateRequest(
    @Pattern(regexp = ".*\\S.*", message = "must not be blank")
    @Size(max = 200)
    String title,
    @Size(max = 2000)
    String description,
    Boolean completed,
    List<@NotBlank @Size(max = 100) String> tagNames
) {}
