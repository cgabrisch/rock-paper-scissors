package de.cgabrisch.rock_paper_scissors.server;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PlayerFactory {
    public Player createPlayer(PlayerRegistration registration) {
        return new Player(UUID.randomUUID().toString(), registration.name(), registration.clientUrl(), 100, 0, 0);
    }
}
