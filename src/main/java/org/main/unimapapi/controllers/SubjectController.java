package org.main.unimapapi.controllers;


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

@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class SubjectController {
    JwtToken jwtToken;
    DataFatcherRepository dataFatcherRepository = new DataFatcherRepository();

    @GetMapping("resources/subjects")
    public ResponseEntity<?> getAllSubjects(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            System.out.println("Subject token "+token);

            if (!jwtToken.validateAccessToken(token)) {
                System.out.println("Unauthorized teacher token "+token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("Fetching subjects...");

            Map<String, Object> response = new HashMap<>();
            response.put("subjects", dataFatcherRepository.fetchAllSubjects());
           // System.out.println("ODOSLAL SOM "+response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching subjects: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
