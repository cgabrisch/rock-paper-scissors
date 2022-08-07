package de.cgabrisch.rock_paper_scissors.server;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayService {
    private final AvailablePlayersService availablePlayersService;
    
    @Autowired
    public PlayService(AvailablePlayersService availablePlayersService) {
        this.availablePlayersService = availablePlayersService;
    }

    public Flux<Round> playRounds(int rounds) {
        return Flux.merge(IntStream.range(0, rounds).mapToObj(i -> playRound()).collect(Collectors.toList()));
    }

    private Mono<Round> playRound() {
        return Mono.fromFuture(availablePlayersService.checkOutPairOfPlayers()).map((players) -> {
            Calls calls = new Calls(Symbol.PAPER, Symbol.ROCK);
            Round round = new Round(players.getT1(), players.getT2(), players.getT1(), calls, 1);
            
            availablePlayersService.checkInPlayer(players.getT1());
            availablePlayersService.checkInPlayer(players.getT2());
            return round;
        });
    }

}
