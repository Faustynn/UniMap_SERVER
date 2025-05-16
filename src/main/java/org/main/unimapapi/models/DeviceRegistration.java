package org.main.unimapapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model representing a device registration entry for push notifications.
 *
 * <p>This class tracks the device ID, FCM token, platform type, and last update timestamp.</p>
 */
@Data
@NoArgsConstructor
public class DeviceRegistration {
    /**
     * Unique identifier of the device (e.g., UUID or system ID).
     */
    private String deviceId;

    /**
     * Firebase Cloud Messaging token used for delivering push notifications.
     */
    private String fcmToken;

    /**
     * Platform type of the device, typically {@code "ios"} or {@code "android"}.
     */
    private String platform;

    /**
     * Timestamp of the last update to this registration.
     */
    private LocalDateTime lastUpdated;

    /**
     * Updates the {@code lastUpdated} field to the current system time.
     */
    public void setLastUpdatedNow() {
        this.lastUpdated = LocalDateTime.now();
    }
}