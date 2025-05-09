package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.models.DeviceRegistration;
import org.main.unimapapi.repository_queries.DeviceRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceRegistrationService {

    private final DeviceRegistrationRepository deviceRegistrationRepository;

    public DeviceRegistration saveDeviceRegistration(DeviceRegistration registration) {
        registration.setLastUpdatedNow();
        return deviceRegistrationRepository.save(registration);
    }

    public Optional<DeviceRegistration> getDeviceRegistration(String deviceId) {
        return deviceRegistrationRepository.findById(deviceId);
    }


    public List<DeviceRegistration> getAllDeviceRegistrations() {
        return deviceRegistrationRepository.findAll();
    }


    public List<DeviceRegistration> getDeviceRegistrationsByPlatform(String platform) {
        return deviceRegistrationRepository.findByPlatform(platform);
    }


    public boolean removeDeviceRegistration(String deviceId) {
        if (deviceRegistrationRepository.existsById(deviceId)) {
            deviceRegistrationRepository.deleteById(deviceId);
            return true;
        }
        return false;
    }


    public int getDeviceCount() {
        return (int) deviceRegistrationRepository.count();
    }
}