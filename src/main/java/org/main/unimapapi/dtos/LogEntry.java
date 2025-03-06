package org.main.unimapapi.dtos;

public class LogEntry {
    private int userId;
    private String timestamp;
    private String level;
    private String message;

    public int getUserId() { return userId; }
    public String getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
}
