package com.example.todo.auth.application.usecase;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.auth.domain.gateway.UserGateway;
import com.example.todo.auth.infrastructure.gatewayadapter.CustomUserDetails;
import com.example.todo.shared.domain.exception.SharedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userGateway.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    @Transactional
    public User register(String username, String password) {
        userGateway.findByUsername(username).ifPresent(existing -> {
            throw new SharedException(HttpStatus.CONFLICT, "Username already taken: " + username);
        });
        User user = new User(username, passwordEncoder.encode(password));
        return userGateway.save(user);
    }
}
