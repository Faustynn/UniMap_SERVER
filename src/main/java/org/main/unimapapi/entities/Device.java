package org.main.unimapapi.entities;

import lombok.*;
import java.time.LocalDateTime;


/**
 * Entity representing a registered device used for push notifications.
 *
 * <p>This includes device identification, platform, token, and timestamps for activity tracking.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    /**
     * Unique identifier for the device record in the database.
     */
    private Long id;

    /**
     * Unique device identifier from the client (e.g., UUID).
     */
    private String deviceId;

    /**
     * Push notification token associated with the device.
     */
    private String pushToken;

    /**
     * Platform of the device (e.g., {@code "android"}, {@code "ios"}).
     */
    private String platform;

    /**
     * Notification type or protocol (e.g., {@code "fcm"}, {@code "expo"}).
     */
    private String type;

    /**
     * Timestamp of when the device was first registered.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last activity from the device.
     */
    private LocalDateTime lastActiveAt;
}