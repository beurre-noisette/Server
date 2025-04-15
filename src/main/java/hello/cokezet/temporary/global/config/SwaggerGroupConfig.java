package hello.cokezet.temporary.global.config;

import hello.cokezet.temporary.global.config.swagger.HeaderCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {

    private final HeaderCustomizer headerCustomizer;

    public SwaggerGroupConfig(HeaderCustomizer headerCustomizer) {
        this.headerCustomizer = headerCustomizer;
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. 회원 인증 API")
                .pathsToMatch("/api/auth/**")
                .packagesToScan("hello.cokezet.temporary.domain.user.controller")
                .addOpenApiMethodFilter(method -> {
                    // AuthRestController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("AuthRestController");
                })
                .addOperationCustomizer(headerCustomizer.customizeWithRequiredHeaders()) // 헤더 설정 추가
                .build();
    }

    @Bean
    public GroupedOpenApi guestApi() {
        return GroupedOpenApi.builder()
                .group("2. 비회원 인증 API")
                .pathsToMatch("/api/guest/**")
                .packagesToScan("hello.cokezet.temporary.domain.user.controller")
                .addOpenApiMethodFilter(method -> {
                    // GuestRestController 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("GuestRestController");
                })
                .addOperationCustomizer(headerCustomizer.customizeWithRequiredHeaders()) // 헤더 설정 추가
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("3. 사용자관련 API")
                .pathsToMatch("/api/users/**")
                .packagesToScan("hello.cokezet.temporary.domain.user.controller")
                .addOpenApiMethodFilter(method -> {
                    // UserRestController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("UserRestController");
                })
                .addOperationCustomizer(headerCustomizer.customizeWithRequiredHeaders()) // 헤더 설정 추가
                .build();
    }

    @Bean
    public GroupedOpenApi productAndCardApi() {
        return GroupedOpenApi.builder()
                .group("4. 온라인 스토어 API")
                .pathsToMatch("/api/v1/products/**")
                .addOpenApiMethodFilter(method -> {
                    // ProductController의 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("ProductController");
                })
                .build();
    }

    @Bean
    public GroupedOpenApi contentsApi() {
        return GroupedOpenApi.builder()
                .group("5. 컨텐츠 관련 API")
                .pathsToMatch("/api/contents/**")
                .addOpenApiMethodFilter(method -> {
                    // ContentRestController 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("ContentRestController");
                })
                .addOperationCustomizer(headerCustomizer.customizeWithRequiredHeaders()) // 헤더 설정 추가
                .build();
    }
    
    @Bean
    public GroupedOpenApi conveniencePromotionApi() {
        return GroupedOpenApi.builder()
                .group("6. 편의점 프로모션 API")
                .pathsToMatch("/api/promotions/**")
                .addOpenApiMethodFilter(method -> {
                    // ContentRestController 메서드만 포함
                    return method.getDeclaringClass().getSimpleName().equals("PromotionRestController");
                })
                .build();
    }
}
