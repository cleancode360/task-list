package click.cleancode360.todo.auth.infrastructure.controller.web;

import click.cleancode360.todo.auth.application.usecase.UserUseCase;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.spring.CustomUserDetails;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.jwt.JwtService;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RESTAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserUseCase userUseCase;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        userUseCase.register(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(request.username(), null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String username = authentication.getName();
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        return ResponseEntity.ok(new AuthResponse(username, accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isValid(token) || !jwtService.isRefreshToken(token)) {
            throw new ServletResponseException(401, "Invalid refresh token");
        }

        String username = jwtService.extractUsername(token);
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);
        return ResponseEntity.ok(new AuthResponse(username, newAccessToken, newRefreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new AuthResponse(userDetails.getUsername(), null, null));
    }
}
