package org.main.unimapapi.repository_queries;

import org.main.unimapapi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setLogin(rs.getString("login"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setUsername(rs.getString("username"));
            user.setAdmin(rs.getBoolean("is_admin"));
            user.setSubscribe(rs.getBoolean("subscribe"));
            user.setVerification(rs.getBoolean("verification"));
            user.setAvatar(rs.getInt("avatar"));
            return user;
        }
    };

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, login);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void save(User user) {
        String sql = "INSERT INTO users (login, email, password, username, is_admin, subscribe, verification, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getLogin(), user.getEmail(), user.getPassword(), user.getUsername(), user.isAdmin(), user.isSubscribe(), user.isVerification(), user.getAvatar());
    }

    public void update(User user) {
        String sql = "UPDATE users SET login = ?, email = ?, password = ?, username = ?, is_admin = ?, subscribe = ?, verification = ?, avatar = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getEmail(), user.getPassword(), user.getUsername(), user.isAdmin(), user.isSubscribe(), user.isVerification(), user.getAvatar(), user.getId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}