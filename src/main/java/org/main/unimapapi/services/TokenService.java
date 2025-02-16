package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.main.unimapapi.entities.TokenEntity;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.TokenRepositoryImpl;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
// TODO: LOGING WITH @Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepositoryImpl tokenRepository;
    private final JwtToken jwtToken;

    public void saveUserToken(User user, String refreshToken) {
        Date expiryDate = jwtToken.extractExpiration(refreshToken);

        TokenEntity token = TokenEntity.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .revoked(false)
                .expiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(token);
    }

    public boolean validateToken(String refreshToken, User user) {
        Optional<TokenEntity> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            System.err.println("Token not found in database");
            return false;
        }

        TokenEntity token = tokenOptional.get();

        if (token.isRevoked()) {
            System.err.println("Token is revoked");
            return false;
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            System.err.println("Token is expired");
            return false;
        }

        if (!token.getUserId().equals(user.getId())) {
            System.err.println("Token belongs to another user");
            return false;
        }

        return true;
    }

    public void revokeToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                });
    }

    public void revokeAllUserTokens(Long userId) {
        tokenRepository.revokeAllUserTokens(userId);
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight!
    public void cleanupExpiredTokens() {
        System.err.println("Starting cleanup of expired tokens");
        try {
            // Delete old tokens which have more than 7 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

            int deletedTokens = tokenRepository.deleteExpiredTokens(cutoffDate);
            System.err.println("Deleted expired tokens: " + deletedTokens);
        } catch (Exception e) {
            System.err.println("Error during token cleanup: " + e);
        }
    }

    public String createAccessToken(User user) {
        return jwtToken.generateAccessToken(user.getUsername());
    }

    public boolean validateAccessToken(String accessToken, User user) {
        return jwtToken.validateAccessToken(accessToken, user.getUsername());
    }

    public String extractUsernameFromAccessToken(String accessToken) {
        return jwtToken.extractUsernameFromAccessToken(accessToken);
    }
}