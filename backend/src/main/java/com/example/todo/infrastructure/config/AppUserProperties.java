package com.example.todo.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.user")
@Getter
@Setter
public class AppUserProperties {

    private String username;
    private String password;

}
