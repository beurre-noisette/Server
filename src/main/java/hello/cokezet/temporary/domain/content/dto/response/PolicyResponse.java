package hello.cokezet.temporary.domain.content.dto.response;

import hello.cokezet.temporary.domain.content.model.Policy;
import hello.cokezet.temporary.domain.content.model.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정책 응답 DTO")
public class PolicyResponse {

    @Schema(description = "정책 ID")
    private Long id;

    @Schema(description = "정책 타입", example = "TERMS 또는 PRIVACY_POLICY")
    private PolicyType type;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "작성/수정일")
    private LocalDateTime updatedAt;

    public static PolicyResponse from(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .type(policy.getType())
                .title(policy.getTitle())
                .content(policy.getContent())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
