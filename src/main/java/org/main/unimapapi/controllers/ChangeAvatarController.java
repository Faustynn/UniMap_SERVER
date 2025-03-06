package org.main.unimapapi.controllers;

import org.main.unimapapi.dtos.AvatarChangeRequest;
import org.main.unimapapi.services.ChangeAvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unimap_pc")
public class ChangeAvatarController {

    private final ChangeAvatarService changeAvatarService;

    @Autowired
    public ChangeAvatarController(ChangeAvatarService changeAvatarService) {
        this.changeAvatarService = changeAvatarService;
    }

    @PostMapping("/change_avatar")
    public ResponseEntity<String> changeAvatar(@RequestBody AvatarChangeRequest request) {
        if (request == null || request.getEmail() == null || request.getAvatarPath() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and avatar path are required.");
        }

        boolean avatarChanged = changeAvatarService.changeAvatar(request.getEmail(), request.getAvatarPath());

        if (avatarChanged) {
            return ResponseEntity.ok("Avatar changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}