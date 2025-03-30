package hello.cokezet.temporary.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 회원 전용 기능 접근 시 메시지
        ApiResult<Void> apiResult = ApiResult.error(
                ErrorCode.UNAUTHORIZED.getCode(),
                "이 기능은 회원 전용입니다. 로그인 후 이용해주세요."
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResult));
    }
}
