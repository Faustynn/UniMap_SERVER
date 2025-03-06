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

@AllArgsConstructor
@RestController
@RequestMapping("/api/unimap_pc/")
public class TeacherController {
    JwtToken jwtToken;
    DataFatcherRepository dataFatcherRepository = new DataFatcherRepository();

    @GetMapping("resources/teachers")
    public ResponseEntity<?> getAllTeachers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            System.out.println("Teacher token "+token);

            if (!jwtToken.validateAccessToken(token)) {
                System.out.println("Unauthorized teacher token "+token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("teachers", dataFatcherRepository.fetchAllTeachers());
            //System.out.println("ODOSLAL SOM "+response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching teachers: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
