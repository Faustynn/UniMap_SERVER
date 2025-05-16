package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the server connection status.
 *
 * <p>Used as a response for health-check or connection testing endpoints.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerConnection_dto {

    /**
     * Status of the server (e.g., {@code "UP"} or {@code "DOWN"}).
     */
    private String status;

    /**
     * Timestamp when the status was generated (ISO string).
     */
    private String timestamp;
}