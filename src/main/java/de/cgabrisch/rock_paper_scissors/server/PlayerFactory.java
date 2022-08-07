package de.cgabrisch.rock_paper_scissors.server;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.cgabrisch.rock_paper_scissors.api.player.PlayerRegistration;

@Service
public class PlayerFactory {
    public Player createPlayer(PlayerRegistration registration) {
        return new Player(UUID.randomUUID().toString(), registration.name(), registration.clientUrl(), 100, 0, 0);
    }
}
