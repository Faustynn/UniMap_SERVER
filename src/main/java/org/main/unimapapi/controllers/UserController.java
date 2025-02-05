package org.main.unimapapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.services.AuthService;
import org.main.unimapapi.services.RegistrationService;
import org.main.unimapapi.services.UserService;
import org.main.unimapapi.utils.Decryptor;
import org.main.unimapapi.utils.Hashing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/unimap_pc/")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final RegistrationService registrationService;

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
        System.out.println("A have a request with email: " + email);

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
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
            System.out.println("Username: " + username + ", Email: " + email + ", Login: " + login + ", Password: " + password);

            // Try to add user into users table if it doesn't exist yet or send 303 code if user already exists
            if (userService.findByLogin(login) != null) {
                System.err.println("User with this login already exists: " + login); // 303
                return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
            }else if(userService.findByEmail(email) != null){
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
    public ResponseEntity<User> authenticate(@RequestBody String jsonData) {
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
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid encoded string: " + jsonData);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}