package click.cleancode360.todo.shared.unbounded_concerns.framework.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void buildsOpenApiDefinitionWithJwtBearerSecurity() {
        OpenAPI openApi = config.openAPI();

        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).isEqualTo("Todo API");
        assertThat(openApi.getInfo().getDescription()).isEqualTo("Task and tag management REST API with HATEOAS links");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("0.1.0");
        assertThat(openApi.getSecurity()).singleElement().satisfies(requirement ->
            assertThat(requirement.containsKey("bearerAuth")).isTrue()
        );
        assertThat(openApi.getComponents()).isNotNull();
        assertThat(openApi.getComponents().getSecuritySchemes())
            .containsKey("bearerAuth");

        SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme.getName()).isEqualTo("bearerAuth");
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
    }
}
