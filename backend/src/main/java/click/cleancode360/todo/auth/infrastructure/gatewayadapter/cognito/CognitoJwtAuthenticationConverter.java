package click.cleancode360.todo.auth.infrastructure.gatewayadapter.cognito;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.auth.domain.gateway.UserGateway;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.spring.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CognitoJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserGateway userGateway;

    @Override
    @Transactional
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String username = jwt.getClaimAsString("cognito:username");
        if (username == null) {
            username = jwt.getClaimAsString("username");
        }
        if (username == null) {
            username = cognitoSub;
        }

        String resolvedUsername = username;
        User user = userGateway.findByCognitoSub(cognitoSub)
            .orElseGet(() -> userGateway.save(new User(cognitoSub, resolvedUsername)));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
