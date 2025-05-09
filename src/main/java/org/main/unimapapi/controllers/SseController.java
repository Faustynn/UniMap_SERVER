package org.main.unimapapi.controllers;

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
 * Controller for SSE connections
 *
 * URL prefix: /api/unimap_pc/sse
 * Method: GET
 * Endpoint: /subscribe
 * Output: Server-Sent Events stream for news updates
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/sse")
public class SseController {

    private final SseServer sseServer;

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

    @GetMapping("/status")
    public String status() {
        return "{\"status\":\"running\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
    }

}