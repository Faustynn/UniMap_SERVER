package org.main.unimapapi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.configs.AppConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;

/**
 * Utility component for generating, parsing, and validating JWT access and refresh tokens.
 * <p>
 * Uses HMAC-SHA signing keys loaded from {@link AppConfig}.
 * Supports both access and refresh tokens, each with different keys and expiration times.
 */
@Component
@RequiredArgsConstructor
public class JwtToken {
    private final long EXPIRATION_TIME_ACCESS = AppConfig.getEXPIRATION_TIME_ACCESS();
    private final long EXPIRATION_TIME_REFRESH = AppConfig.getEXPIRATION_TIME_REFRESH();

    private Key accessSigningKey;
    private Key refreshSigningKey;

    /**
     * Initializes signing keys after component construction.
     * Uses secret keys provided by {@link AppConfig}.
     */
    @PostConstruct
    public void init() {
        accessSigningKey = Keys.hmacShaKeyFor(AppConfig.getAccessKey().getBytes());
        refreshSigningKey = Keys.hmacShaKeyFor(AppConfig.getRefreshKey().getBytes());
    }


    /**
     * Generates a signed JWT access token.
     *
     * @param login username or login identifier
     * @return JWT access token as a string
     */
    public String generateAccessToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS))
                .signWith(accessSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Generates a signed JWT refresh token.
     *
     * @param login username or login identifier
     * @return JWT refresh token as a string
     */
    public String generateRefreshToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH))
                .signWith(refreshSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Extracts username (subject) from the access token.
     *
     * @param token access token
     * @return username from token
     * @throws ResponseStatusException if token is invalid or expired
     */
    public String extractUsernameFromAccessToken(String token) {
        try {
            return extractClaims(token, accessSigningKey).getSubject();
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Invalid access token: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
    }


    /**
     * Extracts username (subject) from the refresh token.
     *
     * @param token refresh token
     * @return username from token
     * @throws ResponseStatusException if token is invalid or expired
     */
    public String extractUsernameFromRefreshToken(String token) {
        try {
            return extractClaims(token, refreshSigningKey).getSubject();
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Invalid refresh token: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    /**
     * Parses token and returns all claims (internal use).
     *
     * @param token JWT string
     * @param key   signing key to validate
     * @return parsed {@link Claims}
     */
    private Claims extractClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * Validates access token with username and expiration check.
     *
     * @param token    access token
     * @param username expected subject (username)
     * @return true if valid, false otherwise
     */
    public boolean validateAccessToken(String token, String username) {
        try {
            Claims claims = extractClaims(token, accessSigningKey);
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Error validating access token for user " + username + ": " + e.getMessage());
            return false;
        }
    }


    /**
     * Validates access token against itself (extracts subject internally).
     *
     * @param token access token
     * @return true if valid, false otherwise
     */
    public boolean validateAccessToken(String token) {
        try {
            String username = extractUsernameFromAccessToken(token);
            Claims claims = extractClaims(token, accessSigningKey);
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Error validating access token: " + e.getMessage());
            return false;
        }
    }


    /**
     * Validates refresh token against expected username.
     *
     * @param token    refresh token
     * @param username expected username (subject)
     * @return true if valid, false otherwise
     */
    public boolean validateRefreshToken(String token, String username) {
        try {
            Claims claims = extractClaims(token, refreshSigningKey);
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Error validating refresh token for user " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts expiration date from a refresh token.
     *
     * @param token refresh token
     * @return expiration {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, refreshSigningKey).getExpiration();
    }
}