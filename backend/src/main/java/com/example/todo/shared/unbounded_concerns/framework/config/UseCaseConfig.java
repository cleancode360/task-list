package com.example.todo.shared.unbounded_concerns.framework.config;

import com.example.todo.auth.application.usecase.UserUseCase;
import com.example.todo.auth.domain.gateway.PasswordHasherGateway;
import com.example.todo.auth.domain.gateway.UserGateway;
import com.example.todo.tag.application.usecase.TagUseCase;
import com.example.todo.tag.domain.gateway.TagGateway;
import com.example.todo.task.application.usecase.TaskUseCase;
import com.example.todo.task.domain.gateway.TaskGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public TaskUseCase taskService(TaskGateway taskGateway, TagGateway tagGateway) {
        return new TaskUseCase(taskGateway, tagGateway);
    }

    @Bean
    public TagUseCase tagService(TagGateway tagGateway) {
        return new TagUseCase(tagGateway);
    }

    @Bean
    public UserUseCase userService(UserGateway userGateway, PasswordHasherGateway passwordHasher) {
        return new UserUseCase(userGateway, passwordHasher);
    }
}
