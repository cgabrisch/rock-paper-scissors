package de.cgabrisch.rock_paper_scissors.server;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cgabrisch.rock_paper_scissors.api.round.Move;
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
            
            Mono<Round> roundTry = roundTry(uuid, player1, player2);
            
            return roundTry
              .doOnNext(round -> {
                  Flux.concat(moveService.notifyPlayer(player1, round), moveService.notifyPlayer(player2, round)).subscribe();
                  availablePlayersService.checkInPlayer(updatePlayerAfterRound(player1, round));
                  availablePlayersService.checkInPlayer(updatePlayerAfterRound(player2, round));
              });
        });
    }

    private Mono<Round> roundTry(String uuid, Player player1, Player player2) {
        Mono<Move> movePlayer1 = moveService.getMoveFromPlayer(player1, uuid, player2.name());
        Mono<Move> movePlayer2 = moveService.getMoveFromPlayer(player2, uuid, player1.name());
        
        Mono<Round> roundTry = Mono.zip(movePlayer1, movePlayer2).map((moves) -> {
            Move move1 = moves.getT1();
            int sanitizedStake1 = Math.min(player1.credit(), move1.stake());
            Move move2 = moves.getT2();
            int sanitizedStake2 = Math.min(player2.credit(), move2.stake());
            int stake = Math.min(sanitizedStake1, sanitizedStake2);
            
            Calls calls = new Calls(move1.symbol(), move2.symbol());

            return new Round(uuid, player1, player2, calls, stake);
        });
        return roundTry;
    }
    
    private Player updatePlayerAfterRound(Player player, Round round) {
        return round.getWinner().map(winner -> player == winner ? player.winning(round.stake()) : player.losing(round.stake())).orElse(player);
    }
}
