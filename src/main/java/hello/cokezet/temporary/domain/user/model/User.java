package hello.cokezet.temporary.domain.user.model;

import hello.cokezet.temporary.domain.benefit.model.CardCompany;
import hello.cokezet.temporary.domain.benefit.model.Commerce;
import hello.cokezet.temporary.global.model.BaseTimeEntity;
import hello.cokezet.temporary.global.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_preferred_commerce",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "commerce_id")
    )
    private Set<Commerce> preferredCommerces = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_preferred_card_company",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "card_company_id")
    )
    private Set<CardCompany> preferredCardCompanies = new HashSet<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column
    private LocalDateTime deletedAt;

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void setPreferredCommerces(Set<Commerce> commerces) {
        this.preferredCommerces.clear();

        if (commerces != null) {
            this.preferredCommerces.addAll(commerces);
        }
    }

    public void setPreferredCardCompanies(Set<CardCompany> cardCompanies) {
        this.preferredCardCompanies.clear();

        if (cardCompanies != null) {
            this.preferredCardCompanies.addAll(cardCompanies);
        }
    }

    public boolean isProfileComplete() {
        return nickname != null && !nickname.isBlank() && !preferredCommerces.isEmpty() && !preferredCardCompanies.isEmpty();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

}
