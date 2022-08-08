package de.cgabrisch.rock_paper_scissors.playerregistry;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.cgabrisch.rock_paper_scissors.api.player.Opponents;
import de.cgabrisch.rock_paper_scissors.api.player.Player;
import de.cgabrisch.rock_paper_scissors.api.player.PlayerId;
import de.cgabrisch.rock_paper_scissors.api.player.PlayerRegistration;
import reactor.core.publisher.Mono;

@RestController
@Validated
class PlayerController {
    @Autowired
    private PlayerFactory playerFactory;
    
    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping("/players")
    Mono<PlayerId> newPlayer(@RequestBody @Valid PlayerRegistration playerRegistration) {
        return Mono.just(playerRegistration)
                .map(playerFactory::createPlayer)
                .doOnNext(playerRepository::addPlayer)
                .map(Player::id)
                .map(PlayerId::new);
    }
    
    @PostMapping("/opponents/request")
    Mono<Opponents> requestOpponents() {
        return this.playerRepository.requestOpponents();
    }
    
    @PostMapping("/opponents/release")
    void releaseOpponents(@RequestBody Opponents opponents) {
        this.playerRepository.releaseOpponents(opponents);
    }
}
