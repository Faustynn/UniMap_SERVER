package org.main.unimapapi.repository_queries;

import lombok.AllArgsConstructor;
import org.main.unimapapi.entities.Device;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class DeviceRepository {
    private final JdbcTemplate jdbcTemplate;


    public Optional<Device> findByDeviceId(String deviceId) {
        String sql = "SELECT * FROM devices WHERE device_id = ?";
        List<Device> devices = jdbcTemplate.query(sql, new DeviceRowMapper(), deviceId);
        return devices.stream().findFirst();
    }

    public void deleteByDeviceId(String deviceId) {
        String sql = "DELETE FROM devices WHERE device_id = ?";
        jdbcTemplate.update(sql, deviceId);
    }

    public List<Device> findByLastActiveAtAfter(LocalDateTime date) {
        String sql = "SELECT * FROM devices WHERE last_active_at > ?";
        return jdbcTemplate.query(sql, new DeviceRowMapper(), date);
    }

    public List<String> findAllActivePushTokens(LocalDateTime date) {
        String sql = "SELECT push_token FROM devices WHERE last_active_at > ? AND push_token IS NOT NULL";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("push_token"), date);
    }

    public Device save(Device device) {
        String checkSql = "SELECT COUNT(*) FROM devices WHERE device_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, device.getDeviceId());

        if (count != null && count > 0) {
            String updateSql = "UPDATE devices SET push_token = ?, platform = ?, type = ?, last_active_at = ?, created_at = ? WHERE device_id = ?";
            jdbcTemplate.update(updateSql,
                    device.getPushToken(),
                    device.getPlatform(),
                    device.getType(),
                    device.getLastActiveAt(),
                    device.getCreatedAt(),
                    device.getDeviceId());
        } else {
            String insertSql = "INSERT INTO devices (device_id, push_token, platform, type, last_active_at, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    device.getDeviceId(),
                    device.getPushToken(),
                    device.getPlatform(),
                    device.getType(),
                    device.getLastActiveAt(),
                    device.getCreatedAt());
        }

        return device;
    }

    private static class DeviceRowMapper implements RowMapper<Device> {
        @Override
        public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
            Device device = new Device();
            device.setId(rs.getLong("id"));
            device.setDeviceId(rs.getString("device_id"));
            device.setLastActiveAt(rs.getTimestamp("last_active_at").toLocalDateTime());
            device.setPushToken(rs.getString("push_token"));
            return device;
        }
    }
}