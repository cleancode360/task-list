package com.example.todo.auth.infrastructure.gatewayadapter;

import com.example.todo.auth.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userGateway.findByUsername(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
