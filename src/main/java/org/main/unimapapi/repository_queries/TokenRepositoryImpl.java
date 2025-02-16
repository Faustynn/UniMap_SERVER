package org.main.unimapapi.repository_queries;

import org.main.unimapapi.entities.TokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TokenEntity> tokenRowMapper = new RowMapper<TokenEntity>() {
        @Override
        public TokenEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            TokenEntity token = new TokenEntity();
            token.setId(rs.getLong("id"));
            token.setUserId(rs.getLong("user_id"));
            token.setRefreshToken(rs.getString("refresh_token"));
            token.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
            token.setRevoked(rs.getBoolean("revoked"));
            return token;
        }
    };

    public void save(TokenEntity token) {
        String sql = "INSERT INTO tokens (user_id, refresh_token, expiry_date, revoked, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, token.getUserId(), token.getRefreshToken(), token.getExpiryDate(), token.isRevoked(), token.getCreatedAt());
    }
    public Optional<TokenEntity> findByRefreshToken(String refreshToken) {
        String sql = "SELECT * FROM tokens WHERE refresh_token = ?";
        List<TokenEntity> tokens = jdbcTemplate.query(sql, tokenRowMapper, refreshToken);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    public List<TokenEntity> findAllValidTokensByUser(Long userId, LocalDateTime now) {
        String sql = "SELECT * FROM tokens WHERE user_id = ? AND expiry_date > ? AND revoked = false";
        return jdbcTemplate.query(sql, tokenRowMapper, userId, now);
    }

    public void revokeAllUserTokens(Long userId) {
        String sql = "UPDATE tokens SET revoked = true WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public int deleteExpiredTokens(LocalDateTime cutoffDate) {
        String sql = "DELETE FROM tokens WHERE expiry_date < ?";
        return jdbcTemplate.update(sql, cutoffDate);
    }
}