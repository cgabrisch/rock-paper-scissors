package de.cgabrisch.rock_paper_scissors.server;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayService {
    private final static Logger log = LoggerFactory.getLogger(PlayService.class);
    
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
            String uuid = UUID.randomUUID().toString();
            log.debug("Playing round {}", uuid);
            Calls calls = new Calls(Symbol.PAPER, Symbol.ROCK);
            Round round = new Round(uuid, players.getT1(), players.getT2(), players.getT1(), calls, 1);
            
            availablePlayersService.checkInPlayer(players.getT1());
            availablePlayersService.checkInPlayer(players.getT2());
            return round;
        });
    }

}
