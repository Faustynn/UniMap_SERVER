package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.LogEntry;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.web.bind.annotation.*;

/*
 * Controller for receiving logs from the client (frontend application)
 *
 * URL prefix: /api/unimap_pc
 *
 * Method: POST
 * Endpoint: /log
 * Input: JSON object `LogEntry` with type, message and source
 * Response: string about logging success or error
 */
@RestController
@RequestMapping("/api/unimap_pc")
public class LogsController {

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
