package org.main.unimapapi.entities;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String login;
    private String email;
    private String password;
    private String username;
    private boolean isAdmin;
    private boolean isPremium;
    private String avatar;
    private List<TokenEntity> tokens;
}