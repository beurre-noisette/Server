package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.benefit.model.CardCompany;
import hello.cokezet.temporary.domain.benefit.model.Commerce;
import hello.cokezet.temporary.domain.benefit.repository.CardCompanyRepository;
import hello.cokezet.temporary.domain.benefit.repository.CommerceRepository;
import hello.cokezet.temporary.domain.user.dto.ProfileResponse;
import hello.cokezet.temporary.domain.user.dto.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 닉네임 업데이트
        user.updateProfile(request.getNickname());

        // 선호 커머스 설정
        if (request.getCommerceIds() != null && !request.getCommerceIds().isEmpty()) {
            Set<Commerce> commerces = new HashSet<>(commerceRepository.findAllById(request.getCommerceIds()));

            user.setPreferredCommerces(commerces);
        }
        
        // 선호 카드사 설정
        if (request.getCardCompanyIds() != null && !request.getCardCompanyIds().isEmpty()) {
            Set<CardCompany> cardCompanies = new HashSet<>(cardCompanyRepository.findAllById(request.getCardCompanyIds()));

            user.setPreferredCardCompanies(cardCompanies);
        }

        userRepository.save(user);
        log.info("사용자 프로필 업데이트 완료 = {}", user.getId());
    }

    @Transactional
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return user.isProfileComplete();
    }
}
