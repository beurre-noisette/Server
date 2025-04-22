package hello.cokezet.temporary.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Schema(description = "업데이트 할 유저의 바뀔 닉네임")
    private String nickname;

    @Schema(description = "알림받을 커머스로 등록할 ID 값, ID만 있으면 됩니다.",
    example = "1, 2 이런식으로 보내주시면 됩니다. 참고로 커머스 ID 별 name 매핑은 다음과 같습니다." +
            "ID 1 = 쿠팡, ID 2 = 지마켓, ID 3 = 11번가")
    private Set<Long> commerceIds;

    @Schema(description = "알림받을 카드사로 등록할 ID 값, ID만 있으면 됩니다.",
            example = "1, 2 이런식으로 보내주시면 됩니다. 참고로 카드사 ID 별 name 매핑은 다음과 같습니다." +
                    "ID 1 = 농협, ID 2 = 국민, ID 3 = 신한, ID 4 = 롯데, ID 5 = 하나," +
                    "ID 6 = 삼성, ID 7 = 현대, ID 8 = 우리, ID 9 = 씨티, ID 10 = BC" )
    private Set<Long> cardCompanyIds;

}
