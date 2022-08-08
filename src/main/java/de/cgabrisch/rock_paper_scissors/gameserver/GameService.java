package de.cgabrisch.rock_paper_scissors.gameserver;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.api.player.Opponents;
import de.cgabrisch.rock_paper_scissors.api.player.Player;
import de.cgabrisch.rock_paper_scissors.api.round.Move;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GameService {
    private final static Logger log = LoggerFactory.getLogger(GameService.class);
    
    private final String playerRegistryUrl;
    private final RoundService roundService;
    
    @Autowired
    public GameService(
            @Value("${player_registry.url}") String playerRegistryUrl,
            RoundService roundService) {
        this.playerRegistryUrl = playerRegistryUrl;
        this.roundService = roundService;
    }

    public Flux<Round> playRounds(int rounds) {
        return Flux.merge(IntStream.range(0, rounds).mapToObj(i -> playRound()).collect(Collectors.toList()));
    }

    private Mono<Round> playRound() {
        return requestOpponents().flatMap((opponents) -> {
            Player player1 = opponents.player1();
            Player player2 = opponents.player2();
            
            String roundId = UUID.randomUUID().toString();
            log.debug("Playing round {}", roundId);
            
            return roundMono(roundId, player1, player2)
              .doOnNext(round -> {
                  Flux.concat(
                          roundService.notifyPlayer(player1, round), roundService.notifyPlayer(player2, round),
                          releaseOpponents(updateOpponentStatsAfterRound(opponents, round))).subscribe();
              });
        });
    }

    private Mono<Round> roundMono(String roundId, Player player1, Player player2) {
        Mono<Move> movePlayer1 = roundService.getMoveFromPlayer(player1, roundId, player2.name());
        Mono<Move> movePlayer2 = roundService.getMoveFromPlayer(player2, roundId, player1.name());
        
        return Mono.zip(movePlayer1, movePlayer2).map((moves) -> {
            Move move1 = moves.getT1();
            int sanitizedStake1 = Math.min(player1.credit(), move1.stake());
            Move move2 = moves.getT2();
            int sanitizedStake2 = Math.min(player2.credit(), move2.stake());
            int stake = Math.min(sanitizedStake1, sanitizedStake2);
            
            Calls calls = new Calls(move1.symbol(), move2.symbol());

            return new Round(roundId, player1, player2, calls, stake);
        });
    }
    
    private Opponents updateOpponentStatsAfterRound(Opponents opponents, Round round) {
        return new Opponents(
                updatePlayerStatsAfterRound(opponents.player1(), round), 
                updatePlayerStatsAfterRound(opponents.player2(), round));
    }
    
    private Player updatePlayerStatsAfterRound(Player player, Round round) {
        return round.getWinner().map(winner -> player == winner ? player.winning(round.stake()) : player.losing(round.stake())).orElse(player);
    }

    private Mono<Opponents> requestOpponents() {
        return WebClient.create(playerRegistryUrl).post().uri("/opponents/request").retrieve().bodyToMono(Opponents.class);
    }
    
    private Mono<Void> releaseOpponents(Opponents opponents) {
        return WebClient.create(playerRegistryUrl).post().uri("/opponents/release").bodyValue(opponents).retrieve().bodyToMono(Void.class);
    }
}
