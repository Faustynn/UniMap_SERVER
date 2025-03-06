package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtToken jwtToken;

    public String createAccessToken(User user) {
        return jwtToken.generateAccessToken(user.getLogin());
    }
    public String createAccessToken(String username) {
        return jwtToken.generateAccessToken(username);
    }

    public boolean validateAccessToken(String accessToken, User user) {
        return jwtToken.validateAccessToken(accessToken, user.getUsername());
    }
    public boolean validateAccessToken(String accessToken, String username) {
        return jwtToken.validateAccessToken(accessToken, username);
    }


    public String getLoginFromRefreshToken(String refreshToken) {
        return jwtToken.extractUsernameFromRefreshToken(refreshToken);
    }
    public String getLoginFromAccessToken(String accessToken) {
        return jwtToken.extractUsernameFromAccessToken(accessToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        String username = jwtToken.extractUsernameFromRefreshToken(refreshToken);
        return jwtToken.validateRefreshToken(refreshToken, username);
    }
}