package de.cgabrisch.rock_paper_scissors.gameserver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/game")
@Validated
class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping("/play/{rounds}")
    Flux<Round> playGame(@PathVariable @Valid @Min(1) int rounds) {
        return this.gameService.playRounds(rounds);
    }
}
