package click.cleancode360.todo.shared.exception.framework.advice;

import static org.assertj.core.api.Assertions.assertThat;

import click.cleancode360.todo.shared.exception.domain.entity.ApiErrorResponse;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesServletResponseExceptionWithOriginalStatusAndMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletResponseException exception = new ServletResponseException(404, "Task not found");

        var response = handler.handleServletResponseException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(new ApiErrorResponse(404, "Task not found", null));
        assertThat(request.getAttribute(GlobalExceptionHandler.EXCEPTION_ATTRIBUTE)).isSameAs(exception);
    }

    @Test
    void handlesValidationErrorsWithFieldMap() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "taskCreateRequest");
        bindingResult.addError(new FieldError("taskCreateRequest", "title", "must not be blank"));
        bindingResult.addError(new FieldError("taskCreateRequest", "tagNames[0]", "size must be between 0 and 100"));
        Method method = Objects.requireNonNull(
            GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleMethod", String.class)
        );
        MethodParameter parameter = new MethodParameter(
            method,
            0
        );
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            parameter,
            bindingResult
        );

        var response = handler.handleValidation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(new ApiErrorResponse(
            400,
            "Validation failed",
            java.util.Map.of(
                "title", "must not be blank",
                "tagNames[0]", "size must be between 0 and 100"
            )
        ));
        assertThat(request.getAttribute(GlobalExceptionHandler.EXCEPTION_ATTRIBUTE)).isSameAs(exception);
    }

    @Test
    void handlesAuthenticationExceptionAsUnauthorized() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        AuthenticationException exception = new AuthenticationException("Bad credentials") { };

        var response = handler.handleAuthentication(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(new ApiErrorResponse(401, "Bad credentials", null));
        assertThat(request.getAttribute(GlobalExceptionHandler.EXCEPTION_ATTRIBUTE)).isSameAs(exception);
    }

    @Test
    void handlesUnexpectedExceptionsWithGenericMessage() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Method method = Objects.requireNonNull(
            GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleMethod", String.class)
        );
        MethodParameter parameter = new MethodParameter(method, 0);
        Exception exception = new MethodArgumentTypeMismatchException("x", Integer.class, "id", parameter, null);

        var response = handler.handleUnexpectedException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(new ApiErrorResponse(500, "Unexpected internal server error", null));
        assertThat(request.getAttribute(GlobalExceptionHandler.EXCEPTION_ATTRIBUTE)).isSameAs(exception);
    }

    @SuppressWarnings("unused")
    private static void sampleMethod(String value) {
    }
}
