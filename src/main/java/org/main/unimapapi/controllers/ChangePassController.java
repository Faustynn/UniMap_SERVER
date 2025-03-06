package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.PasswordChangeRequest;
import org.main.unimapapi.services.ChangePassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unimap_pc")
public class ChangePassController {

    private final ChangePassService changePassService;

    @Autowired
    public ChangePassController(ChangePassService changePassService){
        this.changePassService = changePassService;
    }

    @PostMapping("/change_pass")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        if (request == null || request.getEmail() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and new password are required.");
        }

        boolean passwordChanged = changePassService.changePassword(request.getEmail(), request.getNewPassword());

        if (passwordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}