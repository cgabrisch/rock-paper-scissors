package de.cgabrisch.rock_paper_scissors.botserver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BotServer.class);
        
        Map<String, Object> botServerConfig = new HashMap<>();
        botServerConfig.put("server.port", 8081);
        botServerConfig.put("player_registry.url", "http://localhost:8082");
        botServerConfig.put("own_server.url", "http://localhost:8081");
        app.setDefaultProperties(botServerConfig);
        app.run(args);
    }

}
