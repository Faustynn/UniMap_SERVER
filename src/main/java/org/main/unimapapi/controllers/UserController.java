package org.main.unimapapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.services.*;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class UserController {
    private final UserService userService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtToken jwtToken;
    ConfirmationCodeService ConfirmationCodeService;

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");
            if (parts.length != 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String username = parts[0];
            String password = parts[1];
            String passwordHash = Hashing.hashPassword(password);
            String login = parts[3];
            String email = parts[2];

            if (userService.findByLogin(login).isPresent() ||
                    userService.findByEmail(email).isPresent() ||
                    userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
            }

            User user = registrationService.register(new User_dto(login, email, passwordHash, username, false, null));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");
            if (parts.length != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String login = parts[0];
            String password = parts[1];

            User user = authService.authenticate(login, password);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String accessToken = jwtToken.generateAccessToken(user.getLogin());
            String refreshToken = jwtToken.generateRefreshToken(user.getLogin());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(86400) // 1 day
                    .build();

            user.setPassword(null);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", user,
                            "accessToken", accessToken
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("user/email/password")
    public ResponseEntity<Void> changePassword(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");

            if (parts.length != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String email = parts[0];
            String new_password = parts[1];

            if (userService.findByEmail(email).isPresent()) {
                User user = userService.findByEmail(email).get();
                user.setPassword(Hashing.hashPassword(new_password));
                userService.update(user);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("user/email/code")
    public ResponseEntity<Boolean> compareCodes(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");
            if (parts.length != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String email = parts[0];
            String userCode = parts[1];

            Optional<User> user = userService.findByEmail(email);
            Long id = user.map(User::getId).orElse(null);

            boolean isCodeValid = ConfirmationCodeService.validateConfirmationCode(id, userCode);
            return ResponseEntity.ok(isCodeValid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}