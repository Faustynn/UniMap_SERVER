package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import org.main.unimapapi.entities.Device;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository class for performing JDBC-based CRUD operations on {@link Device} entities.
 *
 * <p><strong>Note:</strong> Despite the name, this is not a DTO but a database access component.</p>
 */
@Repository
@AllArgsConstructor
public class Device_dto {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Maps SQL ResultSet rows to {@link Device} objects.
     */
    private static class DeviceRowMapper implements RowMapper<Device> {
        @Override
        public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Device.builder()
                    .id(rs.getLong("id"))
                    .deviceId(rs.getString("device_id"))
                    .pushToken(rs.getString("push_token"))
                    .platform(rs.getString("platform"))
                    .type(rs.getString("type"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .lastActiveAt(rs.getObject("last_active_at", LocalDateTime.class))
                    .build();
        }
    }

    /**
     * Returns a list of all devices from the database.
     */
    public List<Device> findAll() {
        String sql = "SELECT * FROM devices";
        return jdbcTemplate.query(sql, new DeviceRowMapper());
    }

    /**
     * Retrieves a device by its ID.
     *
     * @param id the device's ID
     * @return the found {@link Device} or null if not found
     */
    public Device findById(Long id) {
        String sql = "SELECT * FROM devices WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new DeviceRowMapper(), id);
    }

    /**
     * Saves a new device record in the database.
     */
    public void save(Device device) {
        String sql = "INSERT INTO devices (device_id, push_token, platform, type, created_at, last_active_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, device.getDeviceId(), device.getPushToken(), device.getPlatform(),
                device.getType(), device.getCreatedAt(), device.getLastActiveAt());
    }

    /**
     * Updates an existing device record.
     */
    public void update(Device device) {
        String sql = "UPDATE devices SET device_id = ?, push_token = ?, platform = ?, type = ?, " +
                "created_at = ?, last_active_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, device.getDeviceId(), device.getPushToken(), device.getPlatform(),
                device.getType(), device.getCreatedAt(), device.getLastActiveAt(), device.getId());
    }

    /**
     * Deletes a device by its ID.
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM devices WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}