package org.main.unimapapi.controllers;

import lombok.*;
import org.main.unimapapi.models.DeviceRegistration;
import org.main.unimapapi.services.DeviceRegistrationService;
import org.main.unimapapi.services.FirebaseNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final FirebaseNotificationService firebaseNotificationService;
    private final DeviceRegistrationService deviceRegistrationService;

    @PostMapping("/register-device")
    public ResponseEntity<Map<String, String>> registerDevice(@RequestBody @Validated DeviceRegistrationRequest request) {
        DeviceRegistration registration = new DeviceRegistration();
        registration.setDeviceId(request.getDeviceId());
        registration.setFcmToken(request.getFcmToken());
        registration.setPlatform(request.getPlatform());

        deviceRegistrationService.saveDeviceRegistration(registration);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Device registered successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-news")
    public ResponseEntity<Map<String, Object>> sendNewsNotification(@RequestBody @Validated NewsNotificationRequest request) {
        List<DeviceRegistration> devices = deviceRegistrationService.getAllDeviceRegistrations();
        int sentCount = 0;

        for (DeviceRegistration device : devices) {
            String result = firebaseNotificationService.sendNewsNotification(
                    device.getFcmToken(),
                    request.getNewsId(),
                    request.getTitle(),
                    request.getContent(),
                    device.getPlatform()
            );

            if (result != null) {
                sentCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "News notifications sent");
        response.put("sentCount", sentCount);
        response.put("totalDevices", devices.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/device-count")
    public ResponseEntity<Map<String, Object>> getRegisteredDeviceCount() {
        int count = deviceRegistrationService.getDeviceCount();

        Map<String, Object> response = new HashMap<>();
        response.put("deviceCount", count);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unregister-device/{deviceId}")
    public ResponseEntity<Map<String, String>> unregisterDevice(@PathVariable String deviceId) {
        boolean success = deviceRegistrationService.removeDeviceRegistration(deviceId);

        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("status", "success");
            response.put("message", "Device unregistered successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Device not found or could not be unregistered");
        }

        return ResponseEntity.ok(response);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceRegistrationRequest {
        private String deviceId;
        private String fcmToken;
        private String platform; // "ios" or "android"
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestNotificationRequest {
        private String fcmToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsNotificationRequest {
        private String newsId;
        private String title;
        private String content;
    }
}