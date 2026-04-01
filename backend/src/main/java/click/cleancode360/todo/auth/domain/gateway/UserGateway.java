package click.cleancode360.todo.auth.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import java.util.Optional;

public interface UserGateway {
    Optional<User> findByUsername(String username);
    User save(User user);
}
