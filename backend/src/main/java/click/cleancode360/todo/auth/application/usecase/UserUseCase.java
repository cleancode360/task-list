package click.cleancode360.todo.auth.application.usecase;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.auth.domain.gateway.PasswordHasherGateway;
import click.cleancode360.todo.auth.domain.gateway.UserGateway;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserGateway userGateway;
    private final PasswordHasherGateway passwordHasher;

    public User register(String username, String password) {
        userGateway.findByUsername(username).ifPresent(existing -> {
            throw new ServletResponseException(409, "Username already taken: " + username);
        });
        User user = new User(username, passwordHasher.hash(password));
        return userGateway.save(user);
    }
}
