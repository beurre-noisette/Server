package hello.cokezet.temporary.global.config;

import hello.cokezet.temporary.global.config.swagger.HeaderCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SwaggerConfig {

    private final HeaderCustomizer headerCustomizer;

    public SwaggerConfig(HeaderCustomizer headerCustomizer) {
        this.headerCustomizer = headerCustomizer;
    }

    @Value("${springdoc.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("CokeZet API")
                        .description("CokeZet API 문서")
                        .version("v0.5")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));

        if (!serverUrl.isEmpty()) {
            openAPI.addServersItem(new Server().url(serverUrl));
        }

        return openAPI;
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return headerCustomizer.customizeWithRequiredHeaders();
    }
}
