package de.cgabrisch.rock_paper_scissors.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GameServer.class);
        
        app.run(args);
    }

}
