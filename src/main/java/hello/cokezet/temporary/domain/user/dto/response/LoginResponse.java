package hello.cokezet.temporary.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private UserInfo user; // 사용자 정보

    private boolean newUser; // 신규 사용자 여부

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {

        private Long id;

        private String email;

        private String nickname;

    }
}
