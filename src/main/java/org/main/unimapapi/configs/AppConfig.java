package org.main.unimapapi.configs;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    @Getter
    private static final String sender = "unimapofficial@gmail.com";
    @Getter
    private static final String host = "smtp.gmail.com";
    @Getter
    private static final String port = "587";

    private static final Properties properties = new Properties();
    static {
        try (InputStream input = AppConfig.class.getResourceAsStream("/org.main.unimapapi/config.properties")
        ) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("config.properties file is null");
            }
        } catch (IOException ex) {
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

    @Getter
    @Value("${oauth2-id-google}")
    private String oauth2GoogleId;

    @Getter
    @Value("${oauth2-secret-google}")
    private String oauth2GoogleSecret;

    @Getter
    @Value("${oauth2-id-facebook}")
    private String oauth2FacebookId;

    @Getter
    @Value("${oauth2-secret-facebook}")
    private String oauth2FacebookSecret;

    public static String getOauth2Google_secret() {
        return properties.getProperty("oauth2-secret-google");
    }
    public static String getOauth2Google_id() {
        return properties.getProperty("oauth2-id-google");
    }

    public static String getOauth2Facebook_secret() {
        return properties.getProperty("oauth2-secret-facebook");
    }
    public static String getOauth2Facebook_id() {
        return properties.getProperty("oauth2-id-facebook");
    }

}