package org.main.unimapapi.services;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FirebaseNotificationService {
    private final FirebaseMessaging firebaseMessaging;


    public String sendNewsNotification(String fcmToken, String newsId, String title, String content, String platform) {
        try {
            // Create notification payload - platform specific configurations
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(content)
                    .build();

            // Add news ID as data
            Map<String, String> data = new HashMap<>();
            data.put("newsId", newsId);
            data.put("type", "NEWS");

            // Build base message
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putAllData(data);

            // Add platform-specific configurations
            applyPlatformSpecificConfig(messageBuilder, platform);

            // Send message
            String response = firebaseMessaging.send(messageBuilder.build());
            System.out.println("Successfully sent news notification to " + platform + " device: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Failed to send notification to " + platform + " device: " + e.getMessage());
            return null;
        }
    }

    public int sendNewsNotificationToMultipleDevices(
            List<String> fcmTokens,
            String newsId,
            String title,
            String content) {

        if (fcmTokens == null || fcmTokens.isEmpty()) {
            return 0;
        }

        try {
            // For smaller batches (up to 500), use multicast
            if (fcmTokens.size() <= 500) {
                return sendMulticastNotification(fcmTokens, newsId, title, content);
            }
            // For larger batches, split into multiple requests
            else {
                int totalSent = 0;
                List<List<String>> tokenBatches = splitIntoBatches(fcmTokens, 500);

                for (List<String> batch : tokenBatches) {
                    totalSent += sendMulticastNotification(batch, newsId, title, content);
                }

                return totalSent;
            }
        } catch (Exception e) {
            System.out.println("Failed to send batch notifications: " + e.getMessage());
            return 0;
        }
    }

    private int sendMulticastNotification(List<String> tokens, String newsId, String title, String content)
            throws FirebaseMessagingException {
        // Create notification
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        // Data payload
        Map<String, String> data = new HashMap<>();
        data.put("newsId", newsId);
        data.put("type", "NEWS");

        // Create multicast message
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putAllData(data)
                .addAllTokens(tokens)
                .build();

        // Send the message
        BatchResponse response = firebaseMessaging.sendMulticast(message);

        System.out.println("Multicast completed: " + response.getSuccessCount() +
                " messages were sent successfully, " +
                response.getFailureCount() + " failed");

        return response.getSuccessCount();
    }

    private <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(list.subList(i, end));
        }

        return batches;
    }



    public boolean sendAsync(Message message) {
        try {
            firebaseMessaging.sendAsync(message).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to send async notification: " + e.getMessage());
            return false;
        }
    }


    private void applyPlatformSpecificConfig(Message.Builder messageBuilder, String platform) {
        if ("ios".equalsIgnoreCase(platform)) {
            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .setContentAvailable(true)
                            .setMutableContent(true)
                            .build())
                    .build();

            messageBuilder.setApnsConfig(apnsConfig);
        } else if ("android".equalsIgnoreCase(platform)) {
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                            .build())
                    .build();

            messageBuilder.setAndroidConfig(androidConfig);
        }
    }
}