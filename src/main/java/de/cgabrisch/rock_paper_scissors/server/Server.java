package de.cgabrisch.rock_paper_scissors.server;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Server.class);
        
        Map<String, Object> gameServerConfig = new HashMap<>();
        gameServerConfig.put("server.port", 8080);
        gameServerConfig.put("player_registry.url", "http://localhost:8082");
        app.setDefaultProperties(gameServerConfig);
        app.run(args);
    }

}
