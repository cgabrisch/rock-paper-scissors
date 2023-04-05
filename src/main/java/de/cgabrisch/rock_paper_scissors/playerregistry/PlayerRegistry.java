package de.cgabrisch.rock_paper_scissors.playerregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlayerRegistry {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PlayerRegistry.class);
        
        app.run(args);
    }

}
