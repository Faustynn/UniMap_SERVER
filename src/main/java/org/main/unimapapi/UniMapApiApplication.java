package org.main.unimapapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.main.unimapapi")
public class UniMapApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniMapApiApplication.class,args);
    }
}