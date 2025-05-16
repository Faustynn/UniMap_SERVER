package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.main.unimapapi.dtos.LogEntry;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for receiving log messages from the client (frontend application).
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/log</code><br>
 * <strong>Method:</strong> POST</p>
 *
 * <p>This endpoint allows clients to send log entries to the backend for centralized logging.</p>
 */
@RestController
@RequestMapping("/api/unimap_pc")
public class LogsController {

    /**
     * Receives a log entry from the client and stores or prints it on the server.
     *
     * @param logEntry the log object containing type, message, and source
     * @return a message indicating success or failure
     */
    @Operation(
            summary = "Receive client log entry",
            description = "Accepts a log entry from the frontend and processes it on the server."
    )
    @ApiResponse(responseCode = "200", description = "Log received successfully")
    @PostMapping("/log")
    public String receiveClientLog(@RequestBody LogEntry logEntry) {
        try {
            ServerLogger.logClient(logEntry);
            return "Client log received successfully.";
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to log client message: " + e.getMessage());
            System.out.println("Error saving client log: " + e.getMessage());
            return "Error saving client log: " + e.getMessage();
        }
    }
}
