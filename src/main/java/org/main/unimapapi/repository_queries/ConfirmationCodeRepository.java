package org.main.unimapapi.repository_queries;

import org.main.unimapapi.entities.ConfirmationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class ConfirmationCodeRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ConfirmationCode> confirmationCodeRowMapper = new RowMapper<ConfirmationCode>() {
        @Override
        public ConfirmationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfirmationCode code = new ConfirmationCode();
            code.setUserId(rs.getLong("id_code"));
            code.setCode(rs.getString("code"));
            code.setExpirationTime(rs.getTimestamp("exp_time").toLocalDateTime());
            return code;
        }
    };

    public Optional<ConfirmationCode> findByUserId(Long userId) {
        String sql = "SELECT * FROM confirm_codes WHERE id_code = ?";
        return jdbcTemplate.query(sql, confirmationCodeRowMapper, userId).stream().findFirst();
    }

    public void save(ConfirmationCode confirmationCode) {
        String sql = "INSERT INTO confirm_codes (id_code, code, exp_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, confirmationCode.getUserId(), confirmationCode.getCode(), confirmationCode.getExpirationTime());
    }

    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM confirm_codes WHERE id_code = ?";
        jdbcTemplate.update(sql, userId);
    }
}