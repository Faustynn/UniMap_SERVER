package org.main.unimapapi.controllers;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.DeviceRegistrationRequest;
import org.main.unimapapi.dtos.NotificationResponse;
import org.main.unimapapi.entities.Device;
import org.main.unimapapi.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final DeviceService deviceService;

    @PostMapping("/register-device")
    public ResponseEntity<NotificationResponse> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        try {
            Device device = deviceService.registerDevice(
                    request.getDeviceId(),
                    request.getPushToken(),
                    request.getPlatform(),
                    request.getType()
            );

            return ResponseEntity.ok(new NotificationResponse(true, "Device registered successfully", device.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new NotificationResponse(false, "Failed to register device: " + e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/unregister-device/{deviceId}")
    public ResponseEntity<NotificationResponse> unregisterDevice(@PathVariable String deviceId) {
        boolean result = deviceService.unregisterDevice(deviceId);

        if (result) {
            return ResponseEntity.ok(
                    new NotificationResponse(true, "Device unregistered successfully", null)
            );
        } else {
            return ResponseEntity.badRequest().body(
                    new NotificationResponse(false, "Failed to unregister device", null)
            );
        }
    }


}