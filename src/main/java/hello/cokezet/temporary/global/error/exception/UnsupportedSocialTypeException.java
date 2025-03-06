package hello.cokezet.temporary.global.error.exception;

import hello.cokezet.temporary.global.error.ErrorCode;

public class UnsupportedSocialTypeException extends BusinessException {

    public UnsupportedSocialTypeException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
    }

    public UnsupportedSocialTypeException(String message) {
        super(ErrorCode.UNSUPPORTED_SOCIAL_TYPE, message);
    }
}
