package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.main.unimapapi.services.TokenService;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * REST controller responsible for refreshing JWT access tokens using refresh tokens.
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/refresh</code><br>
 * <strong>Method:</strong> POST</p>
 *
 * <p>Clients provide a valid refresh token in the request body to receive a new access token.</p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class JWTController {
    private final TokenService tokenService;

    /**
     * Refreshes an access token using a provided refresh token.
     *
     * @param request a map containing the "refreshToken" key
     * @return a map with the new access token, or 401 if the token is invalid
     */
    @Operation(
            summary = "Refresh JWT access token",
            description = "Returns a new access token if the provided refresh token is valid."
    )
    @ApiResponse(responseCode = "200", description = "Access token generated successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")
    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !tokenService.validateRefreshToken(refreshToken)) {
            ServerLogger.logServer(ServerLogger.Level.WARNING, "Not valid refresh token received.");
          //  System.out.println("Not valid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = tokenService.getLoginFromRefreshToken(refreshToken);
        String newAccessToken = tokenService.createAccessToken(login);

        System.out.println("access token " + newAccessToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}