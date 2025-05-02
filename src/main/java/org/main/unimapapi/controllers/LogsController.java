package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.LogEntry;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unimap_pc")
public class LogsController {

    @PostMapping("/log")
    public void receiveClientLog(@RequestBody LogEntry logEntry) {
        try {
            ServerLogger.logClient(logEntry);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to log client message: " + e.getMessage());
        }
    }
}
