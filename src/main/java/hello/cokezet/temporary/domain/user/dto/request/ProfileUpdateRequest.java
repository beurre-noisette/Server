package hello.cokezet.temporary.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String nickname;

    private Set<Long> commerceIds;

    private Set<Long> cardCompanyIds;

}
