package com.example.todo.application.service;

import com.example.todo.application.exception.ApiException;
import com.example.todo.domain.model.User;
import com.example.todo.infrastructure.config.CustomUserDetails;
import com.example.todo.infrastructure.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    @Transactional
    public User register(String username, String password) {
        userRepository.findByUsername(username).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "Username already taken: " + username);
        });
        User user = new User(username, passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}
