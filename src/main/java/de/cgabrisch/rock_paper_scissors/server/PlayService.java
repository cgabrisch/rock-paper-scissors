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
    
    private final MoveService moveService;
    
    @Autowired
    public PlayService(AvailablePlayersService availablePlayersService, MoveService moveService) {
        this.availablePlayersService = availablePlayersService;
        this.moveService = moveService;
    }

    public Flux<Round> playRounds(int rounds) {
        return Flux.merge(IntStream.range(0, rounds).mapToObj(i -> playRound()).collect(Collectors.toList()));
    }

    private Mono<Round> playRound() {
        return availablePlayersService.checkOutPairOfPlayers().flatMap((players) -> {
            Player player1 = players.getT1();
            Player player2 = players.getT2();
            
            String uuid = UUID.randomUUID().toString();
            log.debug("Playing round {}", uuid);
            Mono<Move> movePlayer1 = moveService.getMoveFromPlayer(player1, uuid, player2.name());
            Mono<Move> movePlayer2 = moveService.getMoveFromPlayer(player2, uuid, player1.name());
            
            return Mono.zip(movePlayer1, movePlayer2).doAfterTerminate(() -> {
                availablePlayersService.checkInPlayer(player1);
                availablePlayersService.checkInPlayer(player2);
            }).map((moves) -> {
                Move move1 = moves.getT1();
                int sanitizedStake1 = Math.min(player1.credit(), move1.stake());
                Move move2 = moves.getT2();
                int sanitizedStake2 = Math.min(player2.credit(), move2.stake());
                int stake = Math.min(sanitizedStake1, sanitizedStake2);
                
                Calls calls = new Calls(move1.symbol(), move2.symbol());

                return new Round(uuid, player1, player2, calls, stake);
            });
        });
    }
}
