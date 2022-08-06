package de.cgabrisch.rock_paper_scissors.server;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
public class GameController {
    @Autowired
    private PlayService playService;

    @GetMapping("/play/{rounds}")
    public Flux<Round> playGame(@PathVariable @Valid @Min(1) int rounds) {
        return this.playService.playRounds(rounds);
    }
}
