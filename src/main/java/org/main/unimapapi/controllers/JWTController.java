package org.main.unimapapi.controllers;

import lombok.AllArgsConstructor;
import org.main.unimapapi.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class JWTController {
    private final TokenService tokenService;

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !tokenService.validateRefreshToken(refreshToken)) {
            System.out.println("Not valid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = tokenService.getLoginFromRefreshToken(refreshToken);
        String newAccessToken = tokenService.createAccessToken(login);

        System.out.println("I send new access token " + newAccessToken + " for user " + login);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}