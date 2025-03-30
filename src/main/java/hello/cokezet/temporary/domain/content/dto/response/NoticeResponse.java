package hello.cokezet.temporary.domain.content.dto.response;

import hello.cokezet.temporary.domain.content.model.Notice;
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
@Schema(description = "공지사항 응답 DTO")
public class NoticeResponse {

    @Schema(description = "공지사항 ID")
    private Long id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "중요 공지사항 여부")
    private boolean isImportant;

    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    public static NoticeResponse fromEntity(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isImportant(notice.isImportant())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
