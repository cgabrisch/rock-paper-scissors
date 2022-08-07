package de.cgabrisch.rock_paper_scissors.server;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@Validated
public class PlayerController {
    @Autowired
    private PlayerFactory playerFactory;
    
    @Autowired
    private AvailablePlayersService availablePlayersService;

    static record PlayerId(String id) {

    }

    @PostMapping("/players")
    Mono<PlayerId> newPlayer(@RequestBody @Valid PlayerRegistration playerRegistration) {
        return Mono.just(playerRegistration)
                .map(playerFactory::createPlayer)
                .doOnNext(availablePlayersService::checkInPlayer)
                .map(Player::id)
                .map(PlayerId::new);
    }
}
