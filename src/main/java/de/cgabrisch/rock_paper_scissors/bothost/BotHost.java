package de.cgabrisch.rock_paper_scissors.bothost;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotHost {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BotHost.class);
        
        Map<String, Object> botHostConfig = new HashMap<>();
        botHostConfig.put("server.port", 8081);
        botHostConfig.put("player_registry.url", "http://localhost:8082");
        botHostConfig.put("own_server.url", "http://localhost:8081");
        app.setDefaultProperties(botHostConfig);
        app.run(args);
    }

}
