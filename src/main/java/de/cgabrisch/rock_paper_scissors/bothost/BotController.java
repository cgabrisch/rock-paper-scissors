package de.cgabrisch.rock_paper_scissors.bothost;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.api.player.Player;
import de.cgabrisch.rock_paper_scissors.api.player.PlayerId;
import de.cgabrisch.rock_paper_scissors.api.player.PlayerRegistration;
import de.cgabrisch.rock_paper_scissors.api.round.Move;
import de.cgabrisch.rock_paper_scissors.api.round.MoveRequest;
import de.cgabrisch.rock_paper_scissors.api.round.RoundResult;
import de.cgabrisch.rock_paper_scissors.api.round.Symbol;
import reactor.core.publisher.Mono;

@RestController
public class BotController {
    private final static int SYMBOL_COUNT = Symbol.values().length;
    private final static Logger log = LoggerFactory.getLogger(BotController.class);
    
    @Value("${player_registry.url}")
    private String playerRegistryUrl;
    
    @Value("${own_server.url}")
    private String ownUrl;
    
    private final Map<String, String> playerIdsToNames = new HashMap<>();
    
    @PostMapping("/bots")
    Mono<PlayerId> newBot(@RequestParam("name") String name) {
        return WebClient.create(playerRegistryUrl + "/players")
                .post()
                .bodyValue(new PlayerRegistration(name, ownUrl))
                .retrieve()
                .bodyToMono(PlayerId.class)
                .doOnNext(playerId -> this.playerIdsToNames.put(playerId.id(), name));
    }
    
    @PostMapping("/round/call/{playerId}")
    Mono<Move> requestMove(@PathVariable("playerId") String playerId, @RequestBody MoveRequest moveRequest) {
        String playerName = this.playerIdsToNames.getOrDefault(playerId, "UNKNOWN"); // TODO throw error if unknown
        
        return WebClient.create(playerRegistryUrl)
            .get().uri("/players/{playerId}", playerId)
            .retrieve()
            .bodyToMono(Player.class)
            .map(player -> {
                int index = Double.valueOf(Math.random() * SYMBOL_COUNT).intValue();
                int stake = Double.valueOf(Math.random() * player.credit()).intValue() + 1;
                
                Move move = new Move(stake, Symbol.values()[index]);
                
                log.debug("Round {}: {}'s move against {} is {}", moveRequest.roundId(), playerName, moveRequest.opponent(), move);
                
                return move;
            });
    }
    
    @PostMapping("/round/result/{playerId}")
    void notifyResult(@PathVariable("playerId") String playerId, @RequestBody RoundResult roundResult) {
        String player = this.playerIdsToNames.getOrDefault(playerId, "UNKNOWN"); // TODO throw error if unknown
        
        // TODO DRAW berücksichtigen
        log.debug("Round {}: {} {} amount: {} ", roundResult.roundId(), player, roundResult.result(), roundResult.stake());
    }
    
}
