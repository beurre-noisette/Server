package hello.cokezet.temporary.global.error.exception;

import hello.cokezet.temporary.global.error.ErrorCode;

public class InvalidSocialTokenException extends BusinessException {

    public InvalidSocialTokenException() {
        super(ErrorCode.INVALID_SOCIAL_TOKEN);
    }

    public InvalidSocialTokenException(String message) {
        super(ErrorCode.INVALID_SOCIAL_TOKEN, message);
    }
}
