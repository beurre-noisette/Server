package hello.cokezet.temporary.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SwaggerConfig {

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
        return (operation, handlerMethod) -> {
            // Content-Type 헤더 필수 설정
            Parameter contentTypeHeader = new Parameter()
                    .in("header")
                    .name("Content-Type")
                    .description("요청 본문 타입 (항상 application/json으로 설정해야 함)")
                    .required(true)
                    .schema(new io.swagger.v3.oas.models.media.StringSchema()
                            .type("string")
                            .example("application/json"));

            // Accept 헤더 필수 설정
            Parameter acceptHeader = new Parameter()
                    .in("header")
                    .name("Accept")
                    .description("응답 받을 데이터 타입 (항상 application/json으로 설정해야 함)")
                    .required(true)
                    .schema(new io.swagger.v3.oas.models.media.StringSchema()
                            .type("string")
                            .example("application/json"));

            operation.addParametersItem(contentTypeHeader);
            operation.addParametersItem(acceptHeader);

            return operation;
        };
    }
}
