package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.ServerConnection_dto;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/unimap_pc")
public class CheckConnectionController {
    @GetMapping("/check-connection")
    public ResponseEntity<ServerConnection_dto> checkConnection() {
        ServerConnection_dto status = new ServerConnection_dto(
                "UP",
                LocalDateTime.now().toString()
        );
        ServerLogger.logServer(ServerLogger.Level.INFO, "Response sent: " + status);
        //System.out.println("Response sent: " + status);
        return ResponseEntity.ok(status);
    }
}