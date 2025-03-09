package hello.cokezet.temporary.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    private String code; // 비즈니스 에러코드 혹은 성공코드

    private String message;

    /**
     * 성공케이스 -> 응답 DTO
     * 실패케이스 -> 추가 에러 정보
     */
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(String code, String message) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ApiResult<T> error(String code, String message, T data) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
