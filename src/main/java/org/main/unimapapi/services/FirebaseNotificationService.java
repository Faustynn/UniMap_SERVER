package org.main.unimapapi.services;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Service for sending push notifications to mobile devices via Firebase Cloud Messaging (FCM).
 *
 * <p>Supports:</p>
 * <ul>
 *     <li>Single-device notification (platform-specific)</li>
 *     <li>Multicast messaging (batch sending to up to 500 devices)</li>
 *     <li>Asynchronous messaging</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class FirebaseNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * Sends a news notification to a specific device with platform-specific configuration.
     *
     * @param fcmToken device FCM token
     * @param newsId   news identifier
     * @param title    notification title
     * @param content  notification body
     * @param platform target platform ("android" or "ios")
     * @return Firebase message ID or {@code null} if failed
     */
    public String sendNewsNotification(String fcmToken, String newsId, String title, String content, String platform) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(content)
                    .build();

            Map<String, String> data = Map.of(
                    "newsId", newsId,
                    "type", "NEWS"
            );

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putAllData(data);

            applyPlatformSpecificConfig(messageBuilder, platform);

            String response = firebaseMessaging.send(messageBuilder.build());
            System.out.println("Successfully sent news notification to " + platform + " device: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Failed to send notification to " + platform + " device: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sends a news notification to multiple devices in batch (splits if >500 tokens).
     *
     * @param fcmTokens list of device tokens
     * @param newsId    news identifier
     * @param title     title of the notification
     * @param content   message body
     * @return number of successfully sent notifications
     */
    public int sendNewsNotificationToMultipleDevices(List<String> fcmTokens, String newsId, String title, String content) {
        if (fcmTokens == null || fcmTokens.isEmpty()) return 0;

        try {
            if (fcmTokens.size() <= 500) {
                return sendMulticastNotification(fcmTokens, newsId, title, content);
            } else {
                int totalSent = 0;
                for (List<String> batch : splitIntoBatches(fcmTokens, 500)) {
                    totalSent += sendMulticastNotification(batch, newsId, title, content);
                }
                return totalSent;
            }
        } catch (Exception e) {
            System.out.println("Failed to send batch notifications: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Sends a multicast news notification to a batch of devices (max 500).
     */
    private int sendMulticastNotification(List<String> tokens, String newsId, String title, String content)
            throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        Map<String, String> data = Map.of(
                "newsId", newsId,
                "type", "NEWS"
        );

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putAllData(data)
                .addAllTokens(tokens)
                .build();

        BatchResponse response = firebaseMessaging.sendMulticast(message);

        System.out.println("Multicast completed: " +
                response.getSuccessCount() + " sent, " +
                response.getFailureCount() + " failed");

        return response.getSuccessCount();
    }

    /**
     * Sends an arbitrary FCM message asynchronously and waits for result.
     *
     * @param message the Firebase {@link Message}
     * @return {@code true} if sent successfully, otherwise {@code false}
     */
    public boolean sendAsync(Message message) {
        try {
            firebaseMessaging.sendAsync(message).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to send async notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds platform-specific settings to the given message builder.
     *
     * @param messageBuilder the message builder
     * @param platform       "ios" or "android"
     */
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

    /**
     * Utility to split a list into sublists of fixed size.
     */
    private <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(list.subList(i, end));
        }
        return batches;
    }
}
