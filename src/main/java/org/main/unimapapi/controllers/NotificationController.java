package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.DeviceRegistrationRequest;
import org.main.unimapapi.dtos.NotificationResponse;
import org.main.unimapapi.entities.Device;
import org.main.unimapapi.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller for managing push notification device registration.
 *
 * <p><strong>Base URL:</strong> <code>/api/notifications</code></p>
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final DeviceService deviceService;


    /**
     * Registers a new device for push notifications.
     *
     * @param request the registration details including device ID, platform, type, and push token
     * @return a {@link NotificationResponse} indicating success or failure
     */
    @Operation(summary = "Register device", description = "Registers a device to receive push notifications.")
    @ApiResponse(responseCode = "200", description = "Device registered successfully")
    @ApiResponse(responseCode = "400", description = "Failed to register device")
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

    /**
     * Unregisters a device from receiving push notifications.
     *
     * @param deviceId the ID of the device to unregister
     * @return a {@link NotificationResponse} indicating success or failure
     */
    @Operation(summary = "Unregister device", description = "Removes a device from the push notification list.")
    @ApiResponse(responseCode = "200", description = "Device unregistered successfully")
    @ApiResponse(responseCode = "400", description = "Failed to unregister device")
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