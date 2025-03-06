package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.benefit.model.CardCompany;
import hello.cokezet.temporary.domain.benefit.model.Commerce;
import hello.cokezet.temporary.domain.benefit.repository.CardCompanyRepository;
import hello.cokezet.temporary.domain.benefit.repository.CommerceRepository;
import hello.cokezet.temporary.domain.user.dto.response.ProfileResponse;
import hello.cokezet.temporary.domain.user.dto.request.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.error.ErrorCode;
import hello.cokezet.temporary.global.error.exception.BusinessException;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final CommerceRepository commerceRepository;
    private final CardCompanyRepository cardCompanyRepository;

    @Transactional
    public void updateUserProfile(UserPrincipal principal, ProfileUpdateRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        try {
            // 닉네임 업데이트
            if (request.getNickname() != null) {
                user.updateProfile(request.getNickname());
            }

            // 최초 설정 케이스 확인 (프로필이 미완성인 경우)
            boolean isInitialSetup = !user.isProfileComplete();

            // 선호 커머스 설정
            if (request.getCommerceIds() != null) {
                // 최초 설정인 경우 빈 값 체크
                if (isInitialSetup && request.getCommerceIds().isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선호 커머스는 최소 1개 이상 선택해야 합니다.");
                }

                // ID 유효성 검증
                Set<Commerce> commerces = new HashSet<>(commerceRepository.findAllById(request.getCommerceIds()));
                if (commerces.size() != request.getCommerceIds().size()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청한 커머스 ID 중 일부가 존재하지 않습니다.");
                }

                // 기존 값을 새 값으로 대체 (User 클래스의 setPreferredCommerces 메서드가 clear 후 addAll)
                user.setPreferredCommerces(commerces);
            }

            // 선호 카드사 설정
            if (request.getCardCompanyIds() != null) {
                // 최초 설정인 경우 빈 값 체크
                if (isInitialSetup && request.getCardCompanyIds().isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선호 카드사는 최소 1개 이상 선택해야 합니다.");
                }

                // ID 유효성 검증
                Set<CardCompany> cardCompanies = new HashSet<>(cardCompanyRepository.findAllById(request.getCardCompanyIds()));
                if (cardCompanies.size() != request.getCardCompanyIds().size()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청한 카드사 ID 중 일부가 존재하지 않습니다.");
                }

                // 기존 값을 새 값으로 대체
                user.setPreferredCardCompanies(cardCompanies);
            }

            // 최초 설정인 경우, 두 항목 모두 있는지 확인
            if (isInitialSetup && (request.getCommerceIds() == null || request.getCardCompanyIds() == null)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최초 프로필 설정 시 선호 커머스와 카드사 모두 설정해야 합니다.");
            }

            userRepository.save(user);
            log.info("사용자 프로필 업데이트 완료 = {}", user.getId());

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.PROFILE_UPDATE_FAILED, "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 선호 커머스 정보 변환
        Set<ProfileResponse.CommerceInfo> commerceInfos = user.getPreferredCommerces().stream()
                .map(commerce -> ProfileResponse.CommerceInfo.builder()
                        .id(commerce.getId())
                        .name(commerce.getName())
                        .build())
                .collect(Collectors.toSet());

        // 선호 카드사 정보 변환
        Set<ProfileResponse.CardCompanyInfo> cardCompanyInfos = user.getPreferredCardCompanies().stream()
                .map(cardCompany -> ProfileResponse.CardCompanyInfo.builder()
                        .id(cardCompany.getId())
                        .name(cardCompany.getName())
                        .build())
                .collect(Collectors.toSet());

        // 프로필 응답 생성
        return ProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileComplete(user.isProfileComplete())
                .preferredCommerces(commerceInfos)
                .preferredCardCompanies(cardCompanyInfos)
                .build();
    }

    // 사용자 프로필 완성 여부 확인
    public boolean isProfileComplete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return user.isProfileComplete();
    }
}
