package hello.cokezet.temporary.global.error.exception;

import hello.cokezet.temporary.global.error.ErrorCode;

public class TokenReuseDetectedException extends BusinessException {

    public TokenReuseDetectedException() {
        super(ErrorCode.TOKEN_REUSE_DETECTED);
    }

    public TokenReuseDetectedException(String message) {
        super(ErrorCode.TOKEN_REUSE_DETECTED, message);
    }
}
