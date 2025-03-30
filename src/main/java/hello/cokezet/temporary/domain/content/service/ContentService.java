package hello.cokezet.temporary.domain.content.service;

import hello.cokezet.temporary.domain.content.dto.response.NoticeResponse;
import hello.cokezet.temporary.domain.content.dto.response.PolicyResponse;
import hello.cokezet.temporary.domain.content.model.Notice;
import hello.cokezet.temporary.domain.content.model.Policy;
import hello.cokezet.temporary.domain.content.model.PolicyType;
import hello.cokezet.temporary.domain.content.repository.NoticeRepository;
import hello.cokezet.temporary.domain.content.repository.PolicyRepository;
import hello.cokezet.temporary.global.error.ErrorCode;
import hello.cokezet.temporary.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ContentService {

    private final NoticeRepository noticeRepository;
    private final PolicyRepository policyRepository;

    public ContentService(NoticeRepository noticeRepository, PolicyRepository policyRepository) {
        this.noticeRepository = noticeRepository;
        this.policyRepository = policyRepository;
    }

    /**
     * 공지사항 목록 조회
     */
    public List<NoticeResponse> getNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByImportantDescCreatedAtDesc();
        return notices.stream()
                .map(NoticeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 이용약관 조회
     */
    public PolicyResponse getTerms() {
        Policy terms = policyRepository.findByType(PolicyType.TERMS)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND, "이용약관을 찾을 수 없습니다."));
        return PolicyResponse.from(terms);
    }

    /**
     * 개인정보 처리방침 조회
     */
    public PolicyResponse getPrivacyPolicy() {
        Policy privacyPolicy = policyRepository.findByType(PolicyType.PRIVACY_POLICY)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND, "개인정보 처리방침을 찾을 수 없습니다."));
        return PolicyResponse.from(privacyPolicy);
    }

}
