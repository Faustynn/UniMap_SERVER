package org.main.unimapapi.repository_queries;

import org.main.unimapapi.models.DeviceRegistration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRegistrationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeviceRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DeviceRegistration> rowMapper = new RowMapper<>() {
        @Override
        public DeviceRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
            DeviceRegistration registration = new DeviceRegistration();
            registration.setDeviceId(rs.getString("device_id"));
            registration.setFcmToken(rs.getString("fcm_token"));
            registration.setPlatform(rs.getString("platform"));
            registration.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
            return registration;
        }
    };

    public DeviceRegistration save(DeviceRegistration registration) {
        String sql = "INSERT INTO device_registrations (device_id, fcm_token, platform, last_updated) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT (device_id) DO UPDATE SET fcm_token = EXCLUDED.fcm_token, platform = EXCLUDED.platform, last_updated = EXCLUDED.last_updated";
        jdbcTemplate.update(sql, registration.getDeviceId(), registration.getFcmToken(), registration.getPlatform(), registration.getLastUpdated());
        return registration;
    }

    public Optional<DeviceRegistration> findById(String deviceId) {
        String sql = "SELECT * FROM device_registrations WHERE device_id = ?";
        List<DeviceRegistration> results = jdbcTemplate.query(sql, rowMapper, deviceId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<DeviceRegistration> findAll() {
        String sql = "SELECT * FROM device_registrations";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<DeviceRegistration> findByPlatform(String platform) {
        String sql = "SELECT * FROM device_registrations WHERE platform = ?";
        return jdbcTemplate.query(sql, rowMapper, platform);
    }

    public boolean existsById(String deviceId) {
        String sql = "SELECT COUNT(*) FROM device_registrations WHERE device_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, deviceId);
        return count != null && count > 0;
    }

    public void deleteById(String deviceId) {
        String sql = "DELETE FROM device_registrations WHERE device_id = ?";
        jdbcTemplate.update(sql, deviceId);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM device_registrations";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}