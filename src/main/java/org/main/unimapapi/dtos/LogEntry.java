package org.main.unimapapi.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO representing a log entry sent from the client to the server.
 *
 * <p>This object is used to capture logging information such as who sent the message,
 * at what time, and with what severity level.</p>
 */
@Getter
@RequiredArgsConstructor
public class LogEntry {

    /**
     * The ID of the user who generated the log entry.
     */
    private final int userId;

    /**
     * Timestamp of when the log entry was created (ISO 8601 or formatted string).
     */
    private final String timestamp;

    /**
     * The severity level of the log (e.g., {@code "INFO"}, {@code "WARNING"}, {@code "ERROR"}).
     */
    private final String level;

    /**
     * The log message describing what happened.
     */
    private final String message;
}