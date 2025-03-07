package org.main.unimapapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.services.*;
import org.main.unimapapi.utils.EmailSender;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.JwtToken;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class UserController {
    private final UserService userService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final JwtToken jwtToken;
    private final TokenService tokenService;
    private final ConfirmationCodeService confirmationCodeService;

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody String jsonData) {
        try {
            System.out.println("TEST "+jsonData);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");
            if (parts.length != 4) {
                //ServerLogger.logServer(ServerLogger.Level.WARNING, "Invalid registration data format.");

                System.out.println("TEST2");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

         //   TEST {"data":"QWERTY:1234567890q:qwerty@stuba.sk:qwerty"}
            String username = parts[0];
            String email = parts[2];
            String password = parts[1];
            String passwordHash = Hashing.hashPassword(password);
            String login = parts[3];

            ServerLogger.logServer(ServerLogger.Level.INFO, "Registration attempt: username=" + username + ", email=" + email + ", login=" + login);

            System.out.println("TEST3");
            if (userService.findByLogin(login).isPresent() ||
                    userService.findByEmail(email).isPresent()) {
                 //ServerLogger.logServer(ServerLogger.Level.WARNING, "Registration failed: User already exists (login=" + login + ", email=" + email + ")");
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
            }
            User user = registrationService.register(new User_dto(login,email,passwordHash,username, false, false,null));

            System.out.println("TEST4 "+user.getLogin()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getUsername());

            if (user == null) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Registration failed: User object is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            //ServerLogger.logServer(ServerLogger.Level.INFO, "User registered successfully: login=" + login);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Registration error: " + e.getMessage());
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
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Authentication failed | Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("user/email/{email}")
    public ResponseEntity<Void> confirmEmailExists(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) {
                String confirmationCode = confirmationCodeService.generateRandomCode();
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
                ConfirmationCode confirmationCodeEntity = new ConfirmationCode(user.get().getId(), confirmationCode, expirationTime);
                confirmationCodeService.save(confirmationCodeEntity);
                EmailSender.sendEmail(email, confirmationCode);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error confirming email " + e.getMessage());
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
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Change password failed | Error: " + e.getMessage());
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

            boolean isCodeValid = confirmationCodeService.validateConfirmationCode(id, userCode);
            return ResponseEntity.ok(isCodeValid);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Compare code failed | Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("user/delete/all/{userId}")
    private ResponseEntity<Boolean> deleteUserData(@PathVariable String userId,@RequestHeader("Authorization") String accessToken) {
        System.out.println("I have delete userdata request in id: "+userId);
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            System.out.println("I have delete userdata request in id: "+userId);
            userService.delete_all_user_info(Long.parseLong(userId));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error deleting user data - UserID: " + userId + ", Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @DeleteMapping("user/delete/comments/{userId}")
    private ResponseEntity<Boolean> deleteUserComments(@PathVariable String userId, @RequestHeader("Authorization") String accessToken) {
        System.out.println("I have delete user comments request in id: "+userId);
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            userService.delete_all_user_comments(Long.parseLong(userId));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error deleting user comments - UserID: " + userId + ", Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}