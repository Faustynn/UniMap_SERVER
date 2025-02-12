package org.main.unimapapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repositories.TokenRepository;
import org.main.unimapapi.services.*;
import org.main.unimapapi.utils.Decryptor;
import org.main.unimapapi.utils.Hashing;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final RegistrationService registrationService;
    private final ConfirmationCodeService confirmationCodeService;
    private final ChangePassService change;
    private final TokenRepository tokenRepository;
    private final JwtToken jwtToken;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User_dto dto) {
        return new ResponseEntity<>(userService.create(dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> readAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
    }

    @DeleteMapping("user/{id}")
    public HttpStatus delete(@PathVariable Long id) {
        userService.delete(id);
        return HttpStatus.OK;
    }

    @GetMapping("user/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        System.out.println("I have a request with email: " + email);
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            confirmationCodeService.generateConfirmationCode(email,user.get().getId());
            return ResponseEntity.ok(user.get());
        }
    }

    @GetMapping("user/login/{login}")
    public ResponseEntity<User> getByLogin(@PathVariable String login) {
        System.out.println("A have a request with login: " + login);

        User user = userService.findByLogin(login);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }


    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String encryptedData = jsonNode.get("data").asText();

            String decryptedData = Decryptor.decrypt(encryptedData);
            //  System.out.println("Decrypted data: " + decryptedData);
            String[] parts = decryptedData.split(":");
            if (parts.length != 4) {
                System.err.println("Decrypted data format is invalid: " + decryptedData);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String username = parts[0];
            String password = parts[1];
            String passwordHash = Hashing.hashPassword(password);
            String login = parts[3];
            String email = parts[2];
         //   System.out.println("Username: " + username + ", Email: " + email + ", Login: " + login + ", Password: " + password);

            // Try to add user into users table if it doesn't exist yet or send 303 code if user already exists
            if (userService.findByLogin(login) != null) {
                System.err.println("User with this login already exists: " + login); // 303
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
            }else if(userService.findByEmail(email).isPresent()){
                System.err.println("User with this email already exists: " + email);
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build(); // 304
            } else if(userService.findByUsername(username) != null){
                System.err.println("User with this username already exists: " + username);
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build(); // 305
            }
            User user = registrationService.register(new User_dto(login, email, passwordHash, username, false, false, false, 0));
            if (user == null) {
                System.err.println("Registration failed for user: " + login);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid encoded string: " + jsonData);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody String jsonData, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String encryptedData = jsonNode.get("data").asText();

            String decryptedData = Decryptor.decrypt(encryptedData);
           // System.out.println("Decrypted data: " + decryptedData);
            String[] parts = decryptedData.split(":");
            if (parts.length != 2) {
                System.err.println("Decrypted data format is invalid: " + decryptedData);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String login = parts[0];
            String password = parts[1];
            //   System.out.println("Login: " + login + ", Password: " + password);

            // Authenticate user, finding by login and comparing password
            User user = authService.authenticate(login, password);
            if (user == null) {
                System.err.println("Authentication failed for user: " + login);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
          //  System.out.println("I send 200 code for user: " + user.getLogin());

            String accessToken = jwtToken.generateAccessToken(user.getUsername());
            String refreshToken = jwtToken.generateRefreshToken(user.getUsername());
            TokenService tokenService = new TokenService(tokenRepository, jwtToken);
            tokenService.saveUserToken(user, refreshToken);

            // Cookie for the refreshToken
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(86400); // 1 day
            response.addCookie(refreshTokenCookie);

            user.setPassword(null);
            return ResponseEntity.ok(Map.of(
                    "user", user,
                    "accessToken", accessToken
            ));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid encoded string: " + jsonData);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
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
                System.err.println("Data format is invalid: " + data);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String email = parts[0];
            String new_password = Decryptor.decrypt(parts[1]);

            if (change.changePassword(email, new_password)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("user/email/code")
    public ResponseEntity<Boolean> compareCodes(@RequestBody String jsonData) {
        try {
            // TODO: проверка на правльность кода написаного человеком и в таблице и если правильно то удалить код из таблицы и дать доступ до смены пароля

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String data = jsonNode.get("data").asText();

            String[] parts = data.split(":");
            if (parts.length != 2) {
                System.err.println("Data format is invalid: " + data);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            String email = parts[0];
            String userCode = parts[1];

       //     System.out.println("i have a request code : " + userCode);

            Optional<User> user = userService.findByEmail(email);
            Long id = user.map(User::getId).orElse(null);

            boolean isCodeValid = confirmationCodeService.validateConfirmationCode(id, userCode);
            return ResponseEntity.ok(isCodeValid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}