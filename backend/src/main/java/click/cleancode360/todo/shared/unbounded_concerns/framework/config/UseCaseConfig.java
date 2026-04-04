package click.cleancode360.todo.shared.unbounded_concerns.framework.config;

import click.cleancode360.todo.tag.application.usecase.TagUseCase;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import click.cleancode360.todo.task.application.usecase.TaskUseCase;
import click.cleancode360.todo.task.domain.gateway.TaskGateway;
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
}
