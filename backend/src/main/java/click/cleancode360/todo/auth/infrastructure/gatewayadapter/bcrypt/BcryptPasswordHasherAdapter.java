package click.cleancode360.todo.auth.infrastructure.gatewayadapter.bcrypt;

import click.cleancode360.todo.auth.domain.gateway.PasswordHasherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BcryptPasswordHasherAdapter implements PasswordHasherGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
