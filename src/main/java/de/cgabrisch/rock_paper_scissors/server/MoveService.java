package de.cgabrisch.rock_paper_scissors.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class MoveService {
    private final static Logger log = LoggerFactory.getLogger(MoveService.class);
    private final static int SYMBOL_COUNT = Symbol.values().length;
    
    Mono<Move> getMoveFromPlayer(Player player, String roundId, String opponentName) {
        int index = Double.valueOf(Math.random() * SYMBOL_COUNT).intValue();
        int stake = Double.valueOf(Math.random() * player.credit()).intValue() + 1;
        
        Move move = new Move(stake, Symbol.values()[index]);
        
        log.debug("{} in round {} against {}: {}", player.name(), roundId, opponentName, move);
        
        return Mono.just(move);
    }
}
