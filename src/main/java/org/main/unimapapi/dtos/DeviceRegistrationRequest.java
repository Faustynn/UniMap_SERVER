package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a request to register a device for push notifications.
 *
 * <p>This is used by the client app when sending its push token to the backend.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegistrationRequest {

    /**
     * Unique identifier of the device (could be UUID or system-assigned ID).
     */
    private String deviceId;

    /**
     * Push token used for sending notifications to this device.
     */
    private String pushToken;

    /**
     * Platform of the device, e.g., {@code "android"} or {@code "ios"}.
     */
    private String platform; // ios, android

    /**
     * Notification service type, e.g., {@code "expo"} or {@code "fcm"}.
     */
    private String type;     // expo, fcm
}
