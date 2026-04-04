package click.cleancode360.todo.auth.infrastructure.gatewayadapter.cognito;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.auth.domain.gateway.UserGateway;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.spring.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CognitoJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserGateway userGateway;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String username = jwt.getClaimAsString("cognito:username");
        if (username == null) {
            username = jwt.getClaimAsString("username");
        }
        if (username == null) {
            username = cognitoSub;
        }

        User user = findOrCreateUser(cognitoSub, username);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private User findOrCreateUser(String cognitoSub, String username) {
        return userGateway.findByCognitoSub(cognitoSub)
            .orElseGet(() -> {
                try {
                    return userGateway.save(new User(cognitoSub, username));
                } catch (DataIntegrityViolationException e) {
                    return userGateway.findByCognitoSub(cognitoSub)
                        .orElseThrow(() -> e);
                }
            });
    }
}
