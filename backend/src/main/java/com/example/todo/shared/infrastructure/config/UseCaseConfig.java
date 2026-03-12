package com.example.todo.shared.infrastructure.config;

import com.example.todo.auth.application.usecase.UserService;
import com.example.todo.auth.domain.gateway.PasswordHasher;
import com.example.todo.auth.domain.gateway.UserGateway;
import com.example.todo.tag.application.usecase.TagService;
import com.example.todo.tag.domain.gateway.TagGateway;
import com.example.todo.task.application.usecase.TaskService;
import com.example.todo.task.domain.gateway.TaskGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public TaskService taskService(TaskGateway taskGateway, TagGateway tagGateway) {
        return new TaskService(taskGateway, tagGateway);
    }

    @Bean
    public TagService tagService(TagGateway tagGateway) {
        return new TagService(tagGateway);
    }

    @Bean
    public UserService userService(UserGateway userGateway, PasswordHasher passwordHasher) {
        return new UserService(userGateway, passwordHasher);
    }
}
