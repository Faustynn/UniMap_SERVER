package org.main.unimapapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

/*
 * Security configuration for UniMap application
 *
 * Configures:
 * - public and secure routes
 * - OAuth2 authorisation via Google and Facebook
 * - handling of successful login via custom handler
 * - logout
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureHttpSecurity(http);
        return http.build();
    }

    // HTTP Security Configuration
    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers(
                                        // Для мобильного приложения
                                        "/api/unimap_pc/authenticate",
                                        "/api/unimap_pc/authenticate/**",

                                        //SSE
                                        "/api/unimap_pc/sse",
                                        "/api/unimap_pc/sse/subscribe",
                                        "/api/unimap_pc/sse/status",

                                        //oAuth2
                                        "/api/unimap_pc/oauth2/**",

                                        // Swagger
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",

                                        "/",
                                        "/oauth2/**",
                                        "/login**",
                                        "/api/unimap_pc/user/email/change_pass/**",
                                        "/api/unimap_pc/change_avatar/**",
                                        "/api/unimap_pc/change_username/**",
                                        "/api/unimap_pc/premium/**",
                                        "/api/unimap_pc/change_email/**",
                                        "/api/unimap_pc/check-connection",
                                        "/api/unimap_pc/register",
                                        "/api/unimap_pc/user/email/**",
                                        "/api/unimap_pc/user/change_email/**",
                                        "/api/unimap_pc/user/create",
                                        "/api/unimap_pc/refresh",
                                        "/api/unimap_pc/resources/**",

                                        "/api/unimap_pc/news/all",
                                        "/api/unimap_pc/log",

                                        "/api/unimap_pc/comments/teacher/**",
                                        "/api/unimap_pc/comments/subject/**",
                                        "/api/unimap_pc/comments/subject",
                                        "/api/unimap_pc/comments/teacher",
                                        "/api/unimap_pc/news/all",

                                        "/api/unimap_pc/user/email/**",
                                        "/api/unimap_pc/user/delete/comments/**",
                                        "/api/unimap_pc/user/delete/all/**",

                                        "/api/notifications/**",
                                        "/api/notifications/register-device",
                                        "/api/notifications/test",
                                        "/api/notifications/send-news",
                                        "/api/notifications/device-count",
                                        "/api/notifications/unregister-device/{deviceId}",

                                        // Страница ошибки
                                        "/error"
                                ).permitAll()
                                // Все остальные запросы требуют авторизации
                                .anyRequest().authenticated()
                )
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID", "refreshToken")
                );
    }
}