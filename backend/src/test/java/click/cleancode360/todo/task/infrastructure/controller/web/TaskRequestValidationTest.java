package click.cleancode360.todo.task.infrastructure.controller.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TaskRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createRequestRejectsBlankTagNames() {
        TaskCreateRequest request = new TaskCreateRequest("Title", "Description", List.of("work", " "));

        Set<String> invalidPaths = validate(request).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertThat(invalidPaths).contains("tagNames[1].<list element>");
    }

    @Test
    void createRequestRejectsOversizedTagNames() {
        TaskCreateRequest request = new TaskCreateRequest("Title", "Description", List.of("x".repeat(101)));

        Set<String> invalidPaths = validate(request).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertThat(invalidPaths).contains("tagNames[0].<list element>");
    }

    @Test
    void updateRequestAllowsNullTitleForPartialUpdates() {
        TaskUpdateRequest request = new TaskUpdateRequest(null, "Description", Boolean.TRUE, List.of("work"));

        assertThat(validate(request)).isEmpty();
    }

    @Test
    void updateRequestRejectsBlankTitle() {
        TaskUpdateRequest request = new TaskUpdateRequest("   ", "Description", Boolean.FALSE, List.of("work"));

        Set<String> invalidPaths = validate(request).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertThat(invalidPaths).contains("title");
    }

    @Test
    void updateRequestRejectsBlankTagNames() {
        TaskUpdateRequest request = new TaskUpdateRequest("Title", "Description", Boolean.FALSE, List.of(""));

        Set<String> invalidPaths = validate(request).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertThat(invalidPaths).contains("tagNames[0].<list element>");
    }

    private <T> Set<ConstraintViolation<T>> validate(T request) {
        return validator.validate(request);
    }
}
