package hello.cokezet.temporary.domain.user.model;

import hello.cokezet.temporary.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Setter
    private boolean isUsed; // 토큰 사용 여부 (Rotation 확인용)

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

}
