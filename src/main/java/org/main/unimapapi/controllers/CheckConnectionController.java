package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.main.unimapapi.dtos.ServerConnection_dto;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

/**
 * REST controller for checking the connection status of the UniMap server.
 *
 * <p><strong>URL:</strong> <code>/api/unimap_pc/check-connection</code><br>
 * <strong>Method:</strong> GET</p>
 *
 * <p>This endpoint allows clients to verify that the server is running and reachable.</p>
 */
@RestController
@RequestMapping("/api/unimap_pc")
public class CheckConnectionController {

    /**
     * Endpoint to check the server connection status.
     *
     * @return {@link ServerConnection_dto} object containing the status ("UP") and the current timestamp.
     */
    @Operation(
            summary = "Check server connection",
            description = "Returns the current server status and timestamp."
    )
    @ApiResponse(responseCode = "200", description = "Server is reachable and running")
    @GetMapping("/check-connection")
    public ResponseEntity<ServerConnection_dto> checkConnection() {
        ServerConnection_dto status = new ServerConnection_dto("UP", LocalDateTime.now().toString());
        ServerLogger.logServer(ServerLogger.Level.INFO, "Response sent: " + status);
        return ResponseEntity.ok(status);
    }
}