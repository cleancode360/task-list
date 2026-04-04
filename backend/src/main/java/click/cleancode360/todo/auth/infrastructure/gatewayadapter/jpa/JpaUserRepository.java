package click.cleancode360.todo.auth.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByCognitoSub(String cognitoSub);
}
