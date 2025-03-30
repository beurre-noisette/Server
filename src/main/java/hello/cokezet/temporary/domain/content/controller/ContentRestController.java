package hello.cokezet.temporary.domain.content.controller;

import hello.cokezet.temporary.domain.content.dto.response.NoticeResponse;
import hello.cokezet.temporary.domain.content.dto.response.PolicyResponse;
import hello.cokezet.temporary.domain.content.service.ContentService;
import hello.cokezet.temporary.global.common.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/contents")
@Tag(name = "콘텐츠", description = "공지사항, 이용약관, 개인정보 처리방침 API")
public class ContentRestController {

    private final ContentService contentService;

    public ContentRestController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation(
            summary = "공지사항 목록 조회",
            description = "전체 공지사항 목록을 조회합니다."
    )
    @GetMapping("/notices")
    public ResponseEntity<ApiResult<List<NoticeResponse>>> getNotices() {
        log.info("공지사항 목록 조회 요청");
        List<NoticeResponse> notices = contentService.getNotices();
        return ResponseEntity.ok(ApiResult.success(notices));
    }

    @Operation(
            summary = "이용약관 조회",
            description = "이용약관 내용을 조회합니다."
    )
    @GetMapping("/terms")
    public ResponseEntity<ApiResult<PolicyResponse>> getTerms() {
        log.info("이용약관 조회 요청");
        PolicyResponse terms = contentService.getTerms();
        return ResponseEntity.ok(ApiResult.success(terms));
    }

    @Operation(
            summary = "개인정보 처리방침 조회",
            description = "개인정보 처리방침 내용을 조회합니다."
    )
    @GetMapping("/privacy-policy")
    public ResponseEntity<ApiResult<PolicyResponse>> getPrivacyPolicy() {
        log.info("개인정보 처리방침 조회 요청");
        PolicyResponse privacyPolicy = contentService.getPrivacyPolicy();
        return ResponseEntity.ok(ApiResult.success(privacyPolicy));
    }
}
