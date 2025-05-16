package org.main.unimapapi;

// Import the necessary Spring Boot classes
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Entry point for the UniMap API Spring Boot application.
 *
 * <p>This class bootstraps the entire backend application using Spring Boot.
 * The {@code @SpringBootApplication} annotation enables component scanning,
 * auto-configuration, and configuration support.</p>
 *
 * <p><b>Features enabled by @SpringBootApplication:</b></p>
 * <ul>
 *   <li>{@link org.springframework.context.annotation.Configuration} — marks the class as a source of bean definitions</li>
 *   <li>{@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} — enables auto-configuration of the Spring context</li>
 *   <li>{@link org.springframework.context.annotation.ComponentScan} — scans for components (controllers, services, etc.)</li>
 * </ul>
 *
 * <p>The `scanBasePackages` argument ensures that Spring scans the entire project package: {@code org.main.unimapapi}</p>
 */
@SpringBootApplication(scanBasePackages = "org.main.unimapapi")
public class UniMapApiApplication {

    /**
     * Starts the UniMap backend server.
     *
     * @param args application arguments passed at runtime (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(UniMapApiApplication.class,args);
    }
}