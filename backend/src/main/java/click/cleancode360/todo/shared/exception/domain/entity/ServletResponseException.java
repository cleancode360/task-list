package click.cleancode360.todo.shared.exception.domain.entity;

import lombok.Getter;

@Getter
public class ServletResponseException extends RuntimeException {
    private final int status;

    public ServletResponseException(int status, String message) {
        super(message);
        this.status = status;
    }

    public ServletResponseException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
