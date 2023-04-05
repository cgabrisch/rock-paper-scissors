package de.cgabrisch.rock_paper_scissors.botserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.shared.player.Player;
import de.cgabrisch.rock_paper_scissors.shared.player.PlayerId;
import de.cgabrisch.rock_paper_scissors.shared.player.PlayerRegistration;
import de.cgabrisch.rock_paper_scissors.shared.round.Move;
import de.cgabrisch.rock_paper_scissors.shared.round.MoveRequest;
import de.cgabrisch.rock_paper_scissors.shared.round.RoundResult;
import reactor.core.publisher.Mono;

@RestController
class BotController {
    private final static Logger log = LoggerFactory.getLogger(BotController.class);
    
    @Autowired
    private BotFactory botFactory;
    
    @Value("${player_registry.url}")
    private String playerRegistryUrl;
    
    @Value("${bot_server.url}")
    private String ownUrl;
    
    private final Map<String, Bot> playerIdsToBots = new HashMap<>();
    
    @PostMapping("/bots")
    Mono<PlayerId> newBot(@RequestParam("name") String name) {
        return WebClient.create(playerRegistryUrl + "/players")
                .post()
                .bodyValue(new PlayerRegistration(name, ownUrl))
                .retrieve()
                .bodyToMono(PlayerId.class)
                .doOnNext(playerId -> this.playerIdsToBots.put(playerId.id(), this.botFactory.createBot(name, BotFactory.RANDOM_STAKE)));
    }
    
    @PostMapping("/round/call/{playerId}")
    Mono<Move> requestMove(@PathVariable("playerId") String playerId, @RequestBody MoveRequest moveRequest) {
        Bot bot = getBotForPlayerId(playerId);
        
        return WebClient.create(playerRegistryUrl)
            .get().uri("/players/{playerId}", playerId)
            .retrieve()
            .bodyToMono(Player.class)
            .map(player -> {
                Move move = bot.getMove(player.credit());
                
                log.debug("Round {}: {}'s move against {} is {}", moveRequest.roundId(), bot.getName(), moveRequest.opponent(), move);
                
                return move;
            });
    }
    
    @PostMapping("/round/result/{playerId}")
    void notifyResult(@PathVariable("playerId") String playerId, @RequestBody RoundResult roundResult) {
        Bot bot = getBotForPlayerId(playerId);
        
        String logMessage = switch (roundResult.result()) {
            case DRAW -> "Round {} is draw: No credit change for {}";
            case LOST -> "Round {}: {} loses {}";
            case WON -> "Round {}: {} wins {}";
        };
        log.debug(logMessage, roundResult.roundId(), bot.getName(), roundResult.stake());
    }
    

    private Bot getBotForPlayerId(String playerId) {
        // TODO throw error if unknown
        return this.playerIdsToBots.getOrDefault(playerId, this.botFactory.createBot("UNKNOWN", BotFactory.MIN_STAKE));
    }
}
