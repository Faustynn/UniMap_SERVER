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
 * REST controller for retrieving all teachers in the system.
 *
 * <p><strong>URL:</strong> <code>/api/unimap_pc/resources/teachers</code><br>
 * <strong>Method:</strong> GET<br>
 * <strong>Authorization:</strong> JWT Access Token required in the header</p>
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/unimap_pc/")
public class TeacherController {
    JwtToken jwtToken;
    DataFatcherRepository dataFatcherRepository;

    /**
     * Retrieves a list of all teachers.
     *
     * @param authorizationHeader the "Authorization" header containing a valid Bearer token
     * @return a list of teachers or an appropriate error response
     */
    @Operation(
            summary = "Get all teachers",
            description = "Returns a list of all teachers in the system. Requires a valid JWT access token."
    )
    @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("resources/teachers")
    public ResponseEntity<?> getAllTeachers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
      //      System.out.println("Teacher token "+token);

            if (!jwtToken.validateAccessToken(token)) {
             //   System.out.println("Unauthorized teacher token "+token);
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Unauthorized access token: " + token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("teachers", dataFatcherRepository.fetchAllTeachers());
            //System.out.println("ODOSLAL SOM "+response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching teachers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
