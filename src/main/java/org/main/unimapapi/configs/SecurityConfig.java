package org.main.unimapapi.configs;

import org.main.unimapapi.utils.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureHttpSecurity(http);
        return http.build();
    }

    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/",
                                        "/oauth2/**",
                                        "/login**",
                                        "/api/unimap_pc/check-connection",
                                        "/api/unimap_pc/authenticate/**",
                                        "/api/unimap_pc/register",
                                        "/api/unimap_pc/user/email/**",
                                        "/api/unimap_pc/user/create",
                                        "/api/unimap_pc/refresh",
                                        "/api/unimap_pc/resources/**",

                                        "/api/unimap_pc/news/all",
                                        "/api/unimap_pc/log"

                                        "/api/unimap_pc/comments/teacher/**",
                                        "/api/unimap_pc/comments/subject/**",
                                         "/api/unimap_pc/comments/subject",
                                        "/api/unimap_pc/comments/teacher",
                                        "/api/unimap_pc/news/all"
                                         // TOD O: error page "/error"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login")
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureUrl("/login?error=true")
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
                .clientId(AppConfig.getOauth2Google_id())
                .clientSecret(AppConfig.getOauth2Google_secret())
                .redirectUri("{baseUrl}/oauth2/callback/google")
                .scope("email", "profile")
                .build();
    }
    private ClientRegistration facebookClientRegistration() {
        return CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                .clientId(AppConfig.getOauth2Facebook_id())
                .clientSecret(AppConfig.getOauth2Facebook_secret())
                .redirectUri("{baseUrl}/oauth2/callback/facebook")
                .scope("email", "public_profile")
                .build();
    }
}