package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.EmailChangeRequest;
import org.main.unimapapi.services.ChangeEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unimap_pc")
public class ChangeEmailController {

    private final ChangeEmailService changeEmailService;

    @Autowired
    public ChangeEmailController(ChangeEmailService changeEmailService) {
        this.changeEmailService = changeEmailService;
    }

    @PostMapping("/change_email")
    public ResponseEntity<String> changeEmail(@RequestBody EmailChangeRequest request) {
        if (request == null || request.getEmail() == null || request.getLogin() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and avatar path are required.");
        }

        boolean emailChanged = changeEmailService.changeEmail(request.getLogin(), request.getEmail());

        if (emailChanged) {
            return ResponseEntity.ok("Email changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}