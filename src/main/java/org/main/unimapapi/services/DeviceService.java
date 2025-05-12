package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.entities.Device;
import org.main.unimapapi.repository_queries.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

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

    @Transactional
    public boolean unregisterDevice(String deviceId) {
        try {
            deviceRepository.deleteByDeviceId(deviceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Device findByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId).orElse(null);
    }

    public List<Device> getAllActiveDevices() {
        return deviceRepository.findByLastActiveAtAfter(
                LocalDateTime.now().minusDays(30));
    }

    public List<String> getAllActivePushTokens() {
        return deviceRepository.findAllActivePushTokens(
                LocalDateTime.now().minusDays(30));
    }

    @Transactional
    public void updateLastActive(String deviceId) {
        deviceRepository.findByDeviceId(deviceId)
                .ifPresent(device -> {
                    device.setLastActiveAt(LocalDateTime.now());
                    deviceRepository.save(device);
                });
    }
}