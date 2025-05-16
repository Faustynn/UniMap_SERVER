package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.sse.SseServer;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller that manages Server-Sent Events (SSE) connections
 * used for pushing real-time news updates to connected clients.
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/sse</code></p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/sse")
public class SseController {

    private final SseServer sseServer;

    /**
     * Establishes a persistent SSE connection for the client to receive real-time updates.
     *
     * <p><strong>Endpoint:</strong> <code>/subscribe</code><br>
     * <strong>Method:</strong> GET<br>
     * <strong>Response:</strong> Event stream (Content-Type: text/event-stream)</p>
     *
     * @param request  the incoming client request
     * @param response the server response that will be upgraded to SSE
     */
    @GetMapping("/subscribe")
    public void subscribe(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        try {
            String clientIP = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    String.format("New SSE client connected from %s with %s", clientIP, userAgent));

            sseServer.handleClientConnection(request, response);
        } catch (IOException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error establishing SSE connection: " + e.getMessage());

            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error: " + e.getMessage());
                response.getWriter().flush();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Simple status check endpoint for monitoring the SSE service.
     *
     * @return a JSON string with the service status and timestamp
     */
    @Operation(summary = "Check SSE server status", description = "Returns a JSON status with server timestamp.")
    @ApiResponse(responseCode = "200", description = "SSE server is running")
    @GetMapping("/status")
    public String status() {
        return "{\"status\":\"running\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
    }

}