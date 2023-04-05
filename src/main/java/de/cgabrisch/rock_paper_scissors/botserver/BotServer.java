package de.cgabrisch.rock_paper_scissors.botserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BotServer.class);
        
        app.run(args);
    }

}
