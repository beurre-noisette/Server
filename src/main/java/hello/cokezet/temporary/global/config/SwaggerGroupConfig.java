package hello.cokezet.temporary.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. 인증 API")
                .pathsToMatch("/api/auth/**")
                .packagesToScan("hello.cokezet.temporary.domain.user.controller")
                .addOpenApiMethodFilter(method -> {
                    // AuthRestController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("AuthRestController");
                })
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("2. 사용자관련 API")
                .pathsToMatch("/api/users/**")
                .packagesToScan("hello.cokezet.temporary.domain.user.controller")
                .addOpenApiMethodFilter(method -> {
                    // UserRestController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("UserRestController");
                })
                .build();
    }

    @Bean
    public GroupedOpenApi productAndCardApi() {
        return GroupedOpenApi.builder()
                .group("3. 온라인 스토어 API")
                .pathsToMatch("/api/v1/products/**")
                .addOpenApiMethodFilter(method -> {
                    // ProductController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("ProductController");
                })
                .build();
    }
}
