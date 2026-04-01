package click.cleancode360.todo.auth.domain.gateway;

public interface PasswordHasherGateway {
    String hash(String rawPassword);
}
