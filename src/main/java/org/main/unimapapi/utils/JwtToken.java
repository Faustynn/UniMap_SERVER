package org.main.unimapapi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import org.main.unimapapi.configs.AppConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;

@Builder
@Component
public class JwtToken {
    private static final String ACCESS_SECRET_KEY = AppConfig.getAccessKey();
    private static final String REFRESH_SECRET_KEY = AppConfig.getRefreshKey();

    private static final long EXPIRATION_TIME_ACCESS = 60000; // 1 min
    private static final long EXPIRATION_TIME_REFRESH = 86400000; // 1 day

    private Key getAccessSigningKey() {
        return Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes());
    }
    private Key getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS))
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH))
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsernameFromAccessToken(String token) {
        try {
            return extractClaims(token, getAccessSigningKey()).getSubject();
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Invalid access token: "
                    + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
    }
    public String extractUsernameFromRefreshToken(String token) {
        try {
            return extractClaims(token, getRefreshSigningKey()).getSubject();
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Invalid refresh token: "
                    + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    private Claims extractClaims(String token, Key key) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateAccessToken(String token, String username) {
        try {
            Claims claims = extractClaims(token, getAccessSigningKey());
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error validating access token for user: "
                    + username + ". Error: " + e.getMessage());
            return false;
        }
    }


    public boolean validateAccessToken(String token) {
        String username = extractUsernameFromAccessToken(token);
        try {
            Claims claims = extractClaims(token, getAccessSigningKey());
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error validating access token for user: "
                    + username + ". Error: " + e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token, String username) {
        try {
            Claims claims = extractClaims(token, getRefreshSigningKey());
            return claims.getSubject().equals(username) &&
                    !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error validating refresh token for user: "
                    + username + ". Error: " + e.getMessage());
            return false;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, getRefreshSigningKey()).getExpiration();
    }
}