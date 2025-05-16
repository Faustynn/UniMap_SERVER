package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response object for notification-related endpoints.
 *
 * <p>Can also be reused in other API responses to return status, message, and optional data.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;

    /**
     * Message describing the outcome (e.g., error or success message).
     */
    private String message;

    /**
     * Optional data to include in the response (e.g., device ID, list, or null).
     */
    private Object data;
}