package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.stereotype.Service;

/**
 * Service for generating and validating JWT access and refresh tokens.
 *
 * <p>Delegates core logic to {@link JwtToken}, but adds convenience overloads for user-level operations.</p>
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtToken jwtToken;

    /**
     * Generates an access token for the given user.
     *
     * @param user the user entity
     * @return a signed JWT access token
     */
    public String createAccessToken(User user) {
        return createAccessToken(user.getLogin());
    }

    /**
     * Generates an access token for the given login.
     *
     * @param login the user's login
     * @return a signed JWT access token
     */
    public String createAccessToken(String login) {
        return jwtToken.generateAccessToken(login);
    }

    /**
     * Validates whether the given access token belongs to the specified user.
     *
     * @param accessToken the token to validate
     * @param user        the user to match against
     * @return {@code true} if valid, otherwise {@code false}
     */
    public boolean validateAccessToken(String accessToken, User user) {
        return validateAccessToken(accessToken, user.getUsername());
    }

    /**
     * Validates an access token against a specific username.
     *
     * @param accessToken the token to validate
     * @param username    the expected subject of the token
     * @return {@code true} if valid, otherwise {@code false}
     */
    public boolean validateAccessToken(String accessToken, String username) {
        return jwtToken.validateAccessToken(accessToken, username);
    }

    /**
     * Extracts the login from a refresh token.
     *
     * @param refreshToken the JWT refresh token
     * @return the login (username) embedded in the token
     */
    public String getLoginFromRefreshToken(String refreshToken) {
        return jwtToken.extractUsernameFromRefreshToken(refreshToken);
    }

    /**
     * Extracts the login from an access token.
     *
     * @param accessToken the JWT access token
     * @return the login (username) embedded in the token
     */
    public String getLoginFromAccessToken(String accessToken) {
        return jwtToken.extractUsernameFromAccessToken(accessToken);
    }

    /**
     * Validates a refresh token based on its signature and username.
     *
     * @param refreshToken the JWT refresh token
     * @return {@code true} if valid, otherwise {@code false}
     */
    public boolean validateRefreshToken(String refreshToken) {
        String username = getLoginFromRefreshToken(refreshToken);
        return jwtToken.validateRefreshToken(refreshToken, username);
    }
}