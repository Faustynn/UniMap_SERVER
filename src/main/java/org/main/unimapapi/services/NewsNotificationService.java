package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.entities.Device;
import org.main.unimapapi.dtos.News_dto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class NewsNotificationService {
    private static final Logger LOGGER = Logger.getLogger(NewsNotificationService.class.getName());

    private final DeviceService deviceService;
    private final FirebaseNotificationService firebaseNotificationService;



    @Async
    public CompletableFuture<Integer> sendNewsNotificationToAllDevices(News_dto news) {
        try {
            List<String> activeTokens = deviceService.getAllActivePushTokens();

            if (activeTokens == null || activeTokens.isEmpty()) {
                LOGGER.info("No active devices to send notifications to");
                return CompletableFuture.completedFuture(0);
            }

            LOGGER.info("Sending news notification to " + activeTokens.size() + " devices");

            String newsId = String.valueOf(news.getId());
            int sentCount = firebaseNotificationService.sendNewsNotificationToMultipleDevices(
                    activeTokens,
                    newsId,
                    news.getTitle(),
                    news.getContent()
            );

            LOGGER.info("Successfully sent notifications to " + sentCount + " devices");
            return CompletableFuture.completedFuture(sentCount);
        } catch (Exception e) {
            LOGGER.severe("Error sending news notifications: " + e.getMessage());
            return CompletableFuture.completedFuture(0);
        }
    }


    public boolean sendNewsNotificationToDevice(News_dto news, String deviceId) {
        try {
            Device device = deviceService.findByDeviceId(deviceId);

            if (device == null || device.getPushToken() == null) {
                LOGGER.warning("Device not found or has no push token: " + deviceId);
                return false;
            }

            String newsId = String.valueOf(news.getId());
            String result = firebaseNotificationService.sendNewsNotification(
                    device.getPushToken(),
                    newsId,
                    news.getTitle(),
                    news.getContent(),
                    device.getPlatform()
            );

            return result != null;
        } catch (Exception e) {
            LOGGER.severe("Error sending news notification to device: " + e.getMessage());
            return false;
        }
    }
}