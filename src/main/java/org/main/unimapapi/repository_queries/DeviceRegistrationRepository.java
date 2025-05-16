package org.main.unimapapi.repository_queries;

import org.main.unimapapi.models.DeviceRegistration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing device registration records in the database.
 *
 * <p>Handles storing, updating, and querying device push token registrations (FCM).</p>
 * <p><strong>Table:</strong> {@code device_registrations}</p>
 */
@Repository
public class DeviceRegistrationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeviceRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Maps SQL result set rows to {@link DeviceRegistration} objects.
     */
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

    /**
     * Saves or updates a device registration.
     * <p>If the {@code device_id} already exists, updates its fields.</p>
     *
     * @param registration the device registration to store
     * @return the same registration object
     */
    public DeviceRegistration save(DeviceRegistration registration) {
        String sql = "INSERT INTO device_registrations (device_id, fcm_token, platform, last_updated) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT (device_id) DO UPDATE SET fcm_token = EXCLUDED.fcm_token, platform = EXCLUDED.platform, last_updated = EXCLUDED.last_updated";
        jdbcTemplate.update(sql, registration.getDeviceId(), registration.getFcmToken(), registration.getPlatform(), registration.getLastUpdated());
        return registration;
    }

    /**
     * Finds a device registration by its device ID.
     *
     * @param deviceId the device ID to search for
     * @return an {@link Optional} containing the device if found
     */
    public Optional<DeviceRegistration> findById(String deviceId) {
        String sql = "SELECT * FROM device_registrations WHERE device_id = ?";
        List<DeviceRegistration> results = jdbcTemplate.query(sql, rowMapper, deviceId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Returns all registered devices.
     *
     * @return list of {@link DeviceRegistration}
     */
    public List<DeviceRegistration> findAll() {
        String sql = "SELECT * FROM device_registrations";
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Returns all devices for a given platform (e.g., {@code "android"}, {@code "ios"}).
     *
     * @param platform the platform filter
     * @return list of devices matching the platform
     */
    public List<DeviceRegistration> findByPlatform(String platform) {
        String sql = "SELECT * FROM device_registrations WHERE platform = ?";
        return jdbcTemplate.query(sql, rowMapper, platform);
    }

    /**
     * Checks if a device with the given ID exists in the database.
     *
     * @param deviceId the device ID to check
     * @return {@code true} if the device exists, otherwise {@code false}
     */
    public boolean existsById(String deviceId) {
        String sql = "SELECT COUNT(*) FROM device_registrations WHERE device_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, deviceId);
        return count != null && count > 0;
    }

    /**
     * Deletes a device registration by its device ID.
     *
     * @param deviceId the device ID to delete
     */
    public void deleteById(String deviceId) {
        String sql = "DELETE FROM device_registrations WHERE device_id = ?";
        jdbcTemplate.update(sql, deviceId);
    }

    /**
     * Counts the total number of registered devices.
     *
     * @return number of device registrations
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM device_registrations";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}