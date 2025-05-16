package org.main.unimapapi.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.main.unimapapi.repository_queries.DataFatcherRepository;
import org.main.unimapapi.utils.JwtToken;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for retrieving all available subjects.
 *
 * <p><strong>URL:</strong> <code>/api/unimap_pc/resources/subjects</code><br>
 * <strong>Method:</strong> GET<br>
 * <strong>Authorization:</strong> JWT Access Token in header</p>
 *
 * <p>Returns a JSON array of subjects available in the system.</p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class SubjectController {
    JwtToken jwtToken;
    DataFatcherRepository dataFatcherRepository;


    /**
     * Retrieves a list of all available subjects.
     *
     * @param authorizationHeader the "Authorization" HTTP header containing the Bearer token
     * @return a JSON object containing the subject list or an error response
     */
    @Operation(
            summary = "Get all subjects",
            description = "Returns a list of all subjects in the system. Requires a valid JWT access token."
    )
    @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("resources/subjects")
    public ResponseEntity<?> getAllSubjects(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            if (!jwtToken.validateAccessToken(token)) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Unauthorized teacher token: " + token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("subjects", dataFatcherRepository.fetchAllSubjects());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching subjects: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }
}
