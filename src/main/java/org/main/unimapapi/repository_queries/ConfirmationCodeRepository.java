package org.main.unimapapi.repository_queries;

import org.main.unimapapi.entities.ConfirmationCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Repository for managing confirmation codes in the database.
 *
 * <p>Used for operations related to email verification and password recovery via time-limited codes.</p>
 *
 * <p><b>Database table:</b> {@code confirm_codes}</p>
 */
@Repository
@RequiredArgsConstructor
public class ConfirmationCodeRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Maps result set rows to {@link ConfirmationCode} objects.
     */
    private final RowMapper<ConfirmationCode> confirmationCodeRowMapper = (rs, rowNum) -> {
        ConfirmationCode code = new ConfirmationCode();
        code.setUserId(rs.getLong("id_code"));
        code.setCode(rs.getString("code"));
        code.setExpirationTime(rs.getTimestamp("exp_time").toLocalDateTime());
        return code;
    };

    /**
     * Checks if a given confirmation code exists for a user.
     *
     * @param userId the user ID
     * @param code   the confirmation code string
     * @return {@code true} if a matching code is found, otherwise {@code false}
     */
    public boolean find(Long userId, String code) {
        String sql = "SELECT * FROM confirm_codes WHERE id_code = ? and code = ?";
        List<ConfirmationCode> results = jdbcTemplate.query(sql, confirmationCodeRowMapper, userId, code);
        System.out.println(results);
        return !results.isEmpty();
    }

    /**
     * Stores a new confirmation code in the database.
     *
     * @param confirmationCode the code to save
     * @throws IllegalArgumentException if the code is {@code null}
     */
    public void save(ConfirmationCode confirmationCode) {
        // The only way injection may appear here is
        // When the code is null, jdbc may not screen it properly
        if (confirmationCode.getCode() == null) {
            throw new IllegalArgumentException("CODE CAN NOT BE NULL");
        }

        // If it is ok - continuing
        String sql = "INSERT INTO confirm_codes (id_code, code, exp_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, confirmationCode.getUserId(), confirmationCode.getCode(), confirmationCode.getExpirationTime());
    }

    /**
     * Deletes a confirmation code from the database by user ID and code value.
     *
     * @param userId the user ID
     * @param code   the code to delete
     */
    public void deleteByUserId(Long userId, String code) {
        String sql = "DELETE FROM confirm_codes WHERE code = ? and id_code = ?";
        jdbcTemplate.update(sql, code, userId);
    }
}