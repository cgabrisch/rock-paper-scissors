package de.cgabrisch.rock_paper_scissors.server;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class PlayService {
    public Flux<Round> playRounds(int rounds) {
        Player player1 = new Player("1", "Player 1", 101, 1, 0, "http://localhost:8081");
        Player player2 = new Player("2", "Player 2", 99, 0, 1, "http://localhost:8082");
        Calls calls = new Calls(Symbol.PAPER, Symbol.ROCK);
        Round round = new Round(player1, player2, player1, calls, 1);
        return Flux.fromStream(Stream.generate(() -> round)).take(rounds, true);
    }

}
