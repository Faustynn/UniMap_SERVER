package org.main.unimapapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DeviceRegistration {
    private String deviceId;
    private String fcmToken;
    private String platform; // "ios" or "android"
    private LocalDateTime lastUpdated;

    public void setLastUpdatedNow() {
        this.lastUpdated = LocalDateTime.now();
    }
}