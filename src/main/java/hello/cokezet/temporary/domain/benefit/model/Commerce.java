package hello.cokezet.temporary.domain.benefit.model;

import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "commerce")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commerce extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "preferredCommerces")
    private Set<User> preferringUsers = new HashSet<>();

}
