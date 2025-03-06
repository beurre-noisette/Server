package hello.cokezet.temporary.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-002", "지원하지 않는 HTTP 메서드입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-003", "서버 내부 오류가 발생했습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON-004", "잘못된 타입의 값입니다"),

    // 인증 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증되지 않은 접근입니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "만료된 토큰입니다"),
    TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "AUTH-004", "토큰 재사용이 감지되었습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-005", "리프레시 토큰을 찾을 수 없습니다"),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER-002", "이미 존재하는 이메일입니다"),

    // 소셜 로그인 관련 에러
    UNSUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "SOCIAL-001", "지원하지 않는 소셜 로그인 유형입니다"),
    SOCIAL_COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL-002", "소셜 플랫폼 통신 오류가 발생했습니다"),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "SOCIAL-003", "유효하지 않은 소셜 토큰입니다"),

    // 프로필 관련 에러
    PROFILE_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "PROFILE-001", "프로필 업데이트에 실패했습니다");

    private final HttpStatus status;

    private final String code;

    private final String message;

}
