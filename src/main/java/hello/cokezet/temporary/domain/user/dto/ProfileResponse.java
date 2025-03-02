package hello.cokezet.temporary.domain.user.dto;

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

    private Long id;

    private String email;

    private String nickname;

    private boolean profileComplete;

    private Set<CommerceInfo> preferredCommerces;

    private Set<CardCompanyInfo> preferredCardCompanies;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommerceInfo {

        private Long id;

        private String name;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardCompanyInfo {

        private Long id;

        private String name;

    }
}
