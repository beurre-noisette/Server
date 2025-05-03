package hello.cokezet.temporary.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    @Schema(description = "유저 고유 ID",
            example = "1")
    private Long id;

    @Schema(description = "유저가 가입 시 사용한 이메일",
            example = "abc@gamil.com")
    private String email;

    @Schema(description = "유저의 닉네임")
    private String nickname;

    @Schema(description = "유저 프로필 완성 여부",
            example = "true -> 프로필 완성, false -> 프로필 미완성")
    private boolean profileComplete;

    @Schema(description = "유저가 저장한 정보를 받고 싶은 커머스들")
    private Set<CommerceInfo> preferredCommerces;

    @Schema(description = "유저가 저장한 정보를 받고 싶은 카드사들")
    private Set<CardCompanyInfo> preferredCardCompanies;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommerceInfo {

        @Schema(description = "커머스 고유 ID",
                example = "ID 1 = 쿠팡, ID 2 = 지마켓, ID 3 = 11번가")
        private Long id;

        @Schema(description = "커머스 고유 ID에 대응되는 커머스 이름",
                example = "ID 1 = 쿠팡, ID 2 = 지마켓, ID 3 = 11번가")
        private String name;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardCompanyInfo {

        @Schema(description = "카드사 고유 ID",
                example = "ID 1 = 농협, ID 2 = 국민, ID 3 = 신한, ID 4 = 롯데, ID 5 = 하나," +
                        "ID 6 = 삼성, ID 7 = 현대, ID 8 = 우리, ID 9 = 씨티, ID 10 = BC")
        private Long id;

        @Schema(description = "카드사 고유 ID에 대응되는 카드사 이름",
                example = "ID 1 = 농협, ID 2 = 국민, ID 3 = 신한, ID 4 = 롯데, ID 5 = 하나," +
                        "ID 6 = 삼성, ID 7 = 현대, ID 8 = 우리, ID 9 = 씨티, ID 10 = BC")
        private String name;

    }
}
