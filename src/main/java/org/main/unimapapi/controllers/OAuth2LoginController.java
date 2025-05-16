package org.main.unimapapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.services.RegistrationService;
import org.main.unimapapi.utils.JwtToken;
import org.main.unimapapi.utils.ServerLogger;
import org.main.unimapapi.configs.AppConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for handling OAuth2 login via Google and Facebook.
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/oauth2/</code></p>
 * <p>This endpoint handles exchanging an OAuth2 authorization code for a token and user information.</p>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/oauth2/")
public class OAuth2LoginController {
    private final JwtToken jwtToken;
    private final UserRepository userRepository;
    private final RegistrationService registrationService;
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * Handles login via OAuth2 provider (Google or Facebook).
     *
     * @param code     the authorization code from the OAuth2 provider
     * @param provider the name of the provider ("google" or "facebook")
     * @return JSON response with user object and JWT access token, or error response
     */
    @Operation(
            summary = "OAuth2 login",
            description = "Handles login via Google or Facebook. Exchanges the authorization code for user info and tokens."
    )
    @ApiResponse(responseCode = "200", description = "Successfully authenticated and registered/logged in user")
    @ApiResponse(responseCode = "400", description = "Unsupported provider or missing data")
    @ApiResponse(responseCode = "500", description = "Authentication failed due to internal error")
    @PostMapping("login")
    public ResponseEntity<?> oauth2Login(@RequestParam("code") String code,
                                         @RequestParam("provider") String provider) {
  //      System.out.println("provider: " + provider);

        try {
            if ("google".equals(provider)) {
                return handleGoogleAuthentication(code);
            } else if ("facebook".equals(provider)) {
                return handleFacebookAuthentication(code);
            } else {
                return ResponseEntity.badRequest().body("Unsupport provider: " + provider);
            }
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "OAuth2 login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed: " + e.getMessage());
        }
    }


    /**
     * Handles OAuth2 authentication flow with Google.
     *
     * @param code the Google OAuth2 authorization code
     * @return a response with access token and user data
     */
    private ResponseEntity<?> handleGoogleAuthentication(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", AppConfig.getGOOGLE_CLIENT_ID());
        map.add("client_secret", AppConfig.getGOOGLE_CLIENT_SECRET());
        map.add("code", code);
        map.add("redirect_uri", AppConfig.getGOOGLE_REDIRECT_URI());
        map.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
                AppConfig.getGOOGLE_TOKEN_URL(), request, String.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to get Google token: " + tokenResponse.getBody());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to authenticate with Google");
        }

        // Parse token response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tokenData = objectMapper.readTree(tokenResponse.getBody());
        String accessToken = tokenData.get("access_token").asText();

        // Get user info
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                AppConfig.getGOOGLE_USER_INFO_URL(), HttpMethod.GET, userInfoRequest, String.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to get Google user info: " + userInfoResponse.getBody());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get user information");
        }

        // Parse user info
        JsonNode userInfo = objectMapper.readTree(userInfoResponse.getBody());
        String email = userInfo.get("email").asText();
        String name = userInfo.get("name") != null ? userInfo.get("name").asText() : userInfo.get("email").asText().split("@")[0];

        // Check if user exists
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // User exists, generate tokens
            String jwtAccessToken = jwtToken.generateAccessToken(user.getLogin());
            String refreshToken = jwtToken.generateRefreshToken(user.getLogin());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(86400) // 1 day
                    .build();

            user.setPassword(null);
            if (user.getAvatar() == null) {
                user.setAvatar("null".getBytes());
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", user,
                            "accessToken", jwtAccessToken
                    ));
        } else {
            // Register new user
            String login = generateUniqueLogin(name);
            User_dto user_dto = new User_dto(login, email, null, name, false, false, null, null);
            User newUser = registrationService.register(user_dto);

            if (newUser == null) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Registration failed: User object is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Generate tokens for new user
            String jwtAccessToken = jwtToken.generateAccessToken(newUser.getLogin());
            String refreshToken = jwtToken.generateRefreshToken(newUser.getLogin());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(86400) // 1 day
                    .build();

            newUser.setPassword(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", newUser,
                            "accessToken", jwtAccessToken
                    ));
        }
    }

    /**
     * Handles OAuth2 authentication flow with Facebook.
     *
     * @param code the Facebook OAuth2 authorization code
     * @return a response with access token and user data
     */
    private ResponseEntity<?> handleFacebookAuthentication(String code) throws JsonProcessingException {
        // Exchange code for token
        String tokenUrl = AppConfig.getFACEBOOK_TOKEN_URL() +
                "?client_id=" + AppConfig.getFACEBOOK_CLIENT_ID() +
                "&client_secret=" + AppConfig.getFACEBOOK_CLIENT_SECRET() +
                "&code=" + code +
                "&redirect_uri=" + AppConfig.getFACEBOOK_REDIRECT_URI();

        ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenUrl, String.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to get Facebook token: " + tokenResponse.getBody());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to authenticate with Facebook");
        }

        // Parse token response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tokenData = objectMapper.readTree(tokenResponse.getBody());
        String accessToken = tokenData.get("access_token").asText();

        // Get user info
        String userInfoUrl = AppConfig.getFACEBOOK_USER_INFO_URL() + "&access_token=" + accessToken;
        ResponseEntity<String> userInfoResponse = restTemplate.getForEntity(userInfoUrl, String.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to get Facebook user info: " + userInfoResponse.getBody());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get user information");
        }

        // Parse user info
        JsonNode userInfo = objectMapper.readTree(userInfoResponse.getBody());
        String email = userInfo.get("email") != null ? userInfo.get("email").asText() : "";
        String name = userInfo.get("name").asText();

        if (email.isEmpty()) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Email is required but not provided by Facebook");
            return ResponseEntity.badRequest().body("Email is required but not provided by Facebook");
        }

        // Check if user exists
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // User exists, generate tokens
            String jwtAccessToken = jwtToken.generateAccessToken(user.getLogin());
            String refreshToken = jwtToken.generateRefreshToken(user.getLogin());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(86400) // 1 day
                    .build();

            user.setPassword(null);
            if (user.getAvatar() == null) {
                user.setAvatar("null".getBytes());
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", user,
                            "accessToken", jwtAccessToken
                    ));
        } else {
            // Register new user
            String login = generateUniqueLogin(name);
            User_dto user_dto = new User_dto(login, email, null, name, false, false, null, null);
            User newUser = registrationService.register(user_dto);

            if (newUser == null) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Registration failed: User object is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Generate tokens for new user
            String jwtAccessToken = jwtToken.generateAccessToken(newUser.getLogin());
            String refreshToken = jwtToken.generateRefreshToken(newUser.getLogin());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(86400) // 1 day
                    .build();

            newUser.setPassword(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(Map.of(
                            "user", newUser,
                            "accessToken", jwtAccessToken
                    ));
        }
    }

    /**
     * Generates a unique login name based on the provided base name.
     *
     * @param name the user's base name (from OAuth2)
     * @return a unique login name
     */
    private String generateUniqueLogin(String name) {
        String baseName = name.toLowerCase().replaceAll("\\s+", "");
        String login = baseName;

        // Check if login exists and append random suffix if needed
        while (userRepository.findByLogin(login).isPresent()) {
            login = baseName + "_" + UUID.randomUUID().toString().substring(0, 6);
        }

        return login;
    }
}