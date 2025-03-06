package hello.cokezet.temporary.global.error.exception;

import hello.cokezet.temporary.global.error.ErrorCode;

public class SocialCommunicationException extends BusinessException {

    public SocialCommunicationException() {
        super(ErrorCode.SOCIAL_COMMUNICATION_ERROR);
    }

    public SocialCommunicationException(String message) {
        super(ErrorCode.SOCIAL_COMMUNICATION_ERROR, message);
    }

    public SocialCommunicationException(String message, Throwable cause) {
        super(ErrorCode.SOCIAL_COMMUNICATION_ERROR, message, cause);
    }
}
