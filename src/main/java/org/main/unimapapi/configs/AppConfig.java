package org.main.unimapapi.configs;


import lombok.Getter;

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
        return properties.getProperty("PASSWORD");
    }

    public static String getAccessKey() {
        return properties.getProperty("ACCESS_SECRET_KEY");
    }

    public static String getRefreshKey() {
        return properties.getProperty("REFRESH_SECRET_KEY");
    }
}