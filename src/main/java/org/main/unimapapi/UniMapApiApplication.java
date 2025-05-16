package org.main.unimapapi;

// Import the necessary Spring Boot classes
import org.main.unimapapi.utils.ParserUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/*
 * UniMap API main application launch class
 *
 * The @SpringBootApplication annotation indicates that this is the Spring Boot application entry point
 * It combines:
 * - @Configuration: allows configuration bins to be defined
 * - @EnableAutoConfiguration: enables automatic Spring configuration
 * - @ComponentScan: tells Spring where to look for components, services, controllers, etc.
 *
 * The scanBasePackages parameter specifies where to look for project components
 */
@SpringBootApplication(scanBasePackages = "org.main.unimapapi")
public class UniMapApiApplication {

    public static void main(String[] args) {
        ParserUtil.startParsingSchedule();

        SpringApplication.run(UniMapApiApplication.class,args);
    }
}