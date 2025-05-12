package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegistrationRequest {
    private String deviceId;
    private String pushToken;
    private String platform; // ios, android
    private String type;     // expo, fcm
}
