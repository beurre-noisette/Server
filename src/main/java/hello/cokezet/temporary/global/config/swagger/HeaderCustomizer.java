package hello.cokezet.temporary.global.config.swagger;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;

@Component
public class HeaderCustomizer {

    public OperationCustomizer customizeWithRequiredHeaders() {
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
