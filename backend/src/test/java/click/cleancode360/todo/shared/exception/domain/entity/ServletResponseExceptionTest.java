package click.cleancode360.todo.shared.exception.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServletResponseExceptionTest {

    @Test
    void storesStatusAndMessage() {
        ServletResponseException exception = new ServletResponseException(409, "Conflict");

        assertThat(exception.getStatus()).isEqualTo(409);
        assertThat(exception).hasMessage("Conflict");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void storesStatusMessageAndCause() {
        IllegalStateException cause = new IllegalStateException("root cause");
        ServletResponseException exception = new ServletResponseException(500, "Failure", cause);

        assertThat(exception.getStatus()).isEqualTo(500);
        assertThat(exception).hasMessage("Failure");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}
