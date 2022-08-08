package de.cgabrisch.rock_paper_scissors.playerregistry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlayerRegistry {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PlayerRegistry.class);
        
        Map<String, Object> config = new HashMap<>();
        config.put("server.port", 8082);
        app.setDefaultProperties(config);
        app.run(args);
    }

}
