package de.cgabrisch.rock_paper_scissors.server;

import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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

    @GetMapping("/play/{rounds}")
    public Flux<Round> playGame(@PathVariable @Valid @Min(1) int rounds) {
        Player player1 = new Player("1", "Player 1", 101, 1, 0, "http://localhost:8081");
        Player player2 = new Player("2", "Player 2", 99, 0, 1, "http://localhost:8082");
        Calls calls = new Calls(Symbol.PAPER, Symbol.ROCK);
        Round round = new Round(player1, player2, player1, calls, 1);
        return Flux.fromStream(Stream.generate(() -> round)).take(rounds, true);
    }
}
