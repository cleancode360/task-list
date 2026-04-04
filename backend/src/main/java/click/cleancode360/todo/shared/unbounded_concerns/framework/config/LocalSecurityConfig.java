package click.cleancode360.todo.shared.unbounded_concerns.framework.config;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.auth.domain.gateway.UserGateway;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.spring.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@Profile("local")
public class LocalSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> new Jwt(token, Instant.now(), Instant.now().plusSeconds(300),
            Map.of("alg", "none"), Map.of("sub", "local"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserGateway userGateway,
                                                   @Value("${app.local-auth.username}") String localUsername) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new LocalAuthFilter(userGateway, localUsername), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    static class LocalAuthFilter extends OncePerRequestFilter {

        private final UserGateway userGateway;
        private final String localUsername;

        LocalAuthFilter(UserGateway userGateway, String localUsername) {
            this.userGateway = userGateway;
            this.localUsername = localUsername;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userGateway.findByUsername(localUsername)
                    .orElseGet(() -> userGateway.save(new User("local-dev-sub", localUsername)));

                CustomUserDetails userDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        }
    }
}
