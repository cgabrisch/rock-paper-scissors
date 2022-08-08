package de.cgabrisch.rock_paper_scissors.gameserver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GameServer.class);
        
        // TODO externalize configuration
        Map<String, Object> gameServerConfig = new HashMap<>();
        gameServerConfig.put("server.port", 8080);
        gameServerConfig.put("player_registry.url", "http://localhost:8082");
        app.setDefaultProperties(gameServerConfig);
        app.run(args);
    }

}
