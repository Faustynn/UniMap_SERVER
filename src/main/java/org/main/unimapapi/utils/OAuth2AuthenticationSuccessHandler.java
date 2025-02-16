package org.main.unimapapi.utils;

import lombok.AllArgsConstructor;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepositoryImpl;
import org.main.unimapapi.repository_queries.TokenRepositoryImpl;
import org.main.unimapapi.services.TokenService;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepositoryImpl userRepository;
    private final JwtToken jwtToken;
    private final TokenRepositoryImpl tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        String login = email;

        // Trying find existing user or create new
        Optional<User> userOptional = userRepository.findByLogin(login);
        User user = userOptional.orElseGet(() -> createNewUser(email, name, login));
        user.setEmail(email);
        user.setUsername(name);
        userRepository.update(user);

        // Generate tokens
        String accessToken = jwtToken.generateAccessToken(user.getUsername());
        String refreshToken = jwtToken.generateRefreshToken(user.getUsername());

        // Save refresh token
        TokenService tokenService = new TokenService(tokenRepository, jwtToken);
        tokenService.saveUserToken(user, refreshToken);

        // Set refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400); // 1 day
        response.addCookie(refreshTokenCookie);

        System.out.println("OAuth2 login success: " + user.getUsername());

        // Send response with user data and access token
        response.setContentType("application/json");
        String jsonResponse = String.format("""
                {"user": {"id": %d, "username": "%s", "email": "%s"},
                 "accessToken": "%s"}""",
                user.getId(), user.getUsername(), user.getEmail(), accessToken);
        response.getWriter().write(jsonResponse);
    }

    private User createNewUser(String email, String name, String provider) {
        User_dto userDto = new User_dto();
        userDto.setEmail(email);
        userDto.setUsername(name);
        userDto.setLogin(email); // email as login for OAuth users
        userDto.setPassword(null); // pass will be null
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setAdmin(userDto.isAdmin());
        user.setSubscribe(userDto.isSubscribe());
        user.setVerification(userDto.isVerification());
        user.setAvatar(userDto.getAvatar());
        userRepository.save(user);
        return user;
    }
    // TODO: changing pass when login throw OAuth
}