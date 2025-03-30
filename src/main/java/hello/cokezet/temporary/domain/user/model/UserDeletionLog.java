package hello.cokezet.temporary.domain.user.model;

import hello.cokezet.temporary.global.model.BaseTimeEntity;
import hello.cokezet.temporary.global.model.SocialProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원탈퇴 로그를 저장하는 엔티티
 * 사용자가 탈퇴한 후에도 분석 및 감사 목적으로 최소한의 정보를 저장
 */
@Entity
@Table(name = "user_deletion_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletionLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 탈퇴한 사용자의 ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 탈퇴한 사용자의 이메일
     */
    @Column(nullable = false)
    private String email;

    /**
     * 탈퇴한 사용자의 닉네임
     */
    @Column(nullable = false)
    private String nickname;

    /**
     * 소셜 로그인 제공자 (GOOGLE, APPLE 등)
     */
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    /**
     * 소셜 플랫폼에서의 사용자 ID
     */
    private String socialProviderId;

    /**
     * 탈퇴 시간
     */
    @Column(nullable = false)
    private LocalDateTime deletedAt;
}
