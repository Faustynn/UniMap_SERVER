package org.main.unimapapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String username;

    @Column(nullable = false)
    private boolean isAdmin;

    private boolean subscribe;
    private boolean verification;
    private int avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TokenEntity> tokens;

}