package org.main.unimapapi.configs;


import lombok.Data;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * UniMap application configuration class
 *
 * Responsible for:
 * - Connection to the mail server (SMTP)
 * - Loading sensitive data from config.properties
 * - OAuth2 settings (Google and Facebook)
 * - Encryption (AES)
 */
@Data
@UtilityClass
public class AppConfig {
    @Getter
    private static final String sender = "unimapofficial@gmail.com";

    // Google's SMTP server
    @Getter
    private static final String host = "smtp.gmail.com";
    // SMTP server port
    @Getter
    private static final String port = "587";

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class.getResourceAsStream("/org.main.unimapapi/config.properties")
        ) {
            if (input != null) {
                properties.load(input);
            } else {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "config.properties file is missing.");
                throw new RuntimeException("config.properties file is null");
            }
        } catch (IOException ex) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to load application.properties: " + ex.getMessage());
            throw new RuntimeException("Failed to load application.properties: " + ex.getMessage(), ex);
        }
    }

    public static String getAesAlgorithm() {
        return properties.getProperty("AES_ALGORITHM");
    }

    public static String getSecretKey() {
        return properties.getProperty("SECRET_KEY");
    }

    public static String getIv() {
        return properties.getProperty("IV");
    }

    public static String getPassword() {
        return properties.getProperty("PASSWORD_email");
    }

    public static String getAccessKey() {
        return properties.getProperty("ACCESS_SECRET_KEY");
    }

    public static String getRefreshKey() {
        return properties.getProperty("REFRESH_SECRET_KEY");
    }





                                         //oAuth2 settings
    // Google OAuth2 constants
    @Getter
    private final String GOOGLE_CLIENT_ID = "162210744287-qn8sq1a09rbjcagtnqmk38t98f8132u3.apps.googleusercontent.com";
    @Getter
    private final String GOOGLE_CLIENT_SECRET = "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";
    @Getter
    private final String GOOGLE_REDIRECT_URI = "http://localhost:3000/api/unimap_pc/oauth2/google";
    @Getter
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    @Getter
    private final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    // Facebook OAuth2 constants
    @Getter
    private final String FACEBOOK_CLIENT_ID = "577543324819426";
    @Getter
    private final String FACEBOOK_CLIENT_SECRET = "e0cf51de3d6d04e8cba3776cf89b50d9";
    @Getter
    private final String FACEBOOK_REDIRECT_URI = "http://localhost:3000/api/unimap_pc/oauth2/facebook";
    @Getter
    private final String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/v12.0/oauth/access_token";
    @Getter
    private final String FACEBOOK_USER_INFO_URL = "https://graph.facebook.com/me?fields=id,name,email";

    // OAuth2 login URL
    @Getter private final String OAUTH2_LOGIN_URL = "http://localhost:8080/api/unimap_pc/oauth2/login";





    @Getter
    private static final long EXPIRATION_TIME_ACCESS = 60000;

    @Getter
    private static final long EXPIRATION_TIME_REFRESH = 86400000;

    public static String getLogLevel() {
        return properties.getProperty("LOG_LEVEL", "INFO");
    }

    @Getter
    private static final String SERVER_LOG_FILE = "src/main/resources/org.main.unimapapi/logs/server_logs.xml";
    @Getter
    private static final String CLIENT_LOG_FILE = "src/main/resources/org.main.unimapapi/logs/client_logs.xml";

}