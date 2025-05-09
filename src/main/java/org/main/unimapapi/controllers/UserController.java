package org.main.unimapapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.EmailChangeRequest;
import org.main.unimapapi.dtos.PasswordChangeRequest;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.dtos.UsernameChangeRequest;
import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.services.*;
import org.main.unimapapi.utils.EmailSender;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.JwtToken;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/*
 * Controller that manages registration, authentication, access restoration,
 * email confirmation and deletion of user data
 *
 * URL prefix: /api/unimap_pc/
 */
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
    private final EmailSender emailSender;

    /*
     * Method: POST
     * Endpoint: /register
     * Body: string "username:password:email:login"
     * Response: User object or error code
     */
    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody String jsonData) {
        try {
            System.out.println("TEST "+jsonData);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            String data = jsonNode.get("data").asText();
            String[] parts = data.split(":");
            if (parts.length != 4) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Registration failed: Invalid data format.");
           //     System.out.println("TEST2");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

         //   TEST {"data":"QWERTY:1234567890q:qwerty@stuba.sk:qwerty"}
            String username = parts[0];
            String email = parts[2];
            String password = parts[1];
            String passwordHash = Hashing.hashPassword(password);
            String login = parts[3];

           // ServerLogger.logServer(ServerLogger.Level.INFO, "Registration attempt: username=" + username + ", email=" + email + ", login=" + login);

           // System.out.println("TEST3");
            if (userService.findByLogin(login).isPresent() || userService.findByEmail(email).isPresent()) {
                 //ServerLogger.logServer(ServerLogger.Level.WARNING, "Registration failed: User already exists (login=" + login + ", email=" + email + ")");
             //   System.out.println("TESTZZZ");
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
            }
          //  System.out.println("TEST0");

            User_dto user_dto = new User_dto(login, email, passwordHash, username, false, false, null, null);
            User user = registrationService.register(user_dto);

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

    /*
     * Method: POST
     * Endpoint: /authenticate
     * Body: string "login:password"
     * Response: User + accessToken + refreshToken (in cookie)
     */
    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody String jsonData) {
            System.out.println("TEST "+jsonData);
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
            if(user.getAvatar() == null) {
                user.setAvatar("null".getBytes());
            }


       //     System.out.println("TEST User info: " + user.getId() + " " + user.getLogin() + " " + user.getEmail() + " " + user.getUsername() + " " + user.isAdmin() + " " + user.isPremium() + " " + user.getAvatarFileName() + " " + Arrays.toString(user.getAvatar()));
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", user,
                            "accessToken", accessToken
                          //  "avatar", base64Avatar
                    ));
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Authentication failed | Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /*
     * Method: GET
     * Endpoint: /user/email/{email}
     * Action: generate and send confirmation code
     */
    @GetMapping("user/email/{email}")
    public ResponseEntity<Void> confirmEmailExists(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) {
                String confirmationCode = ConfirmationCodeService.generateRandomCode();                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
                ConfirmationCode confirmationCodeEntity = new ConfirmationCode(user.get().getId(), confirmationCode, expirationTime);
                confirmationCodeService.save(confirmationCodeEntity);
                emailSender.sendVerificationCode(email, confirmationCode);                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /*
     * Method: PUT
     * Endpoint: /user/email/password
     * Body: string "email:new_password"
     */
    @PutMapping("user/email/password")
    public ResponseEntity<Void> changePassword(@RequestBody String jsonData) {
        try {
            System.out.println("TEST "+jsonData);
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


    /*
     * Method: POST
     * Endpoint: /user/email/code
     * Body: string "email:code"
     * Response: true / false
     */
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


    /*
     * Method: DELETE
     * Endpoint: /user/delete/all/{userId}
     * Required: JWT access token
     */
    @DeleteMapping("user/delete/all/{userId}")
    private ResponseEntity<Boolean> deleteUserData(@PathVariable String userId,@RequestHeader("Authorization") String accessToken) {
        System.out.println("I have delete userdata request in id: "+userId);
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        //    System.out.println("I have delete userdata request in id: "+userId);
            userService.deleteAllUserInfo(Long.parseLong(userId));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,"Delete user data failed | Error: " + e.getMessage());
        //    e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /*
     * Method: DELETE
     * Endpoint: /user/delete/comments/{userId}
     * Required: JWT access token
     */
    @DeleteMapping("user/delete/comments/{userId}")
    private ResponseEntity<Boolean> deleteUserComments(@PathVariable String userId, @RequestHeader("Authorization") String accessToken) {
        System.out.println("I have delete user comments request in id: "+userId);
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            userService.deleteAllUserComments(Long.parseLong(userId));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
          //  e.printStackTrace();
            ServerLogger.logServer(ServerLogger.Level.ERROR,"Delete user comments failed | Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }




    @Operation(summary = "Upload a file",
            description = "Uploads a file with content type application/octet-stream")
    @PutMapping(value = "/change_avatar", consumes = {"application/octet-stream", "image/png", "image/jpeg", "image/gif"})
    public ResponseEntity<String> changeAvatar(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody byte[] avatarData,
            @RequestParam("fileName") String fileName) {

        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            if (avatarData == null || avatarData.length == 0 || decodedFileName == null || decodedFileName.isBlank()) {
                return ResponseEntity.badRequest().body("Invalid request. Avatar data and file name are required.");
            }

            String token = authorizationHeader.replace("Bearer ", "");
            if (token.isEmpty() || !jwtToken.validateAccessToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Token is required.");
            }

            String login = jwtToken.extractUsernameFromAccessToken(token);

            boolean avatarUpdated = userService.updateAvatarData(login, avatarData, decodedFileName);
        //    System.out.println("Avatar DATAA: " + Arrays.toString(avatarData));

            if (avatarUpdated) {
                return ResponseEntity.ok("Avatar updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
        //    System.out.println("Error processing the avatar: " + e.getMessage());
         //   e.printStackTrace();
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error processing the avatar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the avatar file: " + e.getMessage());
        }
    }


    @PutMapping(value = "/change_avatar", consumes = {"multipart/form-data"})
    public ResponseEntity<String> changeAvatar(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("file") MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request. File is required.");
            }

            String token = authorizationHeader.replace("Bearer ", "");
            if (token.isEmpty() || !jwtToken.validateAccessToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Token is required.");
            }

            String login = jwtToken.extractUsernameFromAccessToken(token);

            boolean avatarUpdated = userService.updateAvatarData(
                    login,
                    file.getBytes(),
                    file.getOriginalFilename()
            );

            if (avatarUpdated) {
                return ResponseEntity.ok("Avatar updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error processing the avatar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the avatar file: " + e.getMessage());
        }
    }

    @PutMapping("/change_email")
    public ResponseEntity<String> changeEmail(@RequestBody EmailChangeRequest request) {
        if (request == null || request.getEmail() == null || request.getLogin() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and avatar path are required.");
        }

        boolean emailChanged = userService.changeEmail(request.getLogin(), request.getEmail());

        if (emailChanged) {
            return ResponseEntity.ok("Email changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @PutMapping("/change_pass")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        if (request == null || request.getEmail() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and new password are required.");
        }

        boolean passwordChanged = userService.changePassword(request.getEmail(), request.getNewPassword());

        if (passwordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @PutMapping("/change_username")
    public ResponseEntity<String> changeUsername(@RequestBody UsernameChangeRequest request) {
        System.out.println("Change username request: " + request);
        if (request == null || request.getEmail() == null || request.getUsername() == null) {
            return ResponseEntity.badRequest().body("Invalid request. Email and avatar path are required.");
        }

        boolean avatarChanged = userService.changeUsername(request.getEmail(), request.getUsername());

        if (avatarChanged) {
            return ResponseEntity.ok("Avatar changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }


    @PutMapping("/premium/{login}")
    public ResponseEntity<?> changePremium(@PathVariable String login,@RequestHeader("Authorization") String accessToken) {
        System.out.println("I have premium request in id: "+ login);
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);

            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            }

            User user = userService.updatePremiumStatus(login);

            if (user != null) {
                return ResponseEntity.ok()
                        .body(Map.of("user", user));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error changing premium status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing premium status: " + e.getMessage());
        }
    }
}