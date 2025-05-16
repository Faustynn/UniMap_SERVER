package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.entities.Device;
import org.main.unimapapi.repository_queries.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling device registration, updates, and push notification logic.
 *
 * <p>Works with the {@link DeviceRepository} to manage device-related operations
 * such as registering, updating activity, and retrieving push tokens.</p>
 */
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    /**
     * Registers a device or updates an existing one with new token and metadata.
     *
     * @param deviceId   Unique ID of the device
     * @param pushToken  Push notification token (e.g., FCM or Expo)
     * @param platform   Device platform (e.g., "android", "ios")
     * @param type       Push token type (e.g., "fcm", "expo")
     * @return the saved or updated {@link Device}
     */
    @Transactional
    public Device registerDevice(String deviceId, String pushToken, String platform, String type) {
        Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);

        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setPushToken(pushToken);
            device.setPlatform(platform);
            device.setType(type);
            device.setLastActiveAt(LocalDateTime.now());
            return deviceRepository.save(device);
        } else {
            Device device = Device.builder()
                    .deviceId(deviceId)
                    .pushToken(pushToken)
                    .platform(platform)
                    .type(type)
                    .createdAt(LocalDateTime.now())
                    .lastActiveAt(LocalDateTime.now())
                    .build();
            return deviceRepository.save(device);
        }
    }

    /**
     * Unregisters a device by deleting its record.
     *
     * @param deviceId device identifier
     * @return {@code true} if deleted successfully, otherwise {@code false}
     */
    @Transactional
    public boolean unregisterDevice(String deviceId) {
        try {
            deviceRepository.deleteByDeviceId(deviceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Finds a registered device by its ID.
     *
     * @param deviceId device identifier
     * @return the {@link Device}, or {@code null} if not found
     */
    public Device findByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId).orElse(null);
    }

    /**
     * Retrieves all devices that have been active in the last 30 days.
     *
     * @return list of active devices
     */
    public List<Device> getAllActiveDevices() {
        return deviceRepository.findByLastActiveAtAfter(
                LocalDateTime.now().minusDays(30));
    }

    /**
     * Retrieves all push tokens from devices active in the last 30 days.
     *
     * @return list of push tokens
     */
    public List<String> getAllActivePushTokens() {
        return deviceRepository.findAllActivePushTokens(
                LocalDateTime.now().minusDays(30));
    }

    /**
     * Updates the last active timestamp of a device to now.
     *
     * @param deviceId device identifier
     */
    @Transactional
    public void updateLastActive(String deviceId) {
        deviceRepository.findByDeviceId(deviceId)
                .ifPresent(device -> {
                    device.setLastActiveAt(LocalDateTime.now());
                    deviceRepository.save(device);
                });
    }
}