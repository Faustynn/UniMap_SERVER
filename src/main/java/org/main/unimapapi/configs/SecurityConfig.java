package org.main.unimapapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

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
                                // List all endpoints accessible without authorisation
                                .requestMatchers(
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
                                        "/api/unimap_pc/change_email/**",
                                        "/api/unimap_pc/check-connection",
                                        "/api/unimap_pc/authenticate/**",
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
                                        "/api/unimap_pc/user/delete/all/**"
                                         // TOD O: error page "/error"
                                ).permitAll()
                                // All other requests require authorisation
                                .anyRequest().authenticated()
                )
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID", "refreshToken")
                );
    }
// TODO: IF ERR THROW ROTATE TOKENS WE NEED TO CLEAR REFRESH FROM COOKIES
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                this.googleClientRegistration(),
                this.facebookClientRegistration()
        );
    }

    private ClientRegistration googleClientRegistration() {
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(AppConfig.getGOOGLE_CLIENT_ID())
                .clientSecret(AppConfig.getGOOGLE_CLIENT_SECRET())
                .redirectUri("{baseUrl}/oauth2/callback/google")
                .scope("email", "profile")
                .build();
    }
    private ClientRegistration facebookClientRegistration() {
        return CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                .clientId(AppConfig.getFACEBOOK_CLIENT_ID())
                .clientSecret(AppConfig.getFACEBOOK_CLIENT_SECRET())
                .redirectUri("{baseUrl}/oauth2/callback/facebook")
                .scope("email", "public_profile")
                .build();
    }
}